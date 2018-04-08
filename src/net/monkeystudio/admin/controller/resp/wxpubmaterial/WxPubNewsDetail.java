package net.monkeystudio.admin.controller.resp.wxpubmaterial;

import org.springframework.beans.BeanUtils;

import net.monkeystudio.chatrbtw.entity.WxPubNews;

public class WxPubNewsDetail extends WxPubNews{

	private String wxPubName;
	
	public WxPubNewsDetail(WxPubNews wxPubNews, String wxPubName){
		BeanUtils.copyProperties(wxPubNews, this);
		this.wxPubName = wxPubName;
	}

	public String getWxPubName() {
		return wxPubName;
	}

	public void setWxPubName(String wxPubName) {
		this.wxPubName = wxPubName;
	}
}
