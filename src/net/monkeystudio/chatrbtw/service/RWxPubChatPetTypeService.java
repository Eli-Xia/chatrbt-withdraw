package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.RWxPubChatPetType;
import net.monkeystudio.chatrbtw.mapper.RWxPubChatPetTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2018/5/17.
 */
@Service
public class RWxPubChatPetTypeService {

    @Autowired
    RWxPubChatPetTypeMapper rWxPubChatPetTypeMapper;


    private RWxPubChatPetType getRWxPubChatPetType(String wxPubOriginId){

        RWxPubChatPetType rWxPubChatPetType = rWxPubChatPetTypeMapper.selectByWxPubOriginId(wxPubOriginId);

        return rWxPubChatPetType;
    }

    public Integer getChatPetType(String wxPubOriginId){
        RWxPubChatPetType rWxPubChatPetType = this.getRWxPubChatPetType(wxPubOriginId);
        return rWxPubChatPetType.getChatPetType();
    }

}
