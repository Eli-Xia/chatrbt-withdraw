package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.KrKeyword;
import net.monkeystudio.chatrbtw.entity.KrResponse;
import net.monkeystudio.chatrbtw.mapper.KrKeywordMapper;
import net.monkeystudio.chatrbtw.mapper.KrResponseMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 关键字回复业务功能
 * @author hebo
 *
 */
@Service
public class KeywordResponseService {

	private static List<Krp> krpCache = new ArrayList<Krp>();
	
	public static final int RULE_STRICT = 0;
	public static final int RULE_ALL_MATCH = 1;


	
	@Autowired
	KrKeywordMapper krKeywordMapper;

	@Autowired
	KrResponseMapper krResponseMapper;

	@Autowired
	WxPubKeywordStatusService wxPubKeywordStatusService;

	@PostConstruct
	public void init() throws Exception {	
		Log.i("Init KeywordRepsonseService...");
		loadKrpCache();
	}


	/**
	 * 获取公共的回复
	 * @param recStr
	 * @return
	 */
	public KrResponse getBaseResponse(String recStr){


		for ( Krp krp : krpCache ){

			if ( StringUtils.isNotBlank(krp.getWxPubOriginId()) ){
				continue;
			}

			switch ( krp.getRule() ){
				case RULE_STRICT:
					if ( recStr.trim().equals(krp.getKeywordOrigin())){
						return krp.getKrResponse();
					};
					break;
				case RULE_ALL_MATCH:
					if ( includeAllKeywords(recStr, krp.getKeywords())){
						return krp.getKrResponse();
					}
					break;
				default:
					return null;
			}
		}
		return null;
	}



	/**
	 * 根据输入内容获取公众号主定制的响应信息
	 * @param recStr 关键字
	 * @param wxPubOriginId
	 * @return
	 */
	public KrResponse getWxPubResponse(String recStr, String wxPubOriginId){

		//判断公众号的关键字回复开关是否开启
		Integer status = wxPubKeywordStatusService.getStatusByOriginId(wxPubOriginId);
		if(status.equals(WxPubKeywordStatusService.WXPUB_KEYWORD_STATUS_OFF)){
			return null;
		}
		
		for ( Krp krp : krpCache ){
			
			if ( StringUtils.isBlank(krp.getWxPubOriginId())  || !krp.getWxPubOriginId().equals(wxPubOriginId)){
				continue;
			}

			switch ( krp.getRule() ){
				case RULE_STRICT:
					if ( recStr.trim().equals(krp.getKeywordOrigin())){
						return krp.getKrResponse();
					};
					break;
				case RULE_ALL_MATCH:
					if ( includeAllKeywords(recStr, krp.getKeywords())){
						return krp.getKrResponse();
					}
					break;
				default:
					return null;
			}
		}		
		return null;
	}


	private boolean includeAllKeywords(String str, String[] keywords){
		
		for ( int i = 0; i < keywords.length; i++ ){
			if ( str.indexOf(keywords[i]) < 0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 将关键字-规则-响应数据装载进内容缓存
	 */
	public void loadKrpCache(){
		
		krpCache.clear();
		
		List<KrKeyword> krKeywordList = krKeywordMapper.selectAll();
		List<KrResponse> krResponseList = krResponseMapper.selectAll();
		
		for ( KrKeyword krKeyword : krKeywordList){
			Krp krp = new Krp();
			krp.setKeywordOrigin(krKeyword.getKeywords());
			krp.setKeywords(krKeyword.getKeywords().split(","));
			krp.setRule(krKeyword.getRule());
			krp.setWxPubOriginId(krKeyword.getWxPubOriginId());

			for ( KrResponse krResponse : krResponseList ){
				if ( krResponse.getId().equals(krKeyword.getResponseId())){
					krp.setKrResponse(krResponse);
					break;
				}
			}
			
			krpCache.add(krp);
		}
	}


	
	/**
	 * 缓存Item
	 * @author hebo
	 *
	 */
	private class Krp{
		
		private int rule;
		private String keywordOrigin; //关键字-DB原始字符串
		private String[] keywords;    //关键字-DB原始字符串拆分后列表
		private KrResponse krResponse;
		private String wxPubOriginId;
		
		
		
		public int getRule() {
			return rule;
		}
		public void setRule(int rule) {
			this.rule = rule;
		}
		public String[] getKeywords() {
			return keywords;
		}
		public void setKeywords(String[] keywords) {
			this.keywords = keywords;
		}
		public String getWxPubOriginId() {
			return wxPubOriginId;
		}
		public void setWxPubOriginId(String wxPubOriginId) {
			this.wxPubOriginId = wxPubOriginId;
		}
		public String getKeywordOrigin() {
			return keywordOrigin;
		}
		public void setKeywordOrigin(String keywordOrigin) {
			this.keywordOrigin = keywordOrigin;
		}
		public KrResponse getKrResponse() {
			return krResponse;
		}
		public void setKrResponse(KrResponse krResponse) {
			this.krResponse = krResponse;
		}
		
	}
	
}
