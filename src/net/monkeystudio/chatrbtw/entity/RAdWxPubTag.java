package net.monkeystudio.chatrbtw.entity;

import java.util.Objects;

public class RAdWxPubTag {
    private Integer id;

    private Integer adId;

    private Integer wxPubTagId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }

    public Integer getWxPubTagId() {
        return wxPubTagId;
    }

    public void setWxPubTagId(Integer wxPubTagId) {
        this.wxPubTagId = wxPubTagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RAdWxPubTag that = (RAdWxPubTag) o;
        return Objects.equals(adId, that.adId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(adId);
    }
}