package net.monkeystudio.chatrbtw.sdk.wx.bean.miniapp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2018/8/3.
 */
public class MiniProgramAccessToken {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
