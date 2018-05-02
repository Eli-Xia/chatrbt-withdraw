package net.monkeystudio.portal.controller;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.local.Msg;
import net.monkeystudio.base.service.GlobalConstants;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.service.WxPubAuthService;
import net.monkeystudio.chatrbtw.service.bean.wxpub.WxPubCountBaseInfo;
import net.monkeystudio.portal.controller.req.chatrotot.AddChatRobot;
import net.monkeystudio.portal.controller.req.wxpub.QueryWxPubList;
import net.monkeystudio.wx.service.WxAuthApiService;
import net.monkeystudio.wx.service.WxPubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by bint on 22/12/2017.
 */
@RequestMapping(value = "/wx-pub")
@Controller
public class PortalWxPubController extends PortalBaseController{

    @Autowired
    private WxAuthApiService wxAuthApiService;

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private WxPubAuthService wxPubAuthService;

    @Autowired
    private WxPubService wxPubService;


    /**
     * 获取金豆平台授权URL
     * @return
     */
    @RequestMapping(value = "/join-url", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getPortalJoinUrl(@RequestBody AddChatRobot addChatRobot){
        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        String url = null;
        try {
            url = wxPubAuthService.getPortalJoinUrl(addChatRobot,userId);
        } catch (BizException e) {
            Log.e(e);
            return respHelper.failed(e.getBizExceptionMsg());
        }

        return respHelper.ok(url);
    }


    /**
     * 获取金豆平台授权URL
     * @return
     */
    @RequestMapping(value = "/weizan/join-url", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getWeizanJoinUrl(){

        String url = null;
        try {
            url = wxPubAuthService.getWeizanAuthUrl();
        } catch (BizException e) {
            Log.e(e);
            return respHelper.failed(e.getBizExceptionMsg());
        }

        return respHelper.ok(url);
    }


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getWxPubs(@RequestBody QueryWxPubList queryWxPubList){
        Integer userId = getUserId();
        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        List<WxPub> wxPubList = wxPubService.getWxPubs(userId ,queryWxPubList.getWxPubNickname());

        return respHelper.ok(wxPubList);

    }


    /**
     * 获取公众号的基础统计数据
     * @param wxPubOrginId
     * @return
     */
    @RequestMapping(value = "/count-base-info/{orginId}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getWxPubCountBaseInfo( @PathVariable("orginId") String wxPubOrginId){

        Integer userId = this.getUserId();
        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        WxPubCountBaseInfo wxPubCountBaseInfo = wxPubService.getWxPubCountBaseInfo(wxPubOrginId,userId);

        return respHelper.ok(wxPubCountBaseInfo);

    }

    /*@RequestMapping(value = "/auth-callback/handle/{user}/{robot}", method = RequestMethod.GET)
    @ResponseBody
    public String getHandler(
            @RequestParam("auth_code") String authCode,
            @RequestParam("expires_in") String expiresIn,
            @PathVariable("user") Integer user ,
            @PathVariable("robot") Integer chatRobotId ){

        Integer userId = this.getUserId();
        if(userId == null){
            return Msg.text("common.user.nologin");
        }

        try {
            wxAuthApiService.authCodeValueHandle(authCode,user ,chatRobotId);
        } catch (BizException e) {
            Log.e(e);
            return "授权失败";
        }

        return "授权成功";
    }*/

    @RequestMapping(value = "/auth-callback/{key}", method = RequestMethod.GET)
    public String getWeizanHandler(
            @RequestParam("auth_code") String authCode,
            @RequestParam("expires_in") String expiresIn,
            @PathVariable("key") String key){

        Integer source = null;
        try {
            source = wxAuthApiService.authCodeValueHandle(authCode,key);
        } catch (BizException e) {
            Log.e(e);
            return "redirect:/error.html";
        }

        if(source.intValue() == GlobalConstants.JOIN_SOURCE_PORTAL){
            return "redirect:/portal_success.html";
        }

        if(source.intValue() == GlobalConstants.JOIN_SOURCE_WEIZAN){
            return "redirect:/res/weizan/ask_search_success.html";
        }

        return null;
    }

}
