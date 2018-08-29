package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.WithdrawReq;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.bean.chatpet.MyInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpetmyinfo.ChatPetDividendDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bint on 2018/7/11.
 */
@Controller
@RequestMapping(value = "/chat-pet/my-info")
public class ChatPetMyInfoController extends ChatPetBaseController{


    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private RespHelper respHelper;


    /**
     * 获取"我的"信息
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getMyInfo(){

        Integer wxFanId = this.getUserId();

        if(wxFanId == null){
            return respHelper.nologin();
        }

        MyInfo myInfo = chatPetService.getMyInfo(wxFanId);

        return respHelper.ok(myInfo);
    }

    /**
     * 城市分红详情
     * @return
     */
    @RequestMapping(value = "/dividend-detail", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getDividendDetail(){

        Integer wxFanId = this.getUserId();

        ChatPet chatPet = chatPetService.getByWxFanId(wxFanId);

        ChatPetDividendDetailVO vo = chatPetService.getChatPetDividendDetailVO(chatPet.getId());

        return respHelper.ok(vo);
    }

    /**
     * 提现
     * @return
     */
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    @ResponseBody
    public RespBase withdraw(@RequestBody WithdrawReq withdrawReq){

        Integer wxFanId = this.getUserId();

        /*  1,提现金额最小金额100
            2,提现金额上限
            3,单个用户单日限额多少?500
            4,单个用户每天最多可以付款的次数? (微信可以设置)3
            5,付款时间间隔不得低于15S 15
            6,提现金额需要大于100?
            7,系统账户余额 > 提现金额 >= 用户账户余额 >= 100 ?
            8,当系统金额小于设定的阈值后是否需要发送邮件提醒?? no*/

        return respHelper.ok();
    }



}

