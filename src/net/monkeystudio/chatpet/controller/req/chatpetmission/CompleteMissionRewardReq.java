package net.monkeystudio.chatpet.controller.req.chatpetmission;

/**
 * 完成每日任务领取奖励请求
 * @author xiaxin
 */
public class CompleteMissionRewardReq {
    private Integer itemId;//任务池记录id


    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
