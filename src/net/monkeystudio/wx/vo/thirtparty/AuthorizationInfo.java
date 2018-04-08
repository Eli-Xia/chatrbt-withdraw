package net.monkeystudio.wx.vo.thirtparty;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by bint on 2017/11/2.
 */
public class AuthorizationInfo {
    @JsonProperty("authorizer_appid")
    private String authorizerSppid;

    @JsonProperty("func_info")
    private List<FuncInfoItem> funcInfo;

    @JsonProperty("authorizer_refresh_token")
    private String authorizerRefreshToken;

    public String getAuthorizerSppid() {
        return authorizerSppid;
    }

    public void setAuthorizerSppid(String authorizerSppid) {
        this.authorizerSppid = authorizerSppid;
    }

    public List<FuncInfoItem> getFuncInfo() {
        return funcInfo;
    }

    public void setFuncInfo(List<FuncInfoItem> funcInfo) {
        this.funcInfo = funcInfo;
    }

    public String getAuthorizerRefreshToken() {
        return authorizerRefreshToken;
    }

    public void setAuthorizerRefreshToken(String authorizerRefreshToken) {
        this.authorizerRefreshToken = authorizerRefreshToken;
    }
}
