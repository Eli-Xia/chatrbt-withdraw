package net.monkeystudio.chatrbtw.service.bean.chatpetmission;

/**
 * 派发任务参数
 * Created by bint on 2018/5/29.
 */
public class DispatchMissionParam {
    private Integer missionCode;
    private Integer chatPetId;
    private Integer adId;

    public Integer getMissionCode() {
        return missionCode;
    }

    public void setMissionCode(Integer missionCode) {
        this.missionCode = missionCode;
    }

    public Integer getChatPetId() {
        return chatPetId;
    }

    public void setChatPetId(Integer chatPetId) {
        this.chatPetId = chatPetId;
    }

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }
}
