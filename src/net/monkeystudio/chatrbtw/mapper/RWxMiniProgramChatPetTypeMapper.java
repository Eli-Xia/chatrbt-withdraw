package net.monkeystudio.chatrbtw.mapper;


import net.monkeystudio.chatrbtw.entity.RMiniProgramChatPetType;

/**
 * Created by bint on 2018/7/11.
 */
public interface RWxMiniProgramChatPetTypeMapper {

    RMiniProgramChatPetType selectByWxMinProgramId(Integer wxMiniProgram);
}
