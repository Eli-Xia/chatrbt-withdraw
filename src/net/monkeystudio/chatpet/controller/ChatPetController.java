package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatpet.controller.req.ChatPetIdReq;
import net.monkeystudio.chatpet.controller.req.chatpetmission.CompleteMissionRewardReq;
import net.monkeystudio.chatrbtw.service.ChatPetMissionPoolService;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetSessionVo;

import net.monkeystudio.wx.service.WxOauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.portlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bint on 2018/4/19.
 */
@Controller
@RequestMapping(value = "/chat-pet/pet")
public class ChatPetController extends ChatPetBaseController{

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private RespHelper respHelper;

    //private final static String ZEBRA_HTML = "https://test.keendo.com.cn/res/wedo/zebra.html?id=";


    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public RespBase getAdClickLogList(HttpServletRequest request,HttpServletResponse response){

        Integer fanId = getUserId();

        if(fanId == null){
            respHelper.nologin();
        }

        ChatPetInfo chatPetInfo = chatPetService.getInfo(fanId);

        return respHelper.ok(chatPetInfo);
    }


    /**
     * 当用户禁止授权的时候,只会传state值过来.
     * * @param request
     * @param code
     * @param state
     * @param appId
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/oauth/fan-info", method = RequestMethod.GET)
    public ModelAndView oauth(HttpServletResponse response, HttpServletRequest request, @RequestParam(value = "code",required = false)String code, @RequestParam("state")String state, @RequestParam(value = "appid",required = false)String appId)throws Exception{

        if(!WxOauthService.OAUTH_CODE_URL_STATE.equals(state)){
            return null;
        }

        //微信网页授权处理
        ChatPetSessionVo vo = chatPetService.wxOauthHandle(response,code,appId);

        //fanId存入session
        this.saveSessionUserId(vo.getWxFanId());

        String zebraHtml = chatPetService.getZebraHtmlUrl(vo.getWxPubId());

        response.sendRedirect(zebraHtml);

        return null;
    }

    /**
     * @param
     * @return
     */
    @RequestMapping(value = "/check-login", method = RequestMethod.GET)
    public String asd(@RequestParam("id") Integer wxPubId,HttpServletResponse response) throws Exception {
        Integer userId = getUserId();
        if(userId == null){
            //授权
            response.sendRedirect(chatPetService.getWxOauthUrl(wxPubId));
        }else{
            response.sendRedirect(chatPetService.getZebraHtmlUrl(wxPubId));
        }
        return null;
    }

    /**
     * 完成今日任务领取奖励
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/mission/reward", method = RequestMethod.POST)
    public RespBase rewardAfterCompleteMission(@RequestBody CompleteMissionRewardReq req) throws BizException {
        if(req!=null){
            Log.d("=============== itemid = {?} , chatpetid = {?} ===========",req.getItemId().toString(),req.getChatPetId().toString());
        }
        ChatPetInfo info = chatPetService.rewardHandle(req.getChatPetId(), req.getItemId());

        return respHelper.ok(info);
    }


}
