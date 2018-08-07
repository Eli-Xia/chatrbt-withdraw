package net.monkeystudio.chatrbtw.entity;

//表名e_msg_template
public class MsgTemplate {
    private Integer id;
    private String templateId;
    private Integer miniProgramId;
    private String remark;
    private Integer code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Integer getMiniProgramId() {
        return miniProgramId;
    }

    public void setMiniProgramId(Integer miniProgramId) {
        this.miniProgramId = miniProgramId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}