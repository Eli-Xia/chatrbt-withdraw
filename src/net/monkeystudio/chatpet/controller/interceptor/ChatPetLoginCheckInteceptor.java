package net.monkeystudio.chatpet.controller.interceptor;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.base.utils.StringUtil;
import net.monkeystudio.chatpet.controller.ChatPetBaseController;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.MiniProgramLoginService;
import net.monkeystudio.chatrbtw.service.WxFanService;
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
    private MiniProgramLoginService miniProgramLoginService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private ChatPetService chatPetService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Boolean isLogin = this.isLogin(request);
        if(!isLogin){
            returnErrorResponse(response,respHelper.nologin());
        }

        Integer wxFanId = this.getWxFanIdByRequest(request);
        ChatPet chatPet = chatPetService.getByWxFanId(wxFanId);
        if(chatPet == null){
            returnErrorResponse(response,respHelper.failed("user does not have a lucky cat !"));
        }

        return isLogin;

    }

    private Integer getWxFanIdByRequest(HttpServletRequest request){
        Integer userId = null;
        String sessionToken = request.getHeader("token");

        if(StringUtil.isEmpty(sessionToken)){
            HttpSession session = request.getSession();
            userId =  (Integer) session.getAttribute(ChatPetBaseController.SESSION_ATTR_NAME_CHATPET_USERID);
        }else{
            String sessionTokenCacheKey = miniProgramLoginService.getSessionTokenCacheKey(sessionToken);
            String sessionValue = redisCacheTemplate.getString(sessionTokenCacheKey);
            String userOpenId = sessionValue.split("\\+")[0];
            WxFan wxFan = wxFanService.getWxFan(userOpenId, wxFanService.LUCK_CAT_MINI_APP_ID);
            userId = wxFan.getId();
        }
        return userId;
    }

    private Boolean isLogin(HttpServletRequest request){
        String sessionToken = request.getHeader("token");
        Log.i("========= session token = {?} ==========",sessionToken);

        if(StringUtil.isEmpty(sessionToken)){
            //公众号登录
            HttpSession session = request.getSession();
            Integer userId =  (Integer) session.getAttribute(ChatPetBaseController.SESSION_ATTR_NAME_CHATPET_USERID);
            if(userId == null){
                return false;
            }
        }else{
            //小程序登录
            String sessionTokenCacheKey = miniProgramLoginService.getSessionTokenCacheKey(sessionToken);
            String sessionValue = redisCacheTemplate.getString(sessionTokenCacheKey);
            if(StringUtil.isEmpty(sessionValue)){
                return false;
            }
            /*String userOpenId = sessionValue.split("\\+")[0];
            //根据openId查询wxFanId
            WxFan wxFan = wxFanService.getWxFan(userOpenId, wxFanService.LUCK_CAT_MINI_APP_ID);
            if(wxFan != null){
                ChatPet chatPet = chatPetService.getByWxFanId(wxFan.getId());
                if(chatPet.)
            }*/
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
