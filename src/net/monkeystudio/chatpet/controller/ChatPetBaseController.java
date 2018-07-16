package net.monkeystudio.chatpet.controller;


import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.chatrbtw.service.SessionTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author xiaxin
 */
public class ChatPetBaseController extends BaseController {

    @Autowired
    private SessionTokenService sessionTokenService;

    @Autowired
    private HttpSession httpSession;

    public static final String SESSION_ATTR_NAME_CHATPET_USERID = "SESSION_ATTR_NAME_CHATPET_USERID";


    @Override
    protected String getSessionUserIdAttrName() {
       return SESSION_ATTR_NAME_CHATPET_USERID;
    }


    @Override
    protected Integer getUserId() {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String token = request.getHeader("token");

        if(token != null){
            Integer wxFanId = sessionTokenService.getWxFanIdByToken(token);
            return wxFanId;
        }

        Integer userId = (Integer)httpSession.getAttribute(SESSION_ATTR_NAME_CHATPET_USERID);

        return userId;
    }


}
