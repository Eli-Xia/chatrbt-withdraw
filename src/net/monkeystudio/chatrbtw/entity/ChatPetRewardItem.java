package net.monkeystudio.chatrbtw.entity;

public class ChatPetRewardItem {
    private Integer id;

    private Integer goldValue;

    private Integer rewardState;

    private Integer missionItemId;

    private Integer chatPetId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGoldValue() {
        return goldValue;
    }

    public void setGoldValue(Integer goldValue) {
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