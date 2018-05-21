package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.chatrbtw.entity.ChatPetMission;
import net.monkeystudio.chatrbtw.entity.ChatPetPersonalMission;
import net.monkeystudio.chatrbtw.entity.ChatPetRewardItem;
import net.monkeystudio.chatrbtw.enums.chatpet.ChatPetTaskEnum;
import net.monkeystudio.chatrbtw.mapper.ChatPetRewardItemMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetGoldItem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 陪聊宠奖励
 * @author xiaxin
 */
@Service
public class ChatPetRewardItemService {

    @Autowired
    private ChatPetRewardItemMapper chatPetRewardItemMapper;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private ChatPetMissionService chatPetMissionService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    private static final Integer NOT_AWARD = 0;
    private static final Integer HAVE_AWARD = 1;



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

        //创建宠物奖励池
        //List<ChatPetRewardItem> items = new ArrayList<>();

        //每日可领取奖励
        ChatPetRewardItem fixedItem = new ChatPetRewardItem();

        //根据宠物等级获取每日可领取奖励值
        Integer chatPetLevel = chatPetService.getChatPetLevel(chatPetId);
        //每日可领取奖励 = (宠物等级 + 1) * 1
        fixedItem.setGoldValue( (chatPetLevel + 1) * 0.5F );

        fixedItem.setRewardState(NOT_AWARD);
        fixedItem.setChatPetId(chatPetId);

        this.save(fixedItem);

        //获取当前已完成任务,并创建奖励insert
        List<ChatPetPersonalMission> finishedItems = chatPetMissionPoolService.getFinishedMissionItem(chatPetId);
        if(ListUtil.isNotEmpty(finishedItems)){
            for (ChatPetPersonalMission cppm:finishedItems){

                ChatPetRewardItem item = new ChatPetRewardItem();

                item.setGoldValue(ChatPetTaskEnum.codeOf(cppm.getMissionCode()).getCoinValue());
                item.setChatPetId(chatPetId);
                item.setMissionItemId(cppm.getId());
                item.setRewardState(NOT_AWARD);

                this.save(item);
                //items.add(item);
            }
        }

        //this.chatPetRewardItemMapper.batchInsert(items);

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
     * 完成宠物任务后奖励池中插入数据
     * @param chatPetId
     * @param missionItemId
     */
    public void saveRewardItemWhenMissionDone(Integer chatPetId,Integer missionItemId){
        ChatPetPersonalMission cppm = chatPetMissionPoolService.getById(missionItemId);
        Integer missionCode = cppm.getMissionCode();
        ChatPetMission cpm = chatPetMissionService.getByMissionCode(missionCode);

        ChatPetRewardItem item = new ChatPetRewardItem();

        item.setChatPetId(chatPetId);
        item.setRewardState(NOT_AWARD);
        item.setGoldValue(cpm.getCoin());
        item.setMissionItemId(missionItemId);

        this.save(item);
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

    //初始化奖励池count cache
    private String getInitRewardItemCountKey(Integer chatPetId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "initRewardItem:" + chatPetId;
    }

}
