package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.enums.mission.MissionStateEnum;
import net.monkeystudio.chatrbtw.mapper.ChatPetPersonalMissionMapper;
import net.monkeystudio.chatrbtw.sdk.wx.WxCustomerHelper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.MissionItem;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.DispatchMissionParam;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.TodayMission;
import net.monkeystudio.wx.service.WxPubService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xiaxin
 */
@Service
public class ChatPetMissionPoolService {

    private final static String MESSAGE_KEY = "chat_pet_mission";

    @Autowired
    private ChatPetMissionEnumService chatPetMissionEnumService;

    @Autowired
    private ChatPetPersonalMissionMapper chatPetPersonalMissionMapper;

    @Autowired
    private ChatPetMissionService chatPetMissionService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private RWxPubProductService rWxPubProductService;

    @Autowired
    private ChatPetRewardService chatPetRewardService;

    @Autowired
    private WxCustomerHelper wxCustomerHelper;

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private AdService adService;

    @Autowired
    private ChatPetTypeConfigService chatPetTypeConfigService;

    @Autowired
    private TransactionTemplate txTemplate;

    //每天只能最多完成三次邀请任务
    private static final Integer DAILY_INVITE_MISSION_MAX_TIME = 3;

    //每天阅读文章只能最多十次
    private static final Integer DAILY_SEARCH_NEWS_MISSION_MAX_TIME = 10;

    //每日互动任务
    private static final Integer DAILY_INTERACTION_MAX_TIME = 1;

    private final static String CHAT_PET_NEWS_MISSION_REWARD_TIPS =  "\u2705任务完成 前往\ud83d\udc49<a href=\"%s\">领取奖励</a>\ud83d\udc48";


    public void initSubscribe(){
        //起一条独立的线程去监听
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    List<String> list = redisCacheTemplate.brpop(0,MESSAGE_KEY);
                    String string = list.get(1);
                    Log.i("receive the message [?]",string);
                    String str[] = string.split(":");
                    Integer wxFanId = Integer.valueOf(str[0]);
                    String wxFanOpenId = str[1];
                    Integer adId = Integer.valueOf(str[2]);

                    if(str.length != 3){
                        Log.d("chatpet mission message errror." + str);
                        return ;
                    }

                    if(validatedWxFan(wxFanId,wxFanOpenId)){
                        ChatPet chatPet = chatPetService.getByWxFanId(wxFanId);

                        ChatPetPersonalMission searchMission = getChatPetOngoingMissionByMissionType(chatPet.getId(), ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE);

                        try {
                            completeChatPetMission(adId,null,searchMission.getId());
                        }catch (Exception e){
                            Log.e(e);
                        }
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("NewsMission");
        thread.start();
        Log.d("finished Subscribe");
    }


    private Boolean validatedWxFan(Integer wxFanId ,String wxFanOpenId){
        WxFan wxFan = wxFanService.getById(wxFanId);

        if(wxFan == null){
            return false;
        }

        if(wxFanOpenId == null){
            return false;
        }

        String wxFanOpenIdFromDb = wxFan.getWxFanOpenId();

        if(wxFanOpenId.equals(wxFanOpenIdFromDb)){
            return true;
        }

        return false;
    }

    /**
     * 当日第一次进入宠物陪聊h5或者粉丝第一次聊天互动时创建当日任务
     */
    public void createMissionWhenFirstChatOrComeH5(String wxPubOriginId,String wxFanOpenId){

        Integer chatPetId = chatPetService.getChatPetIdByFans(wxPubOriginId,wxFanOpenId);

        String createDailyMissionCountCacheKey = this.getCreateDailyMissionCountCacheKey(chatPetId);

        Long incr = redisCacheTemplate.incr(createDailyMissionCountCacheKey);

        if(incr.intValue() == 1){
            //填充任务池
            this.createDailyMission(chatPetId);
            //缓存时间为当日
            redisCacheTemplate.expire(createDailyMissionCountCacheKey, DateUtils.getCacheSeconds());
        }
    }


    /**
     * 组装当日任务数据
     */
    private void createDailyMission(Integer chatPetId){
        List<ChatPetMission> activeMissions = this.chatPetMissionService.getActiveMissions();

        if(ListUtil.isEmpty(activeMissions)){
            return;
        }

        for(ChatPetMission cpm:activeMissions){
            //派发任务
            DispatchMissionParam param = new DispatchMissionParam();
            param.setChatPetId(chatPetId);
            param.setMissionCode(cpm.getMissionCode());

            this.dispatchMission(param);
        }
    }


    /**
     * 派发任务
     * @param dispatchMissionParam
     */
    public void dispatchMission(DispatchMissionParam dispatchMissionParam) {

        ChatPetPersonalMission chatPetPersonalMission = new ChatPetPersonalMission();

        Integer missionCode = dispatchMissionParam.getMissionCode();
        Integer chatPetId = dispatchMissionParam.getChatPetId();

        if(chatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){

            //如果当天派发邀请好友任务次数不超过三次，可以派发任务
            Integer count = this.countDispatchMissionAmount(chatPetId, missionCode);//已完成任务数量
            if(count.intValue() < DAILY_INVITE_MISSION_MAX_TIME.intValue()){
                Integer adId = dispatchMissionParam.getAdId();

                chatPetPersonalMission.setChatPetId(chatPetId);
                chatPetPersonalMission.setCreateTime(new Date());
                chatPetPersonalMission.setState(MissionStateEnum.GOING_ON.getCode());
                chatPetPersonalMission.setMissionCode(missionCode);
                chatPetPersonalMission.setAdId(adId);

                this.save(chatPetPersonalMission);
                return;
            }
        }

        if(chatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){

            //如果当天派发资讯任务不超过十次，可以派发任务
            Integer count = this.countDispatchMissionAmount(chatPetId, missionCode);//已完成任务数量
            if(count.intValue() < DAILY_SEARCH_NEWS_MISSION_MAX_TIME.intValue()){
                //从任务池中获取一条陪聊宠广告
                Ad ad = this.getSearchNewsMissionAd(chatPetId);

                if(ad == null){
                    return;
                }

                chatPetPersonalMission.setChatPetId(chatPetId);
                chatPetPersonalMission.setCreateTime(new Date());
                chatPetPersonalMission.setState(MissionStateEnum.GOING_ON.getCode());
                chatPetPersonalMission.setMissionCode(missionCode);
                chatPetPersonalMission.setAdId(ad.getId());

                this.save(chatPetPersonalMission);

            }
            return ;
        }

        if(chatPetMissionEnumService.DAILY_CHAT_MISSION_CODE.equals(missionCode)){

            //如果当天派发每日互动任务不超过一次，可以派发任务
            Integer count = this.countDispatchMissionAmount(chatPetId, missionCode);//已完成任务数量
            if(count.intValue() < DAILY_INTERACTION_MAX_TIME.intValue()){

                chatPetPersonalMission.setChatPetId(chatPetId);
                chatPetPersonalMission.setCreateTime(new Date());
                chatPetPersonalMission.setState(MissionStateEnum.GOING_ON.getCode());
                chatPetPersonalMission.setMissionCode(missionCode);

                this.save(chatPetPersonalMission);

                return ;
            }
        }

        if(chatPetMissionEnumService.DAILY_PLAY_MINI_GAME_CODE.equals(missionCode)){
            chatPetPersonalMission.setChatPetId(chatPetId);
            chatPetPersonalMission.setCreateTime(new Date());
            chatPetPersonalMission.setState(MissionStateEnum.GOING_ON.getCode());
            chatPetPersonalMission.setMissionCode(missionCode);
            chatPetPersonalMission.setWxMiniGameId(dispatchMissionParam.getWxMiniGameId());

            this.save(chatPetPersonalMission);
        }

        if(chatPetMissionEnumService.DAILY_LOGIN_MINI_PROGRAM_CODE.equals(missionCode)){
            chatPetPersonalMission.setChatPetId(chatPetId);
            chatPetPersonalMission.setCreateTime(new Date());
            chatPetPersonalMission.setState(MissionStateEnum.GOING_ON.getCode());
            chatPetPersonalMission.setMissionCode(missionCode);

            this.save(chatPetPersonalMission);
        }

    }

    /**
     * 为宠物获取一则咨询任务广告
     * @param chatPetId
     * @return
     */
    private Ad getSearchNewsMissionAd(Integer chatPetId){
        ChatPet chatPet = chatPetService.getById(chatPetId);

        WxFan wxFan = wxFanService.getWxFan(chatPet.getWxPubOriginId(), chatPet.getWxFanOpenId());

        Ad missionAd = adService.getChatPetPushAd(wxFan.getId());

        return missionAd;
    }

    /**
     * @param chatPetId
     * @param missionCode
     * @return
     */
    public Boolean isReachMaxDispatchTime(Integer chatPetId,Integer missionCode){
        Boolean result = true;
        Integer count = this.countDispatchMissionAmount(chatPetId, missionCode);
        if(count.intValue() < DAILY_SEARCH_NEWS_MISSION_MAX_TIME.intValue()){
            result = false;
        }
        return result;
    }

    /**
     * 根据任务类型统计当天派发的次数
     * @param chatPetId
     * @param missionCode
     * @return
     */
    public Integer countDispatchMissionAmount(Integer chatPetId,Integer missionCode){
        Date createTime = DateUtils.getBeginDate(new Date());
        return chatPetPersonalMissionMapper.countDispatchMissionAmountByMissionCode(chatPetId,missionCode,createTime);
    }


    /**
     * 任务池新增记录
     * @param chatPetPersonalMission
     */
    public Integer save(ChatPetPersonalMission chatPetPersonalMission){
        ChatPetPersonalMission cppm = new ChatPetPersonalMission();

        BeanUtils.copyProperties(chatPetPersonalMission,cppm);

        return chatPetPersonalMissionMapper.insert(cppm);
    }


    /**
     * 场景:用于完成分享卡邀请任务
     * 功能:完成宠物任务
     * @param inviteeWxFanId    :被邀请人wxFanId
     * @param chatPetPersonalMissionId  :任务记录id
     */
    public void completeChatPetMission(Integer inviteeWxFanId,Integer chatPetPersonalMissionId) throws BizException{
        ChatPetPersonalMission chatPetPersonalMission = this.getById(chatPetPersonalMissionId);
        if(chatPetPersonalMission == null) return;

        //update 被邀请人wxFanId
        chatPetPersonalMission.setInviteeWxFanId(inviteeWxFanId);
        this.update(chatPetPersonalMission);

        this.completeChatPetMission(chatPetPersonalMissionId);
    }

    /**
     * 场景:用于完成阅读资讯任务,邀请人任务
     * 功能:完成宠物任务
     * 使用方法:阅读任务时inviteeWxFanId传入null,邀请任务时adId传入null
     * @param adId    :咨询任务广告id
     * @param inviteeWxFanId    :被邀请人wxFanId
     * @param chatPetPersonalMissionId  :任务记录id
     */
    public void completeChatPetMission(Integer adId,Integer inviteeWxFanId,Integer chatPetPersonalMissionId) throws BizException{
        ChatPetPersonalMission chatPetPersonalMission = this.getById(chatPetPersonalMissionId);
        if(chatPetPersonalMission == null) return;

        //update
        chatPetPersonalMission.setAdId(adId);
        chatPetPersonalMission.setInviteeWxFanId(inviteeWxFanId);
        this.update(chatPetPersonalMission);

        this.completeChatPetMission(chatPetPersonalMissionId);
    }

    /**
     * 完成宠物任务
     * @param chatPetPersonalMissionId
     */
    public void completeChatPetMission(Integer chatPetPersonalMissionId){
        ChatPetPersonalMission chatPetPersonalMission = this.getById(chatPetPersonalMissionId);

        if(chatPetPersonalMission == null) return;

        //任务状态更新为已完成
        chatPetPersonalMission.setState(MissionStateEnum.FINISH_NOT_AWARD.getCode());
        this.update(chatPetPersonalMission);

        Integer chatPetId = chatPetPersonalMission.getChatPetId();
        ChatPet chatPet = chatPetService.getById(chatPetId);
        Integer chatPetType = chatPet.getChatPetType();

        ChatPetTypeConfig chatPetTypeConfig = chatPetTypeConfigService.getChatPetTypeConfig(chatPetType);

        if(chatPetTypeConfig.getRewardType() == ChatPetTypeConfigService.Constants.MANUALLY_EXPERIENCE_COIN){
            //奖励池生成奖励
            chatPetRewardService.generateRewardWhenMissionDone(chatPetPersonalMissionId);

            //完成任务后给粉丝发送领奖tips
            this.sendChatPetRewardTips(chatPetId);

            //完成任务后派发一条新的任务
            DispatchMissionParam dispatchMissionParam = new DispatchMissionParam();
            dispatchMissionParam.setMissionCode(chatPetPersonalMission.getMissionCode());
            dispatchMissionParam.setChatPetId(chatPetId);

            this.dispatchMission(dispatchMissionParam);
        }

        //如果是仅需要领取金币，则完成任务的时候就直接添加经验
        if(chatPetTypeConfig.getRewardType() == ChatPetTypeConfigService.Constants.MANUALLY_ONLY_EXPERIENCE){
            chatPetRewardService.generateRewardWhenMissionDone(chatPetPersonalMission.getId());

            ChatPetRewardItem chatPetRewardItem = chatPetRewardService.getByMissionItemId(chatPetPersonalMission.getId());

            chatPetRewardService.reward(chatPetRewardItem.getId());
        }
    }



    /**
     * 完成任务后发送tips
     * @param chatPetId     宠物id
     */
    private void sendChatPetRewardTips(Integer chatPetId){

        ChatPet chatPet = chatPetService.getById(chatPetId);

        String wxPubOriginId = chatPet.getWxPubOriginId();
        String wxFanOpenId = chatPet.getWxFanOpenId();
        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);

        String tips = String.format(CHAT_PET_NEWS_MISSION_REWARD_TIPS,chatPetService.getHomePageUrl(wxPub.getId()));

        String result = wxCustomerHelper.sendTextMessageByAuthorizerId(wxFanOpenId,wxPub.getAppId(), tips);

        if(result.indexOf("errcode") == -1){
            Log.d(" =============== tips send failed ! error info : {?} =================" + result);

            return ;
        }
    }

    /**
     * 根据宠物id,广告id获取任务记录条数
     * @param chatPetId
     * @param adId
     * @return
     */
    private Integer getCountByChatPetIdAndAdId(Integer chatPetId,Integer adId){
        return this.chatPetPersonalMissionMapper.countByChatPetIdAndAdId(chatPetId,adId);
    }


    /**
     * 查询任务池记录
     * @param param 查询条件
     * @return
     */
    public ChatPetPersonalMission getPersonalMissionByParam(ChatPetPersonalMission param){
        return this.chatPetPersonalMissionMapper.selectByParam(param);
    }

    /**
     * 查询任务池记录集
     * @param param 查询条件
     * @return
     */
    public List<ChatPetPersonalMission> getPersonalMissionListByParam(ChatPetPersonalMission param){
        return this.chatPetPersonalMissionMapper.selectListByParam(param);
    }


    /**
     * 领取奖励后更新任务池记录
     * @param itemId
     */
    public void updateMissionWhenReward(Integer itemId){
        ChatPetPersonalMission cppm = this.getById(itemId);
        cppm.setState(MissionStateEnum.FINISH_AND_AWARD.getCode());
        cppm.setFinishTime(new Date());

        this.update(cppm);
    }




    public ChatPetPersonalMission getById(Integer id){
        return chatPetPersonalMissionMapper.selectByPrimaryKey(id);
    }



    public void update(ChatPetPersonalMission chatPetPersonalMission){
        chatPetPersonalMissionMapper.updateByPrimaryKey(chatPetPersonalMission);
    }


    public String getCreateDailyMissionCountCacheKey(Integer chatPetId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "chatPetDailyMission:" + chatPetId;
    }

    /**
     * 判断游戏任务是否已经完成
     * @param wxFanId
     * @param wxMiniGameId
     * @return
     */
    public Boolean missionIsFinished(Integer wxFanId ,Integer wxMiniGameId){
        ChatPet chatPet = chatPetService.getByWxFanId(wxFanId);
        Integer chatPetId = chatPet.getId();
        //通过fanId + gameId 找到
        ChatPetPersonalMission chatPetPersonalMissionParam = new ChatPetPersonalMission();
        chatPetPersonalMissionParam.setState(MissionStateEnum.GOING_ON.getCode());
        chatPetPersonalMissionParam.setMissionCode(ChatPetMissionEnumService.DAILY_PLAY_MINI_GAME_CODE);
        chatPetPersonalMissionParam.setCreateTime(DateUtils.getBeginDate(new Date()));
        chatPetPersonalMissionParam.setWxMiniGameId(wxMiniGameId);
        chatPetPersonalMissionParam.setChatPetId(chatPetId);

        ChatPetPersonalMission miniGameMission = this.getPersonalMissionByParam(chatPetPersonalMissionParam);
        if(miniGameMission == null){
            return true;
        }else {
            return false;
        }
    }


    @Transactional
    public void finishDailyMiniGameMission(Integer wxFanId ,Integer wxMiniGameId) {
        ChatPet chatPet = chatPetService.getByWxFanId(wxFanId);

        Integer chatPetId = chatPet.getId();

        ChatPetPersonalMission miniGameMission = this.getChatPetOngoingMissionByMissionType(chatPetId, ChatPetMissionEnumService.DAILY_PLAY_MINI_GAME_CODE, wxMiniGameId);

        if(miniGameMission != null){
            //txTemplate.execute(new TransactionCallbackWithoutResult() {
                //@Override
                //protected void doInTransactionWithoutResult(TransactionStatus status) {
            //}
            //});
            completeChatPetMission(miniGameMission.getId());
        }

    }


    /**
     * 根据宠物id,任务类型获取宠物当前处于未完成(正在进行中)状态的任务记录
     * @param chatPetId:宠物id
     * @param missionCode:任务类型code
     * @return:宠物任务记录
     */
    public ChatPetPersonalMission getChatPetOngoingMissionByMissionType(Integer chatPetId,Integer missionCode){
        ChatPetPersonalMission chatPetPersonalMissionParam = new ChatPetPersonalMission();

        chatPetPersonalMissionParam.setState(MissionStateEnum.GOING_ON.getCode());
        chatPetPersonalMissionParam.setMissionCode(missionCode);
        chatPetPersonalMissionParam.setCreateTime(DateUtils.getBeginDate(new Date()));
        chatPetPersonalMissionParam.setChatPetId(chatPetId);

        ChatPetPersonalMission record = this.getPersonalMissionByParam(chatPetPersonalMissionParam);

        return record;
    }

    /**
     * 使用场景:用于玩小游戏任务
     * 功能:根据宠物id,任务类型,小程序id获取宠物当前处于未完成(正在进行中)状态的任务记录
     * @param chatPetId:宠物id
     * @param missionCode:任务类型code
     * @return:宠物任务记录
     */
    public ChatPetPersonalMission getChatPetOngoingMissionByMissionType(Integer chatPetId,Integer missionCode,Integer miniGameId){
        ChatPetPersonalMission chatPetPersonalMissionParam = new ChatPetPersonalMission();

        chatPetPersonalMissionParam.setState(MissionStateEnum.GOING_ON.getCode());
        chatPetPersonalMissionParam.setMissionCode(missionCode);
        chatPetPersonalMissionParam.setWxMiniGameId(miniGameId);
        chatPetPersonalMissionParam.setCreateTime(DateUtils.getBeginDate(new Date()));
        chatPetPersonalMissionParam.setChatPetId(chatPetId);

        ChatPetPersonalMission record = this.getPersonalMissionByParam(chatPetPersonalMissionParam);

        return record;
    }

    /**
     * 获取昨日玩小游戏总人数
     * @return
     */
    public Integer getYesterdayPlayGamePeopleAmount(){
        Date yesterday = CommonUtils.dateOffset(new Date(),-1);

        Date beginTime = CommonUtils.dateStartTime(yesterday);

        Date endTime = CommonUtils.dateEndTime(yesterday);

        return chatPetPersonalMissionMapper.countDayPlayGamePeople(beginTime,endTime);
    }

    /**
     * 获取昨日玩小游戏总次数
     * @return
     */
    public Integer getYesterPlayGameTotalAmount(){
        Date yesterday = CommonUtils.dateOffset(new Date(),-1);

        Date beginTime = CommonUtils.dateStartTime(yesterday);

        Date endTime = CommonUtils.dateEndTime(yesterday);

        return chatPetPersonalMissionMapper.countDayPlayGameTotalAmount(beginTime,endTime);
    }

    /**
     * 统计小游戏被玩次数
     * @param miniGameId
     * @return
     */
    public Long getMiniGamePlayerNum(Integer miniGameId){
        return chatPetPersonalMissionMapper.countMiniGameFinishAmount(miniGameId,ChatPetMissionEnumService.DAILY_PLAY_MINI_GAME_CODE,MissionStateEnum.FINISH_AND_AWARD.getCode());
    }


    /**
     * 获取宠物今日小游戏任务map
     * @param chatPetId : 宠物id
     * @return   key:小游戏id  value:ChatPetPersonalMission任务对象
     */
    public Map<Integer,ChatPetPersonalMission> getTodayMiniGameMissionMap(Integer chatPetId){
        Date today = new Date();
        Date startTime = CommonUtils.dateStartTime(today);
        Date endTime = CommonUtils.dateEndTime(today);

        return chatPetPersonalMissionMapper.selectMiniGameMissionMap(chatPetId,startTime,endTime);
    }




    /**
     * 任务墙
     * @return
     */
    public TodayMission getTodayMissionWall(Integer chatPetId){
        /*ChatPetPersonalMission param = new ChatPetPersonalMission();
        param.setChatPetId(chatPetId);
        param.setCreateTime(DateUtils.getBeginDate(new Date()));

        List<ChatPetPersonalMission> cppms = this.getPersonalMissionListByParam(param);

        List<TodayMissionItem> fixedMissionList = new ArrayList<>();
        List<TodayMissionItem> randomMissionList = new ArrayList<>();

        for (ChatPetPersonalMission cppm : cppms){
            TodayMissionItem item = new TodayMissionItem();

            String missionName = chatPetMissionEnumService.getMissionByCode(cppm.getMissionCode()).getMissionName();

            Integer missionCode = cppm.getMissionCode();
            if(chatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode) || chatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){
                missionName = ChatPetMissionNoUtil.getMissionNo(cppm.getCreateTime()) + " " + missionName;
            }

            item.setMissionName(missionName);

            item.setState(cppm.getState());
            item.setItemId(cppm.getId());

            Integer missionType = chatPetMissionEnumService.getMissionByCode(missionCode).getMissionType();
            if(ChatPetMissionService.CHAT_PET_MISSION_TYPE_FIXED.equals(missionType)){
                fixedMissionList.add(item);
            }else if(ChatPetMissionService.CHAT_PET_MISSION_TYPE_RANDOM.equals(missionType)){
                randomMissionList.add(item);
            }
        }

        TodayMission todayMission = new TodayMission();
        todayMission.setFixedMissionList(fixedMissionList);
        todayMission.setRandomMissionList(randomMissionList);*/


        //互动任务
        TodayMission todayMission = new TodayMission();

        ChatPetPersonalMission dailyInteractionParam = new ChatPetPersonalMission();
        dailyInteractionParam.setChatPetId(chatPetId);
        dailyInteractionParam.setCreateTime(DateUtils.getBeginDate(new Date()));
        dailyInteractionParam.setState(MissionStateEnum.FINISH_AND_AWARD.getCode());

        MissionItem dailyInteraction = new MissionItem();

        dailyInteraction.setNeedToFinish(DAILY_INTERACTION_MAX_TIME);
        dailyInteraction.setMissionType(ChatPetMissionEnumService.DAILY_CHAT_MISSION_CODE);

        List<ChatPetPersonalMission> dailyInteractionMission = this.getPersonalMissionListByParam(dailyInteractionParam);

        //如果没有任务，则已经完成次数为0
        if(ListUtil.isEmpty(dailyInteractionMission)){
            dailyInteraction.setFinishTime(0);
        }else {
            dailyInteraction.setFinishTime(1);
        }
        todayMission.setDailyInteraction(dailyInteraction);

        //今日邀请
        ChatPetPersonalMission inviteFriendParam = new ChatPetPersonalMission();
        inviteFriendParam.setChatPetId(chatPetId);
        inviteFriendParam.setCreateTime(DateUtils.getBeginDate(new Date()));
        inviteFriendParam.setMissionCode(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE);
        inviteFriendParam.setState(MissionStateEnum.FINISH_AND_AWARD.getCode());

        MissionItem inviteFriend = new MissionItem();

        inviteFriend.setMissionType(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE);
        inviteFriend.setNeedToFinish(DAILY_INVITE_MISSION_MAX_TIME);

        List<ChatPetPersonalMission> inviteFriendList = this.getPersonalMissionListByParam(inviteFriendParam);

        //如果没有任务，则已经完成次数为0
        if(ListUtil.isEmpty(inviteFriendList)){
            inviteFriend.setFinishTime(0);
        }else {
            inviteFriend.setFinishTime(inviteFriendList.size());
        }
        todayMission.setInviteFriend(inviteFriend);


        //每日资讯
        ChatPetPersonalMission newsMissionParam = new ChatPetPersonalMission();
        newsMissionParam.setChatPetId(chatPetId);
        newsMissionParam.setCreateTime(DateUtils.getBeginDate(new Date()));
        newsMissionParam.setState(MissionStateEnum.FINISH_AND_AWARD.getCode());
        newsMissionParam.setMissionCode(ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE);

        List<ChatPetPersonalMission> newsMissionList = this.getPersonalMissionListByParam(newsMissionParam);

        MissionItem news = new MissionItem();
        //如果没有任务，则已经完成次数为0
        if(ListUtil.isEmpty(newsMissionList)){
            news.setFinishTime(0);
        }else {
            news.setFinishTime(newsMissionList.size());
        }
        todayMission.setNews(news);

        news.setNeedToFinish(DAILY_SEARCH_NEWS_MISSION_MAX_TIME);
        news.setMissionType(ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE);

        return todayMission;
    }

    /**
     * completeMissionParam
     * 完成宠物任务
     * @param completeMissionParam  完成任务参数
     */
    /*public void completeChatPetMission(CompleteMissionParam completeMissionParam){
        //查询当前任务记录查询对象
        ChatPetPersonalMission chatPetPersonalMission = null;

        if(completeMissionParam.getPersonalMissionId() != null){
            chatPetPersonalMission = this.getById(completeMissionParam.getPersonalMissionId());
        }else {
            ChatPetPersonalMission param = new ChatPetPersonalMission();
            Integer chatPetId = completeMissionParam.getChatPetId();
            param.setChatPetId(chatPetId);
            param.setMissionCode(completeMissionParam.getMissionCode());
            param.setAdId(completeMissionParam.getAdId());
            param.setCreateTime(DateUtils.getBeginDate(new Date()));
            param.setState(MissionStateEnum.GOING_ON.getCode());
            chatPetPersonalMission = this.getPersonalMissionByParam(param);
        }


        if(chatPetPersonalMission == null){//查找不到任务记录,任务已经完成
            return ;
        }


        //更新状态
        chatPetPersonalMission.setState(MissionStateEnum.FINISH_NOT_AWARD.getCode());
        chatPetPersonalMission.setInviteeWxFanId(completeMissionParam.getInviteeWxFanId());
        this.update(chatPetPersonalMission);

        Integer chatPetId = chatPetPersonalMission.getChatPetId();
        ChatPet chatPet = chatPetService.getById(chatPetId);
        Integer chatPetType = chatPet.getChatPetType();

        ChatPetTypeConfig chatPetTypeConfig = chatPetTypeConfigService.getChatPetTypeConfig(chatPetType);
        if(chatPetTypeConfig.getRewardType() == ChatPetTypeConfigService.Constants.MANUALLY_EXPERIENCE_COIN){
            //奖励池生成奖励
            chatPetRewardService.generateRewardWhenMissionDone(chatPetPersonalMission.getId());
            //完成任务后给粉丝发送领奖tips
            this.sendChatPetRewardTips(chatPetPersonalMission.getChatPetId());

            //完成任务后派发一条新的任务
            DispatchMissionParam dispatchMissionParam = new DispatchMissionParam();
            dispatchMissionParam.setMissionCode(completeMissionParam.getMissionCode());
            dispatchMissionParam.setChatPetId(completeMissionParam.getChatPetId());

            this.dispatchMission(dispatchMissionParam);
        }

        //如果是仅需要领取金币，则完成任务的时候就直接添加经验
        if(chatPetTypeConfig.getRewardType() == ChatPetTypeConfigService.Constants.MANUALLY_ONLY_EXPERIENCE){
            chatPetRewardService.generateRewardWhenMissionDone(chatPetPersonalMission.getId());
            ChatPetRewardItem chatPetRewardItem = chatPetRewardService.getByMissionItemId(chatPetPersonalMission.getId());
            chatPetRewardService.reward(chatPetRewardItem.getId());

        }
    }*/


}
