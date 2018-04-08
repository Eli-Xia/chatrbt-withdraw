package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.admin.controller.req.AddUpdateRole;
import net.monkeystudio.chatrbtw.entity.Role;
import net.monkeystudio.chatrbtw.entity.UserRole;
import net.monkeystudio.chatrbtw.mapper.RoleMapper;
import net.monkeystudio.chatrbtw.mapper.UserRoleMapper;
import net.monkeystudio.chatrbtw.service.bean.user.UserResp;
import net.monkeystudio.entity.User;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.local.Msg;
import net.monkeystudio.mapper.UserMapper;
import net.monkeystudio.service.CfgService;
import net.monkeystudio.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaxin
 */
@Service
public class UserRoleService {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CfgService cfgService;

    public List<Role> getRoleList(){
        return roleMapper.selectAll();
    }

    public void updateUserRoles(Integer userId,List<String> roleCodes){
        userService.setUserRoles(userId,roleCodes);
    }



    public List<UserResp>  getUserDtoList(List<User> users){
        List<UserResp> dtos = new ArrayList<>();
        for(User user:users){
            UserResp userResp = new UserResp();
            BeanUtils.copyProperties(user, userResp);
            List<Role> roles = roleMapper.selectByUserName(user.getUsername());
            userResp.setRoles(roles);
            dtos.add(userResp);
        }
        return dtos;
    }

    /**
     * 检查用户名是否存在
     * @param username
     * @return
     */
    public boolean checkUsernameExist(String username){

        User user = userMapper.selectByUsername(username);
        if( user == null ){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 添加用户
     * @param username
     * @param nickname
     * @param password
     * @throws BizException
     */
    public void addUser(String username, String nickname, String password,List<String> roles) throws BizException{

        //检查参数
        if ( StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            throw new BizException("username or password is empty.");
        }

        if(checkUsernameExist(username)){
            throw new BizException(Msg.text("user.username.exist"));
        }

        String defaultUserRole = cfgService.get("USER_ROLE_REG_DEFAULT");
        if(defaultUserRole == null){
            throw new BizException("Config USER_ROLE_REG_DEFAULT notfound.");
        }

        //添加用户
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword(password);

        user.setAvatar(cfgService.get("AVATAR_DEFAULT"));
        user.setCreateTime(new Date());

        userMapper.insert(user);

        //添加角色
        User dbUser = userService.getUserByName(username);

        List<UserRole> urs = new ArrayList<>();

        if(CollectionUtils.isEmpty(roles)){
            UserRole ur = new UserRole();
            ur.setUserId(dbUser.getId());
            ur.setRole(defaultUserRole);
            urs.add(ur);
        }else{
            for(String role:roles ){
                UserRole ur = new UserRole();
                ur.setRole(role);
                ur.setUserId(dbUser.getId());
                urs.add(ur);
            }
        }
        userRoleMapper.batchInsert(urs);
    }


    public void addRole(AddUpdateRole addUpdateRole) {
        Role role = new Role();
        BeanUtils.copyProperties(addUpdateRole,role);
        roleMapper.insert(role);
    }

    public void updateRole(String oriCode,AddUpdateRole addUpdateRole) {
        Role role = new Role();
        BeanUtils.copyProperties(addUpdateRole,role);
        roleMapper.update(role,oriCode);
    }

    public void deleteRole(String code) {
        roleMapper.delete(code);
    }
}
