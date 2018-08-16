package net.monkeystudio.admin.controller;

import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.MiniGameTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@RequestMapping(value = "/admin/chat-pet/minigame-tag")
@Controller
public class ChatPetMinigameTagMgrController extends BaseController {
    @Autowired
    private RespHelper respHelper;

    @Autowired
    private MiniGameTagService miniGameTagService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase list(HttpServletRequest request){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }



        return respHelper.ok();
    }
}
