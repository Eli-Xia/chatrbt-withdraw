package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetCoinFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatPetCoinFlowMapper {
    int insert(ChatPetCoinFlow record);

    int insertSelective(ChatPetCoinFlow record);

    ChatPetCoinFlow selectByPrimaryKey(Integer id);

    List<ChatPetCoinFlow> selectCoinFlow(@Param("chatPetId")Integer chatPetId,@Param("startIndex")Integer startIndex,@Param("pageSize")Integer pageSize);

    int update(Float amount,Integer id);

    List<ChatPetCoinFlow> selectAll();
}