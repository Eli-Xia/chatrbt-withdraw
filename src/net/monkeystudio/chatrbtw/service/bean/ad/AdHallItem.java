package net.monkeystudio.chatrbtw.service.bean.ad;

import java.util.Date;

/**
 * @author xiaxin
 */
public class AdHallItem {
    private Integer id;
    private String portalTitle;
    private Date pushTime;
    private Integer state;
    private String coverPic;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPortalTitle() {
        return portalTitle;
    }

    public void setPortalTitle(String portalTitle) {
        this.portalTitle = portalTitle;
    }

    public Date getPushTime() {
        return pushTime;
    }

    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }
}
