package net.monkeystudio.admin.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.monkeystudio.chatrbtw.service.bean.chatlog.ChatStatisticsChartQueryObject;
import net.monkeystudio.local.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.monkeystudio.admin.controller.req.QueryChatLogs;
import net.monkeystudio.base.BaseController;
import net.monkeystudio.base.RespBase;
import net.monkeystudio.chatrbtw.entity.ChatLog;
import net.monkeystudio.chatrbtw.service.ChatLogService;
import net.monkeystudio.utils.RespHelper;


/**
 * 日志管理
 * @author hebo
 *
 */
@Controller
@RequestMapping(value = "/admin/chatlog")
public class ChatLogMgrController extends BaseController{

	@Autowired
	private RespHelper respHelper;
	
	@Autowired
	private ChatLogService chatLogService;
	
	/**
	 * 日志查询列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST) 
	@ResponseBody
	public RespBase getUsers(HttpServletRequest request, @RequestBody QueryChatLogs queryChatLogs){
		
		Integer userId = getUserId();
		if ( userId == null ){
			return respHelper.nologin();
		}
		
		Integer page = queryChatLogs.getPage();
		Integer pageSize = queryChatLogs.getPageSize();
		
		if ( page == null || page < 1 ){
			return respHelper.cliParamError("page error.");
		}
		
		if ( pageSize == null || pageSize < 1 ){
			return respHelper.cliParamError("pageSize error.");
		}
		
		List<ChatLog> chatLogs = chatLogService.getLogs(queryChatLogs);
		
		Integer total = chatLogService.getCount();
		
		return respHelper.ok(chatLogs,total);
	}

	@RequestMapping(value = "/statistic/chatManAndNum/list" ,method = RequestMethod.POST)
	@ResponseBody
	public RespBase yesterdayChatNum(HttpServletRequest request, @RequestBody ChatStatisticsChartQueryObject qo) {

		Integer userId = getUserId();

		if (userId == null) {
			return respHelper.failed(Msg.text("common.user.nologin"));
		}

		List<Map<String, Long>> maps = chatLogService.totalChatManAndNum(qo);

		return respHelper.ok(maps);
	}
	
}
