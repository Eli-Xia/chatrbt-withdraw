package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.dividend.Dividend;
import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.DividendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by bint on 2018/7/10.
 */

@Controller
@RequestMapping(value = "/admin/dividend-record")
public class DividendRecordController extends BaseController {

    @Autowired
    private DividendService dividendService;

    @Autowired
    private RespHelper respHelper;

    @RequestMapping(value = "/dividend", method = RequestMethod.POST)
    @ResponseBody
    public RespBase dividend (@RequestBody Dividend dividend){

        Integer userId = this.getUserId();

        if(userId == null){
            return respHelper.nologin();
        }

        dividendService.dividend(dividend.getTotalMoney(),dividend.getChatPetTpye());
        return respHelper.ok();
    }

}
