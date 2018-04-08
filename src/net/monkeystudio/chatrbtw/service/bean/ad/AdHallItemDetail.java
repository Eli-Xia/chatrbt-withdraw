package net.monkeystudio.chatrbtw.service.bean.ad;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaxin
 */
public class AdHallItemDetail {
    private Integer adId;
    private String portalTitle;
    private Date pushTime;
    private Integer adType;
    private String portalContent;
    private Date closeTime;
    private List<AdHallItemDetailWxPub> wxPubs = new ArrayList<>();

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }

    public Date getPushTime() {
        return pushTime;
    }

    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }

    public Integer getAdType() {
        return adType;
    }

    public void setAdType(Integer adType) {
        this.adType = adType;
    }

    public List<AdHallItemDetailWxPub> getWxPubs() {
        return wxPubs;
    }

    public void setWxPubs(List<AdHallItemDetailWxPub> wxPubs) {
        this.wxPubs = wxPubs;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public String getPortalTitle() {
        return portalTitle;
    }

    public void setPortalTitle(String portalTitle) {
        this.portalTitle = portalTitle;
    }

    public String getPortalContent() {
        return portalContent;
    }

    public void setPortalContent(String portalContent) {
        this.portalContent = portalContent;
    }
}
