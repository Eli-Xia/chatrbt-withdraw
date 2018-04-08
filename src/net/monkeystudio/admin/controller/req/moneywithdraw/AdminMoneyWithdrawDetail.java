package net.monkeystudio.admin.controller.req.moneywithdraw;

/**
 * @author xiaxin
 */
public class AdminMoneyWithdrawDetail {

    private String nickname;

    private Float amount;

    private Integer accountType;

    private String accountHolder;

    private String idNumber;

    private String accountName;

    private String bankName;

    private String location;

    private String bankForkName;

    private String accountNumber;

    private String email;

    private Integer invoiceRet;

    private String invoiceNumber;

    private String BankFlowNumber;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public Integer getInvoiceRet() {
        return invoiceRet;
    }

    public void setInvoiceRet(Integer invoiceRet) {
        this.invoiceRet = invoiceRet;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getBankFlowNumber() {
        return BankFlowNumber;
    }

    public void setBankFlowNumber(String bankFlowNumber) {
        BankFlowNumber = bankFlowNumber;
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
}
