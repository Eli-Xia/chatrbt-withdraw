package net.monkeystudio.wx.vo.pub.autoreply;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by bint on 2017/11/21.
 */
public class MessageDefaultAutoreplyInfo implements Serializable{

    @JsonProperty("type")
    private String type;

    @JsonProperty("content")
    private String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessageDefaultAutoreplyInfo{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
