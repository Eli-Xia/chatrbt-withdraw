package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
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
public class ChatPetBackgroundController {

    @Autowired
    private ChatPetColorService chatPetColorService;

    @Autowired
    private RespHelper respHelper;



    @RequestMapping(value = "/all", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getChatPetAllColor(){

        List<ChatPetColorItem> list = chatPetColorService.getAllColor();

        return respHelper.ok(list);
    }

}
