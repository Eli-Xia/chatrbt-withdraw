package net.monkeystudio.wx.vo.thirtparty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2017/11/2.
 */
public class ConfirmInfo {

    @JsonProperty("need_confirm")
    private String needConfirm;

    @JsonProperty("already_confirm")
    private String alreadyConfirm;

    @JsonProperty("can_confirm")
    private String canConfirm;

    public String getNeedConfirm() {
        return needConfirm;
    }

    public void setNeedConfirm(String needConfirm) {
        this.needConfirm = needConfirm;
    }

    public String getAlreadyConfirm() {
        return alreadyConfirm;
    }

    public void setAlreadyConfirm(String alreadyConfirm) {
        this.alreadyConfirm = alreadyConfirm;
    }

    public String getCanConfirm() {
        return canConfirm;
    }

    public void setCanConfirm(String canConfirm) {
        this.canConfirm = canConfirm;
    }
}
