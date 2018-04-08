package net.monkeystudio.wx.vo.pub.autoreply;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by bint on 2017/11/21.
 */
public class ReplyListInfoItem implements Serializable{

    @JsonProperty("type")
    private String type;

    @JsonProperty("news_info")
    private NewsInfo newsInfo;

    @JsonProperty("content")
    private String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NewsInfo getNewsInfo() {
        return newsInfo;
    }

    public void setNewsInfo(NewsInfo newsInfo) {
        this.newsInfo = newsInfo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ReplyListInfoItem{" +
                "type='" + type + '\'' +
                ", newsInfo=" + newsInfo +
                ", content='" + content + '\'' +
                '}';
    }
}
