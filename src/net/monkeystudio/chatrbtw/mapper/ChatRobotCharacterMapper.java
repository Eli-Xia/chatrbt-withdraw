package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatRobotCharacter;

import java.util.List;

/**
 * Created by bint on 04/01/2018.
 */
public interface ChatRobotCharacterMapper {

    List<ChatRobotCharacter> selectAll();

}
