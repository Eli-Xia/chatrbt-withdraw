package net.monkeystudio.chatrbtw.entity;

import java.util.Objects;

public class RAdWxPub {
    private Integer id;

    private Integer adId;

    private Integer wxPubId;

    private Integer state;

    private Integer isExclude;

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

    public Integer getWxPubId() {
        return wxPubId;
    }

    public void setWxPubId(Integer wxPubId) {
        this.wxPubId = wxPubId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getIsExclude() {
        return isExclude;
    }

    public void setIsExclude(Integer isExclude) {
        this.isExclude = isExclude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RAdWxPub rAdWxPub = (RAdWxPub) o;
        return Objects.equals(adId, rAdWxPub.adId) &&
                Objects.equals(wxPubId, rAdWxPub.wxPubId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(adId, wxPubId);
    }
}