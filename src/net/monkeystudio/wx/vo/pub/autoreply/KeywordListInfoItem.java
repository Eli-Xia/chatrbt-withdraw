package net.monkeystudio.wx.vo.pub.autoreply;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by bint on 2017/11/21.
 */
public class KeywordListInfoItem implements Serializable{

    @JsonProperty("type")
    private String type;

    @JsonProperty("match_mode")
    private String matchMode;

    @JsonProperty("content")
    private String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMatchMode() {
        return matchMode;
    }

    public void setMatchMode(String matchMode) {
        this.matchMode = matchMode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "KeywordListInfoItem{" +
                "type='" + type + '\'' +
                ", matchMode='" + matchMode + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
