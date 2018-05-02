package net.monkeystudio.base.utils;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.controller.bean.RetCode;
import net.monkeystudio.base.local.Msg;
import net.monkeystudio.base.service.CfgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;


@Service
public class RespHelper {
	
	@Autowired
	private CfgService cfgService;

	public RespBase ok(Object result)
	{
		return ok(result, null);
	}
	
	public RespBase ok(Object result, Integer total){
		
		RespBase resp = new RespBase(RetCode.SUCCESS);
		resp.setResult(result);
		resp.setTotal(total);
		
		String respJson = resp.toJsonString();
		Log.d("Response:[" + respJson + "]");
		
		return resp;
	}
	
	public RespBase ok(Object result, Integer total, String retMsg){
		
		RespBase resp = new RespBase(RetCode.SUCCESS);
		resp.setResult(result);
		resp.setTotal(total);
		resp.setRetMsg(retMsg);
		
		String respJson = resp.toJsonString();
		Log.d("Response:[" + respJson + "]");
		
		return resp;
	}
	
	public RespBase ok()
	{
		RespBase resp = new RespBase(RetCode.SUCCESS);
		resp.setResult(null);
		
		return resp;
	}
	
	
	public RespBase failed(String msg)
	{
		RespBase resp = new RespBase(RetCode.FAILED);
		resp.setRetMsg(msg);
		
		return resp;
	}
	
	public RespBase authFailed(){
		RespBase resp = new RespBase(RetCode.AUTH_FAILED);
		resp.setRetMsg(Msg.text("common.user.authfailed"));
		
		return resp;
	}
	
	public RespBase nologin(){
		
		RespBase resp = new RespBase(RetCode.NO_LOGIN);
		resp.setRetMsg(Msg.text("common.user.nologin"));
		
		return resp;
	}
	
	public RespBase cliParamError(String errStr){
		
		RespBase resp = new RespBase(RetCode.FAILED);
		resp.setRetMsg(Msg.text("api.client.param.error") + "[" + errStr + "]");
		
		return resp;

	}
	
	
	public String sok(Object result)
	{
		return sok(result, null);
	}
	
	public String sok(Object result, Integer total){
		
		RespBase resp = new RespBase(RetCode.SUCCESS);
		resp.setResult(result);
		resp.setTotal(total);
		
		String respJson = resp.toJsonString();
		Log.d("Response:[" + respJson + "]");
		
		return respJson;
	}
	
	public String sok()
	{
		RespBase resp = new RespBase(RetCode.SUCCESS);
		resp.setResult(null);
		
		return resp.toJsonString();
	}
	
	
	public String sfailed(String msg)
	{
		RespBase resp = new RespBase(RetCode.FAILED);
		resp.setRetMsg(msg);
		
		return resp.toJsonString();
	}
	
	public String sauthFailed(){
		RespBase resp = new RespBase(RetCode.AUTH_FAILED);
		resp.setRetMsg(Msg.text("common.user.authfailed"));
		
		return resp.toJsonString();
	}
	
	public String snologin(){
		
		RespBase resp = new RespBase(RetCode.NO_LOGIN);
		resp.setRetMsg(Msg.text("common.user.nologin"));
		
		return resp.toJsonString();
	}
	
	public String scliParamError(String errStr){
		
		RespBase resp = new RespBase(RetCode.FAILED);
		resp.setRetMsg(Msg.text("api.client.param.error") + "[" + errStr + "]");
		
		return resp.toJsonString();

	}
	
	public ModelAndView forwardPage(String viewName){
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName(viewName);
		
		return mv;
	}
	
	/**
	 * redirect页面
	 * @param pageName 页文件名，如 /user/login.html
	 * @return
	 */
	public ModelAndView redirectPage(String pageName){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("redirect:" + pageName);
		
		return mv;
	}
	
	/**
	 * redirect到登录页，登录页在全局配置表里配置（PAGE_LOGIN_DEFAULT）。
	 * @return
	 */
	public ModelAndView redirect2LoginPage(){

		String loginPage = cfgService.get("PAGE_LOGIN_DEFAULT");
		
		return redirectPage(loginPage); 
	}
	
	/**
	 * redirect到首页页，登录页在全局配置表里配置（PAGE_MAIN_DEFAULT）。
	 * @return
	 */
	public ModelAndView redirect2MainPage(){

		String mainPage = cfgService.get("PAGE_MAIN_DEFAULT");
		
		return redirectPage(mainPage); 
	}
	
	/**
	 * redirect到错误页，错误页在全局配置表配置（PAGE_ERROR_DEFAULT）。
	 * @return
	 */
	public ModelAndView redirect2ErrorPage(){
		
		String errorPage = cfgService.get("PAGE_ERROR_DEFAULT");
		
		return redirectPage(errorPage); 
	}
	
}
