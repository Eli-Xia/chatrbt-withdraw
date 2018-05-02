package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.moneywithdraw.AdminMoneyWithdrawDetail;
import net.monkeystudio.admin.controller.req.moneywithdraw.InvoiceAuditReq;
import net.monkeystudio.admin.controller.req.moneywithdraw.RemitConfirmReq;
import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.controller.bean.req.ListPaginationReq;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.MoneyWithdrawService;
import net.monkeystudio.chatrbtw.service.bean.moneywithdraw.MoneyWithdrawRecordListResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author xiaxin
 */
@Controller
@RequestMapping(value = "/admin/money-withdraw")
public class MoneyWithdrawMgrController extends BaseController {

    @Autowired
    private RespHelper respHelper;
    @Autowired
    private MoneyWithdrawService moneyWithdrawService;

    //提现记录列表
    @RequestMapping(value = "/record/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase moneyWithdrawRecordList(HttpServletRequest request, @RequestBody ListPaginationReq paginationReq){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        List<MoneyWithdrawRecordListResp> records =  moneyWithdrawService.listPaginationRecord(paginationReq);

        Integer count = moneyWithdrawService.getCount();

        return respHelper.ok(records,count);

    }

    //提现记录
    @RequestMapping(value = "/record/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getMoneyWithdrawRecordById(HttpServletRequest request, @PathVariable("id")Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        AdminMoneyWithdrawDetail detail =  moneyWithdrawService.getAdminRecordDetailById(id);

        return respHelper.ok(detail);

    }

    //获取提现开关
    @RequestMapping(value = "/switch/get", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getMoneyWithdrawSwitch(HttpServletRequest request){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        return respHelper.ok(moneyWithdrawService.getMoneyWithdrawSwitch());

    }

    @RequestMapping(value = "/switch/update", method = RequestMethod.POST)
    @ResponseBody
    public RespBase changeMoneyWithdrawSwitch(HttpServletRequest request){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        moneyWithdrawService.changeSwitch();

        return respHelper.ok();

    }

    //发票审核确认
    @RequestMapping(value = "/invoice-audit/confirm", method = RequestMethod.POST)
    @ResponseBody
    public RespBase confirmInvoiceAudit(HttpServletRequest request, @RequestBody InvoiceAuditReq req){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        moneyWithdrawService.invoiceAudit(req);

        return respHelper.ok();

    }

    //打款确认
    @RequestMapping(value = "/remit/confirm", method = RequestMethod.POST)
    @ResponseBody
    public RespBase confirmRemit(HttpServletRequest request, @RequestBody RemitConfirmReq req) throws BizException {

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        moneyWithdrawService.remitConfirm(req);

        return respHelper.ok();

    }

    //获取用户(公众号主)委托收款证明
    @RequestMapping(value = "/certification/get")
    public ModelAndView handleSensitivePic(@RequestParam("recordId")Integer id,  HttpServletResponse response)  {

        Integer userId = getUserId();

        if(userId == null){
            return null;
        }

        moneyWithdrawService.getCertificationPic(id,response);

        return null;
    }




}
