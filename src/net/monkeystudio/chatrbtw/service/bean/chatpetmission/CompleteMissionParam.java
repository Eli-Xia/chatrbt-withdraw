package net.monkeystudio.chatrbtw.service.bean.chatpetmission;

/**
 * 完成任务对象
 * @author xiaxin
 */
public class CompleteMissionParam {
    private Integer chatPetId;//宠物id
    private Integer missionCode;//任务类型
    private Integer adId;//资讯任务对应广告id
    private Integer inviteeWxFanId;//被邀请人微信粉丝id

    public Integer getChatPetId() {
        return chatPetId;
    }

    public void setChatPetId(Integer chatPetId) {
        this.chatPetId = chatPetId;
    }

    public Integer getMissionCode() {
        return missionCode;
    }

    public void setMissionCode(Integer missionCode) {
        this.missionCode = missionCode;
    }

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }

    public Integer getInviteeWxFanId() {
        return inviteeWxFanId;
    }

    public void setInviteeWxFanId(Integer inviteeWxFanId) {
        this.inviteeWxFanId = inviteeWxFanId;
    }
}
