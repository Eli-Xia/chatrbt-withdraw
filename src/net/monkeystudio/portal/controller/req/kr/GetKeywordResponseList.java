package net.monkeystudio.portal.controller.req.kr;

import java.util.HashMap;
import java.util.Map;

/**
 * 关键字回复
 * @author hebo
 *
 */
public class GetKeywordResponseList {

	private String wxPubOriginId;
	private Integer page;
	private Integer pageSize;
	private String keywords;
	
	public Map<String,Object> getMap(){
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("page",page);
		map.put("pageSize",pageSize);
		map.put("wxPubOriginId",wxPubOriginId);
		map.put("keywords",keywords);
		
		return map;
	}

	public String getWxPubOriginId() {
		return wxPubOriginId;
	}

	public void setWxPubOriginId(String wxPubOriginId) {
		this.wxPubOriginId = wxPubOriginId;
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

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
}
