package net.monkeystudio.base.controller;

import net.monkeystudio.base.Constants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

/**
 * controller基类
 * @author hebo
 *
 */
public class BaseController {

	/**
	 * 取用户ID，用户未登录返回null
	 * @return
	 */
	protected Integer getUserId(){
		
		Subject subject = SecurityUtils.getSubject();
		if ( subject == null ){
			return null;
		}
		
		Session session = subject.getSession();
		if ( session == null) {
			return null;
		}

		Integer userId =  (Integer) session.getAttribute(this.getSessionUserIdAttrName());
		
		return userId;
	}
	
	/**
	 * 保存userId到session
	 * @param userId
	 */
	protected void saveSessionUserId(Integer userId){
		
		Subject subject = SecurityUtils.getSubject();
		subject.getSession().setAttribute(this.getSessionUserIdAttrName(),userId);
	}
	
	/**
	 * Session中保存userId的属性名，不同登录子系统的子类重写使用不同的属性名。
	 * @return
	 */
	protected String getSessionUserIdAttrName(){
		return Constants.SESSION_ATTR_NAME_USERID;
	}
}
