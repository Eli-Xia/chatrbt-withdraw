package net.monkeystudio.admin.controller;

import net.monkeystudio.base.BaseController;
import net.monkeystudio.base.RespBase;
import net.monkeystudio.chatrbtw.entity.Ad;
import net.monkeystudio.chatrbtw.service.PushMessageConfigService;
import net.monkeystudio.chatrbtw.service.bean.ad.AdConfigReq;
import net.monkeystudio.chatrbtw.service.bean.ad.AdConfigResp;
import net.monkeystudio.utils.RespHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bint on 21/12/2017.
 */
@RequestMapping(value = "/admin/push-message/config")
@Controller
public class PushMessageController extends BaseController {

    @Autowired
    private PushMessageConfigService pushMessageConfigService;

    @Autowired
    private RespHelper respHelper;


    @RequestMapping(value = "/ad/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getById(HttpServletRequest request){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        AdConfigResp adConfigResp = pushMessageConfigService.getAdConfig();

        return respHelper.ok(adConfigResp);
    }

    @RequestMapping(value = "/ad/update", method = RequestMethod.POST)
    @ResponseBody
    public RespBase updateAdConfig(@RequestBody AdConfigReq adConfigReq){

        pushMessageConfigService.updateAdConfig(adConfigReq);

        return respHelper.ok();
    }
}
