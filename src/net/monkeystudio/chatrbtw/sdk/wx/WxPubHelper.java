package net.monkeystudio.chatrbtw.sdk.wx;

import net.monkeystudio.base.utils.HttpsHelper;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.wx.service.WxAuthApiService;
import net.monkeystudio.wx.service.WxPubService;
import net.monkeystudio.wx.utils.WxApiUrlUtil;
import net.monkeystudio.wx.vo.thirtparty.PubBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bint on 19/03/2018.
 */
@Service
public class WxPubHelper {

    @Autowired
    private WxAuthApiService wxAuthApiService;

    @Autowired
    private WxPubService wxPubService;

    /**
     * 获取公众号基本信息
     * @param componentAccessToken
     * @param componentAppId
     * @param authorizerAppId
     * @return
     */
    public PubBaseInfo fetchPubBaseInfo(String componentAccessToken , String componentAppId , String authorizerAppId){

        String url = WxApiUrlUtil.getAuthorizerInfoUrl(componentAccessToken);

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

    /**
     * 获取公众号基本信息
     * @param wxPubOriginId
     * @return
     * @throws BizException
     */
    public PubBaseInfo fetchPubBaseInfo(String wxPubOriginId) throws BizException {
        String componentAccessToken = wxAuthApiService.getComponentAccessTokenStr();

        String wxPubAppId =  wxPubService.getWxPubAppIdByOrginId(wxPubOriginId);

        String componentAppId = wxAuthApiService.getComponentAppId();

        return this.fetchPubBaseInfo(componentAccessToken, componentAppId, wxPubAppId);
    }

}
