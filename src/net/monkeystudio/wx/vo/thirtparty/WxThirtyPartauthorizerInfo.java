package net.monkeystudio.wx.vo.thirtparty;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by bint on 2017/10/27.
 */
public class WxThirtyPartauthorizerInfo {

    @JsonProperty("authorizer_appid")
    private String authorizerAppId;

    @JsonProperty("authorizer_access_token")
    private String authorizerAccessToken;

    @JsonProperty("expires_in")
    private String expiresIn;

    @JsonProperty("authorizer_refresh_token")
    private String authorizerRefreshToken;

    @JsonProperty("func_info")
    private List<FuncInfoItem> funcscopeCategory;

    public String getAuthorizerAppId() {
        return authorizerAppId;
    }

    public void setAuthorizerAppId(String authorizerAppId) {
        this.authorizerAppId = authorizerAppId;
    }

    public String getAuthorizerAccessToken() {
        return authorizerAccessToken;
    }

    public void setAuthorizerAccessToken(String authorizerAccessToken) {
        this.authorizerAccessToken = authorizerAccessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAuthorizerRefreshToken() {
        return authorizerRefreshToken;
    }

    public void setAuthorizerRefreshToken(String authorizerRefreshToken) {
        this.authorizerRefreshToken = authorizerRefreshToken;
    }


    public List<FuncInfoItem> getFuncscopeCategory() {
        return funcscopeCategory;
    }

    public void setFuncscopeCategory(List<FuncInfoItem> funcscopeCategory) {
        this.funcscopeCategory = funcscopeCategory;
    }


    @Override
    public String toString() {
        return "WxThirtyPartauthorizerInfo{" +
                "authorizerAppId='" + authorizerAppId + '\'' +
                ", authorizerAccessToken='" + authorizerAccessToken + '\'' +
                ", expiresIn='" + expiresIn + '\'' +
                ", authorizerRefreshToken='" + authorizerRefreshToken + '\'' +
                ", funcscopeCategory=" + funcscopeCategory +
                '}';
    }
}
