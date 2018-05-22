package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.ChatPetMission;
import net.monkeystudio.chatrbtw.entity.ChatPetPersonalMission;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.enums.mission.MissionStateEnum;
import net.monkeystudio.chatrbtw.mapper.ChatPetPersonalMissionMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.TodayMission;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.TodayMissionItem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaxin
 */
@Service
public class ChatPetMissionPoolService {
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
    private ChatPetRewardItemService chatPetRewardItemService;



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
     * TODO 支持阅读任务和互动任务
     * 派发任务,目前仅仅支持邀请好友类型任务
     * @param missionCode
     * @param chatPetId
     */
    public void dispatchMission(Integer missionCode ,Integer chatPetId){

        ChatPetPersonalMission cppm = new ChatPetPersonalMission();
        if(chatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){

            cppm.setChatPetId(chatPetId);
            cppm.setCreateTime(new Date());
            cppm.setState(MissionStateEnum.GOING_ON.getCode());
            cppm.setMissionCode(missionCode);

            this.save(cppm);
        }


    }


    /**
     * 任务池新增记录
     * @param chatPetPersonalMission
     */
    public void save(ChatPetPersonalMission chatPetPersonalMission){
        ChatPetPersonalMission cppm = new ChatPetPersonalMission();

        BeanUtils.copyProperties(chatPetPersonalMission,cppm);

        chatPetPersonalMissionMapper.insert(cppm);
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
    public void updateMissionWhenFinish(Integer chatPetId,Integer missionCode){

        ChatPetPersonalMission cppm = this.getDailyPersonalMission(chatPetId, missionCode);

        cppm.setState(MissionStateEnum.FINISH_NOT_AWARD.getCode());

        this.update(cppm);

        /*if(MissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){
            this.dispatchMission(MissionEnumService.INVITE_FRIENDS_MISSION_CODE ,chatPetId);
        }*/
    }



    /**
     * 完成任务但是未领取奖励时更新任务池记录
     * @param missionId
     */
    public void updateMissionWhenFinishByMissionId(Integer missionId){

        ChatPetPersonalMission cppm = this.getById(missionId);

        cppm.setState(MissionStateEnum.FINISH_NOT_AWARD.getCode());

        this.update(cppm);
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
        }

        ChatPetPersonalMission cppm = this.getDailyPersonalMission(chatPet.getId(), missionCode);

        chatPetRewardItemService.saveRewardItemWhenMissionDone(chatPet.getId(),cppm.getId());
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

        //此次点击阅读任务广告与今日任务广告应为同一条广告
        boolean isEquals = checkMissionAdIsEqual(chatPet.getId(), missionCode, adId);
        if(!isEquals){
            return;
        }

        //判断今日阅读任务是否完成
        boolean isDone = this.isDailyMissionDone(chatPet.getId(), missionCode);

        if(isDone){
            return;
        }

        ChatPetPersonalMission cppm = this.getDailyPersonalMission(chatPet.getId(), missionCode);

        chatPetRewardItemService.saveRewardItemWhenMissionDone(chatPet.getId(),cppm.getId());

        this.updateMissionWhenFinish(chatPet.getId(),missionCode);

    }


    private boolean  checkMissionAdIsEqual(Integer chatPetId,Integer missionCode,Integer adId){
        ChatPetPersonalMission cppm = this.getDailyPersonalMission(chatPetId, missionCode);

        Integer missionAdId = cppm.getAdId();

        return adId.equals(missionAdId);
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

                Long time = cppm.getCreateTime().getTime();
                String no = String.valueOf(time/1000 % 10000 );
                missionName = "NO." + no + " " + missionName;
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



    public void update(ChatPetPersonalMission id){
        chatPetPersonalMissionMapper.updateByPrimaryKey(id);
    }


    public String getCreateDailyMissionCountCacheKey(Integer chatPetId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "chatPetDailyMission:" +chatPetId;
    }


}
