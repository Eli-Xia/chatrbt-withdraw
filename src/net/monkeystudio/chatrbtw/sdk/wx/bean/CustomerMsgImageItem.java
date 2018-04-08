package net.monkeystudio.chatrbtw.sdk.wx.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 28/12/2017.
 */
public class CustomerMsgImageItem {

    @JsonProperty("media_id")
    private String mediaId ;

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
}
