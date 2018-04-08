package net.monkeystudio.portal.controller;

import javax.servlet.http.HttpServletRequest;

import com.google.code.kaptcha.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;

import net.monkeystudio.base.RespBase;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.entity.UserExt;
import net.monkeystudio.chatrbtw.local.Msg;
import net.monkeystudio.chatrbtw.service.OpLogService;
import net.monkeystudio.chatrbtw.service.UserExtService;
import net.monkeystudio.portal.controller.req.Login;
import net.monkeystudio.entity.User;
import net.monkeystudio.entity.UserProfile;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.portal.controller.req.Register;
import net.monkeystudio.portal.controller.req.ResendActiveEmail;
import net.monkeystudio.portal.controller.req.ResetPassword;
import net.monkeystudio.portal.controller.req.RetrievePassword;
import net.monkeystudio.portal.controller.req.UpdatePassword;
import net.monkeystudio.portal.controller.req.UpdateUser;
import net.monkeystudio.portal.controller.resp.user.UserDetail;
import net.monkeystudio.service.UserService;
import net.monkeystudio.utils.Log;
import net.monkeystudio.utils.RespHelper;

/**
 * portal用户接口
 * @author hebo
 *
 */
@Controller
@RequestMapping(value = "/user")
public class PortalUserController extends PortalBaseController{

	@Autowired
	private RespHelper respHelper;

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserExtService userExtService;
	
	@Autowired
	private OpLogService opLogService;
	
	/**
	 * 用户注册
	 * @param request
	 * @param register
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase register(HttpServletRequest request, @RequestBody Register register){

		String capText = (String)request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);

		if(capText == null){
			return respHelper.failed("验证码出错.");
		}

		if(!capText.equals(register.getCapText())){
			return respHelper.failed("验证码校验不一致.");
		}

		if ( StringUtils.isBlank(register.getNickname())){
			return respHelper.failed("昵称不能为空.");
		}
		
		if ( StringUtils.isBlank(register.getEmail())){
			return respHelper.failed("Email不能为空。");
		}
		
		if ( StringUtils.isBlank(register.getPassword())){
			return respHelper.failed("密码不能为空。");
		}
		
		try {
			User user = userService.addUserByEmail(register.getEmail(), register.getPassword(), register.getNickname());
			
			userService.sendActiveEmail(user.getId(), true);
		} catch (BizException e) {
			return respHelper.failed(e.getBizExceptionMsg());
		}
		
		return respHelper.ok();
	}
	
		
	/**
	 * Email激活
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/active/{activeCode}", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView emailActive(HttpServletRequest request, @PathVariable("activeCode") String activeCode){
		
		ModelAndView mv = new ModelAndView();
		boolean success = userService.emailActive(activeCode);
		if ( success ){
			mv.setViewName("redirect:/active_success.html");
		}else{
			mv.setViewName("redirect:/active_fail.html");
		}
		
		return mv;
	}
	

	/**
	 * 重发激活Email
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/active/email/resend", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase activeEmailResend(HttpServletRequest request, @RequestBody ResendActiveEmail resendActiveEmail){
		
		if ( StringUtils.isBlank(resendActiveEmail.getEmail())){
			return respHelper.cliParamError("email");
		}
		
		try {
			userService.resendActiveEmail(resendActiveEmail.getEmail(),true);
		} catch (BizException e) {
			return respHelper.failed(e.getBizExceptionMsg());
		}
		
		return respHelper.ok();
	}
		
	
	/**
	 * 用户登录
	 * @param request
	 * @param Login
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase login(HttpServletRequest request, @RequestBody Login login){
		
		if ( StringUtils.isBlank(login.getEmail())){
			return respHelper.failed("Email不能为空.");
		}
		
		if ( StringUtils.isBlank(login.getPassword())){
			return respHelper.failed("密码不能为空。");
		}
		
		//用户登录鉴权
		UsernamePasswordToken token = new UsernamePasswordToken(login.getEmail(), login.getPassword());
		token.setRememberMe(false);
		Subject subject = SecurityUtils.getSubject();
		
		try {
			subject.logout(); //登录前先退出登录，目的是清除realm缓存
			subject.login(token);
			if (subject.isAuthenticated()) {
				String username = (String) subject.getPrincipal();
				User user = userService.getUserByName(username);
				if(user == null){
					Log.e("user not exist");
					return respHelper.failed(Msg.text("api.login.failed"));
				}
				
				/**
				 * 这里potal与admin存入session的属性名不同,通过不同属性名进行区分前后台用户。
				 */
				this.saveSessionUserId(user.getId());
				
				//登录成功返回用户基本信t息
                UserProfile up = userService.getUserProfile(user.getId());

                opLogService.userOper(up.getId(), AppConstants.OP_LOG_TAG_P_LOGIN, "用户[" + up.getNickname() + "]登录系统。");
                return respHelper.ok(up);
			} else {
				return respHelper.authFailed();
			}
		} catch (IncorrectCredentialsException e){
			return respHelper.failed(Msg.text("api.login.failed"));
		}catch (UnknownAccountException e){
			return respHelper.failed(Msg.text("api.login.failed"));
		}catch (Exception e) {
			Log.e(e.getMessage());
			return respHelper.failed(Msg.text("api.login.failed"));
		}
	}

	/**
	 * 用户退出登录
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public RespBase logout(HttpServletRequest request){

		//todo:注销SESSION，退出登录
		Subject subject = SecurityUtils.getSubject();
		try {
			if(subject!=null){
				subject.logout();
			}
		} catch (Exception e) {
			Log.e(e.getMessage());
		}
		return respHelper.ok();
	}

	/**
	 * 修改密码
	 * @param request
	 * @param updatePassword
	 * @return
	 */
	@RequestMapping(value = "/password/update", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase updatePassword(HttpServletRequest request, @RequestBody UpdatePassword updatePassword){
		
		Integer userId = getUserId();
		if ( userId == null ){
			return respHelper.nologin();
		}
		if ( StringUtils.isBlank(updatePassword.getCurPassword())){
			return respHelper.failed("当前密码不能为空.");
		}
		
		if ( StringUtils.isBlank(updatePassword.getNewPassword())){
			return respHelper.failed("新密码不能为空。");
		}
		
		
		try {
			User user = userService.getUser(userId);
			if ( user == null || !userService.auth(user.getUsername(), updatePassword.getCurPassword()) ){
				return respHelper.failed("密码错误。");
			}
	
			userService.updatePassword(userId, updatePassword.getNewPassword());
			return respHelper.ok();
			
		} catch (BizException e) {
			return respHelper.failed(e.getBizExceptionMsg());
		}
		
	}
	
	
	/**
	 * 找回密码
	 * @param request
	 * @param retrievePassword
	 * @return
	 */
	@RequestMapping(value = "/password/retrieve", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase retrievePassword(HttpServletRequest request, @RequestBody RetrievePassword retrievePassword){
		
		if ( StringUtils.isBlank(retrievePassword.getEmail())){
			return respHelper.failed("Email不能为空.");
		}

		try {
			userService.sendPasswordRetrieveEmail(retrievePassword.getEmail());
		} catch (BizException e) {
			return respHelper.failed(e.getBizExceptionMsg());
		}
		
		return respHelper.ok();
	}


	
	
	
	/**
	 * 得置密码
	 * @param request
	 * @param resetPassword
	 * @return
	 */
	@RequestMapping(value = "/password/reset", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase resetPassword(HttpServletRequest request, @RequestBody ResetPassword resetPassword){
		
		
		if ( StringUtils.isBlank(resetPassword.getActiveCode())){
			return respHelper.failed("activeCode参数不能为空.");
		}
		
		if ( StringUtils.isBlank(resetPassword.getNewPassword())){
			return respHelper.failed("新密码不能为空。");
		}
		
		
		try {
			User user = userService.getUserByActiveCode(resetPassword.getActiveCode());
			if ( user == null){
				return respHelper.failed("重置密码链接已失效。");
			}
	
			userService.updatePassword(user.getId(),resetPassword.getNewPassword());
			return respHelper.ok();
			
		} catch (BizException e) {
			return respHelper.failed(e.getBizExceptionMsg());
		}
		
	}

	
	/**
	 * 修改用户个人信息
	 * @param request
	 * @param updatePassword
	 * @return
	 */
	@RequestMapping(value = "/info/update", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase update(HttpServletRequest request, @RequestBody UpdateUser updateUser){
		
		Integer userId = this.getUserId();
		if ( userId == null ){
			return respHelper.nologin();
		}
		
		try {
			if ( updateUser.getNickname() != null ){
				userService.update(userId, updateUser.getMap());
			}
			
			if ( updateUser.getContactEmail() != null || updateUser.getContactPhone() != null){
				userExtService.update(userId, updateUser.getMap());
			}
		} catch (BizException e) {
			return respHelper.failed(e.getBizExceptionMsg());
		}
		
		return respHelper.ok();
	}
	
	/**
	 * 获取用户个人信息
	 * @param request
	 * @param updatePassword
	 * @return
	 */
	@RequestMapping(value = "/info/query", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase info(HttpServletRequest request){
		
		Integer userId = this.getUserId();
		if ( userId == null ){
			return respHelper.nologin();
		}
		
		try {
			User user = userService.getUser(userId);
		
			UserExt userExt = userExtService.getUserExt(userId);
			
			UserDetail ud = new UserDetail();
			ud.setUserId(userId);
			ud.setEmail(user.getEmail());
			ud.setNickname(user.getNickname());
			ud.setContactEmail(userExt.getContactEmail());
			ud.setContactPhone(userExt.getContactPhone());
			
			return respHelper.ok(ud);
		
		} catch (BizException e) {
			return respHelper.failed(e.getBizExceptionMsg());
		}
	}

}
