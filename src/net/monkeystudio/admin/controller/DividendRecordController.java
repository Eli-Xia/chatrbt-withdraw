package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.dividend.Dividend;
import net.monkeystudio.admin.controller.req.dividend.DividendPageReq;
import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.ChatPetTypeService;
import net.monkeystudio.chatrbtw.service.DividendRecordService;
import net.monkeystudio.chatrbtw.service.DividendService;
import net.monkeystudio.chatrbtw.service.bean.dividendrecord.DividendRecordResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by bint on 2018/7/10.
 */

@Controller
@RequestMapping(value = "/admin/dividend-record")
public class DividendRecordController extends BaseController {

    @Autowired
    private DividendService dividendService;


    @Autowired
    private DividendRecordService dividendRecordService;

    @Autowired
    private RespHelper respHelper;

    @RequestMapping(value = "/dividend", method = RequestMethod.POST)
    @ResponseBody
    public RespBase dividend (@RequestBody Dividend dividend){

        Integer userId = this.getUserId();

        if(userId == null){
            return respHelper.nologin();
        }

        if(dividend.getChatPetType().intValue() != ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT.intValue()){
            return respHelper.failed("该宠物类型不支持分红");
        }

        dividendService.dividend(dividend.getTotalMoney(),dividend.getChatPetType());
        return respHelper.ok();
    }



    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getPageList (@RequestBody DividendPageReq dividendPageReq){

        Integer userId = this.getUserId();

        if(userId == null){
            return respHelper.nologin();
        }

        Integer page = dividendPageReq.getPage();
        Integer pageSize = dividendPageReq.getPageSize();
        List<DividendRecordResp> dividendRecordRespList = dividendRecordService.getDividendRecordRespList(page, pageSize);

        return respHelper.ok(dividendRecordRespList);
    }
}
