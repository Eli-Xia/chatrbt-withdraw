package net.monkeystudio.chatrbtw.entity;

public class ChatPetMission {
    private Integer id;

    private String missionName;

    private Integer missionCode;

    private Integer isActive;

    private Float coin;

    private Float experience;

    private Integer missionType;

    public Integer getMissionType() {
        return missionType;
    }

    public void setMissionType(Integer missionType) {
        this.missionType = missionType;
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

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName == null ? null : missionName.trim();
    }

    public Integer getMissionCode() {
        return missionCode;
    }

    public void setMissionCode(Integer missionCode) {
        this.missionCode = missionCode;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Float getCoin() {
        return coin;
    }

    public void setCoin(Float coin) {
        this.coin = coin;
    }
}