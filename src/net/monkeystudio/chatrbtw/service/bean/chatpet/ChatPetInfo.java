package net.monkeystudio.chatrbtw.service.bean.chatpet;

/**
 * Created by bint on 2018/4/19.
 */
public class ChatPetInfo {


    private Integer tempAppearance;

    private OwnerInfo ownerInfo;

    public Integer getTempAppearance() {
        return tempAppearance;
    }

    public void setTempAppearance(Integer tempAppearance) {
        this.tempAppearance = tempAppearance;
    }

    public OwnerInfo getOwnerInfo() {
        return ownerInfo;
    }

    public void setOwnerInfo(OwnerInfo ownerInfo) {
        this.ownerInfo = ownerInfo;
    }
}
