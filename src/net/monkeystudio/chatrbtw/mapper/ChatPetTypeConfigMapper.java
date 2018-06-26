package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetTypeConfig;
import org.apache.ibatis.annotations.Param;

/**
 * Created by bint on 2018/5/17.
 */
public interface ChatPetTypeConfigMapper {
    ChatPetTypeConfig selectByChatPetTypeId(Integer chatPetType);

    Integer increaseCoin(@Param("id") Integer id ,@Param("coin") Float coin);
}
