package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.ChatPetGameCenterService;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.ChatPetGameCenterResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author xiaxin
 */
@Controller
@RequestMapping(value = "/chat-pet/game-center")
public class ChatPetGameCenterController extends ChatPetBaseController{

    @Autowired
    private ChatPetGameCenterService chatPetGameCenterService;
    @Autowired
    private RespHelper respHelper;

    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public RespBase getGameCenterList(){
        Integer fanId = getUserId();

        ChatPetGameCenterResp gameCenterResp = chatPetGameCenterService.getGameCenterResp(fanId);

        return respHelper.ok(gameCenterResp);
    }
}
