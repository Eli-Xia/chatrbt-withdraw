package net.monkeystudio.chatrbtw.service.bean.ad;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 * TagId映射List<WxPub>
 */
public class TagId2WxPubsItem {
    private Integer tagId;
    private List<AdWxPubResp> wxPubs = new ArrayList<>();

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public List<AdWxPubResp> getWxPubs() {
        return wxPubs;
    }

    public void setWxPubs(List<AdWxPubResp> wxPubs) {
        this.wxPubs = wxPubs;
    }
}
