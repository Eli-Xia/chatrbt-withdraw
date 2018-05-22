package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

public class ChatPetRewardItem {
    private Integer id;

    private Float goldValue;

    private Float experience;

    private Integer rewardState;

    private Integer missionItemId;

    private Integer chatPetId;

    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Float getExperience() {
        return experience;
    }

    public void setExperience(Float experience) {
        this.experience = experience;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getGoldValue() {
        return goldValue;
    }

    public void setGoldValue(Float goldValue) {
        this.goldValue = goldValue;
    }

    public Integer getRewardState() {
        return rewardState;
    }

    public void setRewardState(Integer rewardState) {
        this.rewardState = rewardState;
    }

    public Integer getMissionItemId() {
        return missionItemId;
    }

    public void setMissionItemId(Integer missionItemId) {
        this.missionItemId = missionItemId;
    }

    public Integer getChatPetId() {
        return chatPetId;
    }

    public void setChatPetId(Integer chatPetId) {
        this.chatPetId = chatPetId;
    }
}