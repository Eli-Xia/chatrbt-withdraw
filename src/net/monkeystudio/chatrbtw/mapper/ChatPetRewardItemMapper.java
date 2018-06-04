package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetRewardItem;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ChatPetRewardItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ChatPetRewardItem record);

    int insertSelective(ChatPetRewardItem record);

    ChatPetRewardItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ChatPetRewardItem record);

    int updateByPrimaryKey(ChatPetRewardItem record);

    int batchInsert(List<ChatPetRewardItem> items);

    List<ChatPetRewardItem> selectByParam(ChatPetRewardItem param);

    ChatPetRewardItem selectLevelRewardItem(@Param("chatPetId")Integer chatPetId);

    int updateRewarded(ChatPetRewardItem record);

    Float countDayGoldByChatPetType(@Param("beginTime")Date beginTime,@Param("endTime")Date endTime,@Param("chatPetType")Integer chatPetType);

    Float countTotalGoldByChatPetType(Integer chatPetType);

    List<ChatPetRewardItem> selectByDateAndChatPet(@Param("createTime") Date createTime , @Param("chatPetId")Integer chatPetId ,@Param("rewardState") Integer rewardState );

    Integer updateMissionRewardState(@Param("originState") Integer originState ,@Param("newState") Integer newState );
}