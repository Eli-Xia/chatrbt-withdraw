package net.monkeystudio.chatrbtw.controller;

import com.google.zxing.WriterException;
import net.monkeystudio.base.RespBase;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.utils.*;
import net.monkeystudio.chatrbtw.sdk.wx.QrCodeHelper;
import net.monkeystudio.chatrbtw.sdk.wx.WxPubHelper;
import net.monkeystudio.chatrbtw.sdk.wx.bean.qrcode.QrCodeTicker;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.chatrbtw.service.*;
import net.monkeystudio.exception.BizException;
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
import java.awt.image.BufferedImage;
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
    WxService wxService;

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

    @Autowired
    private WxPubHelper wxPubHelper;

    @Autowired
    private QrCodeHelper qrCodeHelper;

    @Autowired
    private EthnicGroupsService ethnicGroupsService;

    @Autowired
    private WxEventMessageHandler wxEventMessageHandler;


    @RequestMapping(value = "/getKrResponse", method = RequestMethod.POST)
    @ResponseBody
    public String getKrResponse(HttpServletRequest request, @RequestBody TestGetKrResponse testGetKrResponse) {

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

        Integer result = ethnicGroupsService.createSecondEthnicGroups("gh_902e0d566cd9", "oRQue05TvSudtScEa8wZWtnJK98g");
        return null;
    }

    @RequestMapping(value = "/test8", method = RequestMethod.GET)
    @ResponseBody
    public String test8(HttpServletRequest request) throws BizException {

        String result = qrCodeHelper.createQrCodeByWxPubOriginId("gh_902e0d566cd9", 60 * 30 * 30, QrCodeHelper.QrCodeType.TEMP, "abcd");

        QrCodeTicker qrCodeTicker = JsonUtil.readValue(result, QrCodeTicker.class);
        try {
            BufferedImage bufferedImage = QRCodeUtil.toBufferedImage(qrCodeTicker.getUrl(), 100, 100);
            String str = ImageUtils.encodeImgageToBase64(bufferedImage, "jpg");

            Log.d("base: " + str);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @RequestMapping(value = "/uploadFile", method = RequestMethod.GET)
    @ResponseBody
    public String test6(HttpServletRequest request) {

        File file = new File("//Users/bint/Downloads/WechatIMG3278.jpeg");

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = IOUtils.toByteArray(fileInputStream);

            cosService.uploadFile("/ad/1111.jpeg", bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @RequestMapping(value = "/test7", method = RequestMethod.POST)
    @ResponseBody
    public String test7(HttpServletRequest request) {

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
    public String test9(HttpServletRequest request) {
        wxTextMessageHandler.metarialHandle(null, null, null);
        return null;
    }

    @RequestMapping(value = "/testDate", method = RequestMethod.POST)
    @ResponseBody
    public String testDate(HttpServletRequest request) {
        TextMsgRec textMsgRec = new TextMsgRec();
        textMsgRec.setFromUserName("ovoy80zwgzSHMC4W1nhcGySaekvw");
        textMsgRec.setToUserName("gh_371e413ded76");
        textMsgRec.setContent("星座");
        textMsgRec.setCreateTime(TimeUtil.getCurrentTimestamp());
        textMsgRec.setMsgType("text");

        try {
            ethnicGroupsService.createFounderQrCodeImage("gh_371e413ded76");
        } catch (BizException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }


        return null;

    }


    @RequestMapping(value = "/test10", method = RequestMethod.GET)
    @ResponseBody
    public String test10(HttpServletRequest request) {

        String content = "<xml>    <ToUserName><![CDATA[gh_371e413ded76]]></ToUserName>    <Encrypt><![CDATA[KVUoxnl9h891OJtg5tV/NpR/XeOYP1FjVC46W4VGcQYS9IA0xVyBU1tE5AwP/U6iQtlUN0ghX9hV0l+4naHKVqjx1i8+RwHwwnqgG40RjGMKUXc2o7AoXFMS8ExnCaAhLnB+J98FewJpgjXXJvoIhfLgJzhg9aV4eSEWRwpRsc2mQlEZqZXffkRsGIZ3enjImtjIJi9wMNfAf9x1Qg1enmXp0WAoqIDRubmdvMClWyAaiJhWaT60Mszzo48KDauPEaSpvTF8NhIvtAgr3fsojWJ+64jugX0aXpl4+RnWpjFp3UzZ97e9tVZw8KffyFx8yKmgd+/mtExZ8//5lC9VlnCw/KcrJHWsvUlCYMGlzTbPj15CD5bWPY3717qaXifwFaEvHy4f34U84/u5mwOHALs5QGr0HAzBxBDrxRGehUWb7IlLaLuu5loRvRdypCwHgx7T+I4i38nxVmiswKZSY+fkoKwaNWqpFVRMSOUYKzqLbFVARiyYfSc1BBTxPFb97SMek4HYUYzZhTTsV65AI43E2P0TrAJ2EkjafZudObpln4dg0OGYpuwSwRE5b94lnUb0FMcg21XXVQH6AqSu92/kViX6oVbvvZUirDi255ORxMMLQUuQ48m4hzFMD9mi]]></Encrypt></xml>";

        content = content.replace(" ", "");
        wxService.handleData(content, "", "");

        return null;
    }
}
