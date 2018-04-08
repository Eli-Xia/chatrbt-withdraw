package net.monkeystudio.wx.vo.customerservice;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by bint on 2017/12/8.
 */
public class WxNewsArticles{

    private List<CustomerNewsItem> articles;

    public List<CustomerNewsItem> getArticles() {
        return articles;
    }

    public void setArticles(List<CustomerNewsItem> articles) {
        this.articles = articles;
    }
}
