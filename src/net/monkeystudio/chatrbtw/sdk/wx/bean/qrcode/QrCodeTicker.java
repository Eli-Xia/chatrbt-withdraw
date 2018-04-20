package net.monkeystudio.chatrbtw.sdk.wx.bean.qrcode;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2018/4/13.
 */
public class QrCodeTicker {

    private String ticket;

    @JsonProperty("expire_seconds")
    private String expireSeconds;
    private String url;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(String expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
