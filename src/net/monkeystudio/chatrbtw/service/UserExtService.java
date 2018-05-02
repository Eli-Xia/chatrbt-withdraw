package net.monkeystudio.chatrbtw.service;

import java.util.Date;
import java.util.Map;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.local.Msg;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.chatrbtw.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.entity.UserExt;
import net.monkeystudio.chatrbtw.mapper.UserExtMapper;

/**
 * 用户扩展信息管理
 * @author hebo
 *
 */
@Service
public class UserExtService {

	@Autowired
	UserExtMapper userExtMapper;
	
	@Autowired
	UserService userService;

	@Autowired
	CfgService cfgService;
	
	private boolean userExtExist(Integer userId){
		UserExt u = userExtMapper.selectByPrimaryKey(userId);
		if ( u == null ){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 更新用户信息
	 * @param userId
	 * @param params
	 */
	public void update(Integer userId, Map<String,Object> params) throws BizException {
		
		if ( !userService.checkUserIdExist(userId) ){
			throw new BizException(Msg.text("user.notfound"));
		}
		
		if(!userExtExist(userId)){
			createUserExt(userId);
		}
		
		params.put("userId",userId);
		userExtMapper.updateByPrimaryKeySelectiveWithMapParams(params);
	}
	
	/**
	 * 获取用户扩展信息（如果表中没有记录会自动创建）
	 * @param userId
	 * @return
	 */
	public UserExt getUserExt(Integer userId){
		
		UserExt ue = userExtMapper.selectByPrimaryKey(userId);
		if ( ue == null ){
			ue = createUserExt(userId);
		}
		return ue;
	}
	
	private UserExt createUserExt(Integer userId){
		
		UserExt ue = new UserExt();
		ue.setUserId(userId);
		ue.setCreateTime(new Date());
		ue.setStatus(AppConstants.USER_EXT_STATUS_APPROVED);
		userExtMapper.insert(ue);
		
		return ue;
	}

	private User getByUserId(Integer userId ) throws BizException {
		return  userService.getUser(userId);
	}

	/**
	 * 获取微赞用户
	 * @return
	 * @throws BizException
	 */
	public User getWeizanUser() throws BizException {
		Integer weizanUserId = this.getWeizanUserId();

		User weizanUser = this.getByUserId(weizanUserId);

		return weizanUser;
	}

	public Integer getWeizanUserId() throws BizException {
		Integer weizanUserId = cfgService.getInteger(GlobalConfigConstants.WEIZAN_USER_ID_KEY);

		if(weizanUserId == null ){
			throw new BizException("找不到微赞用户");
		}

		return weizanUserId;
	}

}
