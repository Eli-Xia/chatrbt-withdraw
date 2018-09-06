package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.dividendmsg.AddDividendMsg;
import net.monkeystudio.admin.controller.req.dividendmsg.UpdateDividendMsg;
import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.DividendMsg;
import net.monkeystudio.chatrbtw.service.DividendMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping(value = "/admin/dividend-msg")
@Controller
public class DividendMsgController extends BaseController {

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private DividendMsgService dividendMsgService;

    /**
     * 分红消息模板列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getMsgs(HttpServletRequest request) {
        Integer userId = getUserId();
        if (userId == null) {
            return respHelper.nologin();
        }
        List<DividendMsg> msgs = dividendMsgService.getMsgs();
        return respHelper.ok(msgs);
    }

    /**
     * 根据id查看
     *
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/get", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getById(HttpServletRequest request, @PathVariable("id") Integer id) {

        Integer userId = getUserId();
        if (userId == null) {
            return respHelper.nologin();
        }

        DividendMsg dm = dividendMsgService.getById(id);
        return respHelper.ok(dm);
    }

    /**
     * 编辑模板
     * @param updateDividendMsg
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public RespBase update(@RequestBody UpdateDividendMsg updateDividendMsg) {

        Integer userId = getUserId();
        if (userId == null) {
            return respHelper.nologin();
        }

        Integer id = updateDividendMsg.getId();
        String content = updateDividendMsg.getContent();
        String description = updateDividendMsg.getDescription();

        dividendMsgService.update(id, content, description);

        return respHelper.ok();
    }

    /**
     * 新增模板
     * @param addDividendMsg
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public RespBase save(@RequestBody AddDividendMsg addDividendMsg) {
        Integer userId = getUserId();
        if (userId == null) {
            return respHelper.nologin();
        }

        String content = addDividendMsg.getContent();
        String description = addDividendMsg.getDescription();

        dividendMsgService.save(content, description);

        return respHelper.ok();
    }


}
