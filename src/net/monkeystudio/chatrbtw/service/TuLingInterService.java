package net.monkeystudio.chatrbtw.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.chatrbtw.entity.RChatUidTuling;
import net.monkeystudio.chatrbtw.entity.TuLingRobot;
import net.monkeystudio.chatrbtw.mapper.RChatUidTulingMapper;
import net.monkeystudio.chatrbtw.mapper.TuLingRobotMapper;
import net.monkeystudio.chatrbtw.service.bean.TuLingReq;
import net.monkeystudio.chatrbtw.service.bean.TuLingResp;
import net.monkeystudio.chatrbtw.service.bean.TuLingRespItem;
import net.monkeystudio.service.CfgService;
import net.monkeystudio.utils.HttpUtils;
import net.monkeystudio.utils.JsonHelper;
import net.monkeystudio.utils.Log;

/**
 * 图灵接口服务
 * @author hebo
 *
 */
@Service
public class TuLingInterService {

	//缓存图灵机器人信息
	private static Map<Integer,TuLingRobot> robotMapCache = new HashMap<Integer,TuLingRobot>();
	
	//缓存用户默认图灵机器人映射表
	private static Map<Integer,Integer> chatUid2RobotMap = new HashMap<Integer,Integer>();
	
	
	private static String TULING_API_URL;
	
	@Autowired
	private CfgService cfgService;
	
	@Autowired
	private TuLingRobotMapper tuLingRobotMapper;
	
	@Autowired
	private RChatUidTulingMapper rChatUidTulingMapper;
	
	/**
	 * 启动时自动始化获取配置信息到内存
	 * @throws Exception
	 */
	@PostConstruct
	public void init() throws Exception {
		
		Log.i("Init TuLingInterService...");
		
		TULING_API_URL = cfgService.get("TULING_API_URL");
		
		List<TuLingRobot> list = tuLingRobotMapper.selectAll();
		if ( list != null ){
			for ( TuLingRobot robot : list ){
				robotMapCache.put(robot.getId(),robot);
			}
		}
	}
	
	/**
	 * 图灵接口
	 * @param uIdentify ID用于标识用户
	 * @param info   聊天输入
	 * @return
	 */
	public String getResponse(Integer uIdentify, String info){
		
		TuLingReq req = new TuLingReq();
		req.setKey(getRobot(uIdentify).getApiKey());
		req.setInfo(info);
		req.setUserid(uIdentify+"");
		String reqJson = JsonUtil.toJSon(req);
		
		String respJson = HttpUtils.postJson(TULING_API_URL, reqJson);
		
		TuLingResp resp = JsonUtil.readValue(respJson, TuLingResp.class);
		if ( resp.getCode() == 100000){
			return resp.getText();
		}else if ( resp.getCode() == 200000){
			return resp.getText() + " <a href=\"" + resp.getUrl() + "\">点击这里</a>";
		}else if (resp.getCode() == 308000){
			List<TuLingRespItem> list = resp.getList();
			TuLingRespItem tuLingRespItem = list.get(0);
			return resp.getText() + " <a href=\"" + tuLingRespItem.getDetailUrl() + "\">点击这里</a>";
		}

		return "";
	}
	
	/**
	 * 根据用户ID选择图灵机器人
	 * @param uIdentify
	 * @return
	 */
	private TuLingRobot getRobot(Integer chatUid){
		
		Integer robotId = chatUid2RobotMap.get(chatUid);
		if ( robotId == null ){
			//缓存中没有用户-机器人映射信息，从DB中获取
			RChatUidTuling chatUidTuling = rChatUidTulingMapper.selectByPrimaryKey(chatUid);
			if ( chatUidTuling == null ){
				return chooseDefaultRobot(chatUid); //这里会写缓存，不需要单独写代码
			}else{
				chatUid2RobotMap.put(chatUidTuling.getChatUid(),chatUidTuling.getTulingRobotId());
				robotId = chatUidTuling.getTulingRobotId();
			}
		}
		
		TuLingRobot robot = robotMapCache.get(robotId);
		if ( robot != null ){
			return robot;
		}else{
			//如果图灵机器人失效（如删除）这里获取可能为空，重新分配一个
			return chooseDefaultRobot(chatUid);
		}
	}
	
	/**
	 * 为用户选择一个默认机器人
	 * @param chatUid
	 * @return
	 */
	private TuLingRobot chooseDefaultRobot(Integer chatUid){
		
		TuLingRobot robot = getRandomRobotFromCache();
		
		RChatUidTuling record = rChatUidTulingMapper.selectByPrimaryKey(chatUid);
		if ( record == null ){
			//新插入
			record = new RChatUidTuling();
			record.setChatUid(chatUid);
			record.setTulingRobotId(robot.getId());
			rChatUidTulingMapper.insert(record);
		}else{
			//更新
			record.setTulingRobotId(robot.getId());
			rChatUidTulingMapper.updateByPrimaryKey(record);
		}
		
		//写入缓存
		chatUid2RobotMap.put(chatUid, robot.getId());
		
		return robot;
	}
	
	/**
	 * 从缓存中随机获取一个机器人
	 * @return
	 */
	private TuLingRobot getRandomRobotFromCache(){
		
		Integer[] robotIds = robotMapCache.keySet().toArray(new Integer[0]); 

		Integer randomIndex = new Random().nextInt(robotIds.length);
		Integer randomRobotId = robotIds[randomIndex];
		
		return robotMapCache.get(randomRobotId);
	}
	
	public static void main(String[] args){
		
	}
	
}
