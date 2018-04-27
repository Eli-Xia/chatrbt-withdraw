package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPet;
import org.apache.ibatis.annotations.Param;

/**
 * Created by bint on 2018/4/16.
 */
public interface ChatPetMapper {

    Integer insert(ChatPet chatPet);

    ChatPet selectById(Integer id);

    ChatPet selectByParam(ChatPet chatPet);

    Integer increaseExperience(@Param("id") Integer chatPetId ,@Param("augend") Integer augend);
}
