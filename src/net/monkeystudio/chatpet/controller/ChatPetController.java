package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.ChatPetIdReq;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * Created by bint on 2018/4/19.
 */
@Controller
@RequestMapping(value = "/chat-pet/pet")
public class ChatPetController {

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private RespHelper respHelper;


    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public RespBase getAdClickLogList(@RequestBody ChatPetIdReq chatPetIdReq){

        Integer ChatPetId = chatPetIdReq.getId();

        ChatPetInfo chatPetInfo = chatPetService.getInfo(ChatPetId);

        return respHelper.ok(chatPetInfo);
    }
}
