package net.monkeystudio.chatrbtw.service.bean.income;

/**
 * Created by bint on 13/03/2018.
 */
public class WxPubDailyIncomeOverview {
    private String wxPubName;
    private Float wxDailyIncome;
    private String wxPubHeadImgUrl;

    public String getWxPubName() {
        return wxPubName;
    }

    public void setWxPubName(String wxPubName) {
        this.wxPubName = wxPubName;
    }

    public Float getWxDailyIncome() {
        return wxDailyIncome;
    }

    public void setWxDailyIncome(Float wxDailyIncome) {
        this.wxDailyIncome = wxDailyIncome;
    }

    public String getWxPubHeadImgUrl() {
        return wxPubHeadImgUrl;
    }

    public void setWxPubHeadImgUrl(String wxPubHeadImgUrl) {
        this.wxPubHeadImgUrl = wxPubHeadImgUrl;
    }
}
