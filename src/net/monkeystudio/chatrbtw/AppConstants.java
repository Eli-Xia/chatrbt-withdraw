package net.monkeystudio.chatrbtw;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量
 * @author hebo
 *
 */
public class AppConstants {

	public final static int CHAT_UID_DEFAULT = 0;
	
	public final static int USER_EXT_STATUS_UNAPPROVED = 0;//资料未审核
	public final static int USER_EXT_STATUS_APPROVED   = 1;//资料已审核
	
	public final static String RESPONSE_TYPE_TEXT = "text";     //文本
	public final static String RESPONSE_TYPE_MPNEWS = "mpnews"; //公众号图文
	
	public final static int KEYWORD_RESPONSE_RULE_STRICK = 0; //严格匹配
	public final static int KEYWORD_RESPONSE_RULE_ALL    = 1; //全匹配到
	
	public final static int PAGE_DEFAULTE = 1;
	public final static int PAGE_SIZE_DEFAULT = 20;
	
	//关键字-回复 匹配规则
	public final static int[] KEYWORD_RESPONSE_RULES = {0,   //严格匹配
														1    //所有关键词匹配到
														};





	//操作日志标签
	public final static String OP_LOG_TAG_P_LOGIN = "P_LOGIN";
	public final static String OP_LOG_TAG_A_LOGIN = "A_LOGIN";
	public final static String OP_LOG_TAG_S_MATERIALS_UPDATE = "S_MAT_UPDATE";
	public final static String OP_LOG_TAG_S_MATERIALS_UPDATE_ERR = "S_MAT_UPDATE_ERR";
	public final static String OP_LOG_TAG_S_DELETE_INVALID_ROBOT = "S_DEL_INV_ROBOT";
	public final static String OP_LOG_TAG_S_RESET_ETHNIC_GROUPS_DAILY_RESTRICTIONS = "S_RESET_ETHNIC_GROUPS_DAILY_RESTRICTIONS";
	public final static String OP_LOG_TAG_S_GENERATE_LEVEL_REWARD = "S_RESET_ETHNIC_GROUPS_DAILY_RESTRICTIONS";
	public final static String OP_LOG_TAG_S_DELETE_MISSION_REWARD = "S_DEL_MISSION_REWARD";

	public final static Map<String,String> OP_LOG_TAG_NAMES = new HashMap<String,String>();
	static {
		OP_LOG_TAG_NAMES.put(OP_LOG_TAG_P_LOGIN,"P-用户登录");
		OP_LOG_TAG_NAMES.put(OP_LOG_TAG_A_LOGIN,"A-用户登录");
		OP_LOG_TAG_NAMES.put(OP_LOG_TAG_S_MATERIALS_UPDATE,"S-更新公众号素材");
		OP_LOG_TAG_NAMES.put(OP_LOG_TAG_S_MATERIALS_UPDATE_ERR,"S-更新公众号素材异常");
		OP_LOG_TAG_NAMES.put(OP_LOG_TAG_S_DELETE_INVALID_ROBOT,"S-删除无用机器人");
		OP_LOG_TAG_NAMES.put(OP_LOG_TAG_S_GENERATE_LEVEL_REWARD ,"S-生成等级奖励");

	}
}
