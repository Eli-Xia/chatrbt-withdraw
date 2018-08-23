package net.monkeystudio.chatpet.controller.req.minigame;

import net.monkeystudio.base.controller.bean.req.ListPaginationReq;

public class QueryMiniGameReq extends ListPaginationReq {
    private Integer tagId;

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }
}
