package net.monkeystudio.admin.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.admin.controller.req.adcount.AdCountDailyReq;
import net.monkeystudio.chatrbtw.service.AdCountService;
import net.monkeystudio.chatrbtw.service.bean.adcount.AdDaliyCountResp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created by bint on 21/03/2018.
 */
@RequestMapping(value = "/admin/ad-count")
@Controller
public class AdCountController {

    @Autowired
    private AdCountService adCountService;

    @Autowired
    private RespHelper respHelper;


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getAdDailyCount(@RequestBody AdCountDailyReq adCountDailyReq){

        Date startDate = adCountDailyReq.getStartDate();
        startDate = TimeUtil.getStartDate(startDate);

        Date endDate = adCountDailyReq.getEndDate();
        endDate = TimeUtil.getStartDate(endDate);

        Integer adId = adCountDailyReq.getAdId();

        AdDaliyCountResp resp = adCountService.countDaily(startDate, endDate, adId);

        return respHelper.ok(resp);
    }



}
