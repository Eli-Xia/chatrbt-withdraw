package net.monkeystudio.chatrbtw.service.bean.chatpetlog;

/**
 * 保存宠物日志参数
 * @author xiaxin
 */
public class SaveChatPetLogParam {
    private Integer chatPetRewardItemId;
    private Boolean isUpgrade;//任务类型日志需要传
    private Integer chatPetId;
    private Integer chatPetLogType;//宠物日志类型

    public Integer getChatPetRewardItemId() {
        return chatPetRewardItemId;
    }

    public void setChatPetRewardItemId(Integer chatPetRewardItemId) {
        this.chatPetRewardItemId = chatPetRewardItemId;
    }

    public Boolean getUpgrade() {
        return isUpgrade;
    }

    public void setUpgrade(Boolean upgrade) {
        isUpgrade = upgrade;
    }

    public Integer getChatPetId() {
        return chatPetId;
    }

    public void setChatPetId(Integer chatPetId) {
        this.chatPetId = chatPetId;
    }

    public Integer getChatPetLogType() {
        return chatPetLogType;
    }

    public void setChatPetLogType(Integer chatPetLogType) {
        this.chatPetLogType = chatPetLogType;
    }
}
