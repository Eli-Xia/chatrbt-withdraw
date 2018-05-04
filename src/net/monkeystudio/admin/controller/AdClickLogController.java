package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.adclicklog.AdClickLogQueryReq;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.AdClickLog;
import net.monkeystudio.chatrbtw.service.AdClickLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by bint on 14/03/2018.
 */
@RequestMapping(value = "/admin/ad-click-log")
@Controller
public class AdClickLogController {


    @Autowired
    private AdClickLogService adClickLogService;

    @Autowired
    private RespHelper respHelper;

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RespBase getAdClickLogList(@RequestBody AdClickLogQueryReq adClickLogQueryReq){

        List<AdClickLog> adClickLogList = adClickLogService.getAdClickLog(adClickLogQueryReq.getPage(), adClickLogQueryReq.getPageSize());

        Integer count = adClickLogService.count();

        return respHelper.ok(adClickLogList,count);

    }
}
