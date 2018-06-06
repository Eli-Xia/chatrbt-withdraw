package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetPersonalMission;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ChatPetPersonalMissionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ChatPetPersonalMission record);

    ChatPetPersonalMission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ChatPetPersonalMission record);

    int updateByPrimaryKey(ChatPetPersonalMission record);

    List<ChatPetPersonalMission> selectListByParam(ChatPetPersonalMission param);

    ChatPetPersonalMission selectByParam(ChatPetPersonalMission param);

    Integer countByChatPetIdAndAdId(@Param("chatPetId")Integer chatPetId,@Param("adId")Integer adId);

    Integer countCompletedMissionAmountByMissionCode(@Param("chatPetId")Integer chatPetId,@Param("missionCode")Integer missionCode,@Param("state")Integer state,@Param("createTime")Date createTime);
}