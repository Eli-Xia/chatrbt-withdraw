package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetExpFlow;

public interface ChatPetExpFlowMapper {
    int insert(ChatPetExpFlow record);

    int insertSelective(ChatPetExpFlow record);

    ChatPetExpFlow selectByPrimaryKey(Integer id);

}