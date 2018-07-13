package net.monkeystudio.admin.controller;

import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.service.ChatPetGameCenterService;
import net.monkeystudio.chatrbtw.service.WxMiniGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public RespBase add(@RequestParam("headImg")MultipartFile headImg,@RequestParam("qrCodeImg")MultipartFile qrCodeImg,
                        @RequestParam("nickname")String nickname,@RequestParam("needSign")Integer needSign){
        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        wxMiniGameService.save(headImg,qrCodeImg,nickname,needSign);

        return respHelper.ok();
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public RespBase update(HttpServletRequest request, @PathVariable("id")Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        wxMiniGameService.delete(id);

        return respHelper.ok();
    }

}
