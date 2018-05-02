package net.monkeystudio.admin.controller;

import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.chatrbtw.entity.ChatRobotCharacter;
import net.monkeystudio.chatrbtw.entity.UserProfile;
import net.monkeystudio.chatrbtw.service.ChatRobotCharacterService;
import net.monkeystudio.chatrbtw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value = "/admin")
public class PageController extends BaseController {

	@Autowired
	UserService userService;

	@Autowired
	ChatRobotCharacterService chatRobotCharacterService;

	/**
	 * 首页
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView mvIndex(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/views/index");

		Integer userId = getUserId();
		if( userId == null ){
			mv.setViewName("redirect:/admin/login.html");
			return mv;
		}

		try {
			UserProfile user = userService.getUserProfile(userId);
			mv.addObject("user",user);
		} catch (BizException e) {
			mv.setViewName("redirect:/admin/login.html");
		}

		return mv;
	}

	/**
	 * 登录页
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView mvLogin(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/views/login");

		return mv;
	}

	/**
	 * 公众号-接入
	 * @return
	 */
	@RequestMapping(value = "/wx_pub_join", method = RequestMethod.GET)
	public ModelAndView mvWxPubJoin(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/views/wx_pub_join");

		List<ChatRobotCharacter> chatRobotCharacterList = chatRobotCharacterService.getAllCharacter();

		mv.addObject("chatRobotCharacterList", chatRobotCharacterList);

		return mv;
	}

	/**
	 * 公众号-管理
	 * @return
	 */
	@RequestMapping(value = "/wx_pub_mgr", method = RequestMethod.GET)
	public ModelAndView mvWxPubMgr(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/views/wx_pub_mgr");

		return mv;
	}

	/**
	 * 公众号-推送
	 * @return
	 */
	@RequestMapping(value = "/push_message_config", method = RequestMethod.GET)
	public ModelAndView pushMessageConfig(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/views/push_message_config");

		return mv;
	}

	/**
	 * 公众号-标签
	 * @return
	 */
	@RequestMapping(value = "/wx_pub_tag_mgr", method = RequestMethod.GET)
	public ModelAndView mvWxPubTagMgr(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/views/wx_pub_tag_mgr");

		return mv;
	}

	/**
	 * 用户管理页
	 * @return
	 */
	@RequestMapping(value = "/user_mgr", method = RequestMethod.GET)
	public ModelAndView mvUserMgrPage(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/views/user_mgr");

		return mv;
	}

	/**
	 * 聊天日志（聊天记录）页
	 * @return
	 */
	@RequestMapping(value = "/chatlog", method = RequestMethod.GET)
	public ModelAndView mvChatLog(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/views/chatlog");

		return mv;
	}
	/**
	 * 广告 管理
	 * @return
	 */
	@RequestMapping(value = "/ad_mgr", method = RequestMethod.GET)
	public ModelAndView mvAdMgr(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/views/ad_mgr");

		return mv;
	}
	/**
	 * 数据  统计
	 * @return
	 */
	@RequestMapping(value = "/data_count", method = RequestMethod.GET)
	public ModelAndView mvDataCount(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/views/data_count");

		return mv;
	}
	/**
	 * 公共 关键字 管理
	 * @return
	 */
	@RequestMapping(value = "/base_keyword_mgr", method = RequestMethod.GET)
	public ModelAndView mvBaseKeyword(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/views/base_keyword_mgr");

		return mv;
	}

	/**
	 * 资源权限-管理
	 * @return
	 */
	@RequestMapping(value = "/permission_mgr", method = RequestMethod.GET)
	public ModelAndView mvPermissionMgr(){

		ModelAndView mv = new ModelAndView();
		mv.setViewName("/views/permission_mgr");

		return mv;
	}




}