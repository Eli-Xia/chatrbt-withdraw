package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.MiniAppUserInfoReq;
import net.monkeystudio.chatrbtw.service.MiniProgramLoginService;
import net.monkeystudio.chatrbtw.service.MiniProgramUserInfoService;
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
public class MiniProgramLoginRegisterController extends ChatPetBaseController{

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private MiniProgramLoginService miniProgramLoginService;

    @Autowired
    private MiniProgramUserInfoService miniProgramUserInfoService;

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public RespBase miniAppLogin(@RequestParam("code")String code){
        String token = miniProgramLoginService.loginHandle(code);

        Map<String,String> result = new HashMap<>();
        result.put("token",token);

        return respHelper.ok(result);
    }

    @ResponseBody
    @RequestMapping(value = "/update/fan-info", method = RequestMethod.POST)
    public RespBase miniAppUserInfo(@RequestBody MiniAppUserInfoReq req) throws Exception{

        Map<String,Object> ret = miniProgramUserInfoService.getUserInfoAndRegister(req.getRawData(),req.getEncryptedData(),req.getIv(),req.getSignature());

        return respHelper.ok(ret);
    }

    //注册接口代替上面
    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public RespBase register(@RequestBody MiniAppUserInfoReq req) throws Exception{

        Map<String,Object> ret = miniProgramUserInfoService.getUserInfoAndRegister(req.getRawData(),req.getEncryptedData(),req.getIv(),req.getSignature());

        return respHelper.ok(ret);
    }


}
