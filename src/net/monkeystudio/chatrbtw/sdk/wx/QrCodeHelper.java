package net.monkeystudio.chatrbtw.sdk.wx;

import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.chatrbtw.sdk.wx.bean.qrcode.ActionInfo;
import net.monkeystudio.chatrbtw.sdk.wx.bean.qrcode.CreateQrCode;
import net.monkeystudio.chatrbtw.sdk.wx.bean.qrcode.Scene;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.utils.HttpUtils;
import net.monkeystudio.wx.service.WxAuthApiService;
import net.monkeystudio.wx.utils.WxApiUrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2018/4/12.
 */
@Service
public class QrCodeHelper {

    @Autowired
    private WxAuthApiService wxAuthApiService;

    private String createQrCode(String accessToken ,Integer expireSeconds , String actionName ,String sceneStr) throws BizException {

        String url = WxApiUrlUtil.getCreateTempQrCodeUrl(accessToken);

        CreateQrCode createQrCode = new CreateQrCode();

        createQrCode.setExpireSeconds(expireSeconds);
        createQrCode.setActionName(actionName);

        ActionInfo actionInfo = new ActionInfo();

        Scene scene = new Scene();
        scene.setSceneStr(sceneStr);

        actionInfo.setScene(scene);

        createQrCode.setActionInfo(actionInfo);

        String json = JsonUtil.toJSon(createQrCode);

        String response = HttpUtils.postJson(url,json);
        return response;
    }


    /*private String createQrCodeByWxAppId(String wxPubAppId ,Integer expireSeconds , String actionName ,String sceneStr) throws BizException {
        String accessToken = wxAuthApiService.getAuthorizerAccessToken(wxPubAppId);
        return this.createQrCode(accessToken ,expireSeconds , actionName ,sceneStr);
    }*/

    /**
     * 创建二维码
     * @param wxPubOriginId
     * @param expireSeconds
     * @param qrCodeType
     * @param sceneStr
     * @return
     * @throws BizException
     */
    public String createQrCodeByWxPubOriginId(String wxPubOriginId ,Integer expireSeconds , QrCodeType qrCodeType, String sceneStr) throws BizException {
        String accessToken = wxAuthApiService.getWxPubAccessTokenByOriginId(wxPubOriginId);
        return this.createQrCode(accessToken, expireSeconds, qrCodeType.getType(), sceneStr);
    }


    public enum QrCodeType{
        TEMP("QR_SCENE"),PERPETUAL("QR_LIMIT_STR_SCENE");

        private String type;

        QrCodeType(String type){
            this.type = type;
        }

        public String getType(){
            return type;
        }
    }
}
