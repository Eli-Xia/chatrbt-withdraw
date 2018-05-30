package net.monkeystudio.chatrbtw.service.bean.chatpet;

/**
 * Created by bint on 2018/5/29.
 */
public class MissionItem {
    private Integer missionType;
    private Integer finishTime;
    private Integer needToFinish;

    public Integer getMissionType() {
        return missionType;
    }

    public void setMissionType(Integer missionType) {
        this.missionType = missionType;
    }

    public Integer getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Integer finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getNeedToFinish() {
        return needToFinish;
    }

    public void setNeedToFinish(Integer needToFinish) {
        this.needToFinish = needToFinish;
    }
}
