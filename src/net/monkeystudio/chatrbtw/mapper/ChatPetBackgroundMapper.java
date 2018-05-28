package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetBackground;

public interface ChatPetBackgroundMapper {

    int insert(ChatPetBackground record);

    ChatPetBackground selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(ChatPetBackground record);
}