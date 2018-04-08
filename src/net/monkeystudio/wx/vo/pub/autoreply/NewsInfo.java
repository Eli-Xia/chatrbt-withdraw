package net.monkeystudio.wx.vo.pub.autoreply;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bint on 2017/11/21.
 */
public class NewsInfo implements Serializable{

    @JsonProperty("list")
    private List<NewsInfoItem> list;

    public List<NewsInfoItem> getList() {
        return list;
    }

    public void setList(List<NewsInfoItem> list) {
        this.list = list;
    }
}
