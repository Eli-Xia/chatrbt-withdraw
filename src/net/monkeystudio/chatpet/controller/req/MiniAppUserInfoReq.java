package net.monkeystudio.chatpet.controller.req;

/**
 * @author xiaxin
 */
public class MiniAppUserInfoReq {
    private String rawData;
    private String encryptedData;
    private String iv;
    private String signature;
    private Integer parentFanId;

    public Integer getParentFanId() {
        return parentFanId;
    }

    public void setParentFanId(Integer parentFanId) {
        this.parentFanId = parentFanId;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
