package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.base.utils.XmlUtil;
import net.monkeystudio.wx.controller.bean.NewsMsgRes;
import net.monkeystudio.wx.controller.bean.TextMsgRes;
import net.monkeystudio.wx.vo.customerservice.CustomerNewsItem;

import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2018/4/18.
 */
public class WxBaseMessageHandler {


    /**
     * 回复多条图文
     * @param wxPubOriginId
     * @param wxfanOpenId
     * @param customerNewsList
     * @return
     */
    public String replyNewsStr(String wxPubOriginId, String wxfanOpenId ,List<CustomerNewsItem> customerNewsList){

        NewsMsgRes newsMsgRes = this.replyNews(wxPubOriginId, wxfanOpenId, customerNewsList);

        return XmlUtil.convertToXml(newsMsgRes);
    }


    /**
     * 回复文本
     * @param wxPubOriginId
     * @param wxfanOpenId
     * @param content
     * @return
     */
    public String replyTextStr(String wxPubOriginId, String wxfanOpenId , String content){
        TextMsgRes textMsgRes = this.replyText(wxPubOriginId, wxfanOpenId, content);

        return XmlUtil.convertToXml(textMsgRes);
    }

    /**
     * 回复图文
     * @param wxPubOriginId
     * @param wxfanOpenId
     * @param customerNewsList
     * @return
     */
    private NewsMsgRes replyNews(String wxPubOriginId, String wxfanOpenId ,List<CustomerNewsItem> customerNewsList ){
        NewsMsgRes res = new NewsMsgRes();
        res.setFromUserName(wxPubOriginId);
        res.setToUserName(wxfanOpenId);
        res.setMsgType("news");
        res.setArticles(customerNewsList);
        res.setCreateTime(new Date().getTime() / 1000L);
        res.setArticleCount(customerNewsList.size());

        return res;
    }

    /**
     * 回复文本
     * @param wxPubOriginId
     * @param wxfanOpenId
     * @param content
     * @return
     */
    private TextMsgRes replyText(String wxPubOriginId, String wxfanOpenId , String content){
        TextMsgRes textMsgRes = new TextMsgRes();

        textMsgRes.setCreateTime(TimeUtil.getCurrentTimestamp());
        textMsgRes.setContent("欢迎关注我们公众号，回复关键字，即可获取我们公众号的过往历史文章！");
        textMsgRes.setMsgType("text");

        textMsgRes.setToUserName(wxfanOpenId);

        textMsgRes.setFromUserName(wxPubOriginId);

        return textMsgRes;

    }
}
