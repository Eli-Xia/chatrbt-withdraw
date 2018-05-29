package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.enums.mission.MissionStateEnum;
import net.monkeystudio.chatrbtw.mapper.ChatPetPersonalMissionMapper;
import net.monkeystudio.chatrbtw.sdk.wx.WxCustomerHelper;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.CompleteMissionParam;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.DispatchMissionParam;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.TodayMission;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.TodayMissionItem;
import net.monkeystudio.chatrbtw.utils.ChatPetMissionNoUtil;
import net.monkeystudio.wx.service.WxPubService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaxin
 */
@DependsOn
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

    private final static String CHAT_PET_NEWS_MISSION_REWARD_TIPS =  "恭喜你采矿成功,点击<a href=\"%s\">领取奖励</a>";

    @PostConstruct
    private void initSubscribe(){
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

                        completeChatPetMission(param);
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


        if(chatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){
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

        chatPetPersonalMission.setChatPetId(chatPetId);
        chatPetPersonalMission.setCreateTime(new Date());
        chatPetPersonalMission.setState(MissionStateEnum.GOING_ON.getCode());
        chatPetPersonalMission.setMissionCode(missionCode);

        this.save(chatPetPersonalMission);
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
     * 推送当日阅读任务广告时,维护任务与广告关联关系
     * @param adId 广告id
     * @param wxfanId 微信粉丝id
     */
    public void updateMissionWhenPushChatPetAd(Integer adId,Integer wxfanId){
        //获取fanopenid
        WxFan wxfan = wxFanService.getById(wxfanId);
        String wxFanOpenId = wxfan.getWxFanOpenId();
        String wxPubOriginId = wxfan.getWxPubOriginId();

        Integer chatPetId = chatPetService.getChatPetIdByFans(wxPubOriginId,wxFanOpenId);

        ChatPetPersonalMission cppm = this.getDailyPersonalMission(chatPetId, chatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE);

        //update adId
        cppm.setAdId(adId);

        this.update(cppm);

    }

    /**
     * 完成任务但是未领取奖励时更新任务池记录
     */
    private void updateMissionWhenFinish(Integer chatPetId,Integer missionCode){

        ChatPetPersonalMission cppm = this.getDailyPersonalMission(chatPetId, missionCode);

        cppm.setState(MissionStateEnum.FINISH_NOT_AWARD.getCode());

        this.update(cppm);

        /*if(MissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){
            this.dispatchMission(MissionEnumService.INVITE_FRIENDS_MISSION_CODE ,chatPetId);
        }*/
    }

    /**
     * 完成任务后更新任务记录
     * 供邀请人类型任务使用
     * @param chatPetPersonalMissionId
     */
    public void updateMissionWhenInvited(Integer chatPetPersonalMissionId,Integer inviteeWxFanId){
        ChatPetPersonalMission chatPetPersonalMission = this.getById(chatPetPersonalMissionId);

        chatPetPersonalMission.setState(MissionStateEnum.FINISH_NOT_AWARD.getCode());
        chatPetPersonalMission.setInviteeWxFanId(inviteeWxFanId);

        this.update(chatPetPersonalMission);
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
        this.completeMissionCheck(chatPetPersonalMission.getId());

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
    private void completeMissionCheck(Integer chatPetPersonalMissionId){
        ChatPetPersonalMission chatPetPersonalMission = this.getById(chatPetPersonalMissionId);

        Integer state = chatPetPersonalMission.getState();//当前任务状态
        Integer chatPetId = chatPetPersonalMission.getChatPetId();
        ChatPet chatPet = chatPetService.getById(chatPetId);
        String wxPubOriginId = chatPet.getWxPubOriginId();
        String wxFanOpenId = chatPet.getWxFanOpenId();

        //公众号是否开通陪聊宠服务
        Assert.isTrue(rWxPubProductService.isEnable(ProductService.CHAT_PET, wxPubOriginId),"公众号未开通陪聊宠");
        //粉丝是否领取宠物
        Assert.notNull(chatPetService.getChatPetByFans(wxPubOriginId, wxFanOpenId),"未领取陪聊宠");
        //任务是否为正在进行中状态
        Assert.isTrue(MissionStateEnum.GOING_ON.getCode().equals(state),"任务已完成");

        //资讯任务校验
        Integer adId = chatPetPersonalMission.getAdId();
        if(adId != null){
            Assert.isTrue(this.getCountByChatPetIdAndAdId(chatPetId,adId).intValue() == 0,"该资讯已阅读");
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
     * 完成每日聊天签到任务
     */
    public void completeDailyChatCheckinMission(String wxPubOriginId,String wxFanOpenId,Integer missionCode){
        //公众号未开通陪聊宠
        if(rWxPubProductService.isUnable(ProductService.CHAT_PET, wxPubOriginId)){
            return;
        }
        //粉丝未领取宠物
        ChatPet chatPet = chatPetService.getChatPetByFans(wxPubOriginId, wxFanOpenId);
        if(chatPet == null){
            return;
        }

        //任务是否已经完成
        boolean isMissionDone = this.isDailyMissionDone(chatPet.getId(),missionCode);

        //未完成时:
        if(!isMissionDone){
            this.updateMissionWhenFinish(chatPet.getId(),missionCode);
            ChatPetPersonalMission cppm = this.getDailyPersonalMission(chatPet.getId(), missionCode);
            chatPetRewardService.saveRewardItemWhenMissionDone(chatPet.getId(),cppm.getId());
        }


    }

    /**
     * 完成每日资讯任务
     * @param wxfanId
     * @param adId
     */
    public void completeDailyReadMission(Integer wxfanId,Integer adId){
        WxFan wxfan = wxFanService.getById(wxfanId);
        String wxPubOriginId = wxfan.getWxPubOriginId();
        String wxFanOpenId = wxfan.getWxFanOpenId();

        this.completeDailyReadMission(wxPubOriginId,wxFanOpenId,chatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE,adId);

    }



    /**
     * 完成每日资讯任务
     */
    private void completeDailyReadMission(String wxPubOriginId,String wxFanOpenId,Integer missionCode,Integer adId){
        //公众号未开通陪聊宠
        if(rWxPubProductService.isUnable(ProductService.CHAT_PET, wxPubOriginId)){
            return;
        }
        //粉丝未领取宠物
        ChatPet chatPet = chatPetService.getChatPetByFans(wxPubOriginId, wxFanOpenId);
        if(chatPet == null){
            return;
        }

        //此次点击阅读任务广告与今日任务广告应为同一条广告      missionCode  adId  chatpetid
        ChatPetPersonalMission param = new ChatPetPersonalMission();
        param.setState(MissionStateEnum.GOING_ON.getCode());
        param.setMissionCode(ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE);
        param.setCreateTime(DateUtils.getBeginDate(new Date()));
        param.setChatPetId(chatPet.getId());
        List<ChatPetPersonalMission>  goingonSearNewsMissionList = this.getPersonalMissionListByParam(param);//所有正在进行中的资讯任务

        ChatPetPersonalMission chatPetPersonalMission = null;

        boolean flag = false;
        //当前点击广告是否为资讯任务
        for(ChatPetPersonalMission cppm:goingonSearNewsMissionList){
            if(adId.equals(cppm.getAdId())){
                chatPetPersonalMission = cppm;//获取当前
                flag = true;
            }
        }

        //当前点击的这条广告在用户今日资讯阅读任务池中无法找到则return
        if(!flag){
            return ;
        }

        chatPetPersonalMission.setState(MissionStateEnum.FINISH_NOT_AWARD.getCode());
        this.update(chatPetPersonalMission);

        chatPetRewardService.saveRewardItemWhenMissionDone(chatPet.getId(),chatPetPersonalMission.getId());


    }



    /**
     * 今日任务是否完成
     * @return
     */
    public boolean isDailyMissionDone(Integer chatPetId,Integer missionCode){
        ChatPetPersonalMission cppm = this.getDailyPersonalMission(chatPetId, missionCode);

        Integer state = cppm.getState();

        //任务完成判断:  已完成未领奖 或  完成且领奖状态
        if(MissionStateEnum.FINISH_NOT_AWARD.getCode() == state || MissionStateEnum.FINISH_AND_AWARD.getCode() == state){
            return true;
        }

        return false;
    }

    /**
     * 获取粉丝今日任务记录
     * @param missionCode 任务编号
     * @return
     */
    public ChatPetPersonalMission getDailyPersonalMission(Integer chatPetId,Integer missionCode){
        Date now = new Date();
        Date startTime = DateUtils.getBeginDate(now);

        ChatPetPersonalMission param = new ChatPetPersonalMission();
        param.setChatPetId(chatPetId);
        param.setCreateTime(startTime);
        param.setMissionCode(missionCode);

        ChatPetPersonalMission cppm = this.getPersonalMissionByParam(param);

        return cppm;
    }

    /**
     * 获取正在进行中的邀请人任务
     * @param chatPetId
     * @param missionCode
     * @return
     */
    public ChatPetPersonalMission getShouldDoInviteMission(Integer chatPetId,Integer missionCode){
        Date now = new Date();
        Date startTime = DateUtils.getBeginDate(now);

        ChatPetPersonalMission param = new ChatPetPersonalMission();
        param.setChatPetId(chatPetId);
        param.setCreateTime(startTime);
        param.setMissionCode(missionCode);
        param.setState(MissionStateEnum.GOING_ON.getCode());

        ChatPetPersonalMission cppm = this.getPersonalMissionByParam(param);

        return cppm;
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
        ChatPetPersonalMission param = new ChatPetPersonalMission();
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
        todayMission.setRandomMissionList(randomMissionList);

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

    /**
     * 获取当前为已完成状态的missionItem
     * @param chatPetId
     * @return
     */
    public List<ChatPetPersonalMission> getFinishedMissionItem(Integer chatPetId){
        ChatPetPersonalMission param = new ChatPetPersonalMission();

        param.setCreateTime(new Date());
        param.setChatPetId(chatPetId);
        param.setState(MissionStateEnum.FINISH_NOT_AWARD.getCode());

        return this.getPersonalMissionListByParam(param);
    }

    public boolean isFinishMission(Integer missionItemId){
        boolean ret = false;
        ChatPetPersonalMission cppm = this.getById(missionItemId);
        Integer state = cppm.getState();
        if(MissionStateEnum.FINISH_NOT_AWARD.getCode().equals(state)){
            ret = true;
        }
        return ret;
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
