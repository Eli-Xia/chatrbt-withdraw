package net.monkeystudio.wx.controller;

import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.StringUtil;
import net.monkeystudio.wx.service.WxAuthApiService;
import net.monkeystudio.wx.service.WxOauthService;
import net.monkeystudio.wx.service.WxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.portlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

@Controller
@RequestMapping(value = "/wx")
public class WxController extends BaseController {

	@Autowired
	private WxService wxService;

	@Autowired
	private WxOauthService wxOauthService;

	@Autowired
	private WxAuthApiService wxAuthApiService;
	
	
	/**
	 * 微信公众平台接入回调接口
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/handle", method = RequestMethod.GET)
	@ResponseBody
	public String handle_get(HttpServletRequest request,
							 @RequestParam("signature") String signature,
							 @RequestParam("timestamp") String timestamp,
							 @RequestParam("nonce") String nonce,
							 @RequestParam("echostr") String echostr){

		Log.i("Handle wx req. signature:[" + signature + "],timestamp:[" + timestamp + "],nonce:[" + nonce + "],echostr:[" + echostr + "].");
		return echostr;
	}


	/**
	 * 微信公众平台接入回调接口
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/message/handler", method = RequestMethod.POST)
	@ResponseBody
	public String meessageHandle(HttpServletRequest request,
							 @RequestParam("signature") String signature,
							 @RequestParam("timestamp") String timestamp,
							 @RequestParam("nonce") String nonce,
							 @RequestParam("encrypt_type") String encryptType,
							 @RequestParam("msg_signature") String msgSignature,
                                 BufferedReader br){

		Log.i("Handle wx req. signature:[ " + signature +" ] ," + " encryptType:[ " + encryptType + "]"  + ",timestamp:[" + timestamp + "],nonce:[" + nonce + "],msgSignature:[" + msgSignature + "].");

        String postContent = StringUtil.readBuffer(br);

        Log.i(postContent);

        wxAuthApiService.componentVerifyTicketHandle(msgSignature,signature,timestamp,nonce,postContent);

		return "success";
	}


	/**
	 * 微信公众号相关消息接受
	 * @param request
	 * @param appId
	 * @param signature
	 * @param timestamp
	 * @param openId
	 * @param encryptType
	 * @param msgSignature
	 * @param br
	 * @return
	 */
	@RequestMapping(value = "/{appId}/callback", method = RequestMethod.POST)
	@ResponseBody
	public String appMessageReceive(HttpServletRequest request,
									@PathVariable("appId") String appId ,
									@RequestParam("signature") String signature,
									@RequestParam("timestamp") String timestamp,
									@RequestParam("openid") String openId,
									@RequestParam("encrypt_type") String encryptType,
									@RequestParam("msg_signature") String msgSignature,
									BufferedReader br){

		String postContent = StringUtil.readBuffer(br);

		Log.d("callback postContent:" + postContent);
		Log.d("callback appId :" + appId);
		Log.d("callback signature:" + signature);
		Log.d("callback timestamp:" + timestamp);
		Log.d("callback openid:" + openId);
		Log.d("callback encrypt_type:" + encryptType);
		Log.d("callback msg_signature:" + msgSignature);

		String result = null;
		try{
			result = wxService.handleData(postContent,timestamp ,openId);
		}catch (Exception e){
			Log.e(e);
			return "success";
		}

		return result;
	}

	/**
	 * 网页授权
	 * @return
	 */
	@RequestMapping(value = "/oauth/redirect", method = RequestMethod.GET)
    public ModelAndView oauth(HttpServletRequest request, HttpServletResponse response,@RequestParam("id")Integer wxPubId) throws Exception{
	    String redirectUrl = wxOauthService.getRequestCodeUrl(wxPubId);

	    response.sendRedirect(redirectUrl);

	    return null;
	}

   /* *//**
     * 当用户禁止授权的时候,只会传state值过来.
     * * @param request
     * @param code
     * @param state
     * @param appId
     * @return
     * @throws Exception
     *//*
	@RequestMapping(value = "/oauth/code", method = RequestMethod.GET)
	public ModelAndView oauth(HttpServletResponse response,HttpServletRequest request,@RequestParam(value = "code",required = false)String code,@RequestParam("state")String state,@RequestParam(value = "appid",required = false)String appId)throws Exception{
		Log.d("============== code = {?}  , state = {?} ,  appid = {?}",code,state,appId);

		wxOauthService.handleCode(code,appId);

		response.sendRedirect("http://www.baidu.com");//测试,跳转h5
		return null;
	}*/







}
