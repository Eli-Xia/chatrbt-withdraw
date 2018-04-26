package net.monkeystudio.chatrbtw.service.bean.wxmessage;

/**
 * Created by bint on 2018/4/24.
 */
public class ReplyMessage {

    private Integer chatLogId = null;
    private String replySource = null;
    private Integer replyMsgType = null;

    private Object object;


    public Integer getReplyMsgType() {
        return replyMsgType;
    }

    public void setReplyMsgType(Integer replyMsgType) {
        this.replyMsgType = replyMsgType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Integer getChatLogId() {
        return chatLogId;
    }

    public void setChatLogId(Integer chatLogId) {
        this.chatLogId = chatLogId;
    }

    public String getReplySource() {
        return replySource;
    }

    public void setReplySource(String replySource) {
        this.replySource = replySource;
    }
}
