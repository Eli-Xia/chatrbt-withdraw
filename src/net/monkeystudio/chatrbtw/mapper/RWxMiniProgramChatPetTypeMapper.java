package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.RWxMiniProgramChatPetType;

import java.util.List;

/**
 * Created by bint on 2018/7/11.
 */
public interface RWxMiniProgramChatPetTypeMapper {

    RWxMiniProgramChatPetType selectByWxMinProgramId(Integer wxMiniProgram);
}
