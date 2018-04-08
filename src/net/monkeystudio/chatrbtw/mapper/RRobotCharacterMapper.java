package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.RRobotCharacter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by bint on 03/01/2018.
 */
public interface RRobotCharacterMapper {
    Integer insert(RRobotCharacter rRobotCharacter);
    Integer deleteByChatRobotId(@Param("chatRobotId") Integer chatRobotId);

    List<RRobotCharacter> selectByChatRobotId(@Param("chatRobotId") Integer chatRobotId);
}
