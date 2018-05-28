package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.ChatPetPersonalMission;
import net.monkeystudio.chatrbtw.entity.ChatPetRewardItem;
import net.monkeystudio.chatrbtw.mapper.ChatPetRewardItemMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetGoldItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 陪聊宠奖励
 * @author xiaxin
 */
@Service
public class ChatPetRewardService {
    @Autowired
    private ChatPetMissionEnumService chatPetMissionEnumService;
    @Autowired
    private ChatPetRewardItemMapper chatPetRewardItemMapper;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private ChatPetLogService chatPetLogService;

    @Autowired
    private ChatPetLevelService chatPetLevelService;

    public static final Integer NOT_AWARD = 0;
    public static final Integer HAVE_AWARD = 1;



    public ChatPetRewardItem getChatPetRewardItemById(Integer id){
        return chatPetRewardItemMapper.selectByPrimaryKey(id);
    }

    public void save(ChatPetRewardItem item){
        chatPetRewardItemMapper.insert(item);
    }

    public void update(ChatPetRewardItem item){
        chatPetRewardItemMapper.updateByPrimaryKey(item);
    }

    /**
     * 给宠物填充奖励池
     * @param chatPetId
     */
    public void createInitRewardItems(Integer chatPetId) {
        String initRewardItemCountKey = this.getInitRewardItemCountKey(chatPetId);
        Long count = redisCacheTemplate.incr(initRewardItemCountKey);

        if(count.intValue() == 1){
            redisCacheTemplate.expire(initRewardItemCountKey, DateUtils.getCacheSeconds());
        }else{
            return ;
        }

        //每日可领取奖励
        ChatPetRewardItem fixedItem = new ChatPetRewardItem();

        //根据宠物等级获取每日可领取奖励值
        Integer chatPetLevel = chatPetService.getChatPetLevel(chatPetId);
        //每日可领取奖励 = (宠物等级 + 1) * 0.12F
        fixedItem.setGoldValue( (chatPetLevel + 1) * 0.12F );

        fixedItem.setRewardState(NOT_AWARD);
        fixedItem.setChatPetId(chatPetId);
        fixedItem.setCreateTime(new Date());

        this.save(fixedItem);

        //获取当前已完成任务,并创建奖励insert
        List<ChatPetPersonalMission> finishedItems = chatPetMissionPoolService.getFinishedMissionItem(chatPetId);
        if(ListUtil.isNotEmpty(finishedItems)){
            for (ChatPetPersonalMission cppm:finishedItems){

                ChatPetRewardItem item = new ChatPetRewardItem();

                item.setGoldValue(item.getGoldValue());
                item.setExperience(item.getExperience());
                item.setChatPetId(chatPetId);
                item.setMissionItemId(cppm.getId());
                item.setRewardState(NOT_AWARD);
                item.setCreateTime(new Date());

                this.save(item);
            }
        }

    }


    /**
     * 获取宠物奖励展示
     * @param chatPetId
     * @return
     */
    public List<ChatPetGoldItem> getChatPetGoldItems(Integer chatPetId){

        ChatPetRewardItem param =new ChatPetRewardItem();
        param.setRewardState(NOT_AWARD);
        param.setChatPetId(chatPetId);
        param.setCreateTime(DateUtils.getBeginDate(new Date()));

        List<ChatPetRewardItem> items = this.chatPetRewardItemMapper.selectByParam(param);

        if(ListUtil.isEmpty(items)){
            return Collections.EMPTY_LIST;
        }

        List<ChatPetGoldItem> goldItems = new ArrayList<>();

        for(ChatPetRewardItem item : items){

            ChatPetGoldItem goldItem = new ChatPetGoldItem();

            goldItem.setGoldValue(item.getGoldValue());
            goldItem.setMissionItemId(item.getMissionItemId());
            goldItem.setRewardItemId(item.getId());

            goldItems.add(goldItem);

        }

        return goldItems;

    }

    /**
     * 宠物领取奖励
     * @param chatPetRewardItemId   领取奖励对象id
     */
    public void reward(Integer chatPetRewardItemId){

        ChatPetRewardItem chatPetRewardItem = this.getChatPetRewardItemById(chatPetRewardItemId);

        Integer missionItemId = chatPetRewardItem.getMissionItemId();

        //是否为任务类型奖励
        Boolean isMissionReward = missionItemId != null;

        if(isMissionReward){

            this.missionRewardHandle(chatPetRewardItemId);

        }else{

            this.levelRewardHandle(chatPetRewardItemId);

        }
    }




    /**
     * 每日可领取奖励(等级奖励)处理
     * @param chatPetRewardItemId 领取奖励对象id
     */
    private void levelRewardHandle(Integer chatPetRewardItemId){

        ChatPetRewardItem chatPetRewardItem = this.getChatPetRewardItemById(chatPetRewardItemId);
        Integer chatPetId = chatPetRewardItem.getChatPetId();

        //修改奖励对象的领取状态为已领取
        Integer updateCount = this.updateRewardState(chatPetRewardItemId);
        if(updateCount <= 0){
            return;
        }

        //加金币
        chatPetService.increaseCoin(chatPetId,chatPetRewardItem.getGoldValue());

        //宠物日志
        chatPetLogService.saveLevelRewardLog(chatPetRewardItemId);
    }

    /**
     * 任务类型奖励处理
     * @param chatPetRewardItemId   领取奖励对象id
     */
    private void missionRewardHandle(Integer chatPetRewardItemId){
        //修改金币状态
        Integer updateCount = this.updateRewardState(chatPetRewardItemId);

        if(updateCount <= 0){
            return;
        }

        //修改任务记录状态
        ChatPetRewardItem chatPetRewardItem = this.getChatPetRewardItemById(chatPetRewardItemId);
        Integer missionItemId = chatPetRewardItem.getMissionItemId();
        chatPetMissionPoolService.updateMissionWhenReward(missionItemId);

        //加金币
        Integer chatPetId = chatPetRewardItem.getChatPetId();
        chatPetService.increaseCoin(chatPetId,chatPetRewardItem.getGoldValue());

        //加经验
        ChatPet chatPet = chatPetService.getById(chatPetId);
        Float oldExperience = chatPet.getExperience();
        chatPetService.increaseExperience(chatPetId,chatPetRewardItem.getExperience());

        //是否升级
        Float newExperience = chatPet.getExperience();
        boolean isUpgrade = chatPetLevelService.isUpgrade(oldExperience, newExperience);

        //宠物日志
        chatPetLogService.savePetLog4MissionReward(chatPetRewardItemId,isUpgrade);

        //邀请人
        ChatPetPersonalMission chatPetPersonalMission = chatPetMissionPoolService.getById(missionItemId);
        if(chatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(chatPetPersonalMission.getMissionCode())){
            chatPetMissionPoolService.dispatchMission(chatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE,chatPetId);
        }
    }

    /**
     * 完成宠物任务后奖励池中插入数据
     * @param chatPetId
     * @param missionItemId
     */
    public void saveRewardItemWhenMissionDone(Integer chatPetId,Integer missionItemId){
        ChatPetPersonalMission cppm = chatPetMissionPoolService.getById(missionItemId);
        Integer missionCode = cppm.getMissionCode();

        this.saveRewardItemByMission(missionCode,chatPetId,missionItemId);
    }


    /**
     * 根据任务类型插入奖励池
     * @param missionCode
     */
    private void saveRewardItemByMission(Integer missionCode,Integer chatPetId,Integer chatPetPersonalMissionId){
        if(chatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){
            ChatPetRewardItem item = new ChatPetRewardItem();

            item.setChatPetId(chatPetId);
            item.setRewardState(NOT_AWARD);
            item.setMissionItemId(chatPetPersonalMissionId);
            item.setCreateTime(new Date());

            item.setExperience(this.getSearchNewMissionRandomExperience());//1.5 ~ 2.5
            item.setGoldValue(this.getSearchNewMissionRandomCoin());//0.38 ~ 0.63

            this.save(item);
        }
        if(ChatPetMissionEnumService.DAILY_CHAT_MISSION_CODE.equals(missionCode)){
            ChatPetRewardItem item = new ChatPetRewardItem();

            item.setChatPetId(chatPetId);
            item.setRewardState(NOT_AWARD);
            item.setGoldValue(chatPetMissionEnumService.getMissionByCode(missionCode).getCoin());
            item.setExperience(chatPetMissionEnumService.getMissionByCode(missionCode).getExperience());
            item.setMissionItemId(chatPetPersonalMissionId);
            item.setCreateTime(new Date());

            this.save(item);
        }

        if(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){
            ChatPetRewardItem item = new ChatPetRewardItem();

            item.setChatPetId(chatPetId);
            item.setRewardState(NOT_AWARD);
            item.setGoldValue(chatPetMissionEnumService.getMissionByCode(missionCode).getCoin());
            item.setExperience(chatPetMissionEnumService.getMissionByCode(missionCode).getExperience());
            item.setMissionItemId(chatPetPersonalMissionId);
            item.setCreateTime(new Date());

            this.save(item);
        }

    }

    //获取资讯任务随机奖励经验值  1.5 ~ 2.5
    private Float getSearchNewMissionRandomExperience(){
        Random random = new Random();
        Float f = ( random.nextInt(10) + 15 ) / 10F;
        return f;
    }

    //获取资讯任务随机奖励金币值  0.38 ~ 0.63
    private Float getSearchNewMissionRandomCoin(){
        // ( 0 ~ 25 + 38 ) / 100
        Random random = new Random();
        int i = random.nextInt(25) + 38;
        Float f = i / 100F;
        return f;
    }


    /**
     * 领取奖励之后修改奖励状态,防止多个领奖请求同时update同一奖励,加入状态判断
     * @param chatPetRewardItemId
     * @return   返回被修改的个数,若updateCount <= 0 则修改失败
     */
    public Integer updateRewardState(Integer chatPetRewardItemId){

        ChatPetRewardItem chatPetRewardItem = this.getChatPetRewardItemById(chatPetRewardItemId);

        chatPetRewardItem.setRewardState(HAVE_AWARD);

        return this.chatPetRewardItemMapper.updateRewarded(chatPetRewardItem);
    }


    /**
     * 判断奖励是否被领取
     * @param rewardItemId
     * @return
     */
    public boolean isGoldAwarded(Integer rewardItemId){
        boolean ret = false;
        ChatPetRewardItem item = this.getChatPetRewardItemById(rewardItemId);
        Integer rewardState = item.getRewardState();
        if(HAVE_AWARD.equals(rewardState)){
            ret = true;
        }
        return ret;
    }

    /**
     *  获取当前宠物等级奖励的奖励值
     * @param chatPetId
     * @return
     */
    public Float getChatPetLevelCoinReward(Integer chatPetId){
        ChatPetRewardItem chatPetRewardItem = this.chatPetRewardItemMapper.selectLevelRewardItem(chatPetId);
        return chatPetRewardItem.getGoldValue();
    }

    //初始化奖励池count cache
    private String getInitRewardItemCountKey(Integer chatPetId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "initRewardItem:" + chatPetId;
    }

}
