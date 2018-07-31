package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetExpFlow;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ChatPetExpFlowMapper {
    int insert(ChatPetExpFlow record);

    int insertSelective(ChatPetExpFlow record);

    ChatPetExpFlow selectByPrimaryKey(Integer id);

    List<ChatPetExpFlow> selectExpFlow(@Param("chatPetId")Integer chatPetId, @Param("startIndex")Integer startIndex, @Param("pageSize")Integer pageSize);

    int update(Float amount,Integer id);

    List<ChatPetExpFlow> selectByActionType(Integer actionType);

    Float countPeriodTotalAmount(@Param("startTime")Date startTime, @Param("endTime")Date endTime);
}