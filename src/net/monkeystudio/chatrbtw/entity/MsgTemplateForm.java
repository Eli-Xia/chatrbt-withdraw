package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

//表名e_msg_template_form
public class MsgTemplateForm {
    private Integer id;
    private String formId;
    private Integer wxFanId;
    private Integer state;
    private Integer usageTime;
    private Date createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Integer getWxFanId() {
        return wxFanId;
    }

    public void setWxFanId(Integer wxFanId) {
        this.wxFanId = wxFanId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(Integer usageTime) {
        this.usageTime = usageTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}