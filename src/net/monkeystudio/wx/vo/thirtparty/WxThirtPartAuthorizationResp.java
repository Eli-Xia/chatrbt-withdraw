package net.monkeystudio.wx.vo.thirtparty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2017/10/27.
 */
public class WxThirtPartAuthorizationResp {

    @JsonProperty("authorization_info")
    private WxThirtyPartauthorizerInfo authorizationInfo;


    public WxThirtyPartauthorizerInfo getAuthorizationInfo() {
        return authorizationInfo;
    }

    public void setAuthorizationInfo(WxThirtyPartauthorizerInfo authorizationInfo) {
        this.authorizationInfo = authorizationInfo;
    }

    @Override
    public String toString() {
        return "WxThirtPartAuthorizationResp{" +
                "authorizationInfo=" + authorizationInfo +
                '}';
    }
}
