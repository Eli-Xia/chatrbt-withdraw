package net.monkeystudio.chatrbtw.service.bean.minigame;

import net.monkeystudio.chatpet.controller.req.minigame.QueryMiniGameReq;

/**
 * 小程序里小游戏分页查询参数
 */
public class QueryMiniGameParam extends QueryMiniGameReq{
    private Boolean handpicked;
    private Integer wxFanId;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean getHandpicked() {
        return handpicked;
    }

    public void setHandpicked(Boolean handpicked) {
        this.handpicked = handpicked;
    }

    public Integer getWxFanId() {
        return wxFanId;
    }

    public void setWxFanId(Integer wxFanId) {
        this.wxFanId = wxFanId;
    }
}
