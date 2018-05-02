package net.monkeystudio.base.shiro;

import net.monkeystudio.base.Constants;
import net.monkeystudio.chatrbtw.entity.Role;
import net.monkeystudio.chatrbtw.entity.User;
import net.monkeystudio.chatrbtw.mapper.RoleMapper;
import net.monkeystudio.chatrbtw.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liujinhua on 2017/4/19.
 */
public class MyRealm extends AuthorizingRealm {
	
    @Autowired
    UserService userService;

    @Autowired
    RoleMapper roleMapper;
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if(principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        // AuthorizingRealm base on roles control
        String username = (String)this.getAvailablePrincipal(principals);
        List<Role> roleList = roleMapper.selectByUserName(username);
        Set<String> roleSet = new LinkedHashSet<>();
        for(Role role : roleList){
            roleSet.add(role.getCode());
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleSet);

        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken)token;
        String username = upToken.getUsername();

        if(username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }

        User user = userService.getUserByName(username);
        if(user == null){
            throw new UnknownAccountException("user not exist");
        }
        if ( user.getStatus() != Constants.USER_STATUS_ACTIVE){
        	throw new AccountException("User no active.");
        }
        
        SimpleAuthenticationInfo info = null;
        info = new SimpleAuthenticationInfo(username, user.getPassword().toCharArray(), this.getName());
//        info.setCredentialsSalt(ByteSource.Util.bytes("")); 如果是使用MD5方式可以考虑使用这个
        return  info;
    }
}
