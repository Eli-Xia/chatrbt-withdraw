package net.monkeystudio.chatrbtw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.monkeystudio.entity.AdminMenu;
import net.monkeystudio.mapper.AdminMenuMapper;

@Service
public class NaviService {

	@Autowired
	private AdminMenuMapper adminMenuMapper;
	
	/**
	 * 获取用户有权限操作的菜单列表
	 * （目前没有多管理员需求，直接返回所有菜单）
	 * @param userId
	 * @return
	 */
	public List<AdminMenu> getAdminMenu(List<String> roles){
		
		if(roles == null){
			return null;
		}
		List<AdminMenu> adminMenuList = adminMenuMapper.selectAll();
		
		return adminMenuList;
	}

}
