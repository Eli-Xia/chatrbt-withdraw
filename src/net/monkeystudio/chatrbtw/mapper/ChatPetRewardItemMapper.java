package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetRewardItem;
import org.apache.ibatis.annotations.Param;

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
}