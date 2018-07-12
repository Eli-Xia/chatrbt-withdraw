package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.RWxMiniProgramChatPetType;
import net.monkeystudio.chatrbtw.mapper.RWxMiniProgramChatPetTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2018/7/12.
 */
@Service
public class RMiniProgramChatPetTypeService {

    @Autowired
    private RWxMiniProgramChatPetTypeMapper rWxMiniProgramChatPetTypeMapper;


    /**
     * 通过小程序获取对应宠物类型
     * @param miniProgramId
     * @return
     */
    public RWxMiniProgramChatPetType getByMiniProgramId(Integer miniProgramId){
        return rWxMiniProgramChatPetTypeMapper.selectByWxMinProgramId(miniProgramId);
    }

}
