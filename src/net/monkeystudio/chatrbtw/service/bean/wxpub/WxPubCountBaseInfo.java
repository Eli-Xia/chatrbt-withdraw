package net.monkeystudio.chatrbtw.service.bean.wxpub;

/**
 * Created by bint on 29/12/2017.
 */
public class WxPubCountBaseInfo {

    private String wxPubNickname;

    private String wxPubHeadImgUrl;

    private Long yesterdayChatMan;

    private Long yesterdayChatNum;

    //昨日收入
    private Float yesterdayIncome;

    public String getWxPubNickname() {
        return wxPubNickname;
    }

    public void setWxPubNickname(String wxPubNickname) {
        this.wxPubNickname = wxPubNickname;
    }

    public String getWxPubHeadImgUrl() {
        return wxPubHeadImgUrl;
    }

    public void setWxPubHeadImgUrl(String wxPubHeadImgUrl) {
        this.wxPubHeadImgUrl = wxPubHeadImgUrl;
    }

    public Long getYesterdayChatMan() {
        return yesterdayChatMan;
    }

    public void setYesterdayChatMan(Long yesterdayChatMan) {
        this.yesterdayChatMan = yesterdayChatMan;
    }

    public Long getYesterdayChatNum() {
        return yesterdayChatNum;
    }

    public void setYesterdayChatNum(Long yesterdayChatNum) {
        this.yesterdayChatNum = yesterdayChatNum;
    }

    public Float getYesterdayIncome() {
        return yesterdayIncome;
    }

    public void setYesterdayIncome(Float yesterdayIncome) {
        this.yesterdayIncome = yesterdayIncome;
    }
}
