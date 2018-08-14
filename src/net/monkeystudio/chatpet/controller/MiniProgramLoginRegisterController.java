package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.MiniAppUserInfoReq;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.service.MiniProgramLoginService;
import net.monkeystudio.chatrbtw.service.MiniProgramUserInfoService;
import net.monkeystudio.chatrbtw.service.SessionTokenService;
import net.monkeystudio.chatrbtw.service.WxFanService;
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

    @Autowired
    private SessionTokenService sessionTokenService;

    @Autowired
    private WxFanService wxFanService;


    @ResponseBody
    @RequestMapping(value = "/test/login", method = RequestMethod.POST)
    public RespBase loginTest(@RequestParam("id")String wxFanId) throws BizException{
        if(!wxFanId.startsWith("keendo")){
            return respHelper.failed("fail");
        }
        int i = wxFanId.lastIndexOf(".");
        String wxFanIdStr = wxFanId.substring(i + 1);
        int id = Integer.parseInt(wxFanIdStr);
        //保存session
        WxFan wxFan = wxFanService.getById(id);
        if(wxFan == null){
            respHelper.failed("id不存在");
        }
        String openid = wxFan.getWxFanOpenId();
        sessionTokenService.saveToken("123",1,openid,"key");
        return respHelper.ok();

    }

    @ResponseBody
    @RequestMapping(value = "/login-or-register", method = RequestMethod.POST)
    public RespBase miniAppLogin(@RequestParam(value = "parentFanId",required = false)String parentFanId,@RequestParam(value = "programId",required = false)String miniProgramId,@RequestParam("code")String code) throws BizException{
        String token = miniProgramLoginService.loginHandle(parentFanId,miniProgramId,code);

        Map<String,String> result = new HashMap<>();
        result.put("token",token);

        return respHelper.ok(result);
    }

    @ResponseBody
    @RequestMapping(value = "/update/fan-info", method = RequestMethod.POST)
    public RespBase miniAppUserInfo(@RequestBody MiniAppUserInfoReq req) throws Exception{

        miniProgramUserInfoService.reviseUserInfo(req.getMiniProgramId(),req.getEncryptedData(),req.getIv(),req.getCode());
        return respHelper.ok();
    }

    /*//注册接口代替上面
    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public RespBase register(@RequestBody MiniAppUserInfoReq req) throws Exception{

        miniProgramUserInfoService.getUserInfoAndRegister(req.getParentFanId(),req.getEncryptedData(),req.getIv());

        return respHelper.ok();
    }*/


}