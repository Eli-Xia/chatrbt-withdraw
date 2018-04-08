package net.monkeystudio.wx.controller;

import net.monkeystudio.base.BaseController;

/**
 * Created by bint on 2017/10/24.
 */
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.local.Msg;
import net.monkeystudio.wx.service.WxAuthApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/third-party")
public class ThirdPartyController extends BaseController{

    @Autowired
    private WxAuthApiService wxAuthApiService;


    /**
     * 微信推送的url接口
     * @param request
     * @param authCode
     * @param expiresIn
     * @return
     */
   /* @RequestMapping(value = "/auth-callback-uri/handle", method = RequestMethod.GET)
    @ResponseBody
    public String handleGet(HttpServletRequest request ,
                            @RequestParam("auth_code") String authCode,
                            @RequestParam("expires_in") String expiresIn,
                            @RequestParam("user") Integer userId ,
                            @RequestParam("robot") Integer chatRobotId ){

        try {
            wxAuthApiService.authCodeValueHandle(authCode,userId ,chatRobotId);
        } catch (BizException e) {
            Log.e(e);
            return "授权失败";
        }

        return "授权成功";
    }*/



    /**
     * 授权事件接收URL
     * @return
     */
    /*@RequestMapping(value = "/auth", method = RequestMethod.GET)
    @ResponseBody
    public String auth(HttpServletRequest request){

        Integer userId = getUserId();

        if(userId == null){
            return Msg.text("common.user.nologin");
        }


        String url = null;
        try {
            url = wxAuthApiService.getAuthPageUrl(userId);
        } catch (BizException e) {
            Log.e(e);
        }

        return url;
    }*/
}
