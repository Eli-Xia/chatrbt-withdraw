package net.monkeystudio.chatrbtw.service.bean.kr;

import java.util.List;

public class KeywordResponse {

	private Integer keywordsId;
	private List<String> keywords;
	private String response;
	
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

	public Integer getKeywordsId() {
		return keywordsId;
	}

	public void setKeywordsId(Integer keywordsId) {
		this.keywordsId = keywordsId;
	}
}
