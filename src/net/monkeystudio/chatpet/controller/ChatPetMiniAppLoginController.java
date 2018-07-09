package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.MiniAppUserInfoReq;
import net.monkeystudio.chatrbtw.service.MiniAppLoginService;
import net.monkeystudio.chatrbtw.service.MiniAppUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaxin
 */
@Controller
@RequestMapping(value = "/mini-app")
public class ChatPetMiniAppLoginController {

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private MiniAppLoginService miniAppLoginService;

    @Autowired
    private MiniAppUserInfoService miniAppUserInfoService;

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public RespBase miniAppLogin(@RequestParam("code")String code){
        String token = miniAppLoginService.loginHandle(code);

        Map<String,String> result = new HashMap<>();
        result.put("token",token);

        return respHelper.ok(result);
    }

    @ResponseBody
    @RequestMapping(value = "/update/user-info", method = RequestMethod.POST)
    public RespBase miniAppUserInfo(@RequestBody MiniAppUserInfoReq req){


        return respHelper.ok();
    }




}
