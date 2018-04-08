package net.monkeystudio.chatrbtw.service.bean.ad;

import net.monkeystudio.chatrbtw.entity.WxPubTag;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class AdDistribute2WxPubResp {
    List<WxPubTag> tags = new ArrayList<>();

    List<Integer> tagIdsOfAd = new ArrayList<>();

    List<Integer> wxPubIdsOfAd = new ArrayList<>();

    List<TagId2WxPubsItem> tagId2WxPubsItems = new ArrayList<>();

    public List<WxPubTag> getTags() {
        return tags;
    }

    public void setTags(List<WxPubTag> tags) {
        this.tags = tags;
    }


    public List<TagId2WxPubsItem> getTagId2WxPubsItems() {
        return tagId2WxPubsItems;
    }

    public void setTagId2WxPubsItems(List<TagId2WxPubsItem> tagId2WxPubsItems) {
        this.tagId2WxPubsItems = tagId2WxPubsItems;
    }

    public List<Integer> getTagIdsOfAd() {
        return tagIdsOfAd;
    }

    public void setTagIdsOfAd(List<Integer> tagIdsOfAd) {
        this.tagIdsOfAd = tagIdsOfAd;
    }

    public List<Integer> getWxPubIdsOfAd() {
        return wxPubIdsOfAd;
    }

    public void setWxPubIdsOfAd(List<Integer> wxPubIdsOfAd) {
        this.wxPubIdsOfAd = wxPubIdsOfAd;
    }
}
