package net.monkeystudio.chatrbtw.service.bean.chatpetmyinfo;

import java.util.Date;
import java.util.Objects;

/**
 * 城市分红记录
 * (按时间从大到小排列)
 * @author xiaxin
 */
public class ChatPetDividendDetail {
    private Date createTime;//产生时间
    private String note;//记录内容

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
