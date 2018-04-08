package net.monkeystudio.chatrbtw.service.bean.chatlog;

import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by bint on 05/01/2018.
 */
public class ChatLogListItem {
    private String wxFanOpenId;

    private Date createTime;

    private String content;

    private Integer type;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWxFanOpenId() {
        return wxFanOpenId;
    }

    public void setWxFanOpenId(String wxFanOpenId) {
        this.wxFanOpenId = wxFanOpenId;
    }

}
