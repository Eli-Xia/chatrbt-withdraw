package net.monkeystudio.chatpet.controller.req.chatpetmission;

/**
 * 领取奖励请求
 * @author xiaxin
 */
public class ChatPetRewardReq {
    private Integer rewardItemId;//奖励池itemId

    public Integer getRewardItemId() {
        return rewardItemId;
    }

    public void setRewardItemId(Integer rewardItemId) {
        this.rewardItemId = rewardItemId;
    }

}
