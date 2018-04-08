package net.monkeystudio.chatrbtw.service.bean.ad;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class AdHallDetailConfirmReq {
    private Integer adId;
    private List<Integer> includeWxPubIds = new ArrayList<>();
    private List<Integer> excludeWxPubIds = new ArrayList<>();

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }

    public List<Integer> getIncludeWxPubIds() {
        return includeWxPubIds;
    }

    public void setIncludeWxPubIds(List<Integer> includeWxPubIds) {
        this.includeWxPubIds = includeWxPubIds;
    }

    public List<Integer> getExcludeWxPubIds() {
        return excludeWxPubIds;
    }

    public void setExcludeWxPubIds(List<Integer> excludeWxPubIds) {
        this.excludeWxPubIds = excludeWxPubIds;
    }
}
