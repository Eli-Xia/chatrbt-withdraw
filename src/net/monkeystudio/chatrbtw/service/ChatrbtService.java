package net.monkeystudio.chatrbtw.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.monkeystudio.base.RespBase;
import net.monkeystudio.chatrbtw.service.bean.TuLingReq;
import net.monkeystudio.chatrbtw.service.bean.TuLingResp;
import net.monkeystudio.service.CfgService;
import net.monkeystudio.utils.HttpUtils;
import net.monkeystudio.utils.JsonHelper;
import net.monkeystudio.utils.Log;

@Service
public class ChatrbtService {

	private static String TULING_API_URL = null;
	private static String TULING_API_KEY = null;
	
	@Autowired
	private CfgService cfgService;
	
	@PostConstruct
	public void init() throws Exception {
		Log.i("Init ChatrbtService...");
		TULING_API_URL = cfgService.get("TULING_API_URL");
		TULING_API_KEY = cfgService.get("TULING_API_KEY");
	}
	
	/**
	 * 自家机器人接口
	 * @param iStr
	 * @return
	 */
	public String getResponse(String iStr){
		String reqJson = "{\"i_str\":\"" + iStr + "\"}";
		String respJson = HttpUtils.postJson("http://123.207.125.230:8000/api/getresponse", reqJson);
		
		RespBase resp = JsonHelper.fromJson(respJson, RespBase.class);
		if ( resp.getRetCode() != 0){
			return null;
		}
		
		return (String)resp.getResult();
	}
	
	/**
	 * 图灵接口
	 * @param info
	 * @return
	 */
	public String getTuLingResponse(String info){
		
		TuLingReq req = new TuLingReq();
		req.setKey(TULING_API_KEY);
		req.setInfo(info);
		req.setUserid("12344");
		String reqJson = JsonHelper.bean2json(req);
		
		String respJson = HttpUtils.postJson(TULING_API_URL, reqJson);
		
		TuLingResp resp = JsonHelper.fromJson(respJson, TuLingResp.class);
		if ( resp.getCode() == 100000){
			return resp.getText();
		}else if ( resp.getCode() == 200000){
			return resp.getText() + " <a href=\"" + resp.getUrl() + "\">点击这里</a>";
		}
		
		return "";
	}
	

	public static void main(String[] args){
		String s = "{\"code\":200000,\"text\":\"亲，已帮你找到菜谱信息\",\"url\":\"http://m.xiachufang.com/search?keyword=%E8%9A%82%E8%9A%81%E4%B8%8A%E6%A0%91\"}";
		TuLingResp resp = JsonHelper.fromJson(s, TuLingResp.class);
		System.out.println(resp.getText());
	}
	
}
