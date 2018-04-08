package net.monkeystudio.portal.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.monkeystudio.chatrbtw.entity.WxPubKeywordStatus;
import net.monkeystudio.chatrbtw.service.WxPubKeywordStatusService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import net.monkeystudio.base.RespBase;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.entity.KrKeyword;
import net.monkeystudio.chatrbtw.service.KeywordRespMgrService;
import net.monkeystudio.chatrbtw.service.bean.kr.KeywordResponse;
import net.monkeystudio.portal.controller.req.kr.GetKeywordResponseList;
import net.monkeystudio.portal.controller.req.kr.SetKeywordResponse;
import net.monkeystudio.portal.controller.req.kr.UpdateKeywordResponse;
import net.monkeystudio.utils.RespHelper;
import net.monkeystudio.wx.service.WxPubService;

/**
 * 关键字-回复设置接口
 * @author hebo
 *
 */
@Controller
@RequestMapping(value = "/kr")
public class PortalKeywordController extends PortalBaseController{

	@Autowired
	private WxPubKeywordStatusService wxPubKeywordStatusService;
	@Autowired
	RespHelper respHelper;
	
	@Autowired
	KeywordRespMgrService keywordRespMgrService;
	
	@Autowired
	WxPubService wxPubService;
	
	/**
	 * 设置关键字-回复
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/set", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase add(HttpServletRequest request, @RequestBody SetKeywordResponse setKeywordResponse){
		
		Integer userId = this.getUserId();
		if ( userId == null ){
			return respHelper.nologin();
		}
		
		if ( setKeywordResponse.getKeywords() == null || setKeywordResponse.getKeywords().size() == 0 ){
			return respHelper.failed("关键字不能为空。");
		}
		
		if ( StringUtils.isBlank( setKeywordResponse.getResponse() ) ){
			return respHelper.failed("响应内容不能为空。");
		}
		
		Integer rule = setKeywordResponse.getRule();
		if ( rule == null ){
			return respHelper.failed("rule参数不能为空。");
		}
		if ( !ruleValid(rule) ){
			return respHelper.failed("rule参数值不对持。");
		}

		
		if ( StringUtils.isBlank(setKeywordResponse.getWxPubOriginId()) ){
			return respHelper.failed("公众号原始ID不能为空。");
		}
		
		if ( ! wxPubService.hasPub(setKeywordResponse.getWxPubOriginId(), userId) ){
			return respHelper.authFailed();
		}
		
		keywordRespMgrService.addKeywordsResponse(setKeywordResponse.getWxPubOriginId(), setKeywordResponse.getKeywords(), setKeywordResponse.getResponse(), setKeywordResponse.getRule());
		return respHelper.ok();
	}
	
	private boolean ruleValid(Integer rule){
		
		for ( int i = 0; i < AppConstants.KEYWORD_RESPONSE_RULES.length; i++ ){
			if ( rule.intValue() == AppConstants.KEYWORD_RESPONSE_RULES[i] ){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 查询关键字-回复
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase getList(HttpServletRequest request, @RequestBody GetKeywordResponseList getKeywordResponseList){
		
		Integer userId = this.getUserId();
		if ( userId == null ){
			return respHelper.nologin();
		}
		
		if ( StringUtils.isBlank(getKeywordResponseList.getWxPubOriginId()) ){
			return respHelper.failed("公众号原始ID不能为空。");
		}
		
		if ( ! wxPubService.hasPub(getKeywordResponseList.getWxPubOriginId(), userId) ){
			return respHelper.authFailed();
		}
		
		Integer page = getKeywordResponseList.getPage();
		Integer pageSize = getKeywordResponseList.getPageSize();
		
		if ( page == null || page < 1 ){
			page = AppConstants.PAGE_DEFAULTE;
		}
		
		if ( pageSize == null || pageSize < 1){
			pageSize = AppConstants.PAGE_SIZE_DEFAULT;
		}
		
		
		List<KeywordResponse> krList = keywordRespMgrService.getKeywordResponseList(getKeywordResponseList.getMap());
		Integer total = keywordRespMgrService.getKeywordResponseCount(getKeywordResponseList.getMap());
		
		return respHelper.ok(krList,total);
	}

	/*@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	@ResponseBody
	public RespBase update(HttpServletRequest request, @PathVariable("id") Integer id){

		keywordRespMgrService.update(){

		}

		return respHelper.ok();
	}*/

	/**
	 * 删除关键字
	 * @param request
	 * @param id
	 * @return
	 */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase delete(HttpServletRequest request, @PathVariable("id") Integer id){

    	Integer userId = getUserId();
    	if ( userId == null ){
    		return respHelper.nologin();
    	}
    	
    	KrKeyword kr = keywordRespMgrService.getKrKeyword(id);
    	if ( kr != null && kr.getWxPubOriginId() != null ){
    		if ( !wxPubService.hasPub(kr.getWxPubOriginId(), userId)){
    			return respHelper.authFailed();
    		}
    	}
    	
	    keywordRespMgrService.deleteKeyword(id);

        return respHelper.ok();
    }
    
    /**
	 * 更新关键字-回复
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase update(HttpServletRequest request, @RequestBody UpdateKeywordResponse updateKeywordResponse){
		
		Integer userId = this.getUserId();
		if ( userId == null ){
			return respHelper.nologin();
		}
		
		if ( updateKeywordResponse.getId() == null ){
			return respHelper.failed("id不能为空。");
		}
		
		if ( updateKeywordResponse.getKeywords() == null || updateKeywordResponse.getKeywords().size() == 0 ){
			return respHelper.failed("关键字不能为空。");
		}
		
		if ( StringUtils.isBlank( updateKeywordResponse.getResponse() ) ){
			return respHelper.failed("响应内容不能为空。");
		}
		
		Integer rule = updateKeywordResponse.getRule();
		if ( rule == null ){
			return respHelper.failed("rule参数不能为空。");
		}
		if ( !ruleValid(rule) ){
			return respHelper.failed("rule参数值不对持。");
		}
		
		KrKeyword krKeyword = keywordRespMgrService.getKrKeyword(updateKeywordResponse.getId());
		if ( krKeyword == null ){
			return respHelper.failed("配置不存在。");
		}
		
		if ( ! wxPubService.hasPub(krKeyword.getWxPubOriginId(), userId) ){
			return respHelper.authFailed();
		}
		
		keywordRespMgrService.updateKeywordsResponse(updateKeywordResponse.getId(), updateKeywordResponse.getKeywords(), updateKeywordResponse.getResponse(), updateKeywordResponse.getRule());
		return respHelper.ok();
	}

	/**
	 *	公众号全局关键字回复开关控制
	 * @param request
	 * @param wxPubOriginId   公众号originId
	 * @param 	wxPubKeywordStatus	  status 0:关闭 1:开启
	 * @return
	 */
	@RequestMapping(value="/update/{wxPubOriginId}/status",method = RequestMethod.POST)
	@ResponseBody
	public RespBase chanageStatus(HttpServletRequest request,@PathVariable("wxPubOriginId")String wxPubOriginId, @RequestBody WxPubKeywordStatus wxPubKeywordStatus ){
		Integer userId = getUserId();
		if ( userId == null ) {
			return respHelper.nologin();
		}
		wxPubKeywordStatusService.updateByOriginId(wxPubOriginId,wxPubKeywordStatus.getSwitchStatus());
		return respHelper.ok();
	}

	/**
	 * 获取公众号当前关键字回复控制状态
	 * @param request
	 * @param wxPubOriginId
	 * @return
	 */
	@RequestMapping(value = "/status/{wxPubOriginId}" , method = RequestMethod.POST)
	@ResponseBody
	public RespBase getStatus(HttpServletRequest request,@PathVariable("wxPubOriginId")String wxPubOriginId){
		Integer userId = getUserId();
		if ( userId == null ) {
			return respHelper.nologin();
		}
		Integer status = wxPubKeywordStatusService.getStatusByOriginId(wxPubOriginId);
		return respHelper.ok(status);
	}


}
