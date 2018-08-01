package net.monkeystudio.chatrbtw.sdk.wx.bean.msgtemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2018/7/31.
 */
public class MsgTemplateParam {

    private String touser;

    @JsonProperty("template_id")
    private String templateId;
    private String page;

    @JsonProperty("form_id")
    private String formId;

    private Data date;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Data getDate() {
        return date;
    }

    public void setDate(Data date) {
        this.date = date;
    }
}
