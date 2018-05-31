package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.RWxPubChatPetBackground;
import net.monkeystudio.chatrbtw.mapper.RWxPubChatPetBackgroundMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaxin
 */
@Service
public class RWxPubChatPetBackgroundService {
    @Autowired
    private RWxPubChatPetBackgroundMapper rWxPubChatPetBackgroundMapper;

    private RWxPubChatPetBackground getRWxPubChatPetBackground(String wxPubOriginId){

        RWxPubChatPetBackground rWxPubChatPetType = rWxPubChatPetBackgroundMapper.selectByWxPubOriginId(wxPubOriginId);

        return rWxPubChatPetType;
    }

    public Integer getChatPetBackgroundId(String wxPubOriginId){
        RWxPubChatPetBackground rWxPubChatPetType = this.getRWxPubChatPetBackground(wxPubOriginId);
        return rWxPubChatPetType.getChatPetBackgroundId();
    }
}
