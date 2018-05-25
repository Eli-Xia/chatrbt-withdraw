package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.ChatPetBackgroundService;
import net.monkeystudio.chatrbtw.service.ChatPetColorService;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetColorItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by bint on 2018/5/8.
 */
@RequestMapping(value = "/chat-pet/background")
@Controller
public class ChatPetBackgroundController extends ChatPetBaseController{

    @Autowired
    private ChatPetBackgroundService chatPetBackgroundService;

    @Autowired
    private RespHelper respHelper;



    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getChatPetBackground(){
        Integer wxFanId = this.getUserId();
        if(wxFanId == null){
            return respHelper.nologin();
        }
        chatPetBackgroundService.getChatPetBackgroundInfo(wxFanId);
        return respHelper.ok();
    }

}
