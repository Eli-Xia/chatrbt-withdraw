package net.monkeystudio.chatrbtw.service.bean.income;

import java.util.Date;
import java.util.List;

/**
 * Created by bint on 13/03/2018.
 */
public class UserDailyIncomeDetail {

    private List<WxPubDailyIncomeOverview> wxPubDailyIncomeOverviewList;
    private Float dailySumIncome;
    private Date date;

    public List<WxPubDailyIncomeOverview> getWxPubDailyIncomeOverviewList() {
        return wxPubDailyIncomeOverviewList;
    }

    public void setWxPubDailyIncomeOverviewList(List<WxPubDailyIncomeOverview> wxPubDailyIncomeOverviewList) {
        this.wxPubDailyIncomeOverviewList = wxPubDailyIncomeOverviewList;
    }

    public Float getDailySumIncome() {
        return dailySumIncome;
    }

    public void setDailySumIncome(Float dailySumIncome) {
        this.dailySumIncome = dailySumIncome;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
