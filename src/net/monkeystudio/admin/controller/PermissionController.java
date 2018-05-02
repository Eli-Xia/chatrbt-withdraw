package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.AddUpdateRole;
import net.monkeystudio.admin.controller.req.QueryResList;
import net.monkeystudio.admin.controller.req.res.AddUpdateRes;
import net.monkeystudio.admin.controller.resp.ResForResp;
import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.Role;
import net.monkeystudio.chatrbtw.service.PermissionService;
import net.monkeystudio.chatrbtw.service.UserRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author xiaxin
 */
@RequestMapping(value = "/admin/permission")
@Controller
public class PermissionController extends BaseController {
    @Autowired
    private RespHelper respHelper;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 权限列表
     * @param request
     * @param queryResList
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getRes(HttpServletRequest request, @RequestBody QueryResList queryResList){

        /*Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }*/

        Integer page = queryResList.getPage();
        Integer pageSize = queryResList.getPageSize();

        if ( page == null || page < 1 ){
            return respHelper.cliParamError("page error.");
        }

        if ( pageSize == null || pageSize < 1 ){
            return respHelper.cliParamError("pageSize error.");
        }

        List<ResForResp> resList = permissionService.getRes(page, pageSize);

        Integer count = permissionService.getCount();

        return respHelper.ok(resList, count);


    }

    /**
     * 根据id获得权限
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getById(HttpServletRequest request , @PathVariable("id") Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        ResForResp resById = permissionService.getResById(id);

        return respHelper.ok(resById);
    }

    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
    @ResponseBody
    public RespBase update(HttpServletRequest request , @PathVariable("id") Integer id ,@RequestBody AddUpdateRes addUpdateRes){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if(!permissionService.checkoutRes(addUpdateRes.getRes())){
            respHelper.failed("res error");
        }
        permissionService.update(id,addUpdateRes);


        return respHelper.ok();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public RespBase add(HttpServletRequest request ,@RequestBody AddUpdateRes addUpdateRes){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if(!permissionService.checkoutRes(addUpdateRes.getRes())){
            return respHelper.cliParamError("res error");
        }

        permissionService.save(addUpdateRes);

        return respHelper.ok();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase add(HttpServletRequest request ,@PathVariable("id") Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        permissionService.delete(id);

        return respHelper.ok();
    }


    @RequestMapping(value = "/role/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase roleList(HttpServletRequest request){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        List<Role> roles = userRoleService.getRoleList();

        return respHelper.ok(roles);
    }

    @RequestMapping(value = "/role/add", method = RequestMethod.POST)
    @ResponseBody
    public RespBase addRole(HttpServletRequest request,@RequestBody AddUpdateRole addUpdateRole){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        userRoleService.addRole(addUpdateRole);

        return respHelper.ok();
    }

    @RequestMapping(value = "/role/{oriCode}/update", method = RequestMethod.POST)
    @ResponseBody
    public RespBase updateRole(HttpServletRequest request,@PathVariable String oriCode ,@RequestBody AddUpdateRole addUpdateRole){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if(StringUtils.isEmpty(addUpdateRole.getCode()) ){
            respHelper.cliParamError("code error");
        }

        if(StringUtils.isEmpty(addUpdateRole.getName())){
            respHelper.cliParamError("name error");
        }

        userRoleService.updateRole(oriCode,addUpdateRole);

        return respHelper.ok();
    }

    @RequestMapping(value = "/role/{code}/delete", method = RequestMethod.POST)
    @ResponseBody
    public RespBase deleteRole(HttpServletRequest request,@PathVariable String code){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        userRoleService.deleteRole(code);

        return respHelper.ok();
    }




















}
