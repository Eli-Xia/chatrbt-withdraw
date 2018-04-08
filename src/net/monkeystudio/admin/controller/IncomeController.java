package net.monkeystudio.admin.controller;

import net.monkeystudio.base.BaseController;
import net.monkeystudio.base.RespBase;
import net.monkeystudio.admin.controller.req.income.UserImcomeReq;
import net.monkeystudio.chatrbtw.service.IncomeSerivce;
import net.monkeystudio.chatrbtw.service.bean.income.UserTotalIncomeDetail;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.utils.RespHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Created by bint on 20/03/2018.
 */
@Controller
@RequestMapping(value = "/admin/income")
public class IncomeController extends BaseController{

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private IncomeSerivce incomeSerivce;

    @RequestMapping(value = "/user/total", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getToalIncome(@RequestBody UserImcomeReq userImcomeReq) throws BizException {

        Integer userId = userImcomeReq.getUserId();

        UserTotalIncomeDetail userTotalIncomeDetail = incomeSerivce.getHistoryTotalIncomeDetail(userId);

        return respHelper.ok(userTotalIncomeDetail);
    }





}
