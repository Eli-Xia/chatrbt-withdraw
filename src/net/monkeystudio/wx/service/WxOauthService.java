package net.monkeystudio.wx.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.HttpsHelper;
import net.monkeystudio.base.utils.JsonHelper;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.WxPub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

/**
 * @author xiaxin
 */
@Service
public class WxOauthService {
    @Autowired
    private WxAuthApiService wxAuthApiService;
    @Autowired
    private CfgService cfgService;
    @Autowired
    private WxPubService wxPubService;

    private static final String OAUTH_CODE_URL_SCOPE = "snsapi_userinfo";
    public static final String OAUTH_CODE_URL_STATE = "KEENdo";
    //private static final String WX_OAUTH_REDIRECT_URL = "https://test.keendo.com.cn/api/chat-pet/pet/oauth/fan-info";
    private static final String FETCH_OAUTH_CODE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s&component_appid=%s#wechat_redirect";
    private static final String FETCH_OAUTH_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/component/access_token?appid=%s&code=%s&grant_type=authorization_code&component_appid=%s&component_access_token=%s";
    private static final String FETCH_OAUTH_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

    /**
     * 微信网页授权回调接口url
     * @return
     */
    private String createWxOauthRedirectUrl(){
        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        String url = "https://"+domain+"/api/chat-pet/pet/oauth/fan-info";
        return url;
    }
    /*
    *       流程:
    *       1,调用获取code接口需要在微信端打开  拼接url 然后重定向过去
	 * 		2, 提供一个redirect回调接口, 微信会把code,state重定向到此接口
	 * 		3, 用code获取access_token
	 *
	 * 	appid	        是	公众号的appid
	 	redirect_uri	是	重定向地址，需要urlencode，这里填写的应是服务开发方的回调地址
	 	response_type	是	填code
	 	scope	        是	授权作用域，拥有多个作用域用逗号（,）分隔
	 	state	        否	重定向后会带上state参数，开发者可以填写任意参数值，最多128字节
	 	component_appid	是	服务方的appid，在申请创建公众号服务成功后，可在公众号服务详情页找到

	    https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE&component_appid=component_appid#wechat_redirect
	 *
    * */
    private String getRequestCodeUrl(String redirectUrl,Integer wxPubId) throws Exception {

        WxPub wxPub = wxPubService.getWxPubById(wxPubId);

        String wxPubAppId = wxPub.getAppId();

        String componentAppid = cfgService.get(GlobalConfigConstants.COMPONENT_APP_ID_KEY);

        return String.format(FETCH_OAUTH_CODE_URL,
                wxPubAppId, URLEncoder.encode(redirectUrl,"UTF-8"), OAUTH_CODE_URL_SCOPE,OAUTH_CODE_URL_STATE,componentAppid);
    }

    /**
     * 网页授权获取code URL拼接
     * @param
     * @return
     * @throws Exception
     */
    public String getRequestCodeUrl(Integer wxPubId) throws  Exception{
        String redirectUrl = this.createWxOauthRedirectUrl();
        return this.getRequestCodeUrl(redirectUrl,wxPubId);
    }

    /**
     * 通过code获取access_token
     *
     * https://api.weixin.qq.com/sns/oauth2/component/access_token
     * ?appid=APPID&code=CODE&grant_type=authorization_code&component_appid=COMPONENT_APPID&component_access_token=COMPONENT_ACCESS_TOKEN


     appid	是	公众号的appid
     code	是	填写第一步获取的code参数
     grant_type	是	填authorization_code
     component_appid	是	服务开发方的appid
     component_access_token	是	服务开发方的access_token


     {
     "access_token":"ACCESS_TOKEN",
     "expires_in":7200,
     "refresh_token":"REFRESH_TOKEN",
     "openid":"OPENID",
     "scope":"SCOPE"
     }

     access_token	接口调用凭证
     expires_in	access_token接口调用凭证超时时间，单位（秒）
     refresh_token	用户刷新access_token
     openid	授权用户唯一标识
     scope	用户授权的作用域，使用逗号（,）分隔
     * @param code
     */
    public void handleCode(String code,String wxPubAppId) throws BizException {
        String fetchAccessTokenUrl = this.getAccessTokenUrl(code,wxPubAppId);
        String response = HttpsHelper.get(fetchAccessTokenUrl);
        String access_token = JsonHelper.getStringFromJson(response,"access_token");
        String fansOpenId = JsonHelper.getStringFromJson(response,"openid");
        Log.d("================通过code获取accessToken结果  access_token = {?}  , openId = {?} =====================");
        String fetchFansInfoUrl = this.getFansInfoUrl(access_token,fansOpenId);
        String info = HttpsHelper.get(fetchFansInfoUrl);
        String openid2 = JsonHelper.getStringFromJson(info,"openid");
        String nickname = JsonHelper.getStringFromJson(info,"nickname");
        Log.d("=================成功获取用户信息 openid = {?} , nickname = {?}==============",openid2,nickname);
        //获取粉丝信息
        /*
        *   GET（请使用https协议） https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
        *
        *   access_token	网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
            openid	用户的唯一标识
            lang	返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语


            {
                "openid":" OPENID",
                " nickname": NICKNAME,
                  "sex":"1",
                "province":"PROVINCE"
                "city":"CITY",
                "country":"COUNTRY",
                "headimgurl":    "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46",
                "privilege":[ "PRIVILEGE1" "PRIVILEGE2"     ],
                "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
            }
        * */

    }

    /**
     * 通过code调用获取access_token URL拼接
     * @param code           :网页授权第一步获取到的code
     * @param wxPubAppId    :微信公众号appid
     * @return
     * @throws BizException
     */
    public String getAccessTokenUrl(String code,String wxPubAppId) throws BizException{
        String componentAppid = cfgService.get(GlobalConfigConstants.COMPONENT_APP_ID_KEY);

        String componentAccessToken = wxAuthApiService.getComponentAccessTokenStr();

        return String.format(FETCH_OAUTH_ACCESS_TOKEN_URL,
               wxPubAppId,code,componentAppid,componentAccessToken );
    }

    /**
     * access_token + openid 获取用户信息 URL拼接
     * @param accessToken
     * @param openId
     * @return
     */
    public String getFansInfoUrl(String accessToken,String openId){
        return String.format(FETCH_OAUTH_USER_INFO_URL,accessToken,openId );
    }


}

