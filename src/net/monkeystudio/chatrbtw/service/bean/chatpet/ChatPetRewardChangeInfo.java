package net.monkeystudio.chatrbtw.service.bean.chatpet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class ChatPetRewardChangeInfo {
    private List<PetLogResp> petLogs = new ArrayList<>();

    private Float fanTotalCoin = 0F;

    private List<ChatPetGoldItem> goldItems = new ArrayList<>();

//    private ExperienceProgressRate experienceProgressRate;
//
//    private Float Experience = 0F;
//
//    private Integer chatPetLevel;
//
//    private TodayMission todayMission;
//
//    private ChatPetExperinceRank groupRank;
//
//
//    private MagicCoinCount magicCoinCount;

    public List<ChatPetGoldItem> getGoldItems() {
        return goldItems;
    }

    public void setGoldItems(List<ChatPetGoldItem> goldItems) {
        this.goldItems = goldItems;
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


//    public MagicCoinCount getMagicCoinCount() {
//        return magicCoinCount;
//    }
//
//    public void setMagicCoinCount(MagicCoinCount magicCoinCount) {
//        this.magicCoinCount = magicCoinCount;
//    }
//
//    public ChatPetExperinceRank getGroupRank() {
//        return groupRank;
//    }
//
//    public void setGroupRank(ChatPetExperinceRank groupRank) {
//        this.groupRank = groupRank;
//    }
//
//    public Float getExperience() {
//        return Experience;
//    }
//
//    public void setExperience(Float experience) {
//        Experience = experience;
//    }
//
//    public ExperienceProgressRate getExperienceProgressRate() {
//        return experienceProgressRate;
//    }
//
//    public void setExperienceProgressRate(ExperienceProgressRate experienceProgressRate) {
//        this.experienceProgressRate = experienceProgressRate;
//    }
//
//    public Integer getChatPetLevel() {
//        return chatPetLevel;
//    }
//
//    public void setChatPetLevel(Integer chatPetLevel) {
//        this.chatPetLevel = chatPetLevel;
//    }
//
//    public TodayMission getTodayMission() {
//        return todayMission;
//    }
//
//    public void setTodayMission(TodayMission todayMission) {
//        this.todayMission = todayMission;
//    }

}
