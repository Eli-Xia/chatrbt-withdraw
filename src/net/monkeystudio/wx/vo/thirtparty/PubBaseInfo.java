package net.monkeystudio.wx.vo.thirtparty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2017/11/2.
 */
public class PubBaseInfo {

    @JsonProperty("authorizer_info")
    private AuthorizerInfo authorizerInfo;

    @JsonProperty("authorization_info")
    private AuthorizationInfo authorizationInfo;

    public AuthorizerInfo getAuthorizerInfo() {
        return authorizerInfo;
    }

    public void setAuthorizerInfo(AuthorizerInfo authorizerInfo) {
        this.authorizerInfo = authorizerInfo;
    }

    public AuthorizationInfo getAuthorizationInfo() {
        return authorizationInfo;
    }

    public void setAuthorizationInfo(AuthorizationInfo authorizationInfo) {
        this.authorizationInfo = authorizationInfo;
    }
}
