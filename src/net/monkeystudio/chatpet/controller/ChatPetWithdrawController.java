package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.local.Msg;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.WithdrawService;
import net.monkeystudio.chatrbtw.service.bean.moneywithdraw.MoneyWithdrawIncomeMgrResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/chat-pet/withdraw")
public class ChatPetWithdrawController extends ChatPetBaseController {

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private WithdrawService withdrawService;

    /**
     * 提现
     * @return
     */
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    @ResponseBody
    public RespBase withdraw() {

        Integer userId = getUserId();


        return respHelper.ok();
    }

    /**
     * 提现成功数据不一致,数据补全接口
     * @return
     */
    @RequestMapping(value = "/revise", method = RequestMethod.POST)
    @ResponseBody
    public RespBase revise() {

        Integer userId = getUserId();


        return respHelper.ok();
    }

    /**
     * 用户解锁
     * @return
     */
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    @ResponseBody
    public RespBase unlock() {

        Integer userId = getUserId();


        return respHelper.ok();
    }
}
