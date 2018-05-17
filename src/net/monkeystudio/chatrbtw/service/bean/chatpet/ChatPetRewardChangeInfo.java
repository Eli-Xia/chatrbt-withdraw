package net.monkeystudio.chatrbtw.service.bean.chatpet;

import net.monkeystudio.chatrbtw.service.bean.chatpetlevel.ExperienceProgressRate;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.TodayMissionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class ChatPetRewardChangeInfo {
    private List<PetLogResp> petLogs = new ArrayList<>();

    private Float fanTotalCoin = 0F;

    private Integer Experience = 0;

    private ExperienceProgressRate experienceProgressRate;

    private Integer chatPetLevel;

    private List<TodayMissionItem> todayMissions = new ArrayList<>();

    private ChatPetExperinceRank groupRank;

    private List<ChatPetGoldItem> goldItems = new ArrayList<>();

    public List<ChatPetGoldItem> getGoldItems() {
        return goldItems;
    }

    public void setGoldItems(List<ChatPetGoldItem> goldItems) {
        this.goldItems = goldItems;
    }

    public ChatPetExperinceRank getGroupRank() {
        return groupRank;
    }

    public void setGroupRank(ChatPetExperinceRank groupRank) {
        this.groupRank = groupRank;
    }

    public List<PetLogResp> getPetLogs() {
        return petLogs;
    }

    public void setPetLogs(List<PetLogResp> petLogs) {
        this.petLogs = petLogs;
    }

    public Float getFanTotalCoin() {
        return fanTotalCoin;
    }

    public void setFanTotalCoin(Float fanTotalCoin) {
        this.fanTotalCoin = fanTotalCoin;
    }

    public Integer getExperience() {
        return Experience;
    }

    public void setExperience(Integer experience) {
        Experience = experience;
    }

    public ExperienceProgressRate getExperienceProgressRate() {
        return experienceProgressRate;
    }

    public void setExperienceProgressRate(ExperienceProgressRate experienceProgressRate) {
        this.experienceProgressRate = experienceProgressRate;
    }

    public Integer getChatPetLevel() {
        return chatPetLevel;
    }

    public void setChatPetLevel(Integer chatPetLevel) {
        this.chatPetLevel = chatPetLevel;
    }

    public List<TodayMissionItem> getTodayMissions() {
        return todayMissions;
    }

    public void setTodayMissions(List<TodayMissionItem> todayMissions) {
        this.todayMissions = todayMissions;
    }
}
