package net.monkeystudio.admin.controller;

import java.util.List;

import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.monkeystudio.admin.controller.req.oplog.QueryOpLogList;
import net.monkeystudio.chatrbtw.entity.OpLog;
import net.monkeystudio.chatrbtw.service.OpLogService;

/**
 * 操作日志管理接口
 * @author hebo
 *
 */
@Controller
@RequestMapping(value = "/admin/oplog")
public class OpLogController extends BaseController {

	@Autowired
	private RespHelper respHelper;
	
	@Autowired
	private OpLogService opLogService;
	
	/**
	 * 操作日志列表
	 * @param queryOpLogList
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
    public RespBase getOpLogList(@RequestBody  QueryOpLogList queryOpLogList) throws Exception {
		
		List<OpLog> list = opLogService.getOpLogList(queryOpLogList.getMap());
		Integer total = opLogService.getOpLogCount(queryOpLogList.getMap());
		
		return respHelper.ok(list,total);
	}
}
