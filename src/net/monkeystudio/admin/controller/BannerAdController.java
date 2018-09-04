package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.IdReq;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.controller.bean.req.ListPaginationReq;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.BannerAd;
import net.monkeystudio.chatrbtw.service.BannerAdService;
import net.monkeystudio.chatrbtw.service.bean.bannerad.AddBannerAd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by bint on 2018/9/4.
 */
@RequestMapping(value = "/admin/banner-ad")
@Controller
public class BannerAdController {

    @Autowired
    private BannerAdService bannerAdService;

    @Autowired
    private RespHelper respHelper;

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RespBase getAuctionItemPage(@RequestBody ListPaginationReq listPaginationReq){

        Integer startIndex = listPaginationReq.getStartIndex();
        Integer pageSize = listPaginationReq.getPageSize();

        List<BannerAd> bannerAdList = bannerAdService.getPage(startIndex, pageSize);

        return respHelper.ok(bannerAdList);
    }


    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public RespBase add(@RequestBody AddBannerAd addBannerAd){

        bannerAdService.add(addBannerAd);
        return respHelper.ok();
    }


    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public RespBase update(@RequestBody BannerAd bannerAd){

        bannerAdService.update(bannerAd);
        return respHelper.ok();
    }


    @ResponseBody
    @RequestMapping(value = "/unshelve", method = RequestMethod.POST)
    public RespBase unshelve(@RequestBody IdReq idReq){

        Integer id = idReq.getId();
        bannerAdService.unshelve(id);

        return respHelper.ok();
    }
}
