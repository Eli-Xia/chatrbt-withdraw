package net.monkeystudio.chatpet.controller.interceptor;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.base.utils.StringUtil;
import net.monkeystudio.chatpet.controller.ChatPetBaseController;
import net.monkeystudio.chatrbtw.service.MiniAppLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

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
    private MiniAppLoginService miniAppLoginService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sessionToken = request.getHeader("token");
        Log.i("========= session token = {?} ==========",sessionToken);

        if(StringUtil.isEmpty(sessionToken)){
            //公众号登录
            HttpSession session = request.getSession();
            Integer userId =  (Integer) session.getAttribute(ChatPetBaseController.SESSION_ATTR_NAME_CHATPET_USERID);
            if(userId == null){
                returnErrorResponse(response,respHelper.nologin());
                return false;
            }
        }else{
            //小程序登录
            String sessionTokenCacheKey = miniAppLoginService.getSessionTokenCacheKey(sessionToken);
            String sessionValue = redisCacheTemplate.getString(sessionTokenCacheKey);
            if(StringUtil.isEmpty(sessionValue)){
                returnErrorResponse(response,respHelper.nologin());
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
