package net.monkeystudio.wx.vo.customerservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bint on 2017/12/8.
 */
@XStreamAlias("item")
public class CustomerNewsItem {

    @XStreamAlias("Title")
    private String title;

    @XStreamAlias("Description")
    private String description;

    @XStreamAlias("Url")
    private String url;

    @XStreamAlias("PicUrl")
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
