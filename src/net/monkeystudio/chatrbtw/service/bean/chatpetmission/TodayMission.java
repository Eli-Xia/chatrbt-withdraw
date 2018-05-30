package net.monkeystudio.chatrbtw.service.bean.chatpetmission;

import net.monkeystudio.chatrbtw.service.bean.chatpet.MissionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class TodayMission {
    /*List<TodayMissionItem> fixedMissionList = new ArrayList<>();
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
    }*/

    private MissionItem dailyInteraction;
    private MissionItem inviteFriend;
    private MissionItem news;

    public MissionItem getDailyInteraction() {
        return dailyInteraction;
    }

    public void setDailyInteraction(MissionItem dailyInteraction) {
        this.dailyInteraction = dailyInteraction;
    }

    public MissionItem getInviteFriend() {
        return inviteFriend;
    }

    public void setInviteFriend(MissionItem inviteFriend) {
        this.inviteFriend = inviteFriend;
    }

    public MissionItem getNews() {
        return news;
    }

    public void setNews(MissionItem news) {
        this.news = news;
    }
}
