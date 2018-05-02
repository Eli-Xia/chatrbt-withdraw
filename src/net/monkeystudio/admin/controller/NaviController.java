package net.monkeystudio.admin.controller;

import java.util.ArrayList;
import java.util.List;

import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.service.UserService;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.AdminMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.monkeystudio.admin.controller.resp.NaviItem;
import net.monkeystudio.chatrbtw.service.NaviService;

@Controller
public class NaviController extends BaseController {

	@Autowired
	private NaviService naviService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RespHelper respHelper;
	
	@ResponseBody
	@RequestMapping(value = "/admin/navi", method = RequestMethod.GET)
    public RespBase navi() throws Exception {
		
		Integer userId = getUserId();
		if(userId == null){
			return respHelper.nologin();
		}
		
		List<NaviItem> navi = new ArrayList<NaviItem>();
		
		List<String> roles = userService.getUserRoles(userId);
		List<AdminMenu> adminMenus = naviService.getAdminMenu(roles);
		
		if ( adminMenus != null ){
			
			for ( AdminMenu adminMenu : adminMenus ){
				
				if( adminMenu.getLevel() == 1){
					
					NaviItem naviItem = new NaviItem();
					naviItem.setId( adminMenu.getId());
					naviItem.setText( adminMenu.getText());
					naviItem.setLink( adminMenu.getLink());
					
					navi.add(naviItem);
				}
			}
			
			for ( NaviItem naviItem : navi ){
				
				Integer curItemId = naviItem.getId();
				List<NaviItem> curItemChildren = new ArrayList<NaviItem>();
				
				for ( AdminMenu adminMenu : adminMenus ){
					
					if( adminMenu.getParentId() == curItemId ){
						
						NaviItem childItem = new NaviItem();
						childItem.setId( adminMenu.getId());
						childItem.setText( adminMenu.getText());
						childItem.setLink( adminMenu.getLink());
						
						curItemChildren.add(childItem);
					}
				}
				
				naviItem.setChildren(curItemChildren);
			}
		}
		
		return respHelper.ok(navi);
		
	}
}
