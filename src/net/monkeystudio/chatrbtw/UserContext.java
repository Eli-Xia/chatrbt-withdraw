package net.monkeystudio.chatrbtw;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

/**
 * 猫六六粉丝上下文
 * 只适用于小程序
 */
public class UserContext {

    public static final String SESSION_ATTR_NAME_CHATPET_USERID = "SESSION_ATTR_NAME_CHATPET_USERID";


    public static HttpSession getSession(){
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
                .getRequest().getSession();
    }

    /**
     * 获取当前用户粉丝id
     * @return
     */
    public static Integer getFanId(){
        return (Integer)getSession().getAttribute(SESSION_ATTR_NAME_CHATPET_USERID);
    }
}
