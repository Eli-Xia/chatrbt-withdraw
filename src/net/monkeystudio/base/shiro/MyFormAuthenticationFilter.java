package net.monkeystudio.base.shiro;

import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.base.utils.WebUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by liujinhua on 2017/6/29.
 */
public class MyFormAuthenticationFilter extends FormAuthenticationFilter {

    @Override
    protected void saveRequestAndRedirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        String logicPath = WebUtils.getLogicPath((HttpServletRequest) request);
        if(logicPath.startsWith("/api/")){
            HttpServletResponse httpServletResponse = (HttpServletResponse)response;
            response.setContentType("application/json;charset=UTF-8");
            String resp = new RespHelper().snologin();
            OutputStream out = response.getOutputStream();
            out.write(resp.getBytes("utf-8"));
            out.flush();
        }else if(logicPath.indexOf("admin/") > 0 ){
			this.setLoginUrl("/admin/login.html");
			super.saveRequestAndRedirectToLogin(request, response);
		}else{
			this.setLoginUrl("/login.html");
			super.saveRequestAndRedirectToLogin(request, response);
		}
    }
}
