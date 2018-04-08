package net.monkeystudio.admin.controller.resp.ad;

import net.monkeystudio.chatrbtw.entity.Ad;

/**
 * @author xiaxin
 */
public class AdMgrListResp extends Ad{

    private Integer totalClickCount;//用户点击数
    private Integer pushState;//广告投放状态 投放进行中,预投放,已结束


    public Integer getTotalClickCount() {
        return totalClickCount;
    }

    public void setTotalClickCount(Integer totalClickCount) {
        this.totalClickCount = totalClickCount;
    }

    public Integer getPushState() {
        return pushState;
    }

    public void setPushState(Integer pushState) {
        this.pushState = pushState;
    }
}
