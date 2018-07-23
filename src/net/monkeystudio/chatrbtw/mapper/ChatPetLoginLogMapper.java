package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetLoginLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface ChatPetLoginLogMapper {
    int insert(ChatPetLoginLog record);

    ChatPetLoginLog selectByPrimaryKey(Integer id);

    Integer countLoginNum(@Param("beginTime")Date beginTime,@Param("endTime")Date endTime);

}