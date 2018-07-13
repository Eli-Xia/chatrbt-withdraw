package net.monkeystudio.base.controller.bean;

import net.monkeystudio.base.local.Msg;

import java.util.HashMap;
import java.util.Map;


/**
 * API返回码定义
 * @author hebo
 *
 */
public class RetCode {

	public final static int SUCCESS = 0; //成功
	public final static int FAILED = -1; //一般失败
	public final static int NO_LOGIN = 2; //未登录
	public final static int AUTH_FAILED = 3; //鉴权失败
	
	public final static Map<Integer,String> codeMsgMap = new HashMap<Integer,String>();
	
	public static String getMsg(Integer retCode){
		return codeMsgMap.get(retCode);
	}
	
	static {
		codeMsgMap.put(SUCCESS, Msg.text("common.api.success"));
		codeMsgMap.put(FAILED, Msg.text("common.api.failed"));
		codeMsgMap.put(NO_LOGIN, Msg.text("common.user.nologin"));
		codeMsgMap.put(AUTH_FAILED, Msg.text("common.user.authfailed"));
	}
	
}
