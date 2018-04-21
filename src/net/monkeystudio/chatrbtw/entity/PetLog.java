package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

public class PetLog {
    private Integer id;

    private Date createTime;

    private Float coin;

    private Integer chatPetId;

    private String wxPubOriginId;

    private String wxFanOpenId;

    private String content;

    private Integer taskCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Float getCoin() {
        return coin;
    }

    public void setCoin(Float coin) {
        this.coin = coin;
    }

    public Integer getChatPetId() {
        return chatPetId;
    }

    public void setChatPetId(Integer chatPetId) {
        this.chatPetId = chatPetId;
    }

    public String getWxPubOriginId() {
        return wxPubOriginId;
    }

    public void setWxPubOriginId(String wxPubOriginId) {
        this.wxPubOriginId = wxPubOriginId == null ? null : wxPubOriginId.trim();
    }

    public String getWxFanOpenId() {
        return wxFanOpenId;
    }

    public void setWxFanOpenId(String wxFanOpenId) {
        this.wxFanOpenId = wxFanOpenId == null ? null : wxFanOpenId.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(Integer taskCode) {
        this.taskCode = taskCode;
    }
}