package net.monkeystudio.portal.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.ChatRobotService;
import net.monkeystudio.chatrbtw.service.bean.chatrobot.UpdateChatRobot;
import net.monkeystudio.chatrbtw.service.bean.chatrobot.resp.ChatRobotInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bint on 05/01/2018.
 */
@Controller
@RequestMapping(value = "/chat-robot")
public class PortalChatRobotController extends PortalBaseController{

    @Autowired
    private ChatRobotService chatRobotService;

    @Autowired
    private RespHelper respHelper;


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public RespBase updateChatRobot(@RequestBody UpdateChatRobot updateChatRobot){

        Integer userId = this.getUserId();

        if(userId == null){
            return respHelper.nologin();
        }

        Integer chatRobotId = updateChatRobot.getId();

        //检查权限
        Boolean hasChatRobot = chatRobotService.hasChatRobot(chatRobotId, userId);

        if(!hasChatRobot){
            return respHelper.authFailed();
        }

        chatRobotService.updateChatRobot(updateChatRobot);

        return respHelper.ok();
    }


    @RequestMapping(value = "/{wxPubOriginId}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getChatRobotInfoRespByWxPubOriginId(@PathVariable("wxPubOriginId") String wxPubOriginId){

        ChatRobotInfoResp chatRobotInfoResp = chatRobotService.getChatRobotInfoRespByWxPubOriginId(wxPubOriginId);

        return respHelper.ok(chatRobotInfoResp);
    }

}
