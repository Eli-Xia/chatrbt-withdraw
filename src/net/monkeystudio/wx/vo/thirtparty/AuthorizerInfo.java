package net.monkeystudio.wx.vo.thirtparty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2017/11/2.
 */
public class AuthorizerInfo {

    @JsonProperty("nick_name")
    private String nickName;

    @JsonProperty("head_img")
    private String headImg;

    @JsonProperty("service_type_info")
    private ServiceTypeInfo serviceTypeInfo;

    @JsonProperty("verify_type_info")
    private VerifyTypeInfo verifyTypeInfo;

    @JsonProperty("user_name")
    private String originId;

    @JsonProperty("idc")
    private String idc;

    @JsonProperty("principal_name")
    private String principalName;

    @JsonProperty("signature")
    private String signature;

    @JsonProperty("business_info")
    private BusinessInfo businessInfo;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("qrcode_url")
    private String qrcodeUrl;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public ServiceTypeInfo getServiceTypeInfo() {
        return serviceTypeInfo;
    }

    public void setServiceTypeInfo(ServiceTypeInfo serviceTypeInfo) {
        this.serviceTypeInfo = serviceTypeInfo;
    }

    public VerifyTypeInfo getVerifyTypeInfo() {
        return verifyTypeInfo;
    }

    public void setVerifyTypeInfo(VerifyTypeInfo verifyTypeInfo) {
        this.verifyTypeInfo = verifyTypeInfo;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public BusinessInfo getBusinessInfo() {
        return businessInfo;
    }

    public void setBusinessInfo(BusinessInfo businessInfo) {
        this.businessInfo = businessInfo;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public String getIdc() {
        return idc;
    }

    public void setIdc(String idc) {
        this.idc = idc;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
