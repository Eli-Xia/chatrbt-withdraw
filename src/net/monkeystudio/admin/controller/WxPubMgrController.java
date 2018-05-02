package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.QueryWxPubList;
import net.monkeystudio.admin.controller.req.SetWxPubAdm;
import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.bean.wxpub.WxPubResp;
import net.monkeystudio.wx.service.WxPubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接入公众号管理
 * @author hebo
 *
 */
@Controller
@RequestMapping(value = "/admin/wx/pub")
public class WxPubMgrController extends BaseController {

	@Autowired
	private RespHelper respHelper;
	
	@Autowired
	private WxPubService wxPubService;
	
	/**
	 * 获取接入公众号列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase getUsers(HttpServletRequest request, @RequestBody QueryWxPubList queryWxPubList){
		
		Integer userId = getUserId();
		if ( userId == null ){
			return respHelper.nologin();
		}
		
		Integer page = queryWxPubList.getPage();
		Integer pageSize = queryWxPubList.getPageSize();
		
		if ( page == null || page < 1 ){
			return respHelper.cliParamError("page error.");
		}
		
		if ( pageSize == null || pageSize < 1 ){
			return respHelper.cliParamError("pageSize error.");
		}
		
		//List<WxPub> wxPubs = wxPubService.getWxPubs(page,pageSize);

		List<WxPubResp> wxPubResps = wxPubService.getWxPubDtos(page,pageSize);
		
		Integer total = wxPubService.getCount();
		
		return respHelper.ok(wxPubResps,total);
	}
	
	/**
	 * 设置公众号管理员（公众号主）
	 * @param request
	 * @param setWxPubAdm
	 * @return
	 */
	@RequestMapping(value = "/adm/set", method = RequestMethod.POST)
	@ResponseBody
	public RespBase setAdm(HttpServletRequest request, @RequestBody SetWxPubAdm setWxPubAdm){
		
		if ( setWxPubAdm.getAdmUserId() == null ){
			return respHelper.cliParamError("admUserId");
		}
		
		if ( setWxPubAdm.getWxPubId() == null ){
			return respHelper.cliParamError("wxPubId");
		}
		
		try {
			wxPubService.setPubAdm( setWxPubAdm.getWxPubId(), setWxPubAdm.getAdmUserId());
		} catch (BizException e) {
			return respHelper.failed(e.getBizExceptionMsg());
		}
		
		return respHelper.ok();
	}
}
