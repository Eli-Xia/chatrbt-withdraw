package net.monkeystudio.chatrbtw.controller;

import net.monkeystudio.base.RespBase;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.base.utils.URLUtil;
import net.monkeystudio.base.utils.XmlUtil;
import net.monkeystudio.chatrbtw.entity.Ad;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.service.*;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.wx.controller.bean.Article;
import net.monkeystudio.wx.controller.bean.NewsMsgRes;
import net.monkeystudio.wx.controller.bean.TestGetKrResponse;
import net.monkeystudio.wx.controller.bean.TextMsgRec;
import net.monkeystudio.wx.service.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2017/10/31.
 */
@Controller
@RequestMapping(value = "/test")
public class TestController {

    @Autowired
    WxService wxService ;

    @Autowired
    private WxAuthApiService wxAuthApiService;

    @Autowired
    private WxBizMsgCryptService wxBizMsgCryptService;

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private WxCustomerServiceService wxCustomerServiceService;
    
    @Autowired
    private KeywordResponseService keywordResponseService;

    @Autowired
    private PushMessageConfigService pushMessageConfigService;

    @Autowired
    private AdPushService adPushService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;


    @Autowired
    private PushMessageService pushMessageService;

    @Autowired
    private ChatLogService chatLogService;

    @Autowired
    private AdPushLogService adPushLogService;

    @Autowired
    private WxTextMessageHandler wxTextMessageHandler;

    @Autowired
    private AdService adService;

    @Autowired
    private COSService cosService;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private WxChatCountService wxChatCountService;

    @Autowired
    private IncomeSerivce incomeSerivce;

    @Autowired
    private AdClickLogService adClickLogService;


    @RequestMapping(value = "/getKrResponse", method = RequestMethod.POST)
    @ResponseBody
    public String getKrResponse(HttpServletRequest request, @RequestBody TestGetKrResponse testGetKrResponse){

    	/*String result = keywordResponseService.getResponse(testGetKrResponse.getStr(),"xxx");
    	if ( result == null ){
    		result = "value empty.";
    	}*/
        return null;
    }



    @RequestMapping(value = "/test4", method = RequestMethod.GET)
    @ResponseBody
    public RespBase test4(HttpServletRequest request) throws BizException {

        TextMsgRec textMsgRec = new TextMsgRec();
        textMsgRec.setFromUserName("ovoy806BiqXBdhSacB7dmClbRvF0");
        textMsgRec.setToUserName("gh_371e413ded76");
        textMsgRec.setContent("星座");
        textMsgRec.setCreateTime(TimeUtil.getCurrentTimestamp());
        textMsgRec.setMsgType("text");

        wxTextMessageHandler.textMsgRecHandle(textMsgRec);

        return null;
    }




    @RequestMapping(value = "/test5", method = RequestMethod.GET)
    @ResponseBody
    public String test5(HttpServletRequest request) throws BizException {

        TextMsgRec textMsgRec = new TextMsgRec();
        textMsgRec.setFromUserName("ovoy806BiqXBdhSacB7dmClbRvF0");
        textMsgRec.setToUserName("gh_371e413ded76");
        textMsgRec.setContent("星座");
        textMsgRec.setCreateTime(TimeUtil.getCurrentTimestamp());
        textMsgRec.setMsgType("text");

        wxTextMessageHandler.textMsgRecHandle(textMsgRec);

        return null;
    }

    @RequestMapping(value = "/test8", method = RequestMethod.GET)
    @ResponseBody
    public String test8(HttpServletRequest request) throws BizException {

        wxTextMessageHandler.reviseWxPub("gh_05cd3e6bacbf");

        return null;
    }



    @RequestMapping(value = "/uploadFile", method = RequestMethod.GET)
    @ResponseBody
    public String test6(HttpServletRequest request){

            File file = new File("//Users/bint/Downloads/WechatIMG3278.jpeg");

            try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = IOUtils.toByteArray(fileInputStream);

            cosService.uploadFile("/ad/1111.jpeg",bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



    @RequestMapping(value = "/test7", method = RequestMethod.POST)
    @ResponseBody
    public String test7(HttpServletRequest request ){

        /*List<CustomerNewsItem> customerNewsItemList = new ArrayList<>();

        CustomerNewsItem customerNewsItem = new CustomerNewsItem();
        customerNewsItem.setDescription("hhhh");
        customerNewsItem.setPicUrl("http://img7.gelonghui.com/201712/column_article_cover_20171208092635556.png");
        customerNewsItem.setTitle("你吼啊");
        customerNewsItem.setUrl("www.baidu.com");

        customerNewsItemList.add(customerNewsItem);
        String result = wxCustomerServiceService.sendNews("wx66ad6a3b63f98d34","oRQue05TvSudtScEa8wZWtnJK98g",customerNewsItemList);
        return result;*/

        return null;
    }



    @RequestMapping(value = "/test9", method = RequestMethod.POST)
    @ResponseBody
    public String test9(HttpServletRequest request ){
        wxTextMessageHandler.metarialHandle(null,null,null);
        return  null;
    }

    @RequestMapping(value = "/testDate", method = RequestMethod.POST)
    @ResponseBody
    public String testDate(HttpServletRequest request ){
        /*TextMsgRec textMsgRec = new TextMsgRec();
        textMsgRec.setFromUserName("ovoy80zwgzSHMC4W1nhcGySaekvw");
        textMsgRec.setToUserName("gh_371e413ded76");
        textMsgRec.setContent("星座");
        textMsgRec.setCreateTime(TimeUtil.getCurrentTimestamp());
        textMsgRec.setMsgType("text");

        wxTextMessageHandler.metarialHandle(textMsgRec);*/

        //wxTextMessageHandler.replyMoreNewsMsg("ovoy80zwgzSHMC4W1nhcGySaekvw","gh_371e413ded76");

        return null;

    }


    private static void run(int i){
        System.out.println(i);
    }

    public static void main(String[]args){
        /*NewsMsgRes newsMsgRes = new NewsMsgRes();
        newsMsgRes.setCreateTime(new Date().getTime());
        newsMsgRes.setFromUserName("bb");
        newsMsgRes.setMsgType("cc");
        newsMsgRes.setToUserName("xiaxin");
        newsMsgRes.setArticleCount(1);

        List<Article> as = new ArrayList<>();
        Article article = new Article();
        article.setDescription("z");
        article.setPicUrl("x");
        article.setTitle("c");
        article.setUrl("v");
        as.add(article);

        newsMsgRes.setArticles(as);

        String s = XmlUtil.convertToXml(newsMsgRes);*/
        
    }

}
