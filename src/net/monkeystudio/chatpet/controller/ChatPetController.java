package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.chatpetmission.ChatPetRewardReq;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetRewardChangeInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetSessionVo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.CreationPost;
import net.monkeystudio.wx.service.WxOauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.portlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bint on 2018/4/19.
 */
@Controller
@RequestMapping(value = "/chat-pet/pet")
public class ChatPetController extends ChatPetBaseController{

    @Autowired
    private ChatPetService chatPetService;


    @Autowired
    private RespHelper respHelper;


    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public RespBase getAdClickLogList(HttpServletRequest request,HttpServletResponse response){
        Integer fanId = getUserId();

        if(fanId == null){
            return respHelper.nologin();
        }

        ChatPetInfo chatPetInfo = chatPetService.getInfoByWxFanId(fanId);

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
    public ModelAndView oauth(HttpServletResponse response, HttpServletRequest request, @RequestParam(value = "code",required = false)String code, @RequestParam("state")String state, @RequestParam(value = "appid",required = false)String appId) throws BizException, IOException {

        if(!WxOauthService.OAUTH_CODE_URL_STATE.equals(state)){
            return null;
        }

        //微信网页授权处理
        ChatPetSessionVo vo = chatPetService.wxOauthHandle(code,appId);

        if(vo == null){
            return null;
        }

        //fanId存入session
        this.saveSessionUserId(vo.getWxFanId());

        String homePageUrl = chatPetService.getHomePageUrl(vo.getWxPubId());

        response.sendRedirect(homePageUrl);

        return null;
    }


    @RequestMapping(value = "/home-page", method = RequestMethod.GET)
    public String homePage(@RequestParam("id") Integer wxPubId,HttpServletResponse response,HttpServletRequest request)  {
        try {
            Integer userId = getUserId();

            if(userId == null){
                //网页授权
                response.sendRedirect(chatPetService.getWxOauthUrl(wxPubId));
            }else{
                //判断是否为跨公众号同session
                if(!chatPetService.isNeed2EmptyUser4Session(userId,wxPubId)){

                    saveSessionUserId(null);

                    response.sendRedirect(chatPetService.getHomePageUrl(wxPubId));

                    return null;
                }

                if(chatPetService.isAble2Access(userId,wxPubId)){

                    chatPetService.dataPrepared(userId,wxPubId);

                    response.sendRedirect(chatPetService.getChatPetPageUrl(wxPubId));

                }else{
                    response.sendRedirect(chatPetService.getChatPetPosterUrl());
                }

            }
        }catch (Exception e){
            Log.e(e);
        }

        return null;
    }

    /**
     * 测试接口
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public RespBase login(@RequestParam("id") String wxFanId, HttpServletResponse response, HttpServletRequest request) {
        if(!wxFanId.startsWith("keendo")){
            return respHelper.failed("fail");
        }
        int i = wxFanId.lastIndexOf(".");
        String wxFanIdStr = wxFanId.substring(i + 1);
        int id = Integer.parseInt(wxFanIdStr);
        this.saveSessionUserId(id);
        ChatPetInfo info = chatPetService.getInfoByWxFanId(id);
        return respHelper.ok(info);
    }

    /**
     * 完成今日任务领取奖励
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/mission/reward", method = RequestMethod.POST)
    public RespBase rewardAfterCompleteMission(@RequestBody ChatPetRewardReq req) throws BizException {

        Integer userId = this.getUserId();

        if(userId == null){
            return respHelper.nologin();
        }


        ChatPetRewardChangeInfo changeInfo = chatPetService.rewardHandle(userId, req.getRewardItemId());

        return respHelper.ok(changeInfo);
    }


    @ResponseBody
    @RequestMapping(value = "/creation-post", method = RequestMethod.POST)
    public RespBase getcreationPost() throws BizException {
        Integer wxFanId = this.getUserId();

        if(wxFanId == null){
            return respHelper.nologin();
        }

        CreationPost creationPost = chatPetService.getCreationPost(wxFanId);

        return respHelper.ok(creationPost);
    }
}
