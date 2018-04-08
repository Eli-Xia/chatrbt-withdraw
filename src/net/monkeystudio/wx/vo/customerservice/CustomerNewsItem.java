package net.monkeystudio.wx.vo.customerservice;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2017/12/8.
 */
public class CustomerNewsItem {

    private String title;
    private String description;
    private String url;

    @JsonProperty("picurl")
    private String picUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
