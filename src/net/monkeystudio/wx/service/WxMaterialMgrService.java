package net.monkeystudio.wx.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.monkeystudio.base.utils.HttpsHelper;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.entity.WxPubMaterial;
import net.monkeystudio.chatrbtw.entity.WxPubNews;
import net.monkeystudio.chatrbtw.entity.WxPubNewsWithBLOBs;
import net.monkeystudio.chatrbtw.mapper.WxPubMaterialMapper;
import net.monkeystudio.chatrbtw.mapper.WxPubNewsMapper;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.utils.CommonUtils;
import net.monkeystudio.wx.utils.WxApiUrlUtil;
import net.monkeystudio.wx.vo.material.BatchGetMaterial;
import net.monkeystudio.wx.vo.material.BatchMaterialNews;
import net.monkeystudio.wx.vo.material.GetMaterialCountResp;

/**
 * 微信公众号素材管理
 * @author hebo
 *
 */
@Service
public class WxMaterialMgrService {
	
	private static int PAGE_SIZE = 20;
	
	@Autowired
	WxAuthApiService wxAuthApiService;

	 @Autowired
	 WxPubService wxPubService;

	 @Autowired
	 WxPubMaterialMapper wxPubMaterialMapper;

	 @Autowired
	 WxPubNewsMapper wxPubNewsMapper;


	 /**
	  * 查询公众号文章
	 * @param wxPubOriginId 公众号原始ID
	 * @param title 文章标题，支持模糊匹配
	 * @param maxRetCount 最大返回数量
	 * @return
	 */
	public List<WxPubNews> getWxPubNews(String wxPubOriginId, String title, Integer maxRetCount){
		
		List<WxPubNews> news = wxPubNewsMapper.selectByTitle(wxPubOriginId, title, maxRetCount);
		return news;
	}
	
	/**
	 * 更新所有公众号素材，用于定时任务调用。
	 */
	public void updateAllWxPubNewsMaterials(){
		
		Integer count = wxPubService.getCount();
		Integer pageSize = 20;
		Integer pageCount = count/pageSize;
		if ( (count % pageSize) > 0 ){
			pageCount++;
		}
		
		for ( int page = 1; page < pageCount+1; page++){
			List<WxPub> wxPubList = wxPubService.getWxPubs(page, pageSize);
			for ( WxPub wxPub : wxPubList){
				updateWxPubNewsMaterials(wxPub.getOriginId());
			}
		}
	}
	
	/**
	 * 获取公众号素材中的图文列表
	 * @param wxPubOriginId
	 */
	public void updateWxPubNewsMaterials(String wxPubOriginId){
		
		try {
			GetMaterialCountResp resp = getWxPubMaterialCount(wxPubOriginId);
			if ( resp.getNews_count() == 0 ){
				Log.d("news count is 0, quit.");
				return;
			}
			
			if ( !checkNeedUpdate(wxPubOriginId)){
				return;
			}
			
			Integer pageCount = resp.getNews_count()/PAGE_SIZE;
			if ( resp.getNews_count()%PAGE_SIZE > 0){
				pageCount++;
			}
			
			wxPubNewsMapper.deleteByWxPubOriginId(wxPubOriginId);
			
			for ( int page = 1; page < (pageCount+1); page++){
				BatchMaterialNews batchMaterialNews = getWxPubNewsMaterials(wxPubOriginId,page);
				BatchMaterialNews.Item[] items = batchMaterialNews.getItem();
				
				for ( int i = 0; i < items.length; i++ ){
					processItem(wxPubOriginId,items[i]);
				}
			}
		} catch (BizException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private boolean checkNeedUpdate(String wxPubOriginId) throws BizException{
		
		Log.d("check need update, wxPubOriginId=" + wxPubOriginId);
		
		BatchMaterialNews batchMaterialNews = getWxPubNewsMaterials(wxPubOriginId,1);
		BatchMaterialNews.Item[] items = batchMaterialNews.getItem();
		if ( items == null || items.length == 0){
			Log.d("Batch materials result empty,no need update.");
			return false;
		}
		
		BatchMaterialNews.Item item = items[0];
		WxPubMaterial dbMaterial = wxPubMaterialMapper.selectByOriginIdAndMediaId(wxPubOriginId, item.getMedia_id());
		if ( dbMaterial != null 
				&& dbMaterial.getMaterialUpdateTime().longValue() == item.getUpdate_time().longValue() 
				&& dbMaterial.getContentUpdateTime().longValue() == item.getContent().getUpdate_time().longValue()){
			Log.d("material exist and update time unchange,no need update.");
			return false;
		}
		
		Log.d("Find new materials, need update.");
		return true;
	}
	
	private void processItem(String wxPubOriginId, BatchMaterialNews.Item item){
		
		WxPubMaterial dbMaterial = wxPubMaterialMapper.selectByOriginIdAndMediaId(wxPubOriginId, item.getMedia_id());

		if ( dbMaterial != null ){
			dbMaterial.setMaterialUpdateTime(item.getUpdate_time());
			dbMaterial.setContentCreateTime(item.getContent().getCreate_time());
			dbMaterial.setContentUpdateTime(item.getContent().getUpdate_time());
			Log.d("update material record, media_id=" + dbMaterial.getMediaId());
			wxPubMaterialMapper.updateByPrimaryKey(dbMaterial);
		}else{
			WxPubMaterial material = new WxPubMaterial();
			material.setWxPubOriginId(wxPubOriginId);
			material.setMediaId(item.getMedia_id());
			material.setMaterialUpdateTime(item.getUpdate_time());
			material.setContentCreateTime(item.getContent().getCreate_time());
			material.setContentUpdateTime(item.getContent().getUpdate_time());
			Log.d("add material record, media_id=" + material.getMediaId());
			wxPubMaterialMapper.insert(material);
		}
		
		dbMaterial = wxPubMaterialMapper.selectByOriginIdAndMediaId(wxPubOriginId, item.getMedia_id());
		for ( BatchMaterialNews.NewsItem news : item.getContent().getNews_item()){
			WxPubNewsWithBLOBs wxPubNewsWithBLOBs = new WxPubNewsWithBLOBs();
			wxPubNewsWithBLOBs.initWithNews(wxPubOriginId,dbMaterial.getId(), news);
			Log.d("add or update news, title=" + wxPubNewsWithBLOBs.getTitle());
			wxPubNewsMapper.insert(wxPubNewsWithBLOBs);
		}
		
	}
	
	
	/**
	 * 取素材数量
	 * @param wxPubOriginId
	 * @throws BizException 
	 */
	public GetMaterialCountResp getWxPubMaterialCount(String wxPubOriginId) throws BizException{
		
		Log.d("Query material count, wxPubOriginId=" + wxPubOriginId);
		
		String wxPubAppId = wxPubService.getWxPubAppIdByOrginId(wxPubOriginId);//在redis里面获取 appid
		String accessToken = wxAuthApiService.getAuthorizerAccessToken(wxPubAppId);
		String getMaterialListUrl = WxApiUrlUtil.getFetchMaterialCountUrl(accessToken);
		
		String response = HttpsHelper.getJson(getMaterialListUrl);
		if ( response.indexOf("errcode") >= 0 ){
			throw new BizException("Query material count error, response:" + response);
		}
		
		GetMaterialCountResp resp = JsonUtil.readValue(response, GetMaterialCountResp.class);
		
		if ( resp == null ){
			Log.e("Query material count failed, parse result error.");
			throw new BizException("Query material count failed, parse result error.");
		}
		
		Log.d("Query material count result: " + resp.toString());
		return resp;
	}
	
	/**
	 * 取永久素材列表
	 * @param wxPubOriginId
	 * @throws BizException 
	 */
	public BatchMaterialNews getWxPubNewsMaterials(String wxPubOriginId, int page) throws BizException{
		
		Log.d("Query material, wxPubOriginId=" + wxPubOriginId + ",page=" + page);
		
		String wxPubAppId = wxPubService.getWxPubAppIdByOrginId(wxPubOriginId);//在redis里面获取 appid
		String accessToken = wxAuthApiService.getAuthorizerAccessToken(wxPubAppId);
		String getMaterialListUrl = WxApiUrlUtil.getFetchMaterialInfoListUrl(accessToken);
		
		BatchGetMaterial req = new BatchGetMaterial();
		req.setType("news");
		Integer offset = CommonUtils.page2startIndex(page, PAGE_SIZE);
		req.setOffset(offset);
		req.setCount(PAGE_SIZE);
		
		String jsonStr = JsonUtil.toJSon(req);
		String response = HttpsHelper.postJsonByStr(getMaterialListUrl,jsonStr);
		if ( response.indexOf("errcode") >= 0 ){
			throw new BizException("Query materials error, response:" + response);
		}
		
		BatchMaterialNews materials = JsonUtil.readValue(response, BatchMaterialNews.class);
		if ( materials == null ){
			Log.e("Query material failed, parse result error.");
			throw new BizException("Query material failed, parse result error");
		}
		
		Log.d("Query material result:" + JsonUtil.toJSon(materials));
		return materials;
	}
	
	/**
	 * 获取公众号素材news列表
	 * @param params
	 * @return
	 */
	public List<WxPubNews> getWxPubNewsList(Map<String,Object> params){
		
		return wxPubNewsMapper.selectByPage(params);
	}
	
	/**
	 * 获取公众号素材news总数
	 * @param params
	 * @return
	 */
	public Integer getWxPubNewsCount(Map<String,Object> params){
		
		return wxPubNewsMapper.count(params);
	}
	
	/**
	 * 设置公众号素材news url2
	 * @param idUrl2Map
	 */
	public void setWxPubNewsUrl(Map<Integer,String> idUrl2Map){
		
		Set<Integer> ids = idUrl2Map.keySet();
		for ( Integer id : ids){
			WxPubNews news = wxPubNewsMapper.selectByPrimaryKey(id);
			if ( news != null ){
				news.setUrl2(idUrl2Map.get(id));
				news.setUpdateTime(new Date());
				wxPubNewsMapper.updateByPrimaryKey(news);
			}
		}
	}
}
