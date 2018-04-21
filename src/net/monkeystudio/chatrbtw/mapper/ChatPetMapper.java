package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPet;

/**
 * Created by bint on 2018/4/16.
 */
public interface ChatPetMapper {

    Integer insert(ChatPet chatPet);

    ChatPet selectById(Integer id);

    ChatPet selectByParam(ChatPet chatPet);
}
