package net.monkeystudio.portal.controller.req.kr;

import java.util.List;

/**
 * 更新关键字-回复
 * @author hebo
 *
 */
public class UpdateKeywordResponse {

	private Integer id;
	private List<String> keywords;
	private String response;
	private Integer rule;

	public List<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public Integer getRule() {
		return rule;
	}
	public void setRule(Integer rule) {
		this.rule = rule;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
}
