package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

public class ChatPetPersonalMission {
    private Integer id;

    private Integer missionCode;

    private Integer adId;

    private Integer chatPetId;

    private Date createTime;

    private Integer state;

    private Date finishTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMissionCode() {
        return missionCode;
    }

    public void setMissionCode(Integer missionCode) {
        this.missionCode = missionCode;
    }

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }

    public Integer getChatPetId() {
        return chatPetId;
    }

    public void setChatPetId(Integer chatPetId) {
        this.chatPetId = chatPetId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
}