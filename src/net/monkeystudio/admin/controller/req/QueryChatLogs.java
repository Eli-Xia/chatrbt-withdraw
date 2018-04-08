package net.monkeystudio.admin.controller.req;

import java.util.HashMap;
import java.util.Map;

/**
 * 查询日志
 * @author hebo
 *
 */
public class QueryChatLogs {

	private Integer page;
	private Integer pageSize;
	
	public Map<String,Object> getMap(){
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("page", page);
		params.put("pageSize", pageSize);
		
		return params;
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
