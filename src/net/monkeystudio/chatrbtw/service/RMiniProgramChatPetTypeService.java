package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.RMiniProgramChatPetType;
import net.monkeystudio.chatrbtw.mapper.RMiniProgramChatPetTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2018/7/12.
 */
@Service
public class RMiniProgramChatPetTypeService {

    @Autowired
    private RMiniProgramChatPetTypeMapper rMiniProgramChatPetTypeMapper;



    private RMiniProgramChatPetType getEntityByMiniProgramId(Integer miniProgramId){
        return rMiniProgramChatPetTypeMapper.selectByWxMinProgramId(miniProgramId);
    }

    /**
     * 通过小程序获取对应宠物类型
     * @param miniProgramId
     * @return
     */
    public Integer getByMiniProgramId(Integer miniProgramId){
        RMiniProgramChatPetType rWxMiniProgramChatPetType = this.getEntityByMiniProgramId(miniProgramId);

        return rWxMiniProgramChatPetType.getChatPetType();
    }
}
