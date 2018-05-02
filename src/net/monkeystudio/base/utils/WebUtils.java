package net.monkeystudio.base.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * Created by liujinhua on 2016/12/2.
 */
public class WebUtils {
    /**
     * 获取请求参数
     * @param req
     * @param paramName
     * @return
     */
    public static String getParam(HttpServletRequest req, String paramName){
        return req.getParameter(paramName);
    }


    /**
     * 获取http客户端的ip
     * @param request
     * @return
     */
    public static String getRemoteIp(HttpServletRequest request){
        String b = request.getRemoteHost();
        return request.getRemoteHost();
    }

    /**
     * 获取请求逻辑路基，如对于 /onecard_appservice/user/login?u=1&p=2(其中/onecard_appservice为上下文路径)，返回/user/login
     * @param request
     * @return
     */
    public static String getLogicPath(HttpServletRequest request){
        String contextPath = request.getContextPath();
        String path =  request.getRequestURI();
        if(!StringUtils.isBlank(contextPath) && !"/".equals(contextPath)){
            if(path.startsWith(contextPath)){
                path = path.substring(contextPath.length());
            }
        }

        if(path.indexOf('?') != -1){
            path = path.substring(0,path.indexOf('?'));
        }

        return path;
    }
}
