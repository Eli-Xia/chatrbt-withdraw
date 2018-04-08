package net.monkeystudio.portal.controller;

import net.monkeystudio.base.RespBase;
import net.monkeystudio.chatrbtw.service.AdHallService;
import net.monkeystudio.chatrbtw.service.bean.ad.AdHallDetailConfirmReq;
import net.monkeystudio.chatrbtw.service.bean.ad.AdHallItemDetail;
import net.monkeystudio.chatrbtw.service.bean.ad.AdHallResp;
import net.monkeystudio.local.Msg;
import net.monkeystudio.utils.RespHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 广告大厅
 * @author xiaxin
 */
@RequestMapping(value = "/ad-hall")
@Controller
public class PortalAdHallController extends PortalBaseController {
    @Autowired
    private RespHelper respHelper;
    @Autowired
    private AdHallService adHallService;


    /**
     * 广告大厅广告按钮
     * @param
     * @return
     */
    @RequestMapping(value = "/state/update", method = RequestMethod.POST)
    @ResponseBody
    public RespBase updateAdPushState(@RequestParam Integer state,@RequestParam Integer adId){
        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        adHallService.updateItemState(userId,state,adId);

        return respHelper.ok();
    }

    @RequestMapping(value = "/item/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getAdHallItems(HttpServletRequest request){
        Integer userId = getUserId();
        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        AdHallResp resp = adHallService.getAds(userId);

        return respHelper.ok(resp);
    }

    //广告详情
    @RequestMapping(value = "/item/detail", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getItemDetail(@RequestParam Integer adId){
        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        AdHallItemDetail detail = adHallService.getItemDetail(userId,adId);

        return respHelper.ok(detail);
    }
    //广告详情确认
    @RequestMapping(value = "/item/detail/confirm", method = RequestMethod.POST)
    @ResponseBody
    public RespBase confirmDetail(@RequestBody AdHallDetailConfirmReq req){
        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        adHallService.detailConfirm(req);

        return respHelper.ok();
    }



}
