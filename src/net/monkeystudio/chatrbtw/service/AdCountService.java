package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.chatrbtw.mapper.bean.adclicklog.AdClickLogDailyCount;
import net.monkeystudio.chatrbtw.mapper.bean.adpushLog.AdPushLogDailyCount;
import net.monkeystudio.chatrbtw.service.bean.adcount.AdDaliyCountResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by bint on 21/03/2018.
 */
@Service
public class AdCountService {

    @Autowired
    private AdClickLogService adClickLogService;

    @Autowired
    private AdPushLogService adPushLogService;

    public AdDaliyCountResp countDaily(Date startDate ,Date endDate ,Integer adId){

        endDate = DateUtils.getEndDate(endDate);

        AdDaliyCountResp adDaliyCountResp = new AdDaliyCountResp();

        List<AdClickLogDailyCount> adClickLogDailyCountList = adClickLogService.countDaily(startDate, endDate ,adId);
        adDaliyCountResp.setAdClickLogDailyCountList(adClickLogDailyCountList);

        List<AdPushLogDailyCount> adPushLogDailyCountList = adPushLogService.countDaily(startDate,endDate,adId);
        adDaliyCountResp.setAdPushLogDailyCountList(adPushLogDailyCountList);

        return adDaliyCountResp;
    }

}
