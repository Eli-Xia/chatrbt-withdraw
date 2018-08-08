package net.monkeystudio.wx.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.service.WxEventMessageHandler;
import net.monkeystudio.wx.mp.aes.XMLParse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class WxService {

	@Autowired
	private RedisCacheTemplate redisCacheTemplate;

	@Autowired
	private WxBizMsgCryptService wxBizMsgCryptService;

	@Autowired
	private WxTextMessageHandler wxTextMessageHandler;

	@Autowired
	private WxEventMessageHandler wxEventMessageHandler;

	public String handleData(String postContent,String timestamp ,String openId ){
		String content = wxBizMsgCryptService.decryptEvent(postContent);

		String dataType = this.judgeDataType(content);

		String xmlStr = null;

		if(dataType == null){
			Log.e("can not parse dataType ");
			return null;
		}

		if(MessageTypeConstants.EVENT.equals(dataType)){
			try {
				xmlStr = wxEventMessageHandler.handleEvent(content);
			} catch (BizException e) {
				Log.e(e);
			}
		}

		if(MessageTypeConstants.TEXT.equals(dataType)){
			try {
				xmlStr = wxTextMessageHandler.handleTextMsg(content);
			} catch (BizException e) {
				Log.e(e);
			}
		}

		//如果是不处理的类型，返回一个空白给微信
		if(xmlStr == null){
			return "success";
		}

		Log.d("response content : " + xmlStr);

		//把XML字符串加密
		String result = wxBizMsgCryptService.encrypt(xmlStr,timestamp,"abcdef");

		return result;
	}

	/**
	 * 判断事件种类
	 * @param str
	 * @return
	 */
	private String judgeEventType(String str){

		String eventType = XMLParse.extractField(str, "Event");
		return eventType;
	}

	/**
	 * 判断数据类型
	 * @param messageStr
	 * @return
	 */
	public String judgeDataType(String messageStr){
		String dataType = XMLParse.extractDataType(messageStr);
		return dataType;
	}

	private String getPubAccessTokenKey(String originId){
		return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "pub-access-token:" + originId;
	}


	public String getPubAccessToken(String originId){
		String key = this.getPubAccessTokenKey(originId);
		return redisCacheTemplate.getString(key);
	}

	/**
	 * 获取24小时内聊天记录次数的统计个数的key
	 * @return
	 *//*
	private String getChatLogCountCacheKey(String wxPubOpenId ,String wxUserOpenId){

		return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "ChatLogCount:" + wxPubOpenId + ":" + wxUserOpenId;
	}*/


	/**
	 * 微信消息类型
	 */
	private class MessageTypeConstants{
		final static String TEXT = "text";
		final static String EVENT = "event";
	}
}
