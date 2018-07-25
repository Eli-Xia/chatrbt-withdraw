package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.SessionTokenService;
import net.monkeystudio.chatrbtw.service.bean.sessiontoken.TokenResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author xiaxin
 */
@Controller
@RequestMapping(value = "/token")
public class TokenController extends ChatPetBaseController{

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private SessionTokenService sessionTokenService;

    @ResponseBody
    @RequestMapping(value = "/check-session", method = RequestMethod.POST)
    public RespBase loginTest(HttpServletRequest request){

        TokenResult result = sessionTokenService.checkSession(request);

        return respHelper.ok(result);
    }


}
