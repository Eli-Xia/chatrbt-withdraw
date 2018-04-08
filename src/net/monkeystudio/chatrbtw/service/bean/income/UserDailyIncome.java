package net.monkeystudio.chatrbtw.service.bean.income;

import java.util.List;

/**
 * Created by bint on 14/03/2018.
 */
public class UserDailyIncome {
    private Float yesterdayIncome;

    private List<UserDaliyInocmeOverview> userDaliyInocmeOverviewList;

    public Float getYesterdayIncome() {
        return yesterdayIncome;
    }

    public void setYesterdayIncome(Float yesterdayIncome) {
        this.yesterdayIncome = yesterdayIncome;
    }

    public List<UserDaliyInocmeOverview> getUserDaliyInocmeOverviewList() {
        return userDaliyInocmeOverviewList;
    }

    public void setUserDaliyInocmeOverviewList(List<UserDaliyInocmeOverview> userDaliyInocmeOverviewList) {
        this.userDaliyInocmeOverviewList = userDaliyInocmeOverviewList;
    }
}
