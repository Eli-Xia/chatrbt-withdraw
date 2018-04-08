package net.monkeystudio.portal.controller.resp.moneywithdraw;

import java.util.Date;

/**
 * @author xiaxin
 */
public class MoneyWithdrawDetail {

    private Float applyAmount = 0F;//提现金额

    private Integer state = 1 ;//提现进度

    private Date applyTime;//提现申请时间

    private Date remitTime;//打款时间

    public Float getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(Float applyAmount) {
        this.applyAmount = applyAmount;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getRemitTime() {
        return remitTime;
    }

    public void setRemitTime(Date remitTime) {
        this.remitTime = remitTime;
    }
}
