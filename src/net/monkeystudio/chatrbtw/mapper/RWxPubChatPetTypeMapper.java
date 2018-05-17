package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.RWxPubChatPetType;

/**
 * Created by bint on 2018/5/17.
 */
public interface RWxPubChatPetTypeMapper {

    RWxPubChatPetType selectByWxPubOriginId(String wxPubOriginId);


}
