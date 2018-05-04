package net.monkeystudio.base.controller.bean.req;

import net.monkeystudio.base.Constants;
import net.monkeystudio.base.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;



/**
 * 查询列表基类
 * @author hebo
 *
 */
public class ListPaginationReq {

	protected Integer page;
	protected Integer pageSize;
	
	/**
	 * 检查并填充默认分页查数
	 */
	public void checkAndFillupDefaultParameters(){
		
		if ( page == null ){
			page = Constants.PAGINATION_DEFAULT_PAGE;
		}
		
		if ( pageSize == null ){
			pageSize = Constants.PAGINATION_DEFAULT_PAGE_SIZE;
		}
	}
	
	/**
	 * 计算获取查询列表起始下标
	 * @return
	 */
	public Integer getStartIndex(){
		if ( page != null && pageSize != null ){
			return CommonUtils.page2startIndex(page, pageSize);
		}
		return null;
	}
	
	/**
	 * 所有参数转为map
	 * @return
	 */
	public Map<String,Object> getMap(){
		
		this.checkAndFillupDefaultParameters();
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("page",page);
		map.put("pageSize", pageSize);
		map.put("startIndex", getStartIndex());
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
