package net.monkeystudio.chatrbtw.service.bean;

import java.util.List;

/**
 * 图灵API响应
 * @author hebo
 *
 */
public class TuLingResp{
	
	private Long code;
	private String text;
	private String url;
	private List<TuLingRespItem> list;
	
	public TuLingResp(){}
	
	public Long getCode() {
		return code;
	}
	public void setCode(Long code) {
		this.code = code;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<TuLingRespItem> getList() {
		return list;
	}

	public void setList(List<TuLingRespItem> list) {
		this.list = list;
	}
}