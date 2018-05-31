package net.monkeystudio.chatrbtw.service.bean.chatpet;

import java.util.Date;

/**
 * @author xiaxin
 */
public class PetLogResp {
    private Date createTime;

    private String content;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
