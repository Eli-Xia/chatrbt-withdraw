package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.minigame.AddMiniGameReq;
import net.monkeystudio.admin.controller.req.minigame.UpdateMiniGameReq;
import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.service.ChatPetGameCenterService;
import net.monkeystudio.chatrbtw.service.OpLogService;
import net.monkeystudio.chatrbtw.service.WxMiniGameService;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.AdminMiniGame;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.AdminMiniGameAdd;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.AdminMiniGameResp;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.AdminMiniGameUpdate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    @Autowired
    private OpLogService opLogService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public RespBase add(AddMiniGameReq req){
        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if(req.getIsHandpicked() == null){
            return respHelper.failed("编辑精选未选择");
        }
        if(ListUtil.isEmpty(req.getTagIdList())){
            return respHelper.failed("小游戏标签未选择");
        }
        if(req.getHeadImg() == null){
            return respHelper.failed("小游戏头像未上传");
        }
        if(req.getQrCodeImg() == null){
            return respHelper.failed("小游戏二维码未上传");
        }
        if(req.getIsHandpicked() && req.getCoverImg() == null){
            return respHelper.failed("小游戏封面未上传");
        }
        if(req.getNickname() == null){
            return respHelper.failed("小游戏名称不能为空");
        }
        if(req.getOnlineTime() == null){
            return respHelper.failed("需要填写小游戏上线时间");
        }
        if(!wxMiniGameService.isOnlineTimeValid(req.getOnlineTime())){
            return respHelper.failed("上线时间不可为设置当天,至少明天");
        }

        AdminMiniGameAdd adminMiniGameAdd = new AdminMiniGameAdd();
        BeanUtils.copyProperties(req,adminMiniGameAdd);

        Integer addMiniGameId = wxMiniGameService.save(adminMiniGameAdd);

        opLogService.userOper(userId, AppConstants.OP_LOG_TAG_A_MINIGAME, "id为" + userId + "的用户新增id为" + addMiniGameId + "的小游戏" );

        return respHelper.ok();
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public RespBase delete(HttpServletRequest request, @PathVariable("id")Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        wxMiniGameService.delete(id);

        opLogService.userOper(userId, AppConstants.OP_LOG_TAG_A_MINIGAME, "id为" + userId + "的用户删除id为" + id + "的小游戏" );

        return respHelper.ok();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public RespBase update(UpdateMiniGameReq req){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if(req.getIsHandpicked() == null){
            return respHelper.failed("编辑精选未选择");
        }
        if(ListUtil.isEmpty(req.getTagIdList())){
            return respHelper.failed("小游戏标签未选择");
        }
        if(req.getHeadImg() == null){
            return respHelper.failed("小游戏头像未上传");
        }
        if(req.getQrCodeImg() == null){
            return respHelper.failed("小游戏二维码未上传");
        }
        if(req.getIsHandpicked() && req.getCoverImg() == null){
            return respHelper.failed("小游戏封面未上传");
        }
        if(req.getNickname() == null){
            return respHelper.failed("小游戏名称不能为空");
        }
        if(req.getOnlineTime() == null){
            return respHelper.failed("需要填写小游戏上线时间");
        }
        if(!wxMiniGameService.isOnlineTimeValid(req.getOnlineTime())){
            return respHelper.failed("上线时间不可为设置当天,至少明天");
        }

        AdminMiniGameUpdate adminMiniGameUpdate = new AdminMiniGameUpdate();
        BeanUtils.copyProperties(req,adminMiniGameUpdate);

        wxMiniGameService.update(adminMiniGameUpdate);

        opLogService.userOper(userId, AppConstants.OP_LOG_TAG_A_MINIGAME, "id为" + userId + "的用户编辑id为" + req.getId() + "的小游戏" );

        return respHelper.ok();
    }

    @RequestMapping(value = "/{id}/get", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getById(HttpServletRequest request, @PathVariable("id")Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        AdminMiniGame adminMiniGame = wxMiniGameService.getAdminGameById(id);

        return respHelper.ok(adminMiniGame);
    }

    /**
     * 下架
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/unshelve", method = RequestMethod.POST)
    @ResponseBody
    public RespBase unshelve(HttpServletRequest request, @PathVariable("id")Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        wxMiniGameService.unshelve(id);

        opLogService.userOper(userId, AppConstants.OP_LOG_TAG_A_MINIGAME, "id为" + userId + "的用户下架id为" + id + "的小游戏" );

        return respHelper.ok();
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase list(HttpServletRequest request){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        List<AdminMiniGameResp> resps = wxMiniGameService.getAdminMiniGameRespList();

        return respHelper.ok(resps);
    }






}
