package net.monkeystudio.chatpet.controller.req;

/**
 * @author xiaxin
 */
public class MiniAppUserInfoReq {
    private String encryptedData;
    private String iv;
    private Integer miniProgramId;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getMiniProgramId() {
        return miniProgramId;
    }

    public void setMiniProgramId(Integer miniProgramId) {
        this.miniProgramId = miniProgramId;
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

}
