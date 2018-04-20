package net.monkeystudio.wx.controller.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import net.monkeystudio.wx.vo.customerservice.CustomerNewsItem;

import java.util.List;

/**
 * @author xiaxin
 */
@XStreamAlias("xml")
public class NewsMsgRes {
    @XStreamAlias("ToUserName")
    private String toUserName;

    @XStreamAlias("FromUserName")
    private String fromUserName;

    @XStreamAlias("CreateTime")
    private Long createTime;

    @XStreamAlias("MsgType")
    private String msgType;

    @XStreamAlias("ArticleCount")
    private Integer articleCount;

    @XStreamAlias("Articles")
    private List<CustomerNewsItem> articles;


    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Integer getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Integer articleCount) {
        this.articleCount = articleCount;
    }

    public List<CustomerNewsItem> getArticles() {
        return articles;
    }

    public void setArticles(List<CustomerNewsItem> articles) {
        this.articles = articles;
    }
}
