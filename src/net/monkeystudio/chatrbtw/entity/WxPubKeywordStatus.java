package net.monkeystudio.chatrbtw.entity;



import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class WxPubKeywordStatus implements Serializable{

    private Integer id;
    private String originId;

    @JsonProperty(value = "status")
    private Integer switchStatus;

    public Integer getId() {
        return id;
    }

    public String getOriginId() {
        return originId;
    }

    public Integer getSwitchStatus() {
        return switchStatus;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public void setSwitchStatus(Integer switchStatus) {
        this.switchStatus = switchStatus;
    }
}

