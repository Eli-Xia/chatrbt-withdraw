package net.monkeystudio.chatrbtw.service.bean.chatpet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bint on 2018/4/19.
 */
public class ChatPetInfo {

    private Integer tempAppearance;
    private String appearanceUrl;
    private OwnerInfo ownerInfo;

    private String ownerId;
    private String geneticCode;

    private List<PetLogResp> petLogs = new ArrayList<>();

    private Float fanTotalCoin = 0F;

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

    public Float getFanTotalCoin() {
        return fanTotalCoin;
    }

    public void setFanTotalCoin(Float fanTotalCoin) {
        this.fanTotalCoin = fanTotalCoin;
    }

    public List<PetLogResp> getPetLogs() {
        return petLogs;
    }

    public void setPetLogs(List<PetLogResp> petLogs) {
        this.petLogs = petLogs;
    }

    public String getAppearanceUrl() {
        return appearanceUrl;
    }

    public void setAppearanceUrl(String appearanceUrl) {
        this.appearanceUrl = appearanceUrl;
    }
}
