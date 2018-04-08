package net.monkeystudio.chatrbtw.service.bean.ad;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class AdHallResp {
    List<AdHallItem> pushList = new ArrayList<>();
    List<AdHallItem> prePushList = new ArrayList<>();
    List<AdHallItem> closePushList = new ArrayList<>();

    public List<AdHallItem> getPushList() {
        return pushList;
    }

    public void setPushList(List<AdHallItem> pushList) {
        this.pushList = pushList;
    }

    public List<AdHallItem> getPrePushList() {
        return prePushList;
    }

    public void setPrePushList(List<AdHallItem> prePushList) {
        this.prePushList = prePushList;
    }

    public List<AdHallItem> getClosePushList() {
        return closePushList;
    }

    public void setClosePushList(List<AdHallItem> closePushList) {
        this.closePushList = closePushList;
    }
}
