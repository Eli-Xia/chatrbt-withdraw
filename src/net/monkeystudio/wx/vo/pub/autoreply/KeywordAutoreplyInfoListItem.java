package net.monkeystudio.wx.vo.pub.autoreply;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.monkeystudio.wx.vo.pub.autoreply.KeywordListInfoItem;
import net.monkeystudio.wx.vo.pub.autoreply.ReplyListInfoItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bint on 2017/11/21.
 */
public class KeywordAutoreplyInfoListItem implements Serializable{

    @JsonProperty("rule_name")
    private String ruleName;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("reply_mode")
    private String replyMode;

    @JsonProperty("keyword_list_info")
    private List<KeywordListInfoItem> keywordListInfo;

    @JsonProperty("reply_list_info")
    private List<ReplyListInfoItem> replyListInfo;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getReplyMode() {
        return replyMode;
    }

    public void setReplyMode(String replyMode) {
        this.replyMode = replyMode;
    }

    public List<KeywordListInfoItem> getKeywordListInfo() {
        return keywordListInfo;
    }

    public void setKeywordListInfo(List<KeywordListInfoItem> keywordListInfo) {
        this.keywordListInfo = keywordListInfo;
    }

    public List<ReplyListInfoItem> getReplyListInfo() {
        return replyListInfo;
    }

    public void setReplyListInfo(List<ReplyListInfoItem> replyListInfo) {
        this.replyListInfo = replyListInfo;
    }

    @Override
    public String toString() {
        return "KeywordAutoreplyInfoListItem{" +
                "ruleName='" + ruleName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", replyMode='" + replyMode + '\'' +
                ", keywordListInfo=" + keywordListInfo +
                ", replyListInfo=" + replyListInfo +
                '}';
    }
}
