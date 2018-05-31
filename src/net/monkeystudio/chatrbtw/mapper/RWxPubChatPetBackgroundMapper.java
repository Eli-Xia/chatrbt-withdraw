package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.RWxPubChatPetBackground;

public interface RWxPubChatPetBackgroundMapper {

    int insert(RWxPubChatPetBackground record);

    RWxPubChatPetBackground selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(RWxPubChatPetBackground record);

    RWxPubChatPetBackground selectByWxPubOriginId(String wxPubOriginId);
}