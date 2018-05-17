package net.monkeystudio.chatrbtw.service.bean.chatpet;

/**
 * Created by bint on 2018/5/17.
 */
public class CreationPost {
    private Integer chatPetType;
    private String wxPubQrCode;

    public Integer getChatPetType() {
        return chatPetType;
    }

    public void setChatPetType(Integer chatPetType) {
        this.chatPetType = chatPetType;
    }

    public String getWxPubQrCode() {
        return wxPubQrCode;
    }

    public void setWxPubQrCode(String wxPubQrCode) {
        this.wxPubQrCode = wxPubQrCode;
    }
}
