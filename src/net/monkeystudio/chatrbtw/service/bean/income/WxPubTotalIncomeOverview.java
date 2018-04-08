package net.monkeystudio.chatrbtw.service.bean.income;

import java.util.List;

/**
 * 微信公众号的详细总收入
 * Created by bint on 20/03/2018.
 */
public class WxPubTotalIncomeOverview {
    private List<WxPubAdIncomeOverview> wxPubAdIncomeOverviewList;
    private String wxPubNickName;

    public List<WxPubAdIncomeOverview> getWxPubAdIncomeOverviewList() {
        return wxPubAdIncomeOverviewList;
    }

    public void setWxPubAdIncomeOverviewList(List<WxPubAdIncomeOverview> wxPubAdIncomeOverviewList) {
        this.wxPubAdIncomeOverviewList = wxPubAdIncomeOverviewList;
    }

    public String getWxPubNickName() {
        return wxPubNickName;
    }

    public void setWxPubNickName(String wxPubNickName) {
        this.wxPubNickName = wxPubNickName;
    }
}
