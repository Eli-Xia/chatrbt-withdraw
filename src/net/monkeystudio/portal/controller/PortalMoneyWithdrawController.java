package net.monkeystudio.portal.controller;

import net.monkeystudio.base.RespBase;
import net.monkeystudio.chatrbtw.service.AccountSettingService;
import net.monkeystudio.chatrbtw.service.MoneyWithdrawService;
import net.monkeystudio.chatrbtw.service.bean.moneywithdraw.MoneyWithdrawIncomeMgrResp;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.local.Msg;
import net.monkeystudio.portal.controller.req.moneywithdraw.MoneyWithdrawApplyReq;
import net.monkeystudio.portal.controller.resp.moneywithdraw.MoneyWithdrawDetail;
import net.monkeystudio.utils.RespHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 收益管理 提现
 * @author xiaxin
 */
@RequestMapping(value = "/money-withdraw")
@Controller
public class PortalMoneyWithdrawController extends PortalBaseController{

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private MoneyWithdrawService moneyWithdrawService;

    @Autowired
    private AccountSettingService accountSettingService;

    //收益管理 提现页面
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    @ResponseBody
    public RespBase incomeMgrVO(HttpServletRequest request) throws BizException {

        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        MoneyWithdrawIncomeMgrResp resp = moneyWithdrawService.getIncomeMgrResp(userId);

        return respHelper.ok(resp);
    }

    //提现记录查看详情
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getRecordById(HttpServletRequest request, @PathVariable("id")Integer id){

        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        MoneyWithdrawDetail moneyWithdrawDetail = moneyWithdrawService.getMoneyWithdrawDetail(id);

        return respHelper.ok(moneyWithdrawDetail);
    }

    //获取用户可提现金额
    @RequestMapping(value = "/available-amount", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getUserAvailableAmount() throws BizException {
        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }
        Float availableAmount = moneyWithdrawService.getAvailableAmount(userId);

        return respHelper.ok(availableAmount);
    }

    //提现申请
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    @ResponseBody
    public RespBase moneyWithdrawApply(HttpServletRequest request, @RequestBody MoneyWithdrawApplyReq req) throws BizException {

        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        if(!accountSettingService.isBindBank(userId)){
            return respHelper.failed(Msg.text("请绑定银行卡"));
        }

        if(req.getAmount() == null || req.getAmount() == 0){
            return respHelper.failed("请填写提现金额");
        }
        //提现开关
        if(!moneyWithdrawService.isSwitchOpen()){
            return respHelper.failed("当前无法进行提现操作");
        }

        if(!moneyWithdrawService.isMoreThanMinAmount(req.getAmount())){
            return respHelper.failed("提现金额至少1000");
        }

        if(!moneyWithdrawService.isLessThanAvailableAmount(req.getAmount(),userId)){
            return respHelper.failed("可提现金额不足");
        }

        moneyWithdrawService.apply(req.getAmount(),userId);

        return respHelper.ok();
    }



    /*//结算说明下载
    @RequestMapping(value = "/instruction")
    public ModelAndView download(HttpServletRequest request, HttpServletResponse response)  {

        Integer userId = getUserId();

        if(userId == null){
            return null;
        }
        moneyWithdrawService.downloadInstruction(response);

        return null;
    }*/


}
