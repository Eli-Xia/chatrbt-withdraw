package net.monkeystudio.chatrbtw.service.bean.income;

import java.util.List;

/**
 * Created by bint on 20/03/2018.
 */
public class UserTotalIncomeDetail {
    private Double userTotal;

    private List<WxPubTotalIncomeOverview> wxPubTotalIncomeOverviewList;

    public Double getUserTotal() {
        return userTotal;
    }

    public void setUserTotal(Double userTotal) {
        this.userTotal = userTotal;
    }

    public List<WxPubTotalIncomeOverview> getWxPubTotalIncomeOverviewList() {
        return wxPubTotalIncomeOverviewList;
    }

    public void setWxPubTotalIncomeOverviewList(List<WxPubTotalIncomeOverview> wxPubTotalIncomeOverviewList) {
        this.wxPubTotalIncomeOverviewList = wxPubTotalIncomeOverviewList;
    }
}
