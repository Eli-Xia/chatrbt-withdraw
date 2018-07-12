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
public class MiniProgramLoginController extends ChatPetBaseController{

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
    public RespBase miniAppUserInfo(@RequestBody MiniAppUserInfoReq req){

        try{
            miniProgramUserInfoService.reviseMiniProgramFan(req.getRawData(),req.getEncryptedData(),req.getIv(),req.getSignature());
        }catch(Exception e){
            Log.e(e);
        }

        return respHelper.ok();
    }
    @ResponseBody
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public RespBase miniAppUserInfoss(){
        System.out.println(1);
        return respHelper.ok();
    }






}
