package net.monkeystudio.admin.controller.base;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求列表基类
 * @author hebo
 *
 */
public class ListReqBase {

	private Integer page;
	private Integer pageSize;
	
	public Map<String,Object> getMap(){
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("page",page);
		map.put("pageSize", pageSize);
		
		return map;
	}
	
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
