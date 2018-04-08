package net.monkeystudio.wx.vo.pub.autoreply;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.monkeystudio.wx.vo.pub.autoreply.KeywordAutoreplyInfo;
import net.monkeystudio.wx.vo.pub.autoreply.AddFriendAutoreplyInfo;

import java.io.Serializable;

/**
 * Created by bint on 2017/11/20.
 */
public class CurrentAutoReplyInfo implements Serializable{

    @JsonProperty("is_add_friend_reply_open")
    private Integer isAddFriendReplyOpen;

    @JsonProperty("is_autoreply_open")
    private Integer isAutoreplyOpen;

    @JsonProperty("add_friend_autoreply_info")
    private AddFriendAutoreplyInfo addFriendAutoreplyInfo;

    @JsonProperty("keyword_autoreply_info")
    private KeywordAutoreplyInfo keywordAutoreplyInfo;

    @JsonProperty("message_default_autoreply_info")
    private MessageDefaultAutoreplyInfo messageDefaultAutoreplyInfo;


    public Integer getIsAddFriendReplyOpen() {
        return isAddFriendReplyOpen;
    }

    public void setIsAddFriendReplyOpen(Integer isAddFriendReplyOpen) {
        this.isAddFriendReplyOpen = isAddFriendReplyOpen;
    }

    public Integer getIsAutoreplyOpen() {
        return isAutoreplyOpen;
    }

    public void setIsAutoreplyOpen(Integer isAutoreplyOpen) {
        this.isAutoreplyOpen = isAutoreplyOpen;
    }

    public AddFriendAutoreplyInfo getAddFriendAutoreplyInfo() {
        return addFriendAutoreplyInfo;
    }

    public void setAddFriendAutoreplyInfo(AddFriendAutoreplyInfo addFriendAutoreplyInfo) {
        this.addFriendAutoreplyInfo = addFriendAutoreplyInfo;
    }

    public KeywordAutoreplyInfo getKeywordAutoreplyInfo() {
        return keywordAutoreplyInfo;
    }

    public void setKeywordAutoreplyInfo(KeywordAutoreplyInfo keywordAutoreplyInfo) {
        this.keywordAutoreplyInfo = keywordAutoreplyInfo;
    }

    public MessageDefaultAutoreplyInfo getMessageDefaultAutoreplyInfo() {
        return messageDefaultAutoreplyInfo;
    }

    public void setMessageDefaultAutoreplyInfo(MessageDefaultAutoreplyInfo messageDefaultAutoreplyInfo) {
        this.messageDefaultAutoreplyInfo = messageDefaultAutoreplyInfo;
    }

    @Override
    public String toString() {
        return "CurrentAutoReplyInfo{" +
                "isAddFriendReplyOpen=" + isAddFriendReplyOpen +
                ", isAutoreplyOpen=" + isAutoreplyOpen +
                ", addFriendAutoreplyInfo=" + addFriendAutoreplyInfo +
                ", keywordAutoreplyInfo=" + keywordAutoreplyInfo +
                ", messageDefaultAutoreplyInfo=" + messageDefaultAutoreplyInfo +
                '}';
    }
}
