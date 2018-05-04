package net.monkeystudio.portal.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.local.Msg;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.AccountSettingService;
import net.monkeystudio.portal.controller.req.accountsetting.AccountSettingReq;
import net.monkeystudio.portal.controller.resp.accountsetting.AccountSettingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xiaxin
 */
@RequestMapping(value = "/settlement")
@Controller
public class PortalAccountSettingController extends PortalBaseController{


    @Autowired
    private RespHelper respHelper;

    @Autowired
    private AccountSettingService accountSettingService;



    /**
     * 结算设置新增和更新
     * @param req
     * @return
     */
    @RequestMapping(value = "/settings/add/update", method = RequestMethod.POST)
    @ResponseBody
    public RespBase saveAccountSettings(AccountSettingReq req){

        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        //当账户类型为个人账户才需要上传图片,进行校验
        if(accountSettingService.isPersonalAccountType(req.getAccountType())){
            if(req.getImage() == null){
                return respHelper.failed("文件不能为空");
            }

            if(!accountSettingService.checkFileSuffix(req.getImage())){
                return respHelper.failed("仅支持jpg,jpeg,png格式文件");
            }

            if(!accountSettingService.isInLimitSize(req.getImage())){
                return respHelper.failed("文件大小应在5M以内");
            }
        }

        accountSettingService.saveOrUpdateAccountSettings(req,userId);

        return respHelper.ok();
    }

    /**
     * 获取用户结算设置
     * @param request
     * @return
     */
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getAccountSetting(HttpServletRequest request){
        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        AccountSettingVO vo = accountSettingService.getAccountSettingVO(userId);

        return respHelper.ok(vo);
    }

    //处理敏感图片
    @RequestMapping(value = "/sensitive-pic")
    public ModelAndView handleSensitivePic(HttpServletRequest request, HttpServletResponse response)  {

        Integer userId = getUserId();

        if(userId == null){
            return null;
        }

        accountSettingService.handleSensitivePic(userId,response);

        return null;
    }

    //委托个人收款证明附件下载
    @RequestMapping(value = "/download/certification")
    public ModelAndView download(HttpServletRequest request, HttpServletResponse response)  {

        Integer userId = getUserId();

        if(userId == null){
            return null;
        }
        accountSettingService.downloadCertification(response);

        return null;
    }






}
