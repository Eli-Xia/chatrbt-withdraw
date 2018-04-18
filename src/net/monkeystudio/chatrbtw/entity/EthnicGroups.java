package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

//表名e_ethnic_groups
public class EthnicGroups {
    private Integer id;
    private Integer totalValidCount;
    private String wxPubOriginId;
    private Integer codeType;
    //二级族群的创始人的openId
    private String secondFounderId;
    //private Long parentCode;
    //private Long code;
    private Integer parentId;
    private Date createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTotalValidCount() {
        return totalValidCount;
    }

    public void setTotalValidCount(Integer totalValidCount) {
        this.totalValidCount = totalValidCount;
    }

    public String getWxPubOriginId() {
        return wxPubOriginId;
    }

    public void setWxPubOriginId(String wxPubOriginId) {
        this.wxPubOriginId = wxPubOriginId;
    }

    public Integer getCodeType() {
        return codeType;
    }

    public void setCodeType(Integer codeType) {
        this.codeType = codeType;
    }

    public String getSecondFounderId() {
        return secondFounderId;
    }

    public void setSecondFounderId(String secondFounderId) {
        this.secondFounderId = secondFounderId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}