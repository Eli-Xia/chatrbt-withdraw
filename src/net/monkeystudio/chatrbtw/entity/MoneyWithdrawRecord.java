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

    private String letterOfDelegationImgUrl;

    private Integer accountType;

    private String accountHolder;

    private String idNumber;

    private String accountName;

    private String bankName;

    private String province;

    private String city;

    private String bankForkName;

    private String accountNumber;

    private String email;

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

    public String getLetterOfDelegationImgUrl() {
        return letterOfDelegationImgUrl;
    }

    public void setLetterOfDelegationImgUrl(String letterOfDelegationImgUrl) {
        this.letterOfDelegationImgUrl = letterOfDelegationImgUrl;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBankForkName() {
        return bankForkName;
    }

    public void setBankForkName(String bankForkName) {
        this.bankForkName = bankForkName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}