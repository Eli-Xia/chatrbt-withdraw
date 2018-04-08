package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

public class ChatLog {
    private Integer id;

    private String wxPubOriginId;

    private String userOpenid;

    private Date createTime;

    private Integer replyId;
    
    private String replySrc;

    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWxPubOriginId() {
        return wxPubOriginId;
    }

    public void setWxPubOriginId(String wxPubOriginId) {
        this.wxPubOriginId = wxPubOriginId == null ? null : wxPubOriginId.trim();
    }

    public String getUserOpenid() {
        return userOpenid;
    }

    public void setUserOpenid(String userOpenid) {
        this.userOpenid = userOpenid == null ? null : userOpenid.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getReplyId() {
        return replyId;
    }

    public void setReplyId(Integer replyId) {
        this.replyId = replyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

	public String getReplySrc() {
		return replySrc;
	}

	public void setReplySrc(String replySrc) {
		this.replySrc = replySrc;
	}
}