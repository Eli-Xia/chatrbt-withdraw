package net.monkeystudio.admin.controller.req.moneywithdraw;

/**
 * 确认打款
 * @author xiaxin
 */
public class RemitConfirmReq {
    private Integer moneyWithdrawRecordId;
    private String bankFlowNumber;

    public Integer getMoneyWithdrawRecordId() {
        return moneyWithdrawRecordId;
    }

    public void setMoneyWithdrawRecordId(Integer moneyWithdrawRecordId) {
        this.moneyWithdrawRecordId = moneyWithdrawRecordId;
    }


    public String getBankFlowNumber() {
        return bankFlowNumber;
    }

    public void setBankFlowNumber(String bankFlowNumber) {
        this.bankFlowNumber = bankFlowNumber;
    }
}
