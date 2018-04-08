package net.monkeystudio.portal.controller;

import java.util.List;

import net.monkeystudio.chatrbtw.service.bean.chatlog.ChatStatisticsChartQueryObject;
import net.monkeystudio.chatrbtw.service.bean.chatlog.YesterdayChatManVO;
import net.monkeystudio.chatrbtw.service.bean.chatlog.YesterdayChatNumVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import net.monkeystudio.base.RespBase;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.chatrbtw.service.ChatLogService;
import net.monkeystudio.chatrbtw.service.bean.chatlog.ChatLogListItem;
import net.monkeystudio.local.Msg;
import net.monkeystudio.portal.controller.req.chatlog.QueryChatLog;
import net.monkeystudio.utils.RespHelper;
import net.monkeystudio.wx.service.WxPubService;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bint on 26/12/2017.
 */
@RequestMapping(value = "/chat-log")
@Controller
public class PortalChatLogController extends PortalBaseController{

    @Autowired
    private ChatLogService chatLogService;

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private WxPubService wxPubService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase list( @RequestBody QueryChatLog queryChatLog){
        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }

        if(!wxPubService.hasPub(queryChatLog.getWxPubOriginId() ,userId)){
            return respHelper.failed("This user does not have permission of the wxPub");
        }

        List<ChatLogListItem> chatLogListItemList = chatLogService.getFuzzyChatLogList(queryChatLog.getPage(),queryChatLog.getPageSize(),queryChatLog.getWxPubOriginId());

        Integer count = chatLogService.getCount(queryChatLog.getWxPubOriginId());

        return respHelper.ok(chatLogListItemList,count);
    }

    /**
     *  昨日聊天人数
     * @return
     */
    @RequestMapping(value = "/{wxPubOriginId}/chatMan-chart")
    @ResponseBody
    public RespBase yesterdayChatMan(HttpServletRequest request,@RequestBody ChatStatisticsChartQueryObject qo,@PathVariable("wxPubOriginId")String wxPubOriginId){
        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }
        //endDate必须是昨天或昨天以前
        if(!DateUtils.isAtLeastYesterday(qo.getEndDate())){
            return respHelper.failed(" endDate must be at least yesterday! ");
        }
        YesterdayChatManVO yesterdayChatManVO = chatLogService.getYesterdayChatManVO(qo,wxPubOriginId);
        return respHelper.ok(yesterdayChatManVO);
    }

    /**
     * 昨日聊天次数
     * @return
     */
    @RequestMapping(value = "/{wxPubOriginId}/chatNum-chart")
    @ResponseBody
    public RespBase yesterdayChatNum(HttpServletRequest request, @RequestBody ChatStatisticsChartQueryObject qo,@PathVariable("wxPubOriginId")String wxPubOriginId){
        Integer userId = getUserId();

        if(userId == null){
            return respHelper.failed(Msg.text("common.user.nologin"));
        }
        if(!DateUtils.isAtLeastYesterday(qo.getEndDate())){
            return respHelper.failed(" endDate must be at least yesterday! ");
        }
        YesterdayChatNumVO yesterdayChatNumVO = chatLogService.getYesterdayChatNumVO(qo,wxPubOriginId);
        return respHelper.ok(yesterdayChatNumVO);
    }

}

