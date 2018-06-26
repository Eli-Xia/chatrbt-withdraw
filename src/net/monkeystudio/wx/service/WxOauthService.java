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

    //private static final String OAUTH_CODE_URL_SCOPE = "snsapi_userinfo";
    private static final String OAUTH_CODE_URL_SCOPE = "snsapi_base";
    public static final String OAUTH_CODE_URL_STATE = "KEENdo";
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

    public static void main(String[]args){
        String a = "恭喜你采矿成功,点击<a href=\"%s\">领取奖励</a>";
        String format = String.format(a, "www.baidu.com");
        System.out.println(1);

    }


}

