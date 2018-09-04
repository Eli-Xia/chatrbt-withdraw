package net.monkeystudio.chatrbtw.service.bean.bannerad;

import java.util.Date;

/**
 * Created by bint on 2018/9/4.
 */
public class AddBannerAd {
    private String adUrl;
    private Date onlineTime;
    private Date createTime;

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
