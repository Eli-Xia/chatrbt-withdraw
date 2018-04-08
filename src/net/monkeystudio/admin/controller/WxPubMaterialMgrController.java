package net.monkeystudio.admin.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.monkeystudio.base.RespBase;
import net.monkeystudio.admin.controller.req.wxpubmaterial.QueryWxPubNewsList;
import net.monkeystudio.admin.controller.req.wxpubmaterial.SetWxPubNewsUrl;
import net.monkeystudio.admin.controller.resp.wxpubmaterial.WxPubNewsDetail;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.entity.WxPubNews;
import net.monkeystudio.utils.RespHelper;
import net.monkeystudio.wx.service.WxMaterialMgrService;
import net.monkeystudio.wx.service.WxPubService;

/**
 * 公众号素材管理
 * @author hebo
 *
 */
@Controller
@RequestMapping(value = "/admin/wx/pub/material")
public class WxPubMaterialMgrController {

	@Autowired
	RespHelper respHelper;
	
	@Autowired
	WxMaterialMgrService wxMaterialMgrService;
	
	@Autowired
	WxPubService wxPubService;
	
	/**
	 * 触发抓取公众号素材
	 * @param wxPubOriginId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/fetch", method = RequestMethod.GET)
    public RespBase fetch(@RequestParam("wxPubOriginId") String wxPubOriginId) throws Exception {
		
		wxMaterialMgrService.updateWxPubNewsMaterials(wxPubOriginId);
		
		return respHelper.ok();
	}
	
	
	
	/**
	 * 公众号素材列表
	 * @param wxPubOriginId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/news/list", method = RequestMethod.POST)
    public RespBase getNewsList(@RequestBody  QueryWxPubNewsList queryWxPubNewsList) throws Exception {
		
		List<WxPubNews> wxPubNewList = wxMaterialMgrService.getWxPubNewsList(queryWxPubNewsList.getMap());
		List<WxPubNewsDetail> list = new ArrayList<WxPubNewsDetail>();
		if ( wxPubNewList != null ){
			for ( WxPubNews news : wxPubNewList){
				WxPub wxPub = wxPubService.getByOrginId(news.getWxPubOriginId());
				if ( wxPub != null ){
					WxPubNewsDetail wxPubNewsDetail = new WxPubNewsDetail(news, wxPub.getNickname());
					list.add(wxPubNewsDetail);
				}
			}
		}
		Integer total = wxMaterialMgrService.getWxPubNewsCount(queryWxPubNewsList.getMap());
		
		return respHelper.ok(list,total);
	}
	
	/**
	 * 设置公众号素材url2
	 * @param wxPubOriginId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/news/url/set", method = RequestMethod.POST)
    public RespBase getNewsList(@RequestBody  SetWxPubNewsUrl setWxPubNewsUrl) throws Exception {
		
		if ( setWxPubNewsUrl.getIdUrl2Map() == null || setWxPubNewsUrl.getIdUrl2Map().size() == 0 ){
			return respHelper.cliParamError("参数为空。");
		}
		
		wxMaterialMgrService.setWxPubNewsUrl(setWxPubNewsUrl.getIdUrl2Map());
		
		return respHelper.ok();
	}
}
