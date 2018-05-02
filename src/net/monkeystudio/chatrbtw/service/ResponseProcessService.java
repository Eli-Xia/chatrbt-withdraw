package net.monkeystudio.chatrbtw.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import net.monkeystudio.base.utils.Log;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.wx.service.WxPubService;

/**
 * 回复内容做二次处理
 * @author hebo
 *
 */
@Service
public class ResponseProcessService {

	private static Map<String,String> replaceTextCacheMap; //缓存直接替换字符串
	
	@Autowired
	private WxPubService wxPubService;
	
	/**
	 * 初始化，关键字替换信息缓存到内存
	 * @throws Exception
	 */
	@PostConstruct
	public void init() throws Exception {	
		
		Log.i("Init ResponseProcessService...");
		
		replaceTextCacheMap = new HashMap<String,String>();
		
		replaceTextCacheMap.put("图灵机器人", "KeenDO机器人");
		replaceTextCacheMap.put("图灵工程师", "KeenDO工程师");
	}
	
	/**
	 * 根据输入内容获取响应信息
	 * @param recStr
	 * @param wxPubOriginId
	 * @return
	 */
	public String responseProecess(String wxPubOriginId, String text){

		for ( String key : replaceTextCacheMap.keySet() ){
			text = text.replaceAll(key, replaceTextCacheMap.get(key));
		}
		
		WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);
		if ( wxPub != null && !StringUtils.isBlank(wxPub.getChatbotName()) ){
			Log.d("wxPubOriginId=" + wxPubOriginId + ",chatbotname=" + wxPub.getChatbotName());
			Log.d("respText1=" + text);
			text = text.replaceAll("keendooo", wxPub.getChatbotName());
			Log.d("respText2=" + text);
		}else{
			Log.d("wxPubOriginId=" + wxPubOriginId + ",WxPub not found or chatbotname is empty.");
		}
		
		return text;
	}
	
	
}
