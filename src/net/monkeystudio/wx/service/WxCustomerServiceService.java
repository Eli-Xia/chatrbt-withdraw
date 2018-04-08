package net.monkeystudio.wx.service;

import net.monkeystudio.base.utils.HttpsHelper;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.wx.utils.WxApiUrlUtil;
import net.monkeystudio.wx.vo.customerservice.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by bint on 2017/11/5.
 */
@Service
public class WxCustomerServiceService {

    /*@Autowired
    private WxService wxService;

    @Autowired
    private WxAuthApiService wxAuthApiService;

    *//**
     * 发送文本信息给粉丝
     * @param openId 粉丝的openId
     * @param content 文本内容
     * @param accessToken 操作的accessToken
     * @return
     *//*
    public String sendTextMessage(String openId,String content ,String accessToken){

        Log.d("send Text message , content : ? ,and openId : ? , accessToken : ? ",content , openId , accessToken);
        
        CustomerMsgText customerMsgText = new CustomerMsgText(openId,content);

        String jsonStr = JsonUtil.toJSon(customerMsgText);

        String url = WxApiUrlUtil.getSendMessageUrl(accessToken);

        String response = HttpsHelper.postJsonByStr(url,jsonStr);

        Log.d("response of sendTextMessage : " + response);

        return response;
    }


    *//**
     * 发送文本信息给粉丝
     * @param openId 粉丝的openId
     * @param authorizerId 公众号的Id
     * @param content
     * @return
     *//*
    public String sendTextMessageByAuthorizerId(String openId,String authorizerId , String content){

        String accessToken = wxAuthApiService.getAuthorizerAccessToken(authorizerId);

        return this.sendTextMessage(openId, content, accessToken);
    }

    *//**
     * 发送公众号图文消息
     * @param openId
     * @param authorizerId
     * @param mediaId
     * @return
     *//*
    public String sendMpNews(String openId, String authorizerId, String mediaId){
    	
    	String accessToken = wxAuthApiService.getAuthorizerAccessToken(authorizerId);
    	
    	Log.d("send MpNews message , mediaId : ? ,and openId : ? , accessToken : ? ",mediaId , openId , accessToken);

        String jsonStr = getCustomerMsgMpNewsJson(openId, mediaId);

        String url = WxApiUrlUtil.getSendMessageUrl(accessToken);
        String response = HttpsHelper.postJsonByStr(url,jsonStr);
        Log.d("response of sendMpNewsMessage : " + response);

        return response;
    }

    *//**
     * 发送公众号外链图文消息
     * @param appId
     * @param wxFanOpenId
     * @param customerNewsItemList
     * @return
     *//*
    public String sendNews(String appId, String wxFanOpenId , List<CustomerNewsItem> customerNewsItemList){

        String accessToken = wxAuthApiService.getAuthorizerAccessToken(appId);

        CustomerNews customerNews = new CustomerNews();
        customerNews.setMsgtype("news");
        customerNews.setTouser(wxFanOpenId);

        WxNewsArticles wxNewsArticles = new WxNewsArticles();
        wxNewsArticles.setArticles(customerNewsItemList);

        customerNews.setArticles(wxNewsArticles);


        String json = JsonUtil.toJSon(customerNews);

        String url = WxApiUrlUtil.getSendMessageUrl(accessToken);

        String response = HttpsHelper.postJsonByStr(url,json);
        Log.d("response of sendMpNewsMessage : " + response);

        return response;
    }
    
    private String getCustomerMsgMpNewsJson(String openId, String mediaId){
    	
    	CustomerMsgMpNews customerMsgMpNews = new CustomerMsgMpNews(openId, mediaId);
        String jsonStr = JsonUtil.toJSon(customerMsgMpNews);
        
        return jsonStr;
    }
    
*/
}
