package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.ChatPetBackground;
import net.monkeystudio.chatrbtw.mapper.ChatPetBackgroundMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetBackgroundInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaxin
 */
@Service
public class ChatPetBackgroundService {

    @Autowired
    private ChatPetBackgroundMapper chatPetBackgroundMapper;

    @Autowired
    private RWxPubChatPetBackgroundService rWxPubChatPetBackgroundService;

    @Autowired
    private ChatPetService chatPetService;

    /**
     * 根据宠物id获取宠物背景信息
     * @param chatPetId
     * @return
     */
    public ChatPetBackgroundInfo getChatPetBackgroundInfo(Integer chatPetId){

        ChatPet chatPet = chatPetService.getById(chatPetId);

        String wxPubOriginId = chatPet.getWxPubOriginId();

        Integer chatPetBackgroundId = rWxPubChatPetBackgroundService.getChatPetBackgroundId(wxPubOriginId);

        ChatPetBackground chatPetBackground = this.getById(chatPetBackgroundId);

        String code = chatPetBackground.getCode();

        String topic = chatPetBackground.getTopic();

        ChatPetBackgroundInfo chatPetBackgroundInfo = new ChatPetBackgroundInfo();

        chatPetBackgroundInfo.setBackgroundInfo(code + "-" + topic);

        return chatPetBackgroundInfo;
    }

    private ChatPetBackground getById(Integer chatPetBackgroundId){
        return this.chatPetBackgroundMapper.selectByPrimaryKey(chatPetBackgroundId);
    }
}
