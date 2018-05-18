package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetTypeConfig;

/**
 * Created by bint on 2018/5/17.
 */
public interface ChatPetTypeConfigMapper {
    ChatPetTypeConfig selectByChatPetTypeId(Integer chatPetType);
}
