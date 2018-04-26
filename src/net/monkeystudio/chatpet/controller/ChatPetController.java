package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.RespBase;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.StringUtil;
import net.monkeystudio.chatpet.controller.req.ChatPetIdReq;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetSessionVo;
import net.monkeystudio.exception.BizException;
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
    private RespHelper respHelper;

    private final static String ZEBRA_HTML = "https://test.keendo.com.cn/res/wedo/zebra.html?id=";


    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public RespBase getAdClickLogList(@RequestBody ChatPetIdReq chatPetIdReq){

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

        if(StringUtil.isEmpty(code)){
            //若用户禁止授权 , 测试如果用户禁止授权则跳转到百度页面.
            response.sendRedirect("https://www.baidu.com/");
        }

        ChatPetSessionVo vo = chatPetService.wxOauthHandle(response,code,appId);


        this.saveSessionUserId(vo.getWxFanId());

        response.sendRedirect(ZEBRA_HTML+vo.getChatPetId());

        return null;
    }
}
