package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.msgtemplateform.AddMsgTemplateFormReq;
import net.monkeystudio.chatrbtw.entity.MsgTemplateForm;
import net.monkeystudio.chatrbtw.service.MsgTemplateFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by bint on 2018/8/1.
 */
@RequestMapping(value = "/msg-template-form")
@Controller
public class MsgTemplateFormController extends ChatPetBaseController{


    @Autowired
    private MsgTemplateFormService msgTemplateFormService;


    @Autowired
    private RespHelper respHelper;

    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public RespBase addMsgTemplateForm(AddMsgTemplateFormReq addMsgTemplateFormReq){

        Integer wxFanId = this.getUserId();

        msgTemplateFormService.save(addMsgTemplateFormReq.getFormId() , wxFanId);

        return respHelper.ok();
    }



}
