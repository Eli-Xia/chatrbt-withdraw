package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

public class RMiniGameTag {
    private Integer id;

    private Integer miniGameId;

    private Integer tagId;

    private Date onlineTime;

    private Integer needSign;

    private Integer shelveState;

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Integer getNeedSign() {
        return needSign;
    }

    public void setNeedSign(Integer needSign) {
        this.needSign = needSign;
    }

    public Integer getShelveState() {
        return shelveState;
    }

    public void setShelveState(Integer shelveState) {
        this.shelveState = shelveState;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMiniGameId() {
        return miniGameId;
    }

    public void setMiniGameId(Integer miniGameId) {
        this.miniGameId = miniGameId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }
}