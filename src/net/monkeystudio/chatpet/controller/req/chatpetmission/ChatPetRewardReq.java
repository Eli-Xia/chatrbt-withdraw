package net.monkeystudio.chatpet.controller.req.chatpetmission;

/**
 * 领取奖励请求
 * @author xiaxin
 */
public class ChatPetRewardReq {
    private Integer rewardItemId;//奖励池itemId
    private Integer missionItemId;//任务池itemId

    public Integer getRewardItemId() {
        return rewardItemId;
    }

    public void setRewardItemId(Integer rewardItemId) {
        this.rewardItemId = rewardItemId;
    }

    public Integer getMissionItemId() {
        return missionItemId;
    }

    public void setMissionItemId(Integer missionItemId) {
        this.missionItemId = missionItemId;
    }
}
