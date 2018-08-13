package net.monkeystudio.chatpet.controller.interceptor;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.base.utils.StringUtil;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.SessionTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.OutputStream;

/**
 * @author xiaxin
 */
public class ChatPetLoginCheckInteceptor implements HandlerInterceptor {

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private SessionTokenService sessionTokenService;

    public static final String SESSION_ATTR_NAME_CHATPET_USERID = "SESSION_ATTR_NAME_CHATPET_USERID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Boolean isLogin = this.isLogin(request);
        if(!isLogin){
            returnErrorResponse(response,respHelper.nologin());
            return false;
        }

        Integer wxFanId = this.getWxFanIdByRequest(request);
        if(wxFanId == null){
            returnErrorResponse(response,respHelper.failed("not registered"));
            return false;
        }

        ChatPet chatPet = chatPetService.getByWxFanId(wxFanId);
        if(chatPet == null){
            returnErrorResponse(response,respHelper.failed("user does not have a lucky cat !"));
            return false;
        }

        return true;
    }

    private Integer getWxFanIdByRequest(HttpServletRequest request){
        Integer userId = null;

        String sessionToken = request.getHeader("token");

        if(StringUtil.isEmpty(sessionToken)){
            HttpSession session = request.getSession();
            userId =  (Integer) session.getAttribute(SESSION_ATTR_NAME_CHATPET_USERID);
        }else{
            userId = sessionTokenService.getWxFanIdByToken(sessionToken);
        }

        return userId;
    }

    private Boolean isLogin(HttpServletRequest request){
        String sessionToken = request.getHeader("token");
        Log.d(" ==>chat pet interceptor : session token = {?} ",sessionToken);

        if(StringUtil.isEmpty(sessionToken)){
            //公众号登录
            HttpSession session = request.getSession();
            Integer userId =  (Integer) session.getAttribute(SESSION_ATTR_NAME_CHATPET_USERID);
            if(userId == null){
                return false;
            }
        }else{
            //小程序登录
            String tokenValue = sessionTokenService.getTokenValue(sessionToken);
            if(StringUtil.isEmpty(tokenValue)){
                return false;
            }
        }
        return true;
    }


    //token校验失败,把nologin的Respbase写出去.
    public void returnErrorResponse(HttpServletResponse response, RespBase result)
            throws Exception {
        OutputStream out=null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtil.toJSon(result).getBytes("utf-8"));
            out.flush();
        } finally{
            if(out!=null){
                out.close();
            }
        }
    }



    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
