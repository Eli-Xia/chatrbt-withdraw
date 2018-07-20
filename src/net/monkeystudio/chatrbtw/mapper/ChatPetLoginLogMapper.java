package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetLoginLog;

public interface ChatPetLoginLogMapper {
    int insert(ChatPetLoginLog record);

    ChatPetLoginLog selectByPrimaryKey(Integer id);

}