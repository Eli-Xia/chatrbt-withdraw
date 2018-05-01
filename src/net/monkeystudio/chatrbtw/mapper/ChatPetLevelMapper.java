package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetLevel;

import java.util.List;

//表名e_chat_pet_level
public interface ChatPetLevelMapper {

    List<ChatPetLevel> selectAscAll();

}