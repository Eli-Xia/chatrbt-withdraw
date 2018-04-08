package net.monkeystudio.wx.vo.thirtparty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2017/11/3.
 */
public class BusinessInfo {

    @JsonProperty("open_store")
    private String openStore;

    @JsonProperty("open_scan")
    private String openScan;

    @JsonProperty("open_pay")
    private String openPay;

    @JsonProperty("open_card")
    private String openCard;

    @JsonProperty("open_shake")
    private String openShake;

    public String getOpenStore() {
        return openStore;
    }

    public void setOpenStore(String openStore) {
        this.openStore = openStore;
    }

    public String getOpenScan() {
        return openScan;
    }

    public void setOpenScan(String openScan) {
        this.openScan = openScan;
    }

    public String getOpenPay() {
        return openPay;
    }

    public void setOpenPay(String openPay) {
        this.openPay = openPay;
    }

    public String getOpenCard() {
        return openCard;
    }

    public void setOpenCard(String openCard) {
        this.openCard = openCard;
    }

    public String getOpenShake() {
        return openShake;
    }

    public void setOpenShake(String openShake) {
        this.openShake = openShake;
    }
}
