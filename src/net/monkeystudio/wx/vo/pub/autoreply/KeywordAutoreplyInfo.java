package net.monkeystudio.wx.vo.pub.autoreply;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.monkeystudio.wx.vo.pub.autoreply.KeywordAutoreplyInfoListItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bint on 2017/11/21.
 */
public class KeywordAutoreplyInfo implements Serializable {

    @JsonProperty("list")
    private List<KeywordAutoreplyInfoListItem> list;

    public List<KeywordAutoreplyInfoListItem> getList() {
        return list;
    }

    public void setList(List<KeywordAutoreplyInfoListItem> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "KeywordAutoreplyInfo{" +
                "list=" + list +
                '}';
    }
}
