package net.monkeystudio.wx.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.service.GlobalConstants;
import net.monkeystudio.base.utils.*;
import net.monkeystudio.chatrbtw.entity.RWxPubProduct;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.entity.WxPubAuthorizerRefreshToken;
import net.monkeystudio.chatrbtw.entity.WxPubKeywordStatus;
import net.monkeystudio.chatrbtw.sdk.wx.WxPubHelper;
import net.monkeystudio.chatrbtw.service.*;
import net.monkeystudio.chatrbtw.service.bean.auth.WxPubJoinStatus;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.service.CfgService;
import net.monkeystudio.wx.mp.aes.XMLParse;
import net.monkeystudio.wx.mp.beam.ComponentVerifyTicket;
import net.monkeystudio.wx.mp.beam.Encryp;
import net.monkeystudio.wx.utils.WxApiUrlUtil;
import net.monkeystudio.wx.vo.thirtparty.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linhongbin on 2017/10/24.
 */
//@SuppressWarnings("unused")
@Service
public class WxAuthApiService {

    private static String authPageUrl = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=#{component_appid}&pre_auth_code=#{pre_auth_code}&redirect_uri=#{redirect_uri}";

    private final static String REDIRECT_URI = "/wx-pub/auth-callback";

    private final static String AUTH_URL = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=#{authCode}";

    private final static String FETCH_COMPONENT_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";

    private final static String PRE_AUTH_CODE_URL = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=#{componentAccessToken}";

    private final static String FETCH_AUTHORIZATION_INFO_INFO_URL = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=#{componentAccessToken}";

    private String FETCH_AUTHORIZER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=#{componentAccessToken}";

    private final static Integer VERIFY_TICKET_PERIOD = 10 * 60;

    //公众号接入的状态
    public final static int HAVE_JOINED = 0;//已经接入
    public final static int FIRST_JOINED = -1;//未曾接入

    @Autowired
    private ChatRobotService chatRobotService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private WxPubAuthorizerRefreshTokenService wxPubAuthorizerRefreshTokenService;

    @Autowired
    private CfgService cfgService ;

    @Autowired
    private WxBizMsgCryptService wxBizMsgCryptService;

    @Autowired
    private WxPubKeywordStatusService wxPubKeywordStatusService;

    @Autowired
    private RWxPubProductService rWxPubProductService;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private WxPubHelper wxPubHelper;

    private final static String VERIFY_TICKER_INFO_CACHE_KEY = RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "verify_ticker_info";

    private final static String COMPONENT_ACCESS_TOKEN_KEY = RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "component_access_tocken";

    //private final static String AUTHORIZATION_INFO_KEY = RedisTypeConstants.KEY_STRING_TYPE_PREFIX ;

    //component_access_tocken 缓存的时间
    private final static Integer COMPONENT_ACCESS_TOKEN_TIMEOUT = 60 * 60;


    private static String componentAppId = null;
    private static String appSecret = null;

    private static String redirctUriPart = null;



    @PostConstruct
    private void init(){
        componentAppId = cfgService.get(GlobalConfigConstants.COMPONENT_APP_ID_KEY);
        appSecret = cfgService.get(GlobalConfigConstants.APP_SECRET_KEY);

        String domain = "http://" + cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        redirctUriPart = domain + REDIRECT_URI;
    }

    public String getComponentAppId(){
        return componentAppId;
    }

    /**
     * @Deprecated
     *  已经弃用，这个不支持绑定机器人
     * @param
     * @return
     */
    /*public String getAuthPageUrl(Integer userId) throws BizException {

        String result = this.replaceComponentAppId(authPageUrl);

        ComponentAccessToken componentAccessToken = this.getComponentAccessToken();

        if(componentAccessToken == null){
            return "error:componentAccessToken is null";
        }

        PreAuthCode preAuthCode = this.fetchPreAuthCodeFromWx(componentAccessToken.getComponentAccessToken());

        result = this.replacePreAuthCode(result,preAuthCode.getPreAuthCode());

        if(result == null){
            return null;
        }

        result = this.replaceRedirectUri(result,userId);

        return result;
    }*/

    public String getWxPubAccessTokenByOriginId(String wxPubOriginId) throws BizException {

        String wxPubAppId = wxPubService.getWxPubAppIdByOrginId(wxPubOriginId);

        return this.getAuthorizerAccessToken(wxPubAppId);
    }




    /**
     * 获取AuthorizerAccessToken
     * 有可能返回空
     * @param wxPubAppId
     * @return
     */
    public String getAuthorizerAccessToken(String wxPubAppId) throws BizException {

        String authorizerAccessTokenRedisKey = this.getAuthorizerAccessTokenKey(wxPubAppId);

        String authorizerRefreshToken = redisCacheTemplate.getObject(authorizerAccessTokenRedisKey);

        if(authorizerRefreshToken != null){
            return authorizerRefreshToken;
        }
		AuthorizerRefreshTokenResp authorizerRefreshTokenResp = this.fetchAuthorizerRefreshTokenResp(wxPubAppId);

        authorizerRefreshToken = authorizerRefreshTokenResp.getAuthorizerRefreshToken();

        String authorizerAccessToken = authorizerRefreshTokenResp.getAuthorizerAccessToken();
        Integer expireIn = authorizerRefreshTokenResp.getExpiresIn();

        redisCacheTemplate.setObject(authorizerAccessTokenRedisKey,authorizerAccessToken);
        redisCacheTemplate.expire(authorizerAccessTokenRedisKey,expireIn);

        wxPubAuthorizerRefreshTokenService.saveOrUpdate(wxPubAppId,authorizerRefreshToken);

        return authorizerAccessToken;
    }

    
    private AuthorizerRefreshTokenResp fetchAuthorizerRefreshTokenResp(String authorizerAppId) throws BizException {

        ComponentAccessToken componentAccessToken = this.getComponentAccessToken();

        if(componentAccessToken == null){
            Log.e("ComponentAccessToken is null , the wxPubAppId is [?] ." ,authorizerAppId );
            throw new BizException("获取不到componentAccessToken");
        }

        String componentAccessTokenValue = componentAccessToken.getComponentAccessToken();
        String refreshAccessTokenUrl = WxApiUrlUtil.getRefreshAccessTokenUrl(componentAccessTokenValue);

        WxPubAuthorizerRefreshToken wxPubAuthorizerRefreshToken = wxPubAuthorizerRefreshTokenService.getByAuthorizerAppId(authorizerAppId);


        Map<String, String> params = new HashMap<>();
        params.put("component_appid",componentAppId);
        params.put("authorizer_appid", authorizerAppId);
        params.put("authorizer_refresh_token",wxPubAuthorizerRefreshToken.getAuthorizerRefreshToken());

        String response = HttpsHelper.postJson(refreshAccessTokenUrl,params);

        AuthorizerRefreshTokenResp authorizerRefreshTokenResp = JsonUtil.readValue(response, AuthorizerRefreshTokenResp.class);

        return authorizerRefreshTokenResp;
    }

    @Transactional
    public Integer authCodeValueHandle(String authCode , String key) throws BizException{

        String url = null;
        try {
            url = Base64EncodingUtil.decryptBASE64(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("参数解密有误");
        }

        String sourceStr = URLUtil.getParam(url, "source");
        if(sourceStr == null){
            throw new BizException("参数缺少source");
        }
        Integer source = Integer.valueOf(sourceStr);

        String userIdStr = URLUtil.getParam(url, "userId");
        if(userIdStr == null){
            throw new BizException("用户id出错");
        }

        Integer userId = Integer.valueOf(userIdStr);

        WxPubJoinStatus wxPubJoinStatus = this.authCodeValueHandle(authCode,userId);

        String wxPubOriginId = wxPubJoinStatus.getWxPubOriginId();
        Integer joinStatus = wxPubJoinStatus.getJoinStatus();

        if(source.intValue() == GlobalConstants.JOIN_SOURCE_WEIZAN){

            //如果是首次接入
            if(joinStatus.intValue() == FIRST_JOINED){
                //不启用智能聊
                RWxPubProduct smartChatRWxPubProduct = new RWxPubProduct();
                smartChatRWxPubProduct.setWxPubOriginId(wxPubOriginId);
                smartChatRWxPubProduct.setStatus(RWxPubProductService.UNENABLE_STATUS);
                smartChatRWxPubProduct.setProductId(ProductService.SMART_CHAT);

                rWxPubProductService.insert(smartChatRWxPubProduct);

                //启动问问搜
                RWxPubProduct askSearchRWxPubProduct = new RWxPubProduct();
                askSearchRWxPubProduct.setProductId(ProductService.ASK_SEARCH);
                askSearchRWxPubProduct.setStatus(RWxPubProductService.ENABLE_STATUS);
                askSearchRWxPubProduct.setWxPubOriginId(wxPubOriginId);

                rWxPubProductService.insert(askSearchRWxPubProduct);
            }
        }

        //如果接入来源是portal
        if(source.intValue() == GlobalConstants.JOIN_SOURCE_PORTAL){
            String chatRobotIdStr = URLUtil.getParam(url,"chatbotId");

            Integer chatRobotId = Integer.valueOf(chatRobotIdStr);

            if(chatRobotId.intValue() != -1){
                //先删除之前存在的对应的机器人
                chatRobotService.deleteRobotByWxPubOriginId(wxPubOriginId);
                chatRobotService.designateChatRobot(chatRobotId, wxPubOriginId);
            }
        }

        return source;
    }

    /**
     * @deprecated 被authCodeValueHandle(String authCodeValue ,Integer userId) 代替
     * 校检码的的处理
     * @param authCodeValue
     */
    /*@Transactional
    public void authCodeValueHandle(String authCodeValue ,Integer userId ,Integer chatRobotId) throws BizException {

        WxThirtPartAuthorizationResp wxThirtPartAuthorizationResp = this.fecthAuthorizationInfo(authCodeValue);

        WxThirtyPartauthorizerInfo wxThirtyPartauthorizerInfo = wxThirtPartAuthorizationResp.getAuthorizationInfo();

        //保存或更新refrshToken
        wxPubAuthorizerRefreshTokenService.saveOrUpdate(wxThirtyPartauthorizerInfo.getAuthorizerAppId(),wxThirtyPartauthorizerInfo.getAuthorizerRefreshToken());

        String authorizerAccessToken = wxThirtyPartauthorizerInfo.getAuthorizerAccessToken();

        String authorizerAccessTokenKey = this.getAuthorizerAccessTokenKey(wxThirtyPartauthorizerInfo.getAuthorizerAppId());

        redisCacheTemplate.setObject(authorizerAccessTokenKey,authorizerAccessToken);
        redisCacheTemplate.expire(authorizerAccessTokenKey, COMPONENT_ACCESS_TOKEN_TIMEOUT);

        String componentAccessToken = this.getComponentAccessTokenStr();
        //PubBaseInfo pubBaseInfo = this.fetchPubBaseInfo(componentAccessToken.getComponentAccessToken(),componentAppId,wxThirtyPartauthorizerInfo.getAuthorizerAppId());
        PubBaseInfo pubBaseInfo = wxPubHelper.fetchPubBaseInfo(componentAccessToken,componentAppId,wxThirtyPartauthorizerInfo.getAuthorizerAppId());

        AuthorizerInfo authorizerInfo = pubBaseInfo.getAuthorizerInfo();

        String originId = authorizerInfo.getOriginId();

        WxPub wxPub = wxPubService.getByOrginId(originId);

        WxPub wxPubNew = new WxPub();
        wxPubNew.setAppId(wxThirtyPartauthorizerInfo.getAuthorizerAppId());
        wxPubNew.setNickname(authorizerInfo.getNickName());
        wxPubNew.setOriginId(authorizerInfo.getOriginId());
        wxPubNew.setCreateTime(new Date());
        wxPubNew.setUserId(userId);
        wxPubNew.setHeadImgUrl(authorizerInfo.getHeadImg());

        String verifyTypeInfo = authorizerInfo.getVerifyTypeInfo().getId();
        wxPubNew.setVerifyTypeInfo(Integer.parseInt(verifyTypeInfo));

        String wxPubOriginId = wxPubNew.getOriginId();
        if(wxPub != null){
            wxPubService.update(wxPubNew);
        }else {
            WxPubKeywordStatus wxPubKeywordStatus = new WxPubKeywordStatus();
            wxPubKeywordStatus.setOriginId(wxPubOriginId);
            wxPubKeywordStatus.setSwitchStatus(1);
            wxPubService.save(wxPubNew);
            wxPubKeywordStatusService.insert(wxPubKeywordStatus);
        }

        if(chatRobotId.intValue() != -1){
            //先删除之前存在的对应的机器人
            chatRobotService.deleteRobotByWxPubOriginId(wxPubOriginId);
            chatRobotService.designateChatRobot(chatRobotId, wxPubOriginId);
        }


    }*/


    /**
     * 校检码的的处理
     * @param authCodeValue
     */
    @Transactional
    public WxPubJoinStatus authCodeValueHandle(String authCodeValue , Integer userId ) throws BizException {

        WxPubJoinStatus wxPubJoinStatus = new WxPubJoinStatus();

        WxThirtPartAuthorizationResp wxThirtPartAuthorizationResp = this.fecthAuthorizationInfo(authCodeValue);

        WxThirtyPartauthorizerInfo wxThirtyPartauthorizerInfo = wxThirtPartAuthorizationResp.getAuthorizationInfo();

        //保存或更新refrshToken
        wxPubAuthorizerRefreshTokenService.saveOrUpdate(wxThirtyPartauthorizerInfo.getAuthorizerAppId(),wxThirtyPartauthorizerInfo.getAuthorizerRefreshToken());

        String authorizerAccessToken = wxThirtyPartauthorizerInfo.getAuthorizerAccessToken();

        String authorizerAccessTokenKey = this.getAuthorizerAccessTokenKey(wxThirtyPartauthorizerInfo.getAuthorizerAppId());

        redisCacheTemplate.setObject(authorizerAccessTokenKey,authorizerAccessToken);
        redisCacheTemplate.expire(authorizerAccessTokenKey, COMPONENT_ACCESS_TOKEN_TIMEOUT);

        ComponentAccessToken componentAccessToken = this.getComponentAccessToken();
        PubBaseInfo pubBaseInfo = this.fetchPubBaseInfo(componentAccessToken.getComponentAccessToken(),componentAppId,wxThirtyPartauthorizerInfo.getAuthorizerAppId());

        AuthorizerInfo authorizerInfo = pubBaseInfo.getAuthorizerInfo();

        String originId = authorizerInfo.getOriginId();
        wxPubJoinStatus.setWxPubOriginId(originId);

        WxPub wxPub = wxPubService.getByOrginId(originId);

        WxPub wxPubNew = new WxPub();
        wxPubNew.setAppId(wxThirtyPartauthorizerInfo.getAuthorizerAppId());
        wxPubNew.setNickname(authorizerInfo.getNickName());
        wxPubNew.setOriginId(originId);
        wxPubNew.setCreateTime(new Date());
        wxPubNew.setUserId(userId);
        wxPubNew.setHeadImgUrl(authorizerInfo.getHeadImg());

        String verifyTypeInfo = authorizerInfo.getVerifyTypeInfo().getId();
        wxPubNew.setVerifyTypeInfo(Integer.parseInt(verifyTypeInfo));

        String wxPubOriginId = wxPubNew.getOriginId();
        if(wxPub != null){
            wxPubService.update(wxPubNew);

            wxPubJoinStatus.setJoinStatus(HAVE_JOINED);
        }else {
            WxPubKeywordStatus wxPubKeywordStatus = new WxPubKeywordStatus();
            wxPubKeywordStatus.setOriginId(wxPubOriginId);
            wxPubKeywordStatus.setSwitchStatus(1);
            wxPubService.save(wxPubNew);
            wxPubKeywordStatusService.insert(wxPubKeywordStatus);

            wxPubJoinStatus.setJoinStatus(FIRST_JOINED);
        }

        return wxPubJoinStatus;
    }

    /**
     * 使用授权码换取公众号或小程序的接口调用凭据和授权信息
     * @param authCodeValue
     * @return
     */
    public WxThirtPartAuthorizationResp fecthAuthorizationInfo(String authCodeValue){

        String authorizationInfoUrl = null;
        try {
            authorizationInfoUrl = this.getAuthorizationInfoUrl();
        } catch (BizException e) {
            Log.e(e);
            return null;
        }

        Map<String, String> map = new HashMap<>();

        map.put("component_appid",componentAppId);
        map.put("authorization_code", authCodeValue);

        String response = HttpsHelper.postJson(authorizationInfoUrl,map);

        if(response.indexOf("errcode") != -1){
            Log.e(response + ",the authCodeValue is :" + authCodeValue);
        }

        WxThirtPartAuthorizationResp wxThirtPartAuthorizationResp = JsonUtil.readValue(response, WxThirtPartAuthorizationResp.class);

        return wxThirtPartAuthorizationResp;
    }

 
    private String getAuthorizationInfoUrl() throws BizException {

        ComponentAccessToken componentAccessToken = this.getComponentAccessToken();

        if(componentAccessToken == null){
            return null;
        }

        String authorizationInfoUrl = this.replaceComponentAccessToken(FETCH_AUTHORIZATION_INFO_INFO_URL, componentAccessToken.getComponentAccessToken() );

        return authorizationInfoUrl;
    }


    /**
     * 获取ComponentAccessToken
     * @return
     */
    public ComponentAccessToken getComponentAccessToken() throws BizException {

        ComponentAccessToken componentAccessToken = redisCacheTemplate.getObject(COMPONENT_ACCESS_TOKEN_KEY);

        if(componentAccessToken != null){
            return componentAccessToken;
        }

        String componentVerifyTicket = this.getComponentVerifyTicket();
        if(componentVerifyTicket == null){
            return null;
        }

        componentAccessToken = this.fetchComponentAccessToken(componentVerifyTicket);

        return componentAccessToken;
    }


    /**
     * 获取ComponentAccessToken(字符串类型)
     * @return
     */
    public String getComponentAccessTokenStr() throws BizException {
        ComponentAccessToken componentAccessToken = this.getComponentAccessToken();

        if(componentAccessToken == null){
            throw new BizException("componentAccessToken is null");
        }

        return componentAccessToken.getComponentAccessToken();
    }


    /**
     * 从微信中获取 ComponentAccessToken
     * @param componentVerifyTicket
     * @return
     */
    private ComponentAccessToken fetchComponentAccessToken(String componentVerifyTicket) throws BizException{

        Map<String, String> map = new HashMap<>();

        map.put("component_appid",componentAppId);
        map.put("component_appsecret",appSecret);
        map.put("component_verify_ticket", componentVerifyTicket);

        String componentAccessTokenString = HttpsHelper.postJson(FETCH_COMPONENT_ACCESS_TOKEN_URL, map);

        if(componentAccessTokenString.indexOf("errcode") != -1){
            throw new BizException("获取不到AccessToken");
        }

        ComponentAccessToken componentAccessToken = JsonUtil.readValue(componentAccessTokenString, ComponentAccessToken.class);

        redisCacheTemplate.setObject(COMPONENT_ACCESS_TOKEN_KEY,componentAccessToken);
        redisCacheTemplate.expire(COMPONENT_ACCESS_TOKEN_KEY, COMPONENT_ACCESS_TOKEN_TIMEOUT);

        return componentAccessToken;
    }

    /**
     * 从微信获取PreAuthCode
     * @param componentAccessToken
     * @return
     */
    
    public PreAuthCode fetchPreAuthCodeFromWx(String componentAccessToken) throws BizException {

        String preAuthCodeUrl = this.getPreAuthCodeUrl(componentAccessToken);

        Map<String, String> map = new HashMap<>();

        map.put("component_appid",componentAppId);

        String response = HttpsHelper.postJson(preAuthCodeUrl, map);

        if(response.indexOf("errcode\":61004") != -1 ){
            throw new BizException("IP不在白名单内");
        }

        PreAuthCode preAuthCode = JsonUtil.readValue(response, PreAuthCode.class);

        return preAuthCode;

    }


    /**
     * 保存微信ticker信息
     * @param signature
     * @param timestamp
     * @param nonce
     * @param ticker
     */
    public void saveVerifyTicket(String signature ,String timestamp, String nonce,String ticker){

        VerifyTicketInfo verifyTicketInfo = new VerifyTicketInfo();

        verifyTicketInfo.setNonce(nonce);
        verifyTicketInfo.setSignature(signature);
        verifyTicketInfo.setTimestamp(timestamp);
        verifyTicketInfo.setTicket(ticker);

        redisCacheTemplate.setObject(VERIFY_TICKER_INFO_CACHE_KEY,verifyTicketInfo);
        redisCacheTemplate.expire(VERIFY_TICKER_INFO_CACHE_KEY,VERIFY_TICKET_PERIOD);
    }


    /**
     * TODO 缺乏校检
     * @param msgSignature
     * @param signature
     * @param timestamp
     * @param nonce
     * @param postData
     */
    public void componentVerifyTicketHandle(String msgSignature,String signature ,String timestamp, String nonce,String postData ){

        Log.i("receive msgSignature :[?] ,signature:[?] ，timestamp[?] ,nonce:[?] , postData:[?] " , msgSignature,signature,timestamp,nonce,postData );

        Encryp encryp = XMLParse.extractEncryp(postData);

        String encryptStr = encryp.getEncrypt();

        String decryptStr = wxBizMsgCryptService.decrypt(encryptStr);

        //如果是取消授权
        if(decryptStr.indexOf("unauthorized") != -1){

            UnauthorizedResp unauthorizedResp = XmlUtil.converyToJavaBean(decryptStr,UnauthorizedResp.class);

            String wxPubOpenId = unauthorizedResp.getWxPubOpenId();

            wxPubAuthorizerRefreshTokenService.unAuthorized(wxPubOpenId);

            return ;
        }


        ComponentVerifyTicket componentVerifyTicket = XmlUtil.converyToJavaBean(decryptStr,ComponentVerifyTicket.class);

        if(componentAppId.equals(componentVerifyTicket.getAppId())){
            Log.i("appId of componentVerifyTicket Validated success ，" + componentVerifyTicket.toString());
            this.saveVerifyTicket(signature,timestamp,nonce,componentVerifyTicket.getTicket());
        }else {
            Log.e("appId of componentVerifyTicket Validated failed !" + componentVerifyTicket.toString());
        }
    }

    public void unauthorizedHandle(String decryptStr){
        String xmlStr = wxBizMsgCryptService.decrypt(decryptStr);
        UnauthorizedResp unauthorizedResp = XmlUtil.converyToJavaBean(xmlStr,UnauthorizedResp.class);

        String wxPubOpenId = unauthorizedResp.getWxPubOpenId();
        String appId = unauthorizedResp.getAppId();

        wxPubAuthorizerRefreshTokenService.unAuthorized(wxPubOpenId);
    }


    /**
     * @deprecated 已经被wxPubHepler中fetchPubBaseInfo代替
     * 获取公众号基本信息
     * @param componentAccessToken
     * @param componentAppId
     * @param authorizerAppId
     * @return
     */
    public PubBaseInfo fetchPubBaseInfo(String componentAccessToken ,String componentAppId ,String authorizerAppId){

        String url = this.getAuthorizerInfoUrl(componentAccessToken);

        Map<String,String> map = new HashMap<>();

        map.put("component_appid", componentAppId);
        map.put("authorizer_appid",authorizerAppId);

        String response = HttpsHelper.postJson(url, map);

        if(response.indexOf("errcode") != -1){
            return null;
        }

        PubBaseInfo pubBaseInfo = JsonUtil.readValue(response, PubBaseInfo.class);

        return pubBaseInfo;
    }

    private String getAuthorizerInfoUrl(String componentAccessToken){
        return this.replaceComponentAccessToken(FETCH_AUTHORIZER_INFO_URL, componentAccessToken);
    }


    public String getComponentVerifyTicket(){

        VerifyTicketInfo verifyTicketInfo = redisCacheTemplate.getObject(VERIFY_TICKER_INFO_CACHE_KEY);

        if(verifyTicketInfo == null){
            Log.e("verifyTicketInfo is null ,please check your system");
            return null;
        }

        return verifyTicketInfo.getTicket();
    }

    private String replaceComponentAppId(String string){
        return string.replace("#{component_appid}", componentAppId);
    }

    private String getPreAuthCodeUrl(String componentAccessToken){
        return this.replaceComponentAccessToken(PRE_AUTH_CODE_URL,componentAccessToken);
    }

    private String replaceComponentAccessToken(String string,String componentAccessToken){
        return string.replace("#{componentAccessToken}",componentAccessToken);
    }

    private String replacePreAuthCode(String string,String preAuthCode){
        return string.replace("#{pre_auth_code}",preAuthCode);
    }

    private String replaceRedirectUri(String string ,Integer userId){

        String userIdStr = String.valueOf(userId);
        String result = URLUtil.addParam(redirctUriPart,"user",userIdStr);
        return string.replace("#{redirect_uri}",result);
    }

    private String getAuthURL(String authCode){
        return this.replaceAuthCode(authCode);
    }

    private String replaceAuthCode(String authCode){
        return AUTH_URL.replace("#{authCode}", authCode);
    }

    private String getAuthorizerAccessTokenKey(String appId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "authorizer_access_token:" + appId;
    }

}
