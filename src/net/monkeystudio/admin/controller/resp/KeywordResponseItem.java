package net.monkeystudio.admin.controller.resp;

import java.util.List;

/**
 * 查询关键字响应列表
 * @author hebo
 *
 */
public class KeywordResponseItem {

	private String wxPubOriginId;
	private String wxPubName;
	private List<String> keywords;
	private String responseId;
	private String response;
	private Integer rule;
	private String type;
	
	public String getWxPubOriginId() {
		return wxPubOriginId;
	}
	public void setWxPubOriginId(String wxPubOriginId) {
		this.wxPubOriginId = wxPubOriginId;
	}
	public String getWxPubName() {
		return wxPubName;
	}
	public void setWxPubName(String wxPubName) {
		this.wxPubName = wxPubName;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getResponseId() {
		return responseId;
	}
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}
	
}
