package net.monkeystudio.chatrbtw.service.bean.adcount;

import net.monkeystudio.chatrbtw.mapper.bean.adclicklog.AdClickLogDailyCount;
import net.monkeystudio.chatrbtw.mapper.bean.adpushLog.AdPushLogDailyCount;

import java.util.List;

/**
 * Created by bint on 21/03/2018.
 */
public class AdDaliyCountResp {

    private List<AdClickLogDailyCount> adClickLogDailyCountList;
    private  List<AdPushLogDailyCount> adPushLogDailyCountList;

    public List<AdClickLogDailyCount> getAdClickLogDailyCountList() {
        return adClickLogDailyCountList;
    }

    public void setAdClickLogDailyCountList(List<AdClickLogDailyCount> adClickLogDailyCountList) {
        this.adClickLogDailyCountList = adClickLogDailyCountList;
    }

    public List<AdPushLogDailyCount> getAdPushLogDailyCountList() {
        return adPushLogDailyCountList;
    }

    public void setAdPushLogDailyCountList(List<AdPushLogDailyCount> adPushLogDailyCountList) {
        this.adPushLogDailyCountList = adPushLogDailyCountList;
    }
}
