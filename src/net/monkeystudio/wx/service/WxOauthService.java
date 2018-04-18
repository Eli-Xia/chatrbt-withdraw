package net.monkeystudio.wx.service;

import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author xiaxin
 */
@Service
public class WxOauthService {
    /*
    *
    *
    *       流程: 1,调用获取code接口需要在微信端打开  拼接url 然后重定向过去
	 * 		2, 提供一个redirect回调接口, 微信会把code,state重定向到此接口
	 * 		3, 用code获取access_token
	 *
	 * 	appid	是	公众号的appid
	 	redirect_uri	是	重定向地址，需要urlencode，这里填写的应是服务开发方的回调地址
	 	response_type	是	填code
	 	scope	是	授权作用域，拥有多个作用域用逗号（,）分隔
	 	state	否	重定向后会带上state参数，开发者可以填写任意参数值，最多128字节
	 	component_appid	是	服务方的appid，在申请创建公众号服务成功后，可在公众号服务详情页找到

	 https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE&component_appid=component_appid#wechat_redirect
	 *
	 * snsapi_base
    * */


    /**
     * 生成用于获取access_token的Code的Url
     *
     * @param redirectUrl
     * @return
     */
    public String getRequestCodeUrl(String redirectUrl,String wxPubAppId) throws Exception {
        return String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                wxPubAppId, URLEncoder.encode(redirectUrl,"UTF-8"), "snsapi_base", "123");
    }

}

