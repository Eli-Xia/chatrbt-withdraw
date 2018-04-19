package net.monkeystudio.chatrbtw.service.bean.chatpet;

/**
 * Created by bint on 2018/4/19.
 */
public class ChatPetInfo {


    private Integer tempAppearance;

    private OwnerInfo ownerInfo;

    private String ownerId;
    private String geneticCode;

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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getGeneticCode() {
        return geneticCode;
    }

    public void setGeneticCode(String geneticCode) {
        this.geneticCode = geneticCode;
    }
}
