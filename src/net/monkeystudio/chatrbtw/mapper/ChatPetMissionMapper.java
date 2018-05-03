package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetMission;

import java.util.List;

public interface ChatPetMissionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ChatPetMission record);

    int insertSelective(ChatPetMission record);

    ChatPetMission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ChatPetMission record);

    int updateByPrimaryKey(ChatPetMission record);

    ChatPetMission selectByMissionCode(Integer code);

    List<ChatPetMission> selectActiveMissionList();
}