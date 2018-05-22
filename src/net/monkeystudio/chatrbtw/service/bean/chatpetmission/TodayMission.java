package net.monkeystudio.chatrbtw.service.bean.chatpetmission;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class TodayMission {
    List<TodayMissionItem> fixedMissionList = new ArrayList<>();
    List<TodayMissionItem> randomMissionList = new ArrayList<>();

    public List<TodayMissionItem> getFixedMissionList() {
        return fixedMissionList;
    }

    public void setFixedMissionList(List<TodayMissionItem> fixedMissionList) {
        this.fixedMissionList = fixedMissionList;
    }

    public List<TodayMissionItem> getRandomMissionList() {
        return randomMissionList;
    }

    public void setRandomMissionList(List<TodayMissionItem> randomMissionList) {
        this.randomMissionList = randomMissionList;
    }
}
