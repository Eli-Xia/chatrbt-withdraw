package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatpet.controller.req.MoreLogReq;
import net.monkeystudio.chatpet.controller.req.chatpetmission.ChatPetRewardReq;
import net.monkeystudio.chatrbtw.MiniProgramChatPetService;
import net.monkeystudio.chatrbtw.service.ChatPetRewardService;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.bean.chatpet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.portlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by bint on 2018/4/19.
 */
@Controller
@RequestMapping(value = "/chat-pet/pet")
public class ChatPetController extends ChatPetBaseController{

    @Autowired
    private ChatPetService chatPetService;
    @Autowired
    private MiniProgramChatPetService miniProgramChatPetService;
    @Autowired
    private ChatPetRewardService chatPetRewardService;
    @Autowired
    private RespHelper respHelper;

//    @ResponseBody
//    @RequestMapping(value = "/more-log", method = RequestMethod.POST)
//    public RespBase getMoreLog(@RequestBody MoreLogReq moreLogReq){
//        Integer fanId = getUserId();
//
//        List<PetLogResp> list = chatPetService.getChatPetLogAfterMore(fanId, moreLogReq.getPageSize());
//
//        return respHelper.ok(list);
//    }



    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public RespBase getChatPetInfo(HttpServletRequest request,HttpServletResponse response){
        Integer fanId = getUserId();

        ChatPetInfo chatPetInfo = chatPetService.getInfoByWxFanId(fanId);

        return respHelper.ok(chatPetInfo);
    }

    @ResponseBody
    @RequestMapping(value = "/mini-program/info", method = RequestMethod.POST)
    public RespBase getMiniAppChatPetInfo(HttpServletRequest request,HttpServletResponse response){
        Integer fanId = getUserId();

        ChatPetInfo chatPetInfo = miniProgramChatPetService.getInfoByFanId(fanId);

        return respHelper.ok(chatPetInfo);
    }



    /*@ResponseBody
    @RequestMapping(value = "/generate-cat", method = RequestMethod.GET)
    public RespBase getChatPetInfo(@RequestParam("parentFanId")Integer parentFanId){
        Integer fanId = getUserId();
        miniProgramChatPetService.inviteFriendHandle(fanId,parentFanId);
        return respHelper.ok();
    }*/





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
    public ModelAndView oauth(HttpServletResponse response, HttpServletRequest request, @RequestParam(value = "code",required = false)String code, @RequestParam("state")String state, @RequestParam(value = "appid",required = false)String appId) throws Exception {

        String pageUri = state;//跳转页面uri
        Log.d("=============== chatpetcontroller oauth redirect args : pageUri = {?} ==================",pageUri);

        //微信网页授权处理
        ChatPetSessionVo vo = chatPetService.wxOauthHandle(code,appId);

        if(vo == null){
            return null;
        }

        //fanId存入session
        this.saveSessionUserId(vo.getWxFanId());

        if(ChatPetService.DEFAULT_PAGE_URI.equals(pageUri)){

            String homePageUrl = chatPetService.getHomePageUrl(vo.getWxPubId());
            response.sendRedirect(homePageUrl);
        }else{
            //response.sendRedirect(chatPetService.getPageRedirectUrlByUrlEncoder(pageUri));
            response.sendRedirect(chatPetService.getPageRedirectUrl(pageUri));
        }
        return null;
    }


    /**
     *
     * @param wxPubId       公众号id
     * @param anchor        锚点
     * @param redirectUri  未登录页面重定向uribase64
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/home-page", method = RequestMethod.GET)
    public String homePage(@RequestParam("id") Integer wxPubId,@RequestParam(value = "anchor",required = false)String anchor, @RequestParam(value = "redirectUri",required = false)String redirectUri,HttpServletResponse response,HttpServletRequest request) throws Exception {
        Log.d("======================  home-page wxPubId = [?] , redirectUri = [?] ==============",wxPubId.toString(),redirectUri);
        try {
            Integer userId = getUserId();

            if(userId == null){
                //网页授权
                response.sendRedirect(chatPetService.getWxOauthUrl(wxPubId, redirectUri));
            }else{
                //判断是否为跨公众号同session
                if(!chatPetService.isNeed2EmptyUser4Session(userId,wxPubId)){

                    saveSessionUserId(null);

                    response.sendRedirect(chatPetService.getHomePageUrl(wxPubId));

                    return null;
                }

                if(chatPetService.isAble2Access(userId,wxPubId)){

                    chatPetService.dataPrepared(userId,wxPubId);

                    if(redirectUri == null){

                        response.sendRedirect(chatPetService.getChatPetPageUrl(wxPubId,anchor));//跳转我的魔宠
                    }else{
                        response.sendRedirect(chatPetService.getPageRedirectUrl(redirectUri));//跳转其他页面
                    }

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
     * 登出接口
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public RespBase login(@RequestParam("id") String wxFanId, HttpServletResponse response, HttpServletRequest request) {
        if(!wxFanId.startsWith("keendo")){
            return respHelper.failed("fail");
        }
        int i = wxFanId.lastIndexOf(".");
        String wxFanIdStr = wxFanId.substring(i + 1);
        int id = Integer.parseInt(wxFanIdStr);
        saveSessionUserId(null);
        return respHelper.ok();
    }

    /**
     * 登录接口
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public RespBase logout(@RequestParam("id") String wxFanId, HttpServletResponse response, HttpServletRequest request) {
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

        Integer wxFanId = getUserId();

        if(!chatPetRewardService.checkRewardState(req.getRewardItemId())){
            return respHelper.failed("奖励已领取");
        }

        if(!chatPetRewardService.checkRewardOwner(wxFanId,req.getRewardItemId())){
            return respHelper.failed("无法领取");
        }

        ChatPetRewardChangeInfo changeInfo = chatPetService.rewardHandle(wxFanId, req.getRewardItemId());

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

    @ResponseBody
    @RequestMapping(value = "/gold/list", method = RequestMethod.POST)
    public RespBase getRewardGoldList(){
        Integer fanId = getUserId();

        if(fanId == null){
            return respHelper.nologin();
        }

        List<ChatPetGoldItem> goldList = chatPetService.getRewardListByWxFanId(fanId);

        return respHelper.ok(goldList);
    }




}
