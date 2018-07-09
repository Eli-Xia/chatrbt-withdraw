package net.monkeystudio.chatrbtw.service.bean.miniapp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author xiaxin
 */
public class Watermark {
    @JsonProperty("appid")
    private String appId;
    @JsonProperty("timestamp")
    private Long timestamp;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
