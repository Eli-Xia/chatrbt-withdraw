package net.monkeystudio.chatrbtw.entity;

//表名e_ethnic_groups_code
public class EthnicGroupsCode {
    private Integer id;
    private String code;
    private String parentCode;
    private Integer totalValidCount;
    private String wxPubOriginId;

    public String getWxPubOriginId() {
        return wxPubOriginId;
    }

    public void setWxPubOriginId(String wxPubOriginId) {
        this.wxPubOriginId = wxPubOriginId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public Integer getTotalValidCount() {
        return totalValidCount;
    }

    public void setTotalValidCount(Integer totalValidCount) {
        this.totalValidCount = totalValidCount;
    }
}