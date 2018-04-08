package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

public class MoneyWithdrawRecord {
    private Integer id;

    private Integer userId;

    private Date applyTime;

    private Float amount;

    private Integer state;

    private Date successTime;

    private String invoiceNumber;

    private Integer invoiceAuditRet;

    private Date invoiceReceiveTime;

    private String bankFlowNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(Date successTime) {
        this.successTime = successTime;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Integer getInvoiceAuditRet() {
        return invoiceAuditRet;
    }

    public void setInvoiceAuditRet(Integer invoiceAuditRet) {
        this.invoiceAuditRet = invoiceAuditRet;
    }

    public Date getInvoiceReceiveTime() {
        return invoiceReceiveTime;
    }

    public void setInvoiceReceiveTime(Date invoiceReceiveTime) {
        this.invoiceReceiveTime = invoiceReceiveTime;
    }

    public String getBankFlowNumber() {
        return bankFlowNumber;
    }

    public void setBankFlowNumber(String bankFlowNumber) {
        this.bankFlowNumber = bankFlowNumber;
    }
}