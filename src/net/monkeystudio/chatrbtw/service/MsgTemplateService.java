package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.MsgTemplate;
import net.monkeystudio.chatrbtw.mapper.MsgTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2018/8/6.
 */
@Service
public class MsgTemplateService {

    @Autowired
    private MsgTemplateMapper msgTemplateMapper;


    public MsgTemplate getByMiniProgramIdAndCode(Integer miniProgramId , Integer code  ){
        return msgTemplateMapper.selectByMiniProgramIdAndCode(miniProgramId, code);
    }


    public static class Constants{
        public static final Integer DIVIDEND_MSG_CODE = 1;
    }
}
