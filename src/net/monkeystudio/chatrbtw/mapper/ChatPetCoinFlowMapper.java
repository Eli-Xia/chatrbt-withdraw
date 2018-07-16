package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatPetCoinFlow;

public interface ChatPetCoinFlowMapper {
    int insert(ChatPetCoinFlow record);

    int insertSelective(ChatPetCoinFlow record);

    ChatPetCoinFlow selectByPrimaryKey(Integer id);

}