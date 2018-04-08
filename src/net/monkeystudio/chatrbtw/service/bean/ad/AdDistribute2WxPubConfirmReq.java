package net.monkeystudio.chatrbtw.service.bean.ad;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class AdDistribute2WxPubConfirmReq {
    private Integer adId;
    private List<Integer> tagIds = new ArrayList<>();
    private List<Integer> wxPubIds = new ArrayList<>();

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }

    public List<Integer> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Integer> tagIds) {
        this.tagIds = tagIds;
    }

    public List<Integer> getWxPubIds() {
        return wxPubIds;
    }

    public void setWxPubIds(List<Integer> wxPubIds) {
        this.wxPubIds = wxPubIds;
    }
}
