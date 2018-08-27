package net.monkeystudio.chatrbtw.mapper.bean.minigame;

import java.util.Date;

/**
 *  根据标签分类及过滤条件获取小游戏id分页数据的查询对象
 */
public class MiniGameIdsQueryObject {
    private Integer startIndex;
    private Integer pageSize;
    private Integer tagId;
    private Integer shelveState;
    private Integer needSign;
    private Date onlineTime;

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public Integer getShelveState() {
        return shelveState;
    }

    public void setShelveState(Integer shelveState) {
        this.shelveState = shelveState;
    }

    public Integer getNeedSign() {
        return needSign;
    }

    public void setNeedSign(Integer needSign) {
        this.needSign = needSign;
    }

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }
}
