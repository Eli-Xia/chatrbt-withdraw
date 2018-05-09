package net.monkeystudio.chatrbtw.service.bean.chatpetmission;

import java.util.Date;

/**
 * @author xiaxin
 */
public class TodayMissionItem {
    private Integer itemId;//任务池中记录id
    private String missionName;//任务名称
    private Integer state;//状态 是否领取

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
