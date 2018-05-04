package net.monkeystudio.admin.controller.req.wxpubmaterial;

import net.monkeystudio.base.controller.bean.req.ListPaginationReq;

import java.util.Map;


/**
 * 获取微信公众号永久素材列表请求
 * @author hebo
 *
 */
public class QueryWxPubNewsList extends ListPaginationReq {

	private String wxPubOriginId;
	private String title;
	
	@Override
	public Map<String, Object> getMap() {

		Map<String,Object> map = super.getMap();
		map.put("wxPubOriginId",wxPubOriginId);
		map.put("title", title);
		return map;
	}

	public String getWxPubOriginId() {
		return wxPubOriginId;
	}

	public void setWxPubOriginId(String wxPubOriginId) {
		this.wxPubOriginId = wxPubOriginId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
}
