package net.monkeystudio.portal.controller;

import net.monkeystudio.chatrbtw.entity.ChatRobotCharacter;
import net.monkeystudio.chatrbtw.service.ChatRobotCharacterService;
import net.monkeystudio.chatrbtw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value = "")
public class PortalPageController extends PortalBaseController{

	@Autowired
	UserService userService;

	@Autowired
	ChatRobotCharacterService chatRobotCharacterService;

	/**
	 * 登录页
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView mvLogin(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/login");

		return mv;
	}

	/**
	 * 后台  index
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView mvIndex(){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/index");
		return mv;
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public ModelAndView mvHome(){
		ModelAndView mv = new ModelAndView();

		Integer userId = this.getUserId();

		if(userId == null){
			mv.setViewName("redirect:/login.html");
			return mv;
		}

		mv.setViewName("/portal/home");

		return mv;
	}
	/**
	 * 注册页
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView mvRegister(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/register");

		return mv;
	}
	/**
	 * 邮箱发送成功
	 * @return
	 */
	@RequestMapping(value = "/email_success", method = RequestMethod.GET)
	public ModelAndView mvEmailSuccess(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/email_success");

		return mv;
	}
	/**
	 * 激活成功
	 * @return
	 */
	@RequestMapping(value = "/active_success", method = RequestMethod.GET)
	public ModelAndView mvActiveSuccess(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/active_success");

		return mv;
	}
	/**
	 * 激活失败
	 * @return
	 */
	@RequestMapping(value = "/active_fail", method = RequestMethod.GET)
	public ModelAndView mvActiveFail(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/active_fail");

		return mv;
	}
	/**
	 * 找回密码 输入邮箱
	 * @return
	 */
	@RequestMapping(value = "/find_password", method = RequestMethod.GET)
	public ModelAndView mvFindPassword(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/find_password");

		return mv;
	}
	/**
	 * 找回密码 邮件发送成功
	 * @return
	 */
	@RequestMapping(value = "/account_email", method = RequestMethod.GET)
	public ModelAndView mvAccountEmail(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/account_email");

		return mv;
	}
	/**
	 * 找回密码 重置密码
	 * @return
	 */
	@RequestMapping(value = "/reset_password", method = RequestMethod.GET)
	public ModelAndView mvResetPassword(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/reset_password");

		return mv;
	}
	/**
	 * 找回密码 重置密码成功
	 * @return
	 */
	@RequestMapping(value = "/reset_success", method = RequestMethod.GET)
	public ModelAndView mvResetSuccess(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/reset_success");

		return mv;
	}
	/**
	 * 修改密码
	 * @return
	 */
	@RequestMapping(value = "/alter_password", method = RequestMethod.GET)
	public ModelAndView mvAlterPassword(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/alter_password");

		return mv;
	}
	/**
	 * 修改密码成功
	 * @return
	 */
	@RequestMapping(value = "/alter_success", method = RequestMethod.GET)
	public ModelAndView mvAlterSuccess(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/alter_success");
		return mv;
	}
	/**
	 * 边栏主页
	 * @return
	 */
	@RequestMapping(value = "/home_page", method = RequestMethod.GET)
	public ModelAndView mvHomePage(){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/home_page");
		return mv;
	}
	/**
	 * 公告页
	 * @return
	 */
	@RequestMapping(value = "/announcement", method = RequestMethod.GET)
	public ModelAndView mvAnnouncement(){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/announcement");
		return mv;
	}
	/**
	 * 公告页
	 * @return
	 */
	@RequestMapping(value = "/instructions", method = RequestMethod.GET)
	public ModelAndView mvInstructions(){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/instructions");
		return mv;
	}
	/**
	 * 接入公众号
	 * @return
	 */
	@RequestMapping(value = "/join_account", method = RequestMethod.GET)
	public ModelAndView mvJoinAccount(){
		ModelAndView mv = new ModelAndView();

		List<ChatRobotCharacter> chatRobotCharacterList = chatRobotCharacterService.getAllCharacter();
		mv.addObject("chatRobotCharacterList", chatRobotCharacterList);

		mv.setViewName("/portal/join_account");
		return mv;
	}
	/**
	 * 管理公众号
	 * @return
	 */
	@RequestMapping(value = "/menage_account", method = RequestMethod.GET)
	public ModelAndView mvMenageAccount(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/menage_account");
		return mv;
	}
	/**
	 * 公众号详情
	 * @return
	 */
	@RequestMapping(value = "/account_detail", method = RequestMethod.GET)
	public ModelAndView mvAccountDetail(){

		ModelAndView mv = new ModelAndView();
		List<ChatRobotCharacter> chatRobotCharacterList = chatRobotCharacterService.getAllCharacter();
		mv.addObject("chatRobotCharacterList", chatRobotCharacterList);
		mv.setViewName("/portal/account_detail");
		return mv;
	}
	/**
	 * 广告大厅
	 * @return
	 */
	@RequestMapping(value = "/ads_hall", method = RequestMethod.GET)
	public ModelAndView mvAdsHall(){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/ads_hall");
		return mv;
	}
	/**
	 * 收益统计
	 * @return
	 */
	@RequestMapping(value = "/revenue_count", method = RequestMethod.GET)
	public ModelAndView mvRevenueCount(){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/revenue_count");
		return mv;
	}
	/**
	 * 结算中心
	 * @return
	 */
	@RequestMapping(value = "/settlement_center", method = RequestMethod.GET)
	public ModelAndView mvSettlementCenter(){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/settlement_center");
		return mv;
	}
	/**
	 * 账号设置
	 * @return
	 */
	@RequestMapping(value = "/account_settings", method = RequestMethod.GET)
	public ModelAndView mvAccountSet(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/account_settings");
		return mv;
	}
	/**
	 * 登录设置
	 * @return
	 */
	@RequestMapping(value = "/login_settings", method = RequestMethod.GET)
	public ModelAndView mvLoginSet(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/login_settings");
		return mv;
	}
	/**
	 * 重置密码
	 * @return
	 */
	@RequestMapping(value = "/user/password/reset/{activeCode}", method = RequestMethod.GET)
	public ModelAndView mvResetPassword(@PathVariable("activeCode") String activeCode){

		ModelAndView mv = new ModelAndView();
		mv.addObject("activeCode", activeCode);

		mv.setViewName("/portal/reset_password");
		return mv;
	}
	/**
	 * 异常页面
	 * @return
	 */
	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public ModelAndView mvError(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/error");
		return mv;
	}
	/**
	 * 404页面
	 * @return
	 */
	@RequestMapping(value = "/404", method = RequestMethod.GET)
	public ModelAndView mv404(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/404");
		return mv;
	}

	/**
	 * 接入成功页面
	 * @return
	 */
	@RequestMapping(value = "/portal_success", method = RequestMethod.GET)
	public ModelAndView mvPortalSuccess(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/portal/portal_success");
		return mv;
	}
	
}
