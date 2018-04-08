package net.monkeystudio.wx.vo.pub.autoreply;

import java.io.Serializable;

/**
 * Created by bint on 2017/11/20.
 */
public class AddFriendAutoreplyInfo implements Serializable{

    private String text;
    private String content;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
