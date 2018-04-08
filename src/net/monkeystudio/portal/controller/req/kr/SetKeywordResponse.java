package net.monkeystudio.portal.controller.req.kr;

import java.util.List;

/**
 * 设置关键字-回复
 * @author hebo
 *
 */
public class SetKeywordResponse {

	private String wxPubOriginId;
	private List<String> keywords;
	private String response;
	private Integer rule;
	
	public String getWxPubOriginId() {
		return wxPubOriginId;
	}
	public void setWxPubOriginId(String wxPubOriginId) {
		this.wxPubOriginId = wxPubOriginId;
	}
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
	
	
}
