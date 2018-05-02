package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.AddUser;
import net.monkeystudio.admin.controller.req.ChangePassword;
import net.monkeystudio.admin.controller.req.DelUser;
import net.monkeystudio.admin.controller.req.Login;
import net.monkeystudio.admin.controller.req.QueryUserList;
import net.monkeystudio.admin.controller.req.UpdateUser;
import net.monkeystudio.admin.controller.resp.UserDetail;
import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.entity.Role;
import net.monkeystudio.chatrbtw.entity.User;
import net.monkeystudio.chatrbtw.entity.UserProfile;
import net.monkeystudio.chatrbtw.local.Msg;
import net.monkeystudio.chatrbtw.service.OpLogService;
import net.monkeystudio.chatrbtw.service.UserRoleService;
import net.monkeystudio.chatrbtw.service.UserService;
import net.monkeystudio.chatrbtw.service.bean.user.UserResp;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 用户接口
 * @author hebo
 *
 */
@Controller
@RequestMapping(value = "/admin/user")
public class UserController extends BaseController {

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private UserService userService;

    @Autowired
    private CfgService cfgService;

    @Autowired
    private UserRoleService userRoleService;
    
    @Autowired
    private OpLogService opLogService;

    /**
     * 用户登录
     * @param request
     * @param login 登录参数
     * @param redirectUri 登录成功跳转路径
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public RespBase login(HttpServletRequest request, @RequestBody Login login, @RequestParam("redirectUri") String redirectUri){

        if ( StringUtils.isBlank(login.getUsername())){
            return respHelper.failed("username is empty.");
        }

        if ( StringUtils.isBlank(login.getPassword())){
            return respHelper.failed("password is empty.");
        }

        //用户登录鉴权
        UsernamePasswordToken token = new UsernamePasswordToken(login.getUsername(), login.getPassword());
        token.setRememberMe(false);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.logout(); //登录前先退出登录，目的是清除realm缓存
            subject.login(token);

            if ( !rolesAllowLogin(subject)){
                subject.logout();
                return respHelper.authFailed();
            }

            if (subject.isAuthenticated()) {
                String username = (String) subject.getPrincipal();
                User user = userService.getUserByName(username);
                if(user == null){
                    Log.e("user not exist");
                    return respHelper.failed(Msg.text("api.login.failed"));
                }

                /**
                 * 这里potal与admin存入session的属性名不同,通过不同属性名进行区分前后台用户。
                 */
                this.saveSessionUserId(user.getId());

                //登录成功返回用户基本信息
                UserProfile up = userService.getUserProfile(user.getId());

                opLogService.userOper(up.getId(), AppConstants.OP_LOG_TAG_A_LOGIN, "用户[" + up.getNickname() + "]登录管理后台。");
                return respHelper.ok(up);
            } else {
                return respHelper.authFailed();
            }
        } catch (IncorrectCredentialsException e){
            return respHelper.failed(Msg.text("api.login.failed"));
        }catch (UnknownAccountException e){
            return respHelper.failed(Msg.text("api.login.failed"));
        }catch (Exception e) {
            Log.e(e.getMessage());
            return respHelper.failed(Msg.text("api.login.failed"));
        }

    }

    private boolean rolesAllowLogin(Subject subject){

        String allowRoles = cfgService.get("ADMIN_LOGIN_ALLOW_ROLES");
        List<String> allowRoleList = CommonUtils.splitString2List(allowRoles, ",");
        for ( String role : allowRoleList){
            if ( subject.hasRole(role) ){
                return true;
            }
        }

        return false;
    }

    /**
     * 用户退出登录
     * @param request
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public RespBase logout(HttpServletRequest request){

        //todo:注销SESSION，退出登录
        Subject subject = SecurityUtils.getSubject();
        try {
            if(subject!=null){
                subject.logout();
            }
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return respHelper.ok();
    }

    /**
     * 检查用户登录状态
     * @param request
     * @return
     */
    @RequestMapping(value = "/login/stat", method = RequestMethod.GET)
    @ResponseBody
    public RespBase loginstat(HttpServletRequest request){

    	Integer userId = getUserId();
    	if ( userId != null ){
    		return respHelper.ok(true);
    	}else{
    		return respHelper.ok(false);
    	}
        //todo:检查当前SESSION信息，判断用户是否登录。
//        Subject subject = SecurityUtils.getSubject();
//        boolean hasLogin = false;
//        if(subject.isAuthenticated()){
//            hasLogin = true;
//        }else{
//            hasLogin = false;
//        }
//        return respHelper.ok(hasLogin);
    }



    /**
     * 修改密码
     * @param request
     * @param changePassword
     * @return
     */
    @RequestMapping(value = "/password/change", method = RequestMethod.POST)
    @ResponseBody
    public RespBase changePassword(HttpServletRequest request, @RequestBody ChangePassword changePassword){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if ( StringUtils.isBlank(changePassword.getCurPassword())){
            return respHelper.cliParamError("curPassword is empty.");
        }

        if ( StringUtils.isBlank(changePassword.getNewPassword())){
            return respHelper.cliParamError("newPassword is etmpy.");
        }

        User user;
        try {
            user = userService.getUser(userId);

            if ( !userService.auth(user.getUsername(), changePassword.getCurPassword()) ){
                return respHelper.failed(Msg.text("user.password.error"));
            }

            userService.updatePassword(userId, changePassword.getNewPassword());

        } catch (BizException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return respHelper.failed(e.getBizExceptionMsg());
        }

        return respHelper.ok();
    }


    /**
     * 获取用户列表
     * @param request
     * @param queryUserList
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getUsers(HttpServletRequest request, @RequestBody QueryUserList queryUserList){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        Integer page = queryUserList.getPage();
        Integer pageSize = queryUserList.getPageSize();
        if ( page == null || page < 1 ){
            return respHelper.cliParamError("page error.");
        }

        if ( pageSize == null || pageSize < 1 ){
            return respHelper.cliParamError("pageSize error.");
        }

        List<User> users = userService.getUsers(page,pageSize);

        List<UserResp> userResps = userRoleService.getUserDtoList(users);

        Integer total = userService.getCount();

        return respHelper.ok(userResps,total);
    }

    /**
     * 获取当前登录用户自己信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/curuser/detail", method = RequestMethod.GET)
    @ResponseBody
    public RespBase getCurUserDetail(HttpServletRequest request){
        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        User user;
        try {
            user = userService.getUser(userId);
        } catch (BizException e) {
            return respHelper.failed(e.getBizExceptionMsg());
        }

        List<String> roles = userService.getUserRoles(userId);

        UserDetail userDetail = new UserDetail(user,roles);

        return respHelper.ok(userDetail);
    }


    /**
     * 添加用户
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public RespBase addUser(HttpServletRequest request, @RequestBody AddUser addUser){

        if( StringUtils.isBlank(addUser.getUsername()) ){
            return respHelper.cliParamError("username");
        }

        if( StringUtils.isBlank(addUser.getNickname()) ){
            return respHelper.cliParamError("nickname");
        }

        if( StringUtils.isBlank(addUser.getPassword()) ){
            return respHelper.cliParamError("password");
        }


        try {
            userRoleService.addUser(addUser.getUsername(),addUser.getNickname(),addUser.getPassword(),addUser.getRoles());
        } catch (BizException e) {
            return respHelper.failed(e.getBizExceptionMsg());
        }

        return respHelper.ok();
    }

    /**
     * 更新用户信息
     * @param request
     * @param updateUser
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public RespBase updateUser(HttpServletRequest request, @RequestBody UpdateUser updateUser){

        if( updateUser.getUserId() == null ){
            return respHelper.cliParamError("userId");
        }

        if( updateUser.getUserId() == 1){
            return respHelper.authFailed();
        }

        if( StringUtils.isBlank(updateUser.getUsername()) ){
            return respHelper.cliParamError("username");
        }

        if( StringUtils.isBlank(updateUser.getNickname()) ){
            return respHelper.cliParamError("nickname");
        }

        if(CollectionUtils.isEmpty(updateUser.getRoles())){
            return respHelper.cliParamError("role must be at least one");
        }

        try {
            userService.updateUser(updateUser.getUserId(),updateUser.getUsername(),updateUser.getNickname(),updateUser.getPassword(),updateUser.getAvatar());
            userRoleService.updateUserRoles(updateUser.getUserId(),updateUser.getRoles());
        } catch (BizException e) {
            return respHelper.failed(e.getBizExceptionMsg());
        }

        return respHelper.ok();
    }

    /**
     * 册除用户
     * @param request
     * @param delUser
     * @return
     */
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public RespBase delUser(HttpServletRequest request, @RequestBody DelUser delUser){

        if( delUser.getUserId() == null ){
            return respHelper.cliParamError("userId");
        }

        if( delUser.getUserId() == 1){
            return respHelper.authFailed();
        }

        userService.delUser(delUser.getUserId());

        return respHelper.ok();
    }


    /**
     * 获取单个用户信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/{userId}/detail", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getUserDetail(HttpServletRequest request, @PathVariable("userId") Integer userId){

        Integer loginUserId = getUserId();
        if ( loginUserId == null ){
            return respHelper.nologin();
        }

        User user;
        try {
            user = userService.getUser(userId);
        } catch (BizException e) {
            return respHelper.failed(e.getBizExceptionMsg());
        }

        List<String> roles = userService.getUserRoles(userId);

        UserDetail userDetail = new UserDetail(user,roles);

        return respHelper.ok(userDetail);
    }

    @RequestMapping(value = "/role/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getAllRoles(HttpServletRequest request){

        Integer loginUserId = getUserId();
        if ( loginUserId == null ){
            return respHelper.nologin();
        }

        List<Role> roles = userRoleService.getRoleList();

        return respHelper.ok(roles);
    }



}
