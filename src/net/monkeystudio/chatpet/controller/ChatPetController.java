package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.RespBase;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.StringUtil;
import net.monkeystudio.chatpet.controller.req.ChatPetIdReq;
import net.monkeystudio.chatpet.controller.req.chatpetmission.CompleteMissionRewardReq;
import net.monkeystudio.chatrbtw.service.ChatPetMissionPoolService;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetSessionVo;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.local.Msg;
import net.monkeystudio.utils.RespHelper;
import net.monkeystudio.wx.service.WxOauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.portlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
    public RespBase getAdClickLogList(@RequestBody ChatPetIdReq chatPetIdReq){

        /*Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }*/
        Integer ChatPetId = chatPetIdReq.getId();

        ChatPetInfo chatPetInfo = chatPetService.getInfo(ChatPetId);

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
        Log.d("============== code = {?}  , state = {?} ,  appid = {?}",code,state,appId);

        if(!WxOauthService.OAUTH_CODE_URL_STATE.equals(state)){
            return null;
        }

        ChatPetSessionVo vo = chatPetService.wxOauthHandle(response,code,appId);


        this.saveSessionUserId(vo.getWxFanId());

        //"https://test.keendo.com.cn/res/wedo/zebra.html?id="
        String zebraHtml = chatPetService.createZebraHtmlUrl();

        response.sendRedirect(zebraHtml+vo.getChatPetId());

        return null;
    }

    /**
     * 完成今日任务领取奖励
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/mission/reward", method = RequestMethod.POST)
    public RespBase rewardAfterCompleteMission(@RequestBody CompleteMissionRewardReq req){

        if(!chatPetService.isFinishNotAwardState(req.getRewardState())){
            return respHelper.failed(Msg.text("can not reward"));
        }

        ChatPetInfo info = chatPetService.rewardHandle(req.getChatPetId(), req.getItemId());

        return respHelper.ok(info);
    }


}
