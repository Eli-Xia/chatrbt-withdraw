package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.QueryAdList;
import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.admin.controller.req.ad.AddAd;
import net.monkeystudio.admin.controller.req.ad.UpdateAd;
import net.monkeystudio.admin.controller.resp.ad.AdMgrListResp;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.Ad;
import net.monkeystudio.chatrbtw.service.AdService;
import net.monkeystudio.chatrbtw.service.bean.UploadFile;
import net.monkeystudio.chatrbtw.service.bean.ad.AdDistribute2WxPubConfirmReq;
import net.monkeystudio.chatrbtw.service.bean.ad.AdDistribute2WxPubResp;
import net.monkeystudio.chatrbtw.service.bean.ad.AdUpdateReq;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by bint on 2017/12/14.
 */
@RequestMapping(value = "/admin/ad")
@Controller
public class AdController extends BaseController {

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private AdService adService;


    /**
     * 广告管理列表
     * @param request
     * @param queryAdList
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getAds(HttpServletRequest request, @RequestBody QueryAdList queryAdList){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        Integer page = queryAdList.getPage();
        Integer pageSize = queryAdList.getPageSize();

        if ( page == null || page < 1 ){
            return respHelper.cliParamError("page error.");
        }

        if ( pageSize == null || pageSize < 1 ){
            return respHelper.cliParamError("pageSize error.");
        }

        List<AdMgrListResp> resp = adService.getAds(page, pageSize);

        Integer count = adService.getCount();

        return respHelper.ok(resp, count);
    }

    /**
     * 根据id获得广告
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getById(HttpServletRequest request , @PathVariable("id") Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        Ad ad = adService.getAdById(id);

        return respHelper.ok(ad);
    }

    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
    @ResponseBody
    public RespBase update(HttpServletRequest request , @PathVariable("id") Integer id , UpdateAd updateAd){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if(StringUtils.isBlank(updateAd.getAlias())){
            return respHelper.failed("广告别名必须填写");
        }

        AdUpdateReq adUpdateReq = BeanUtils.copyBean(updateAd,AdUpdateReq.class);
        adUpdateReq.setId(id);

        adService.updateAd(adUpdateReq);

        return respHelper.ok(null);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public RespBase add(HttpServletRequest request , AddAd addAd){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if(addAd.getIncome() == null){
            return respHelper.failed("请填写广告价格");
        }

        if(!adService.isMoreThanMinIncome(addAd.getIncome())){
            return respHelper.failed("广告价格至少为1分钱");
        }

        if(StringUtils.isBlank(addAd.getAlias())){
            return respHelper.failed("广告别名必须填写");
        }

        adService.save(addAd);

        return respHelper.ok(null);
    }





    /*@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase add(HttpServletRequest request ,@PathVariable("id") Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        adService.delete(id);

        return respHelper.ok(null);
    }*/

    /**
     * 为广告分配公众号 按照标签
     * @param request
     * @param id        广告id
     * @return
     */
    @RequestMapping(value = "/{id}/distribute-wxpub", method = RequestMethod.POST)
    @ResponseBody
    public RespBase distributeAdToWxPub (HttpServletRequest request ,@PathVariable("id") Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        AdDistribute2WxPubResp adDistribute2WxPubResp = adService.createAdDistribute2WxPubResp(id);

        return respHelper.ok(adDistribute2WxPubResp);
    }

    @RequestMapping(value = "/distribute-wxpub-confirm", method = RequestMethod.POST)
    @ResponseBody
    public RespBase distributeAdToWxPubConfirm (HttpServletRequest request , @RequestBody AdDistribute2WxPubConfirmReq req){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if(CollectionUtils.isEmpty(req.getTagIds())){
            return respHelper.failed("至少选择一个标签");
        }

        if(CollectionUtils.isEmpty(req.getWxPubIds())){
            return respHelper.failed("至少选择一个公众号");
        }

        adService.distributeAdToWxPubConfirm(req);

        return respHelper.ok();
    }
}
