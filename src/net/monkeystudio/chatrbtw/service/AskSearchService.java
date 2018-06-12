package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.admin.controller.req.wxpubmaterial.QueryWxPubNewsList;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.base.utils.StringUtil;
import net.monkeystudio.chatrbtw.entity.Ad;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.entity.WxPubNews;
import net.monkeystudio.chatrbtw.service.bean.asksearch.AskSearchVo;
import net.monkeystudio.wx.controller.bean.TextMsgRec;
import net.monkeystudio.wx.service.WxMaterialMgrService;
import net.monkeystudio.wx.service.WxPubService;
import net.monkeystudio.wx.service.WxTextMessageHandler;
import net.monkeystudio.wx.vo.customerservice.CustomerNewsItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bint on 2018/4/25.
 */
@Service
public class AskSearchService {

    private final static String ASK_SEARCH_LAST_ITEM_TITLE = "在下面↓回复\"更多\"即可获取更多结果";
    private final static String ASK_SEARCH_REPLY_TIP = "更多";

    private final static String ASK_SEARCH_FIRST_ITEM_TITLE = " 搜索结果";
    private final static String ASK_SEARCH_FIRST_ITEM_DESC = "Powered by keendo.com.cn";

    //微信文章推送个数
    private final static Integer WX_PUB_ARTICLE_PUSH_COUNT = 5;

    //广告触发次数
    private final static Integer ASK_SEARCH_SEND_COUNT = 1;

    @Autowired
    private WxMaterialMgrService wxMaterialMgrService;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private AdService adService;

    //问问搜"更多"有效时长为半小时
    private final static Integer MORE_NEWS_VALID_TIME = 60 * 30;


    /**
     * 问问搜是否启用卡片式
     * @return
     */
    public Boolean newsAnble(){
        return true;
    }

    /**
     * 获取卡片式问问搜
     * @param textMsgRec
     * @return
     */
    public List<CustomerNewsItem> getAskSearchNews(TextMsgRec textMsgRec){
        List<AskSearchVo> askSearchVoList = this.getAskSearchContent(textMsgRec);

        List<CustomerNewsItem> customerNewsList = new ArrayList<>();

        if(askSearchVoList == null){
            return customerNewsList;
        }

        for(AskSearchVo askSearchVo : askSearchVoList){
            CustomerNewsItem customerNewsItem = new CustomerNewsItem();

            customerNewsItem.setDescription(askSearchVo.getDescription());
            customerNewsItem.setPicUrl(askSearchVo.getPicUrl());
            customerNewsItem.setTitle(askSearchVo.getTitle());
            customerNewsItem.setUrl(askSearchVo.getUrl());

            customerNewsList.add(customerNewsItem);
        }

        return customerNewsList;
    }

    /**
     * 获取问问搜的回复的内容
     * @param textMsgRec
     * @return
     */
    private List<AskSearchVo> getAskSearchContent(TextMsgRec textMsgRec) {

        String content = textMsgRec.getContent();
        String wxPubOriginId = textMsgRec.getToUserName();
        String wxfanOpenId = textMsgRec.getFromUserName();


        List<WxPubNews> wxPubNewsList = null;
        Integer currentPage = null;

        //获取素材的内容
        if (isMoreWord(content)) {

            //TODO 用getMoreNews方法获取
            // "更多"从缓存中获取
            String lastContent = this.getAskSearchLastContentFromCache(wxPubOriginId, wxfanOpenId);

            if (lastContent == null) {
                return null;
            }

            Long currentPageLong = this.incrWordCachePage(wxPubOriginId, wxfanOpenId);

            if(currentPageLong != null){
                currentPage = currentPageLong.intValue();
            }else {
                currentPage = 1;
            }

            QueryWxPubNewsList qo = new QueryWxPubNewsList();
            qo.setTitle(lastContent);
            qo.setWxPubOriginId(wxPubOriginId);
            qo.setPage(currentPage.intValue());
            qo.setPageSize(WX_PUB_ARTICLE_PUSH_COUNT);
            wxPubNewsList = wxMaterialMgrService.getWxPubNewsList(qo.getMap());


        } else {

            //非更多的处理
            this.resetFirstPage(wxPubOriginId, wxfanOpenId);
            this.setWordCache(wxPubOriginId, wxfanOpenId, content);
            currentPage = 1;

            //从第一页查询
            QueryWxPubNewsList qo = new QueryWxPubNewsList();
            qo.setTitle(content);
            qo.setWxPubOriginId(wxPubOriginId);
            qo.setPage(currentPage);
            qo.setPageSize(WX_PUB_ARTICLE_PUSH_COUNT);
            wxPubNewsList = wxMaterialMgrService.getWxPubNewsList(qo.getMap());

        }

        List<AskSearchVo> askSearchVoList = new ArrayList<>();

        //第一条为"XXX关键字"的提醒
        String lastContent = this.getAskSearchLastContentFromCache(wxPubOriginId, wxfanOpenId);
        AskSearchVo firstItem = this.getFirstItem(lastContent);
        askSearchVoList.add(firstItem);

        List<AskSearchVo> askSearchVoFromNews = convert2AskSearchList(wxPubNewsList);
        askSearchVoList.addAll(askSearchVoFromNews);

        //最后一条为提示是否还有"更多"
        AskSearchVo finalItem = this.getFinalItem(wxPubOriginId, wxfanOpenId);
        if (finalItem != null) {
            askSearchVoList.add(finalItem);
        }

        //获取问问搜广告
        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxfanOpenId);

        Ad ad = adService.getAskSearchPushAd(wxFan.getId());

        List<AskSearchVo> result = new ArrayList<>();
        //当前页数为1且为空
        if (this.needToSendAd(wxPubOriginId,wxfanOpenId)) {
            //插入广告
            for (int i = 0; i < askSearchVoList.size(); i++) {

                AskSearchVo askSearchVo = askSearchVoList.get(i);

                if (i == 1 && ad != null) {
                    AskSearchVo adAaskSearchVo = new AskSearchVo();

                    adAaskSearchVo.setTitle(ad.getTitle());
                    adAaskSearchVo.setUrl(ad.getUrl());
                    adAaskSearchVo.setPicUrl(ad.getPicUrl());

                    result.add(adAaskSearchVo);
                }

                result.add(askSearchVo);
            }
        }else {

            for(AskSearchVo askSearchVo : askSearchVoList){
                result.add(askSearchVo);
            }
        }

        return result;
    }

    private Boolean needToSendAd(String wxPubOriginId ,String wxFanOpenId){
        Long page = this.getCurrentPage(wxPubOriginId, wxFanOpenId);

        if(page.intValue() == ASK_SEARCH_SEND_COUNT.intValue()){
            return true;
        }

        return false;
    }

    private AskSearchVo getFinalItem(String wxPubOriginId ,String wxFanOpenId ){

        String lastContent = this.getAskSearchLastContentFromCache(wxPubOriginId,wxFanOpenId);
        Integer totalCount = wxMaterialMgrService.getWxPubNewsCount(wxPubOriginId,lastContent);

        Long page = this.getCurrentPage(wxPubOriginId, wxFanOpenId);

        //当前素材总数
        Integer nowCount = page.intValue() * WX_PUB_ARTICLE_PUSH_COUNT;
        //判断是否有"更多"图文消息
        if(totalCount.intValue() > nowCount.intValue()){
            AskSearchVo askSearchVo = new AskSearchVo();
            askSearchVo.setTitle(ASK_SEARCH_LAST_ITEM_TITLE);
            askSearchVo.setUrl(null);
            askSearchVo.setPicUrl(null);
            return askSearchVo;
        }else{
            this.noMoreHandle(wxPubOriginId,wxFanOpenId);
            return null;
        }

    }




    private Boolean isMoreWord(String reqContent){
        return ASK_SEARCH_REPLY_TIP.equals(StringUtils.trimWhitespace(reqContent));
    }


    /**
     * 获取问问搜聊天次数cache的key
     * @param wxPubOpenId
     * @param wxUserOpenId
     * @return
     */
    private String getAskSearchCountCacheKey(String wxPubOpenId,String wxUserOpenId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "AskSearchCount:" + wxPubOpenId + ":" + wxUserOpenId;
    }

    private String getMoreNewsCountCacheKey(String wxfanOpenId,String wxPubOriginId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "moreNewsPage:" + wxPubOriginId + ":" + wxfanOpenId;
    }

    /**
     * 没有更多的处理
     * @param wxPubOriginId
     * @param wxfanOpenId
     */
    private void noMoreHandle(String wxPubOriginId ,String wxfanOpenId ){

        String askSearchKeywordCacheKey = this.getAskSearchLastContentChacheKey(wxPubOriginId, wxfanOpenId);
        String moreNewsCountCacheKey = this.getMoreNewsCountCacheKey(wxPubOriginId, wxfanOpenId);

        redisCacheTemplate.del(askSearchKeywordCacheKey);
        redisCacheTemplate.del(moreNewsCountCacheKey);
    }

    /**
     * 让"更多"的页数+1
     * @param wxPubOriginId
     * @param wxfanOpenId
     */
    private Long incrWordCachePage(String wxPubOriginId ,String wxfanOpenId ){
        String moreNewsPageCacheKey = this.getMoreNewsCountCacheKey(wxfanOpenId,wxPubOriginId);
        return redisCacheTemplate.incr(moreNewsPageCacheKey);//关键字搜索,每次应从第一页开始
    }

    private Long getCurrentPage(String wxPubOriginId ,String wxfanOpenId ){
        String moreNewsCountCacheKey = this.getMoreNewsCountCacheKey(wxfanOpenId,wxPubOriginId);
        Long result = redisCacheTemplate.getObject(moreNewsCountCacheKey);

        if(result == null){
            result = 1L;
        }

        return result;
    }


    private void resetFirstPage(String wxPubOriginId ,String wxfanOpenId ){
        String moreNewsCountCacheKey = this.getMoreNewsCountCacheKey(wxfanOpenId,wxPubOriginId);

        redisCacheTemplate.del(moreNewsCountCacheKey);
        redisCacheTemplate.incr(moreNewsCountCacheKey);
    }

    private void setWordCache(String wxPubOriginId ,String wxfanOpenId , String word){
        String askSearchKeywordCacheKey = this.getAskSearchLastContentChacheKey(wxPubOriginId,wxfanOpenId);
        redisCacheTemplate.setString(askSearchKeywordCacheKey,word);
        redisCacheTemplate.expire(askSearchKeywordCacheKey,MORE_NEWS_VALID_TIME);
    }

    private List<AskSearchVo> convert2AskSearchList(List<WxPubNews> wxPubNewsList){

        List<AskSearchVo> askSearchVoList = new ArrayList<>();

        for(WxPubNews wxPubNews : wxPubNewsList){

            AskSearchVo askSearchVo = this.convert2AskSearch(wxPubNews);

            askSearchVoList.add(askSearchVo);
        }

        return askSearchVoList;
    }


    private AskSearchVo convert2AskSearch(WxPubNews wxpubNews){


        AskSearchVo askSearchVo =  BeanUtils.copyBean(wxpubNews,AskSearchVo.class) ;
        askSearchVo.setPicUrl(wxpubNews.getThumbUrl());

        String realUrl = wxpubNews.getUrl2();
        if(StringUtil.isNotEmpty(realUrl)){
            askSearchVo.setUrl(realUrl);
        }else{
            askSearchVo.setUrl(wxpubNews.getUrl());
        }

        return askSearchVo;
    }


    private AskSearchVo getFirstItem(String keyword){
        AskSearchVo askSearchVo = new AskSearchVo();
        askSearchVo.setTitle(keyword+ASK_SEARCH_FIRST_ITEM_TITLE);
        askSearchVo.setDescription(ASK_SEARCH_FIRST_ITEM_DESC);
        askSearchVo.setUrl(null);
        askSearchVo.setPicUrl(null);

        return askSearchVo;
    }

    /**
     * 获取上次的对话内容
     * @param wxPubOriginId
     * @param wxfanOpenId
     * @return
     */
    public String getAskSearchLastContentFromCache(String wxPubOriginId ,String wxfanOpenId){
        String askSearchKeywordCacheKey = this.getAskSearchLastContentChacheKey(wxPubOriginId,wxfanOpenId);
        String content = redisCacheTemplate.getString(askSearchKeywordCacheKey);
        return content;
    }


    private String getAskSearchLastContentChacheKey(String wxPubOriginId, String wxfanOpenId) {
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "wxLastContent:" + wxPubOriginId + ":" + wxfanOpenId;
    }
}
