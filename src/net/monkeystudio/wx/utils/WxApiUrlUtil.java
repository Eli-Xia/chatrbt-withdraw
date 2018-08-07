package net.monkeystudio.wx.utils;

/**
 * Created by bint on 2017/11/5.
 */
public class WxApiUrlUtil {

    private final static String SEND_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=#{ACCESS_TOKEN}";
    private final static String REFRESH_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=#{componentAccessToken}";

    private final static String FETCH_AUTOREPLY_INTO_URL = "https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info?access_token=#{accessToken}";

    private final static String FETCH_USER_BASE_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=#{accessToken}&openid=#{wxUserOpenId}&lang=zh_CN";

    private final static String FETCH_MATERIAL_INFO_LIST_URL = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=#{accessToken}";
    private final static String FETCH_MATERIAL_COUNT_URL = "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=#{accessToken}";

    private final static String AUTH_PAGE_URL = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=#{component_appid}&pre_auth_code=#{pre_auth_code}&redirect_uri=#{redirect_uri}";

    private final static String FETCH_AUTHORIZER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=#{componentAccessToken}";

    private final static String FETCH_CREATE_TEMP_QR_CODE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=#{accessToken}";

    private static final String CODE_2_SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=#{app_id}&secret=#{secret}&js_code=#{js_code}&grant_type=authorization_code";

    //发送模版消息
    private static final String SEND_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=#{accessToken}";

    private static final String MINI_PROGRAM_FETCH_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=#{appId}&secret=#{appSecret}";

    public static String getSendMessageUrl(String accessToken){
        return SEND_MESSAGE_URL.replace("#{ACCESS_TOKEN}", accessToken);
    }

    public static String getRefreshAccessTokenUrl(String componentAccessToken){
        return REFRESH_ACCESS_TOKEN_URL.replace("#{componentAccessToken}",componentAccessToken);
    }

    public static String getFetchAutoreplyIntoUrl(String accessToken){
        return FETCH_AUTOREPLY_INTO_URL.replace("#{accessToken}", accessToken);
    }

    public static String getFetchUserBaseInfoUrl(String accessToken ,String wxUserOpenId){
        String url = FETCH_USER_BASE_INFO_URL.replace("#{accessToken}", accessToken);
        url = url.replace("#{wxUserOpenId}", wxUserOpenId);
        return url;
    }

    public static String getFetchMaterialInfoListUrl(String accessToken){
        return FETCH_MATERIAL_INFO_LIST_URL.replace("#{accessToken}" ,accessToken);
    }

    public static String getAuthorizerInfoUrl(String componentAccessToken){
        return FETCH_AUTHORIZER_INFO_URL.replace("#{componentAccessToken}", componentAccessToken);
    }
    
    /**
     * 获取素材总数URL
     * @param accessToken
     * @return
     */
    public static String getFetchMaterialCountUrl(String accessToken){
        return FETCH_MATERIAL_COUNT_URL.replace("#{accessToken}" ,accessToken);
    }

    public static String getAuthPageUrl(String componentAppId ,String preAuthCode ,String redirectUri ){
        String url = AUTH_PAGE_URL;
        url = url.replace("#{component_appid}", componentAppId);
        url = url.replace("#{pre_auth_code}", preAuthCode);
        url = url.replace("#{redirect_uri}",redirectUri);
        return url;
    }

    /**
     * 发送模版消息
     * @param accessToken
     * @return
     */
    public static String getSendTemplateUrl(String accessToken){
        return SEND_TEMPLATE_URL.replace("#{accessToken}", accessToken);
    }

    /**
     * 获取二维码URL
     * @return
     */
    public static String getCreateTempQrCodeUrl(String accessToken){
        return FETCH_CREATE_TEMP_QR_CODE_URL.replace("#{accessToken}", accessToken);
    }

    /**
     * 获取小程序登录校验信息url
     * @param appId     :小程序appId
     * @param secret    :小程序secret
     * @param code      :前端传过来的code
     * @return
     */
    public static String getMiniAppLoginVerifyUrl(String appId,String secret,String code){
        String url = CODE_2_SESSION_URL;
        url = url.replace("#{app_id}",appId);
        url = url.replace("#{secret}",secret);
        url = url.replace("#{js_code}",code);
        return url;
    }

    /**
     * 获取小程序的accessToken的url
     * @param appId
     * @param appSecret
     * @return
     */
    public static String getMiniProgramFetchAccessToken(String appId ,String appSecret){
        String url = MINI_PROGRAM_FETCH_ACCESS_TOKEN.replace("#{appId}", appId);
        url = url.replace("#{appSecret}", appSecret);

        return url;
    }

}
