package net.monkeystudio.chatrbtw.service.bean.moneywithdraw;

import net.monkeystudio.chatrbtw.entity.MoneyWithdrawRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class MoneyWithdrawIncomeMgrResp {
    private Float availableAmount = 0F;//用户可提现金额

    private Float alreadyWithdrawAmount = 0F;//用户已提现金额

    private Float totalAmount = 0F;//用户总收益

    private Integer switchVal ;

    private Float minAmount = 0F;//最小提现金额

    private List<MoneyWithdrawRecord> moneyWithdrawRecordList = new ArrayList<>();//提现历史记录

    public Float getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(Float availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Float getAlreadyWithdrawAmount() {
        return alreadyWithdrawAmount;
    }

    public void setAlreadyWithdrawAmount(Float alreadyWithdrawAmount) {
        this.alreadyWithdrawAmount = alreadyWithdrawAmount;
    }

    public Float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<MoneyWithdrawRecord> getMoneyWithdrawRecordList() {
        return moneyWithdrawRecordList;
    }

    public void setMoneyWithdrawRecordList(List<MoneyWithdrawRecord> moneyWithdrawRecordList) {
        this.moneyWithdrawRecordList = moneyWithdrawRecordList;
    }

    public Integer getSwitchVal() {
        return switchVal;
    }

    public void setSwitchVal(Integer switchVal) {
        this.switchVal = switchVal;
    }

    public Float getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Float minAmount) {
        this.minAmount = minAmount;
    }
}
