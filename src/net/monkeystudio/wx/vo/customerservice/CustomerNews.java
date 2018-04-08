package net.monkeystudio.wx.vo.customerservice;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2017/12/8.
 */
public class CustomerNews extends CustomerMsgBase {

    @JsonProperty("news")
    private WxNewsArticles articles;

    public WxNewsArticles getArticles() {
        return articles;
    }

    public void setArticles(WxNewsArticles articles) {
        this.articles = articles;
    }
}
