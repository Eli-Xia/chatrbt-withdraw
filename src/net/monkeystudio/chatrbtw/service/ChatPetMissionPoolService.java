package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.enums.mission.MissionStateEnum;
import net.monkeystudio.chatrbtw.mapper.ChatPetPersonalMissionMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.MissionItem;
import net.monkeystudio.chatrbtw.sdk.wx.WxCustomerHelper;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.CompleteMissionParam;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.DispatchMissionParam;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.TodayMission;
import net.monkeystudio.wx.service.WxPubService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    //每天只能最多完成三次邀请任务
    private static final Integer DAILY_INVITE_MISSION_MAX_TIME = 3;

    //每天阅读文章只能最多十次
    private static final Integer DAILY_SEARCH_NEWS_MISSION_MAX_TIME = 10;

    //每日互动任务
    private static final Integer DAILY_INTERACTION_MAX_TIME = 1;
    @Autowired
    private WxCustomerHelper wxCustomerHelper;

    @Autowired
    private WxPubService wxPubService;

    //private final static String CHAT_PET_NEWS_MISSION_REWARD_TIPS =  "恭喜你采矿成功,点击<a href=\"%s\">领取奖励</a>";
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
                        WxFan wxFan = wxFanService.getById(wxFanId);

                        CompleteMissionParam param = new CompleteMissionParam();

                        ChatPet chatPet = chatPetService.getChatPetByFans(wxFan.getWxPubOriginId(), wxFan.getWxFanOpenId());
                        param.setAdId(adId);
                        param.setChatPetId(chatPet.getId());
                        param.setMissionCode(ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE);

                        try {
                            completeChatPetMission(param);
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

            //仅初始固定任务数据
            if(!ChatPetMissionService.CHAT_PET_MISSION_TYPE_FIXED.equals(cpm.getMissionType())){
                continue;
            }

            ChatPetPersonalMission cppm = new ChatPetPersonalMission();

            cppm.setChatPetId(chatPetId);
            cppm.setCreateTime(new Date());
            cppm.setState(MissionStateEnum.GOING_ON.getCode());
            cppm.setMissionCode(cpm.getMissionCode());

            this.save(cppm);
        }
    }

    /**
     * 派发任务
     * @param dispatchMissionParam
     */
    public void dispatchMission(DispatchMissionParam dispatchMissionParam ) throws BizException{

        ChatPetPersonalMission chatPetPersonalMission = new ChatPetPersonalMission();

        Integer missionCode = dispatchMissionParam.getMissionCode();
        Integer chatPetId = dispatchMissionParam.getChatPetId();

        if(chatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(chatPetPersonalMission.getMissionCode())){
            Date date = TimeUtil.getStartTimestamp();

            //如果当天没有完成邀请好友次数不超过三次，可以派发任务
            List<ChatPetRewardItem> chatPetRewardItemList = chatPetRewardService.getByChatPetAndState(date, chatPetId ,ChatPetRewardService.HAVE_AWARD);
            if(chatPetRewardItemList == null || chatPetRewardItemList.size() < DAILY_INVITE_MISSION_MAX_TIME.intValue()){
                Integer adId = dispatchMissionParam.getAdId();

                chatPetPersonalMission.setChatPetId(chatPetId);
                chatPetPersonalMission.setCreateTime(new Date());
                chatPetPersonalMission.setState(MissionStateEnum.GOING_ON.getCode());
                chatPetPersonalMission.setMissionCode(missionCode);
                chatPetPersonalMission.setAdId(missionCode);
                chatPetPersonalMission.setAdId(adId);

                this.save(chatPetPersonalMission);
                return;
            }
        }

        if(chatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){

            Date date = TimeUtil.getStartTimestamp();

            //如果当天没有完成资讯任务十次，可以派发任务
            List<ChatPetRewardItem> chatPetRewardItemList = chatPetRewardService.getByChatPetAndState(date, chatPetId ,ChatPetRewardService.HAVE_AWARD);
            if(chatPetRewardItemList == null || chatPetRewardItemList.size() < DAILY_SEARCH_NEWS_MISSION_MAX_TIME.intValue()){

                chatPetPersonalMission.setChatPetId(chatPetId);
                chatPetPersonalMission.setCreateTime(new Date());
                chatPetPersonalMission.setState(MissionStateEnum.GOING_ON.getCode());
                chatPetPersonalMission.setMissionCode(missionCode);

                this.save(chatPetPersonalMission);

            }
            return ;
        }

        if(chatPetMissionEnumService.DAILY_CHAT_MISSION_CODE.equals(missionCode)){
            Date date = TimeUtil.getStartTimestamp();

            //如果当天没有完成每日互动任务一次，可以派发任务
            List<ChatPetRewardItem> chatPetRewardItemList = chatPetRewardService.getByChatPetAndState(date, chatPetId ,ChatPetRewardService.HAVE_AWARD);
            if(chatPetRewardItemList == null || chatPetRewardItemList.size() < DAILY_INTERACTION_MAX_TIME.intValue()){

                chatPetPersonalMission.setChatPetId(chatPetId);
                chatPetPersonalMission.setCreateTime(new Date());
                chatPetPersonalMission.setState(MissionStateEnum.GOING_ON.getCode());
                chatPetPersonalMission.setMissionCode(missionCode);

                this.save(chatPetPersonalMission);

                return ;
            }
        }

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
     * 派发资讯任务
     * @param adId
     * @param wxfanId
     */
    public void saveMissionRecordWhenPushChatPetAd(Integer adId,Integer wxfanId){
        //获取fanopenid
        WxFan wxfan = wxFanService.getById(wxfanId);
        String wxFanOpenId = wxfan.getWxFanOpenId();
        String wxPubOriginId = wxfan.getWxPubOriginId();

        Integer chatPetId = chatPetService.getChatPetIdByFans(wxPubOriginId,wxFanOpenId);

        ChatPetPersonalMission cppm = new ChatPetPersonalMission();
        cppm.setState(MissionStateEnum.GOING_ON.getCode());
        cppm.setChatPetId(chatPetId);
        cppm.setCreateTime(new Date());

        cppm.setMissionCode(chatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE);
        cppm.setAdId(adId);

        this.save(cppm);
    }


    /**
     * 完成宠物任务
     * @param completeMissionParam  完成任务参数
     */
    public void completeChatPetMission(CompleteMissionParam completeMissionParam){
        Log.d("================chatpetid = {?} missioncode = {?} ===========" ,completeMissionParam.getChatPetId().toString(),completeMissionParam.getMissionCode().toString());
        //查询当前任务记录查询对象
        ChatPetPersonalMission param = new ChatPetPersonalMission();

        param.setChatPetId(completeMissionParam.getChatPetId());
        param.setMissionCode(completeMissionParam.getMissionCode());
        param.setAdId(completeMissionParam.getAdId());
        param.setCreateTime(DateUtils.getBeginDate(new Date()));
        param.setState(MissionStateEnum.GOING_ON.getCode());

        //获取当前任务对象
        ChatPetPersonalMission chatPetPersonalMission = this.getPersonalMissionByParam(param);

        if(chatPetPersonalMission == null){//查找不到任务记录,说明任务已经完成
            return ;
        }

        //校验
        try{
            this.completeMissionCheck(chatPetPersonalMission.getId());
        }catch(BizException e){
            Log.e(e);
        }

        //更新状态
        chatPetPersonalMission.setState(MissionStateEnum.FINISH_NOT_AWARD.getCode());
        chatPetPersonalMission.setInviteeWxFanId(param.getInviteeWxFanId());
        this.update(chatPetPersonalMission);

        //奖励池生成奖励 TODO 插入奖励方法需要修改
        chatPetRewardService.saveRewardItemWhenMissionDone(chatPetPersonalMission.getChatPetId(),chatPetPersonalMission.getId());

        //完成任务后给粉丝发送领奖tips
        this.sendChatPetRewardTips(chatPetPersonalMission.getChatPetId());

    }

    /**
     * 完成宠物任务前校验
     * @param chatPetPersonalMissionId
     */
    private void completeMissionCheck(Integer chatPetPersonalMissionId) throws BizException{
        ChatPetPersonalMission chatPetPersonalMission = this.getById(chatPetPersonalMissionId);

        Integer state = chatPetPersonalMission.getState();//当前任务状态
        Integer chatPetId = chatPetPersonalMission.getChatPetId();
        ChatPet chatPet = chatPetService.getById(chatPetId);
        String wxPubOriginId = chatPet.getWxPubOriginId();
        String wxFanOpenId = chatPet.getWxFanOpenId();

        //公众号是否开通陪聊宠服务
        if(rWxPubProductService.isUnable(ProductService.CHAT_PET, wxPubOriginId)){
            throw new BizException("公众号未开通陪聊宠");
        }
        //粉丝是否领取宠物
        if(chatPetService.getChatPetByFans(wxPubOriginId, wxFanOpenId) == null){
            throw new BizException("未领取陪聊宠");
        }
        //任务是否为正在进行中状态
        if(!MissionStateEnum.GOING_ON.getCode().equals(state)){
            throw new BizException("任务已完成");
        }

        //资讯任务校验
        Integer adId = chatPetPersonalMission.getAdId();
        if(adId != null){
            if(this.getCountByChatPetIdAndAdId(chatPetId,adId).intValue() > 1){
                throw new BizException("该资讯已阅读");
            }
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
            Log.e("tips send failed ! error info :" + result);

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
    private ChatPetPersonalMission getPersonalMissionByParam(ChatPetPersonalMission param){
        return this.chatPetPersonalMissionMapper.selectByParam(param);
    }

    /**
     * 查询任务池记录集
     * @param param 查询条件
     * @return
     */
    private List<ChatPetPersonalMission> getPersonalMissionListByParam(ChatPetPersonalMission param){
        return this.chatPetPersonalMissionMapper.selectListByParam(param);
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


}
