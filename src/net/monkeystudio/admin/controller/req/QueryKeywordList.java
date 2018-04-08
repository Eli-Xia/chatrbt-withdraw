package net.monkeystudio.admin.controller.req;

import java.util.Map;

import net.monkeystudio.admin.controller.base.ListReqBase;

/**
 * 获取关键字列表请求
 * @author hebo
 *
 */
public class QueryKeywordList extends ListReqBase{

	private String keywords;

	public Map<String,Object> getMap(){
		
		Map<String,Object> map = super.getMap();
		return map;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
}
