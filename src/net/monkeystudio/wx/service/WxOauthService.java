package net.monkeystudio.wx.service;

import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.HttpsHelper;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.service.CfgService;
import net.monkeystudio.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
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

