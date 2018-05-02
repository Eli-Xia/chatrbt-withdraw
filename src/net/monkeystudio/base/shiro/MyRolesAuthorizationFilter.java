package net.monkeystudio.base.shiro;

import net.monkeystudio.base.utils.RespHelper;
import org.apache.http.HttpResponse;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by liujinhua on 2017/6/29.
 */
public class MyRolesAuthorizationFilter extends RolesAuthorizationFilter {

    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        final Subject subject = getSubject(request, response);
        final String[] rolesArray = (String[]) mappedValue;

        if (rolesArray == null || rolesArray.length == 0) {
            //no roles specified, so nothing to check - allow access.
            return true;
        }

        for (String roleName : rolesArray) {
            if (subject.hasRole(roleName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        Subject subject = this.getSubject(request, response);
        if(subject.getPrincipal() == null) {
            this.saveRequestAndRedirectToLogin(request, response);
        } else {
            String loginPath = net.monkeystudio.base.utils.WebUtils.getLogicPath((HttpServletRequest) request);
            if(loginPath.startsWith("/api/")){
                String resp = new RespHelper().sauthFailed();
                HttpServletResponse httpServletResponse = (HttpServletResponse)response;
                response.setContentType("application/json;charset=UTF-8");
                OutputStream out = response.getOutputStream();
                out.write(resp.getBytes("utf-8"));
                out.flush();
            }else{
                String unauthorizedUrl = this.getUnauthorizedUrl();
                if(StringUtils.hasText(unauthorizedUrl)) {
                    WebUtils.issueRedirect(request, response, unauthorizedUrl);
                } else {
                    WebUtils.toHttp(response).sendError(401);
                }
            }

        }

        return false;
    }
}
