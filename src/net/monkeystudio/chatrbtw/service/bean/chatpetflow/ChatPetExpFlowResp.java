package net.monkeystudio.chatrbtw.service.bean.chatpetflow;

import java.util.Date;

/**
 * @author xiaxin
 */
public class ChatPetExpFlowResp {
    private Integer id;
    private String note;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
