package net.monkeystudio.chatrbtw.mapper;


import net.monkeystudio.chatrbtw.entity.AdminMenu;

import java.util.List;

/**
 * 管理后台导航菜单表接口
 * @author hebo2
 *
 */
public interface AdminMenuMapper 
{
	
	public List<AdminMenu> selectAll();
    
}
