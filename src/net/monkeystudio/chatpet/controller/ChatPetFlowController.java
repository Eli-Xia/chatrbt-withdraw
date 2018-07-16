package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xiaxin
 */
@Controller
@RequestMapping(value = "/chat-pet/flow")
public class ChatPetFlowController extends ChatPetBaseController{
    @Autowired
    private RespHelper respHelper;


    /*@ResponseBody
    @RequestMapping(value = "/experience", method = RequestMethod.POST)
    public RespBase experienceFlow(HttpServletRequest request, HttpServletResponse response){
        Integer fanId = getUserId();


        return respHelper.ok();
    }

    @ResponseBody
    @RequestMapping(value = "/coin", method = RequestMethod.POST)
    public RespBase coinFlow(HttpServletRequest request, HttpServletResponse response){
        Integer fanId = getUserId();


        return respHelper.ok();
    }*/
}
