package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.ChatRobotCharacter;
import net.monkeystudio.chatrbtw.mapper.ChatRobotBaseInfoMapper;
import net.monkeystudio.chatrbtw.mapper.ChatRobotCharacterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by bint on 04/01/2018.
 */
@Service
public class ChatRobotCharacterService {

    @Autowired
    private ChatRobotCharacterMapper chatRobotCharacterMapper;

    /**
     * 获取所有的性格
     * @return
     */
    public List<ChatRobotCharacter> getAllCharacter(){
        List<ChatRobotCharacter> list = chatRobotCharacterMapper.selectAll();

        return list;
    }

}
