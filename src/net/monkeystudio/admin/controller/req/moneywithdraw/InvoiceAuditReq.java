package net.monkeystudio.admin.controller.req.moneywithdraw;

import java.util.Date;

/**
 * 发票审核
 * @author xiaxin
 */
public class InvoiceAuditReq {
    private Integer moneyWithdrawRecordId;//提现记录id
    private Integer ret;
    private String invoiceNumber;

    public Integer getRet() {
        return ret;
    }

    public void setRet(Integer ret) {
        this.ret = ret;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Integer getMoneyWithdrawRecordId() {
        return moneyWithdrawRecordId;
    }

    public void setMoneyWithdrawRecordId(Integer moneyWithdrawRecordId) {
        this.moneyWithdrawRecordId = moneyWithdrawRecordId;
    }
}
