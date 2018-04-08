package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.service.TaskExecutor;
import net.monkeystudio.base.utils.ExcelUtils;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.entity.KrKeyword;
import net.monkeystudio.chatrbtw.entity.KrResponse;
import net.monkeystudio.chatrbtw.mapper.KrKeywordMapper;
import net.monkeystudio.chatrbtw.mapper.KrResponseMapper;
import net.monkeystudio.chatrbtw.service.bean.kr.KeywordResponse;
import net.monkeystudio.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 关键字回复管理功能
 * @author hebo
 *
 */
@SuppressWarnings("ALL")
    @Service
    public class KeywordRespMgrService {

        @Autowired
        private KrKeywordMapper krKeywordMapper;

        @Autowired
        private KrResponseMapper krResponseMapper;

        @Autowired
        private KeywordResponseService keywordResponseService;


	private static final Integer KW_RESP_DEFAULT_RULE = 1;
	
	public List<KrKeyword> getKeyWords(Map<String,Object> params){
		
		Integer startIndex = CommonUtils.page2startIndex((Integer)params.get("page"), (Integer)params.get("pageSize"));
		params.put("startIndex", startIndex);

		return krKeywordMapper.selectByPage(params);
	}

	public Integer count(Map<String,Object> params){
		return krKeywordMapper.count(params);
	}

	/**
	 * 统计公共关键字
	 * @param params
	 * @return
	 */
	public Integer countBase(Map<String,Object> params){
		params.put("wxPubOriginId",null);
		return this.count(params);
	}
	
	/**
	 * 添加关键字-回复配置
	 * @param wxPubOriginId
	 * @param keywords
	 * @param response
	 * @param rule
	 */
	public void addKeywordsResponse(String wxPubOriginId, List<String> keywords, String response, Integer rule){
		
		Integer responseId = addTextKrResponse(wxPubOriginId, response);
		addKrKeywords(wxPubOriginId,keywords,responseId,rule);
		
		keywordResponseService.loadKrpCache();
	}
	
	/**
	 * 更新关键字
	 * @param keywordId
	 * @param keywords
	 * @param response
	 * @param rule
	 */
	public void updateKeywordsResponse(Integer keywordId, List<String> keywords, String response, Integer rule){
		
		KrKeyword krKeyword = this.getKrKeyword(keywordId);
		
		Integer c = krKeywordMapper.countByResponseId(krKeyword.getResponseId());
		if ( c == 1 ){
			KrResponse resp = krResponseMapper.selectByPrimaryKey(krKeyword.getResponseId());
			resp.setResponse(response);
			krResponseMapper.updateByPrimaryKeyWithBLOBs(resp);
			
			krKeyword.setKeywords(keywordList2String(keywords));
			krKeyword.setRule(rule);
			krKeywordMapper.updateByPrimaryKey(krKeyword);
		}else{
			Integer responseId = addTextKrResponse(krKeyword.getWxPubOriginId(), response);
			
			krKeyword.setKeywords(keywordList2String(keywords));
			krKeyword.setRule(rule);
			krKeyword.setResponseId(responseId);
			krKeywordMapper.updateByPrimaryKey(krKeyword);
		}
		
		keywordResponseService.loadKrpCache();
	}
	
	/**
	 * 添加文字回复
	 * @param wxPubOriginId
	 * @param response
	 * @return
	 */
	private Integer addTextKrResponse(String wxPubOriginId, String response){
		
		KrResponse krResponse = new KrResponse();
		
		krResponse.setWxPubOriginId(wxPubOriginId);
		krResponse.setResponse(response.trim());
		krResponse.setType(AppConstants.RESPONSE_TYPE_TEXT);
		
		krResponseMapper.insert(krResponse);
		return krResponse.getId();
	}



	/**
	 * 添加关键字
	 * @param wxPubOriginId
	 * @param keywords
	 * @param responseId
	 * @param rule
	 */
	private void addKrKeywords(String wxPubOriginId, List<String> keywords, Integer responseId, Integer rule){
		
		String keywordsStr = keywordList2String(keywords);
		
		KrKeyword krKeyword = new KrKeyword();
		krKeyword.setKeywords(keywordsStr);
		krKeyword.setResponseId(responseId);
		krKeyword.setRule(rule);
		krKeyword.setWxPubOriginId(wxPubOriginId);
		
		krKeywordMapper.insert(krKeyword);
	}
	
	private String keywordList2String(List<String> keywords){
		
		String keywordsStr = "";
		
		Iterator<String> it = keywords.iterator();
		
		keywordsStr = it.next().trim();
		while ( it.hasNext() ){
			keywordsStr = keywordsStr + "," + it.next().trim();
		}
		
		return keywordsStr;
	}
	
	public List<KeywordResponse> getKeywordResponseList(Map<String,Object> params){
		
		Integer startIndex = CommonUtils.page2startIndex((Integer)params.get("page"), (Integer)params.get("pageSize"));
		params.put("startIndex", startIndex);
		
		List<KrKeyword> kwList = krKeywordMapper.selectByPage(params);
		
		List<KeywordResponse> list = new ArrayList<KeywordResponse>();
		if ( kwList != null ){
			for ( KrKeyword item :  kwList){
				KeywordResponse kr = new KeywordResponse();
				kr.setKeywords(CommonUtils.splitString2List(item.getKeywords(), ","));
				
				KrResponse resp = krResponseMapper.selectByPrimaryKey(item.getResponseId());
				if ( resp != null ){
					kr.setResponse(resp.getResponse());
				}
				kr.setKeywordsId(item.getId());
				list.add(kr);
			}
		}
		
		return list;
	}

    /**
     * 获取公共关键字
     * @param params
     * @return
     */
    public List<KeywordResponse> getBaseKeywordResponseList(Map<String,Object> params){

        params.put("wxPubOriginId",null);
        List<KeywordResponse> list = this.getKeywordResponseList(params);
        return list;
    }

    /**
     * 关键字-回复总数
     * @param wxPubOriginId
     * @return
     */
	public Integer getKeywordResponseCount(Map<String,Object> params){
		
		return krKeywordMapper.count(params);
	}

	/**
	 * 删除关键字回复
	 * @param keywordId
	 */
	public void deleteKeyword(Integer keywordId){

        KrKeyword krKeyword = krKeywordMapper.selectByPrimaryKey(keywordId);
        if ( krKeyword == null ){
        	return;
        }
        
        krKeywordMapper.deleteByPrimaryKey(keywordId);
        
        List<KrKeyword> list = krKeywordMapper.selectByResponseId(krKeyword.getResponseId());
        if ( list == null || list.size() == 0 ){
        	krResponseMapper.deleteByPrimaryKey(krKeyword.getResponseId());
        }
       
    }
	
	/**
	 * 取关键字回复
	 * @param keywordId
	 * @return
	 */
	public KrKeyword getKrKeyword(Integer keywordId){
		
		return krKeywordMapper.selectByPrimaryKey(keywordId);
	}


	public void batchInsertKwRespFromExcel(MultipartFile excelFile)  {

		List<KrKeyword> keywords = this.getKeywords();

		List<Object[]> excelData = ExcelUtils.getExcelData(excelFile, excelFile.getOriginalFilename().endsWith(".xlsx"));

		for(int i = 1; i < excelData.size(); i++){
			Object[] obj = excelData.get(i);
			String keyword = this.objToString(obj[0]);
			String response = this.objToString(obj[1]);

			List<String> kws = new ArrayList<String>();
			kws.add(keyword);

			boolean flag = false;

			for(KrKeyword kw:keywords){
				if(kw.getKeywords().trim().equals(keyword.trim())){
					this.updateKeywordsResponse(kw.getId(),kws,response,KW_RESP_DEFAULT_RULE);
					flag = true;
					break;
				}
			}
			if(!flag)
			this.addKeywordsResponse(null, kws,response,KW_RESP_DEFAULT_RULE);

		}
	}

	private String objToString(Object obj){
		return obj==null?"":obj.toString();
	}


	public List<KrKeyword> getKeywords(){
		return krKeywordMapper.selectAll();
	}

	public boolean isExcel(MultipartFile file){
		String fileName = file.getOriginalFilename();
		return fileName.endsWith("xls") || fileName.endsWith("xlsx");
	}
}
