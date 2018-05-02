package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.Constants;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.local.Msg;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.bean.UserDetail;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.chatrbtw.entity.Role;
import net.monkeystudio.chatrbtw.entity.User;
import net.monkeystudio.chatrbtw.entity.UserProfile;
import net.monkeystudio.chatrbtw.entity.UserRole;
import net.monkeystudio.chatrbtw.mapper.RoleMapper;
import net.monkeystudio.chatrbtw.mapper.UserMapper;
import net.monkeystudio.chatrbtw.mapper.UserRoleMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 用户功能逻辑
 * @author hebo
 *
 */
@Component
public class UserService {

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private UserRoleMapper userRoleMapper;
	
	@Autowired
	private CfgService cfgService;
	
	@Autowired
	private EmailService emailService;
	
	private static List<Role> roleListCache = new ArrayList<Role>();
	

	/**
	 * 添加用户
	 * @param username
	 * @param nickname
	 * @param password
	 * @throws BizException
	 */
	public void addUser(String username, String nickname, String password) throws BizException{
		
		String defaultUserRole = cfgService.get("USER_ROLE_REG_DEFAULT");
		if(defaultUserRole == null){
			throw new BizException("Config USER_ROLE_REG_DEFAULT notfound.");
		}
		
		List<String> roles = new ArrayList<String>();
		roles.add(defaultUserRole);
		addUser(username, nickname, password, roles);
	}

	/**
	 * 添加用户
	 * @param username
	 * @param nickname
	 * @param password
	 * @throws BizException
	 */
	public void addUser(String username, String nickname, String password, List<String> roles) throws BizException{
		
		//检查参数
		if ( StringUtils.isBlank(username) || StringUtils.isBlank(password)){
			throw new BizException("username or password is empty.");
		}
		
		if(checkUsernameExist(username)){
			throw new BizException(Msg.text("user.username.exist"));
		}
			
		//添加用户
		User user = new User();
		user.setUsername(username);
		user.setNickname(nickname);
		user.setPassword(password);
		
		user.setAvatar(cfgService.get("AVATAR_DEFAULT"));
		Integer defaultStatus = cfgService.getInteger("USER_ADD_DEFAULT_STATUS");
		if ( defaultStatus == null ){
			defaultStatus = Constants.USER_STATUS_ACTIVE;
		}
		user.setStatus(defaultStatus);
		user.setCreateTime(new Date());
		
		userMapper.insert(user);	
		
		//添加角色
		User dbUser = this.getUserByName(username);
		
		for ( String role : roles ){
			UserRole ur = new UserRole();
			ur.setUserId(dbUser.getId());
			ur.setRole(role);
			userRoleMapper.insert(ur);
		}
	}


	/**
	 * 更新用户。
	 * 密码为空（null或“”）表示不更新，其它字段不管任何值直接更新。
	 * @param userId
	 * @param username
	 * @param nickname
	 * @param password
	 * @param avatar
	 * @throws BizException
	 */
	public void updateUser( Integer userId, String username, String nickname, String password, String avatar ) throws BizException{
		
		User user = this.getUser(userId);
		
		if ( !StringUtils.isBlank(username) && !user.getUsername().equals( username)){
			if(checkUsernameExist(username)){
				throw new BizException(Msg.text("user.username.exist"));
			}else{
				user.setUsername(username);
			}
		}
		
		if ( !StringUtils.isBlank(nickname)){
			user.setNickname(nickname);
		}
		
		if ( !StringUtils.isBlank( password )){
			user.setPassword(password);
		}
		
		if ( !StringUtils.isBlank(avatar)){
			user.setAvatar(avatar);
		}
		
		userMapper.updateByPrimaryKey(user);
	}
	
	public void update(Integer userId, Map<String,Object> params) throws BizException{
		
		params.put("id", userId);
		userMapper.updateByPrimaryKeySelectiveWithMapParams(params);
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
	 * 用户名-密码 鉴权
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean auth(String username, String password){
		
		if ( StringUtils.isBlank(username) || StringUtils.isBlank(password)){
			return false;
		}
		
		User user = userMapper.selectByUsername(username);
		if ( user == null ){
			return false;
		}
		
		if ( password.equals(user.getPassword())){
			return true;
		}
		
		return false;
	}

	public User getUserByName(String username) {
		return userMapper.selectByUsername(username);
	}
	
	/**
	 * 根据用户ID获取用户信息
	 * @param userId
	 * @return
	 * @throws BizException 
	 */
	public User getUser(Integer userId) throws BizException{
		User user = userMapper.select(userId);
		if ( user == null ){
			throw new BizException(Msg.text("common.user.notfound"));
		}
		
		//默认头像
		if ( user.getAvatar() == null ){
			String avatar = cfgService.get("USER_AVATAR_DEFAULT");
			if ( avatar != null ){
				user.setAvatar(avatar);
			}
		}
		
		return user;
	}
	
	/**
	 * 获取用户详请
	 * @param userId
	 * @return
	 * @throws BizException
	 */
	public UserDetail getUserDetail(Integer userId) throws BizException{
		User user = getUser(userId);
		List<Role> roles = this.getUserRoleList(userId);
		UserDetail ud = new UserDetail(user,roles);
		return ud;
	}
	
	/**
	 * 根据用户ID获取用户信息
	 * @param userId
	 * @return
	 * @throws BizException
	 */
	public UserProfile getUserProfile(Integer userId) throws BizException{
		
		User user = getUser(userId);
		if ( user == null ){
			return null;
		}
		
		UserProfile up = new UserProfile(user, this.getUserRoles(user.getId()));
        return up;       
	}
	
	
	/**
	 * 获取用户角色代码
	 * @param userId
	 * @return
	 */
	public List<String> getUserRoles(Integer userId){
		
		List<UserRole> urs =  userRoleMapper.selectByUserId(userId);
		if ( urs == null ){
			return null;
		}
		
		List<String> roles = new ArrayList<String>();
		for(UserRole ur : urs ){
			roles.add(ur.getRole());
		}
		
		return roles;
	}
	
	/**
	 * 获取用户角色详情列表
	 * @param userId
	 * @return
	 */
	public List<Role> getUserRoleList(Integer userId){
		List<UserRole> urs =  userRoleMapper.selectByUserId(userId);
		if ( urs == null ){
			return null;
		}
		
		List<Role> roles = new ArrayList<Role>();
		for(UserRole ur : urs ){
			Role r = roleMapper.selectByCode(ur.getRole());
			if ( r != null ){
				roles.add(r);
			}
		}
		
		return roles;
	}
	
	/**
	 * 检查用户是否拥有角色
	 * @param userId
	 * @param role
	 * @return
	 */
	public boolean checkUserRole(Integer userId, String role){
		
		List<String> roles = this.getUserRoles(userId);
		if ( roles == null ){
			return false;
		}
		
		for ( String r : roles ){
			if ( r.equals(role)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 更新密码
	 * @param userId
	 * @param newPassword
	 * @throws BizException 
	 */
	public void updatePassword(Integer userId, String newPassword) throws BizException{
		
		User user = getUser(userId);
		user.setPassword(newPassword);
		
		userMapper.updateByPrimaryKey(user);
	}
	
	/**
	 * 更新激活码（使之前激活码失效）
	 * @param userId
	 * @throws BizException
	 */
	public void renewActiveCode(Integer userId) throws BizException{
		User user = getUser(userId);
		user.setActiveCode(createActiveCode());
		
		userMapper.updateByPrimaryKey(user);
	}
	
	/**
	 * 获取用户列表
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<User> getUsers(Integer page, Integer pageSize){

		Integer startIndex = CommonUtils.page2startIndex(page, pageSize);
		
		return userMapper.selectByPage(startIndex, pageSize, null);
	}
	
	/**
	 * 获取用户详情列表
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<UserDetail> getUserDetails(Integer page, Integer pageSize){
		
		List<User> userList = getUsers(page,pageSize);
		List<UserDetail> userDetailList = new ArrayList<UserDetail>();
		for ( User u : userList ){
			List<Role> roles = this.getUserRoleList(u.getId());
			UserDetail ud = new UserDetail(u,roles);
			userDetailList.add(ud);
		}
		
		return userDetailList;
	}
	
	/**
	 * 获取用户列表
	 * @param userIds
	 * @return
	 */
	public List<User> getUsers(List<Integer> userIds){
		
		return userMapper.selectByIdList(userIds);
	}
	
	/**
	 * 获取用户列表
	 * @param page
	 * @param pageSize
	 * @param nickname
	 * @return
	 */
	public List<User> getUsers(Integer page, Integer pageSize, String nickname){

		Integer startIndex = CommonUtils.page2startIndex(page, pageSize);
		
		return userMapper.selectByPage(startIndex, pageSize, nickname);
	}
	
	
	/**
	 * 获取所有用户数
	 * @return
	 */
	public Integer getCount(){
		
		return userMapper.count();
	}


	/**
	 * 设置用户角色，已有角色失效
	 * @param userId
	 * @param roles
	 */
	public void setUserRoles(Integer userId, List<String> roles){
		
		userRoleMapper.deleteByUserId(userId);
		
		for(String role : roles){
			UserRole ur = new UserRole();
			ur.setUserId(userId);
			ur.setRole(role);
			
			userRoleMapper.insert(ur);
		}
	}
	
	/**
	 * 删除用户
	 * @param userId
	 */
	public void delUser(Integer userId){
		
		userMapper.deleteById(userId);
		userRoleMapper.deleteByUserId(userId);
		
	}
	
	/**
	 * 检查用户ID是否存在
	 * @param userId
	 * @return
	 */
	public boolean checkUserIdExist(Integer userId){
		
		User user = userMapper.select(userId);
		if( user != null ){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 检查email是否已存在
	 * @param email
	 * @return
	 */
	public boolean checkEmailExist(String email){
		
		User user = userMapper.selectByEmail(email);
		if( user != null ){
			return true;
		}
		
		return false;
	}

	/**
	 * 添加用户
	 * @param email
	 * @param password
	 * @param nickname
	 * @throws BizException 
	 */
	public User addUserByEmail(String email, String password, String nickname) throws BizException{
		
		if ( this.checkEmailExist(email) ){
			throw new BizException("Email已存在。");
		}
		
		String defaultUserRole = cfgService.get("USER_ROLE_REG_DEFAULT");
		if(defaultUserRole == null){
			throw new BizException("Config USER_ROLE_REG_DEFAULT notfound.");
		}
		
		User user = new User();
		user.setEmail(email);
		user.setUsername(email);
		user.setNickname(nickname);
		user.setCreateTime(new Date());
		user.setPassword(password);
		user.setStatus(Constants.USER_STATUS_UNACTIVE);
		
		userMapper.insert(user);
		
		//添加角色
		User dbUser = userMapper.selectByEmail(email);
		
		UserRole ur = new UserRole();
		ur.setUserId(dbUser.getId());
		ur.setRole(defaultUserRole);
		userRoleMapper.insert(ur);
		
		return dbUser;
	}
	
	/**
	 * 发送帐号激活email
	 * @param generateNewActiveCode 是否生成新的激活码
	 * @throws BizException 
	 */
	public void sendActiveEmail(Integer userId, boolean generateNewActiveCode) throws BizException{
		
		User user = this.getUser(userId);
		
		if ( generateNewActiveCode || StringUtils.isBlank(user.getActiveCode()) ){
			user.setActiveCode(createActiveCode());
			userMapper.updateByPrimaryKey(user);
		}
		
		//发Email
		String webBase = cfgService.get("WEB_BASE");
		if ( webBase == null ){
			throw new BizException("Configuration item WEB_BASE not found.");
		}
		
		String registerActivePath = cfgService.get("REGISTER_ACTIVE_PATH");
		if ( registerActivePath == null ){
			throw new BizException("Configuration item REGISTER_ACTIVE_PATH not found.");
		}
		
		String activeUrl = webBase + registerActivePath + "/" + user.getActiveCode();
		
		String emailContent = cfgService.get("REGISTER_ACTIVE_CONTENT");
		if ( emailContent == null ){
			throw new BizException("Configuration item REGISTER_ACTIVE_CONTENT not found.");
		}
		
		emailContent += "</br><a href='" + activeUrl + "'>" + activeUrl + "</a>";
		
		String emailName = cfgService.get("EMAIL_NAME");
		if ( emailName == null ){
			throw new BizException("Configuration item EMAIL_NAME not found.");
		}
		
		String title = cfgService.get("REGISTER_ACTIVE_TITLE");
		if ( title == null ){
			throw new BizException("Configuration item REGISTER_ACTIVE_TITLE not found.");
		}
		
		String tag;
		try {
			tag = emailService.asyncEmail(emailName, user.getEmail(), title, emailContent);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("发送注册激活Email失败!");
		}
		
		for ( int i = 0; i < 30; i++){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			Integer result = emailService.getSendResult(tag);
			if ( result == EmailService.RESULT_FAIL ){
				throw new BizException("Email发送失败，请重试或更换其它Email注册。");
			}else if ( result == EmailService.RESULT_SUCCESS){
				break;
			}
		}
	}
	
	/**
	 * 重发激活Email
	 * @param email
	 * @param generateNewActiveCode
	 * @throws BizException
	 */
	public void resendActiveEmail(String email, boolean generateNewActiveCode) throws BizException{
		
		User user = this.getUserByEmail(email);
		if ( user == null ){
			throw new BizException("Email未注册。");
		}
		
		this.sendActiveEmail(user.getId(), generateNewActiveCode);
	}
	
	/**
	 * Email激活
	 * @param activeCode
	 * @return
	 */
	public boolean emailActive(String activeCode){
		
		User user = getUserByActiveCode(activeCode);
		if ( user == null ){
			return false;
		}
		
		user.setStatus(Constants.USER_STATUS_ACTIVE);
		userMapper.updateByPrimaryKey(user);
		
		return true;
	}
	
	/**
	 * 激活用户
	 * @param userId
	 */
	public void activeUserById(Integer userId){
		
		User user =  userMapper.select(userId);
		if ( user == null ){
			return;
		}
		
		user.setStatus(Constants.USER_STATUS_ACTIVE);
		userMapper.updateByPrimaryKey(user);
		
		return;
	}
	
	/**
	 * 关闭用户
	 * @param userId
	 */
	public void closeUserById(Integer userId){
		
		User user =  userMapper.select(userId);
		if ( user == null ){
			return;
		}
		
		user.setStatus(Constants.USER_STATUS_CLOSE);
		userMapper.updateByPrimaryKey(user);
		
		return;
		
	}
	
	/**
	 * 通过activeCode获取用户
	 * @param activeCode
	 * @return
	 */
	public User getUserByActiveCode(String activeCode){
		
		return userMapper.selectByActiveCode(activeCode);
	}
	
	/**
	 * 通过email获取用户
	 * @param email
	 * @return
	 */
	public User getUserByEmail(String email){
		
		return userMapper.selectByEmail(email);
	}
	
	/**
	 * 发送重置密码email
	 * @param email
	 * @throws BizException 
	 */
	public void sendPasswordRetrieveEmail(String email) throws BizException{
		
		User user = getUserByEmail(email);
		if ( user == null ){
			throw new BizException(Msg.text("user.email.notfound"));
		}
		
		String activeCode = createActiveCode();
		
		//发Email
		String webBase = cfgService.get("WEB_BASE");
		if ( StringUtils.isBlank(webBase)){
			throw new BizException("Configuraion item WEB_BASE not found.");
		}
		
		String retrievePwdPath = cfgService.get("RETRIEVE_PWD_PATH");
		if ( StringUtils.isBlank(retrievePwdPath)){
			throw new BizException("Configuraion item RETRIEVE_PWD_PATH not found.");
		}
		
		String retrievePasswordUrl = webBase + retrievePwdPath + "/" + activeCode;
		
		String emailContent = cfgService.get("RETRIEVE_PWD_EMAIL_CONTENT");
		if ( StringUtils.isBlank(emailContent)){
			throw new BizException("Configuration item RETRIEVE_PWD_EMAIL_CONTENT not found.");
		}
		emailContent += "</br><a href='" + retrievePasswordUrl + "'>" + retrievePasswordUrl + "</a>";
		user.setActiveCode(activeCode);
		
		try {
			userMapper.updateByPrimaryKey(user);
			emailService.asyncEmail("KeenDo", user.getEmail(), "KeenDo找回密码", emailContent);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("发送找回密码Email失败!");
		}
		return;
	}
	
	/**
	 * 生成Email注册激活码
	 * @return
	 */
	private String createActiveCode() {
		
		String baseStr = CommonUtils.dateFormat(new Date(), "yyyyMMddHHmmss");
		baseStr += CommonUtils.getRandom(3);
		
		String b64 = CommonUtils.base64encode(baseStr);
		String ch = change(b64);
		
		return ch;
		
	}
	
	/**
	 * 系统所有角色列表
	 * @return
	 */
	public List<Role> getAllRoles(){
		if ( roleListCache == null || roleListCache.size() == 0 ){
			roleListCache = roleMapper.select();
		}
		return roleListCache;
	}
	
	/**
	 * 获取角色名称
	 * @param code
	 * @return
	 */
	public String getRoleName(String code){
		List<Role> list = getAllRoles();
		for ( Role r : list ){
			if ( r.getCode().equals(code)){
				return r.getName();
			}
		}
		return null;
	}
	
	private String change(String s) {
		byte abyte0[] = s.getBytes();
		char ac[] = new char[s.length()];
		int i = 0;
		for (int k = abyte0.length; i < k; i++) {
			int j = abyte0[i];
			if (j >= 48 && j <= 57)
				j = ((j - 48) + 5) % 10 + 48;
			else if (j >= 65 && j <= 90)
				j = ((j - 65) + 13) % 26 + 65;
			else if (j >= 97 && j <= 122)
				j = ((j - 97) + 13) % 26 + 97;
			ac[i] = (char) j;
		}
		return String.valueOf(ac);
	}
}
