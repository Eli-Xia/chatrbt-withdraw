package net.monkeystudio.admin.controller;

import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.ChatPetGameCenterService;
import net.monkeystudio.chatrbtw.service.WxMiniGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiaxin
 */
@RequestMapping(value = "/admin/chat-pet/game-center")
@Controller
public class ChatPetGameCenterMgrController extends BaseController{
    @Autowired
    private RespHelper respHelper;
    @Autowired
    private ChatPetGameCenterService chatPetGameCenterService;
    @Autowired
    private WxMiniGameService wxMiniGameService;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ResponseBody
    public RespBase add(@RequestParam("headImg")MultipartFile headImg,@RequestParam("qrCodeImg")MultipartFile qrCodeImg,@RequestParam("nickname")String nickname){
        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        return respHelper.ok(null);
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public RespBase update(HttpServletRequest request ){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }


        return respHelper.ok(null);
    }

}
