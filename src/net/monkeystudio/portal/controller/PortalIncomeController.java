package net.monkeystudio.portal.controller;

import net.monkeystudio.base.RespBase;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.admin.controller.req.income.WxPubDailyIncomeQueryReq;
import net.monkeystudio.chatrbtw.service.IncomeSerivce;
import net.monkeystudio.chatrbtw.service.bean.income.UserDailyIncome;
import net.monkeystudio.chatrbtw.service.bean.income.UserDailyIncomeDetail;
import net.monkeystudio.chatrbtw.service.bean.income.WxPubDailyIncomeItem;
import net.monkeystudio.chatrbtw.service.bean.income.WxPubIncomeCountInfoResp;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.portal.controller.req.income.UserDailyIncomeDetailQueryReq;
import net.monkeystudio.portal.controller.req.income.UserDailyIncomeQueryReq;
import net.monkeystudio.utils.RespHelper;
import net.monkeystudio.wx.service.WxPubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * Created by bint on 09/03/2018.
 */
@Controller
@RequestMapping(value = "/income")
public class PortalIncomeController extends PortalBaseController{
    @Autowired
    private IncomeSerivce incomeSerivce;

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private WxPubService wxPubService;

    /**
     * 获取公众号的收益统计
     * @param wxPubOriginId
     * @return
     */
    @RequestMapping(value = "/wx-pub/base/count",method = RequestMethod.POST)
    @ResponseBody
    public RespBase getWxPubIncomeCount(@RequestBody String wxPubOriginId) throws BizException {

        Integer userId = getUserId();

        if(userId == null){
            return respHelper.nologin();
        }

        if(wxPubService.hasPub(wxPubOriginId,userId)){
            return respHelper.authFailed();
        }

        WxPubIncomeCountInfoResp wxPubIncomeCountInfoResp = incomeSerivce.wxPubIncomeCount(wxPubOriginId ,userId);
        return respHelper.ok(wxPubIncomeCountInfoResp);
    }

    /**
     * 获取公众号的每日收入列表
     * @return
     */
    @RequestMapping(value = "/wx-pub/daily/count",method = RequestMethod.POST)
    @ResponseBody
    public RespBase getWxPubDailyIncome(@RequestBody WxPubDailyIncomeQueryReq wxPubDailyIncomeQueryReq) throws BizException {

        Integer userId = getUserId();

        String wxPubOriginId = wxPubDailyIncomeQueryReq.getWxPubOriginId();

        if(userId == null){
            return respHelper.nologin();
        }

        if(!wxPubService.hasPub(wxPubOriginId,userId)){
            return respHelper.authFailed();
        }


        Date startDate = wxPubDailyIncomeQueryReq.getStartDate();
        Date endDate = wxPubDailyIncomeQueryReq.getEndDate();

        Integer page = wxPubDailyIncomeQueryReq.getPage();

        if(endDate.getTime() < startDate.getTime()){
            return respHelper.failed("时间参数有误");
        }

        List<WxPubDailyIncomeItem> wxPubDailyIncomeItemList = incomeSerivce.getDailyListWxPubIncome(wxPubOriginId, startDate, endDate, page ,userId);

        Integer totalCount = DateUtils.getDiscrepantDays(startDate,endDate);

        return respHelper.ok(wxPubDailyIncomeItemList,totalCount);
    }

    /**
     * 获取用户当日详细收入信息
     * @param userDailyIncomeDetailQueryReq
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/daily/detail",method = RequestMethod.POST)
    public RespBase getUserDailyIncomeDetail(@RequestBody UserDailyIncomeDetailQueryReq userDailyIncomeDetailQueryReq){

        Integer userId = this.getUserId();
        Date date = userDailyIncomeDetailQueryReq.getDate();

        UserDailyIncomeDetail userDailyIncomeDetail = incomeSerivce.getUserDailyIncomeDetail(userId , date);

        if(userDailyIncomeDetail.getWxPubDailyIncomeOverviewList() == null){
            return respHelper.ok();
        }

        return respHelper.ok(userDailyIncomeDetail,userDailyIncomeDetail.getWxPubDailyIncomeOverviewList().size());

    }

    /**
     * 获取用户日收益列表
     * @param userDailyIncomeQueryReq
     * @return
     * @throws BizException
     */
    @ResponseBody
    @RequestMapping(value = "/user/daily/list",method = RequestMethod.POST)
    public RespBase getUserDailyIncomeList(@RequestBody UserDailyIncomeQueryReq userDailyIncomeQueryReq) throws BizException {

        Integer userId = this.getUserId();

        if(userId == null){
            return respHelper.nologin();
        }

        Date startDate = userDailyIncomeQueryReq.getStartDate();
        Date endDate = userDailyIncomeQueryReq.getEndDate();
        Integer page = userDailyIncomeQueryReq.getPage();

        if(endDate.getTime() < startDate.getTime()){
            return respHelper.failed("时间参数有误");
        }

        UserDailyIncome userDailyIncome = incomeSerivce.getUserDailyIncome(userId, startDate, endDate, page);

        Integer totalCount = DateUtils.getDiscrepantDays(startDate,endDate);
        return respHelper.ok(userDailyIncome,totalCount);

    }
}
