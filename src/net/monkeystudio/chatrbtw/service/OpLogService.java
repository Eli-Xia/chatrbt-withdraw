package net.monkeystudio.chatrbtw.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.chatrbtw.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import net.monkeystudio.chatrbtw.entity.OpLog;
import net.monkeystudio.chatrbtw.mapper.OpLogMapper;

/**
 * 操作日志
 * @author hebo
 *
 */
@Service
public class OpLogService {

	@Autowired
	private OpLogMapper opLogMapper;
	
	@Autowired
	private UserService userService;
	
	private static Map<Integer,User> userCache = new HashMap<Integer,User>();
	
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	/**
	 * 系统操作，如系统定时任务执行记录。
	 * @param tag     日志标签
	 * @param content 日志内容
	 */
	public void systemOper(String tag, String content){
		writeLog(-1,tag,content);
	}
	
	/**
	 * 用户操作，如用户登录、修改配置信息记录。
	 * @param userId  操作用户ID
	 * @param tag     日志标签
	 * @param content 日志内容
	 */
	public void userOper(Integer userId, String tag, String content){
		writeLog(userId,tag,content);
	}
	
	/**
	 * 获取操作日志
	 * @param params
	 * @return
	 */
	public List<OpLog> getOpLogList(Map<String,Object> params){
		return opLogMapper.selectByPage(params);
	}
	
	/**
	 * 获取操作日志总数
	 * @param params
	 * @return
	 */
	public Integer getOpLogCount(Map<String,Object> params){
		return opLogMapper.count(params);
	}
	
	private void writeLog(Integer userId, String tag, String content){
		threadPoolTaskExecutor.execute(new OpLogTask(userId,tag,content));
	}
	
	private String getUserNickname(Integer userId){
		User u = userCache.get(userId);
		if ( u != null ){
			return u.getNickname();
		}
		
		try {
			u = userService.getUser(userId);
			userCache.put(userId,u);
			return u.getNickname();
		} catch (BizException e) {
			return null;
		}
	}
	
	private class OpLogTask implements Runnable{
		
		private Integer userId;
		private String tag;
		private String content;
		
    	public OpLogTask(Integer userId, String tag, String content) {
    		this.userId = userId;
    		this.tag = tag;
    		this.content = content;
    	}
    	
		@Override
		public void run() {
			OpLog opLog = new OpLog();
			opLog.setUserId(userId);
			if ( userId == -1 ){
				opLog.setNickname("System");
			}else{
				opLog.setNickname(getUserNickname(userId));
			}
			opLog.setTag(tag);
			opLog.setContent(content);
			opLog.setCreateTime(new Date());
			
			opLogMapper.insert(opLog);
		}
	    	
    }
}
