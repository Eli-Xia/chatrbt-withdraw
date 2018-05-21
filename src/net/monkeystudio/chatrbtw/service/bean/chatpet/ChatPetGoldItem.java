package net.monkeystudio.chatrbtw.service.bean.chatpet;

/**
 * 金币奖励展示
 * @author xiaxin
 */
public class ChatPetGoldItem {

    private Integer rewardItemId;
    private Float goldValue;
    private Integer missionItemId;

    public Integer getRewardItemId() {
        return rewardItemId;
    }

    public void setRewardItemId(Integer rewardItemId) {
        this.rewardItemId = rewardItemId;
    }

    public Float getGoldValue() {
        return goldValue;
    }

    public void setGoldValue(Float goldValue) {
        this.goldValue = goldValue;
    }

    public Integer getMissionItemId() {
        return missionItemId;
    }

    public void setMissionItemId(Integer missionItemId) {
        this.missionItemId = missionItemId;
    }

}
