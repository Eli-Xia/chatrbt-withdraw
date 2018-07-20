package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.ChatPetLoginLog;
import net.monkeystudio.chatrbtw.mapper.ChatPetLoginLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaxin
 */
@Service
public class ChatPetLoginLogService {
    @Autowired
    private ChatPetLoginLogMapper chatPetLoginLogMapper;

    public void save(ChatPetLoginLog chatPetLoginLog){
        chatPetLoginLogMapper.insert(chatPetLoginLog);
    }
}
