package net.monkeystudio.base;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量
 * @author hebo
 *
 */
public class Constants {

	public static final String FM_TAG_CONTEXT_PATH = "base";
	
	public static final String SESSION_ATTR_NAME_USERID = "SESSION_ATTR_NAME_USERID";
	public static final String SESSION_ATTR_NAME_PORTAL_USERID = "SESSION_ATTR_NAME_PORTAL_USERID";
	
	//分页
	public static final int PAGINATION_DEFAULT_PAGE = 1;
	public static final int PAGINATION_DEFAULT_PAGE_SIZE = 20;
	
	//用户状态
	public static final int USER_STATUS_UNACTIVE = 0;
	public static final int USER_STATUS_ACTIVE   = 1;
	public static final int USER_STATUS_CLOSE    = 2;
	public static final Map<Integer,String> USER_STATUS_NAME_MAP = new HashMap<Integer,String>();
	static {
		USER_STATUS_NAME_MAP.put(USER_STATUS_UNACTIVE, "未激活");
		USER_STATUS_NAME_MAP.put(USER_STATUS_ACTIVE, "已激活");
		USER_STATUS_NAME_MAP.put(USER_STATUS_CLOSE, "关闭");
	}

}
