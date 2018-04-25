package net.monkeystudio.chatrbtw.service;

import com.google.zxing.WriterException;
import net.monkeystudio.base.utils.*;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.mapper.ChatPetMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetSessionVo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.OwnerInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.PetLogResp;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.utils.JsonHelper;
import net.monkeystudio.wx.service.WxOauthService;
import net.monkeystudio.wx.service.WxPubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by bint on 2018/4/16.
 */
@Service
public class ChatPetService {

    private final static Integer MAX_APPERANCE_RANGE = 9;

    @Autowired
    private ChatPetMapper chatPetMapper;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private ChatPetLogService chatPetLogService;

    @Autowired
    private CryptoKittiesService cryptoKittiesService;

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private EthnicGroupsService ethnicGroupsService;

    @Autowired
    private WxOauthService wxOauthService;


    /**
     * 生成宠物
     * @param wxPubOriginId
     * @param wxFanOpenId
     * @return 生成的宠物的id
     */
    public Integer generateChatPet(String wxPubOriginId , String wxFanOpenId ,Integer ethnicGroupsId ,Integer secondEthnicGroupsId){

        ChatPet chatPet = new ChatPet();
        //Integer appearance = this.ramdomGenerateAppearence();

        cryptoKittiesService.designateKitty(wxPubOriginId,wxFanOpenId);
        CryptoKitties cryptoKitties = cryptoKittiesService.getKittyByOwner(wxPubOriginId, wxFanOpenId);
        Integer appearance = cryptoKitties.getId();

        chatPet.setTempAppearence(appearance);
        chatPet.setWxFanOpenId(wxFanOpenId);
        chatPet.setWxPubOriginId(wxPubOriginId);
        chatPet.setEthnicGroupsId(ethnicGroupsId);
        chatPet.setSecondEthnicGroupsId(secondEthnicGroupsId);
        chatPet.setCreateTime(new Date());

        this.save(chatPet);

        Integer chatPetId = chatPet.getId();

        chatPetLogService.savePetBornLog(wxPubOriginId,wxFanOpenId,chatPetId);
        return chatPetId;
    }

    /**e
     * 随机生成
     * @return
     */
    public Integer ramdomGenerateAppearence(){
        return RandomUtil.randomIndex(MAX_APPERANCE_RANGE);
    }

    public ChatPet getById(Integer id){
        return chatPetMapper.selectById(id);
    }


    private Integer save(ChatPet chatPet){
        return chatPetMapper.insert(chatPet);
    }


    /**
     * 获取宠物的信息
     * @param chatPetId
     * @return
     */
    public ChatPetInfo getInfo(Integer chatPetId){
        ChatPetInfo chatPetBaseInfo = new ChatPetInfo();

        ChatPet chatPet = this.getById(chatPetId);

        if(chatPet == null){
            return null;
        }

        chatPetBaseInfo.setTempAppearance(chatPet.getTempAppearence());

        String wxPubOriginId = chatPet.getWxPubOriginId();
        String wxFanOpenId = chatPet.getWxFanOpenId();

        OwnerInfo ownerInfo = new OwnerInfo();
        WxFan owner = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
        ownerInfo.setNickname(owner.getNickname());

        String headImgUrl = owner.getHeadImgUrl();
        if(headImgUrl == null){
            wxFanService.reviseWxPub(wxPubOriginId,wxFanOpenId);
        }

        owner = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
        ownerInfo.setHeadImg(owner.getHeadImgUrl());

        chatPetBaseInfo.setOwnerInfo(ownerInfo);

        String owerId = wxFanOpenId.substring(wxFanOpenId.length() - 6, wxFanOpenId.length() - 1);
        chatPetBaseInfo.setOwnerId(owerId);

        //宠物基因
        String geneticCode = this.calculateGeneticCode(chatPet.getCreateTime().getTime());
        chatPetBaseInfo.setGeneticCode(geneticCode);

        //宠物日志
        List<PetLog> dailyPetLogList = chatPetLogService.getDailyPetLogList(chatPetId, new Date());
        List<PetLogResp> resps = new ArrayList<>();

        for(PetLog pl:dailyPetLogList){
            PetLogResp resp = new PetLogResp();
            resp.setChatPetId(pl.getChatPetId());
            resp.setCoin(pl.getCoin());
            resp.setContent(pl.getContent());
            resp.setCreateTime(pl.getCreateTime());
            resp.setId(pl.getId());
            resps.add(resp);
        }
        chatPetBaseInfo.setPetLogs(resps);

        //粉丝拥有代币
        Float fanTotalCoin = chatPetLogService.getFanTotalCoin(wxPubOriginId,wxFanOpenId);
        if(fanTotalCoin == null){
            fanTotalCoin = 0F;
        }
        chatPetBaseInfo.setFanTotalCoin(fanTotalCoin);

        //宠物的url
        CryptoKitties cryptoKitties = cryptoKittiesService.getKittyByOwner(wxPubOriginId, wxFanOpenId);

        String appearanceUrl = cryptoKitties.getUrl();
        chatPetBaseInfo.setAppearanceUrl(appearanceUrl);

        //公众号的头像
        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);
        String wxPubHeadImgUrl = wxPub.getHeadImgUrl();
        chatPetBaseInfo.setwPubHeadImgUrl(wxPubHeadImgUrl);

        //公众号二维码base64
        try {
            String base64 = ethnicGroupsService.createInvitationQrCode(chatPetId);
            chatPetBaseInfo.setInvitationQrCode(base64);
        } catch (BizException e) {
            Log.e(e);
        } catch (IOException e) {
            Log.e(e);
        } catch (WriterException e) {
            Log.e(e);
        }
        return chatPetBaseInfo;
    }

    private String calculateGeneticCode(Long createTime){

        Calendar calendar = Calendar.getInstance();
        calendar.set(2018,1,1);
        Date date = calendar.getTime();

        Long cusTimestamp = createTime - date.getTime();
        String geneticCode = String.valueOf(cusTimestamp);

        return geneticCode;
    }

    public ChatPet getChatPetByFans(String wxPubOriginId,String wxFanOpenId){
        ChatPet param = new ChatPet();

        param.setWxFanOpenId(wxPubOriginId);
        param.setWxFanOpenId(wxFanOpenId);

        return chatPetMapper.selectByParam(param);
    }



    /**
     * 通过code获取access_token
     *
     * https://api.weixin.qq.com/sns/oauth2/component/access_token
     * ?appid=APPID&code=CODE&grant_type=authorization_code&component_appid=COMPONENT_APPID&component_access_token=COMPONENT_ACCESS_TOKEN


     appid	是	公众号的appid
     code	是	填写第一步获取的code参数
     grant_type	是	填authorization_code
     component_appid	是	服务开发方的appid
     component_access_token	是	服务开发方的access_token


     {
     "access_token":"ACCESS_TOKEN",
     "expires_in":7200,
     "refresh_token":"REFRESH_TOKEN",
     "openid":"OPENID",
     "scope":"SCOPE"
     }

     access_token	接口调用凭证
     expires_in	access_token接口调用凭证超时时间，单位（秒）
     refresh_token	用户刷新access_token
     openid	授权用户唯一标识
     scope	用户授权的作用域，使用逗号（,）分隔

     /res/wedo/zebra.html?id=" + chatPetId

     这个方法最好是改成
     * @param code
     */
    public ChatPetSessionVo handleWxOauthCode(HttpServletResponse resp,String code,String wxPubAppId) throws Exception{
        String fetchAccessTokenUrl = wxOauthService.getAccessTokenUrl(code,wxPubAppId);
        String response = HttpsHelper.get(fetchAccessTokenUrl);
        String access_token = JsonHelper.getStringFromJson(response,"access_token");
        String wxFanOpenId = JsonHelper.getStringFromJson(response,"openid");
        Log.d("================通过code获取accessToken结果  access_token = {?}  , openId = {?} =====================");
        /*String fetchFansInfoUrl = wxOauthService.getFansInfoUrl(access_token,fansOpenId);
        String info = HttpsHelper.get(fetchFansInfoUrl);
        String openid2 = JsonHelper.getStringFromJson(info,"openid");
        String nickname = JsonHelper.getStringFromJson(info,"nickname");*/
        String wxPubOriginId = wxPubService.getWxPubOriginIdByAppId(wxPubAppId);

        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);


        ChatPet param = new ChatPet();
        param.setWxFanOpenId(wxFanOpenId);
        param.setWxPubOriginId(wxPubOriginId);
        ChatPet chatPet = this.getChatPetByFans(wxPubOriginId, wxFanOpenId);

        if(chatPet == null){
            resp.sendRedirect("https://test.keendo.com.cn/res/wedo/poster.html");
        }

        ChatPetSessionVo vo = new ChatPetSessionVo();
        vo.setChatPetId(chatPet.getId());
        vo.setWxFanId(wxFan.getId());

        return vo;




        //获取粉丝信息  这个方法应该是要返回一个bean
        /*
        *   GET（请使用https协议） https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
        *
        *   access_token	网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
            openid	用户的唯一标识
            lang	返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语


            {
                "openid":" OPENID",
                " nickname": NICKNAME,
                  "sex":"1",
                "province":"PROVINCE"
                "city":"CITY",
                "country":"COUNTRY",
                "headimgurl":    "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46",
                "privilege":[ "PRIVILEGE1" "PRIVILEGE2"     ],
                "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
            }
        * */

    }

    /**
     * 跳转陪聊宠页
     * 参数:wxAppId FanInfo openId
     * @param response
     * return fansid
     */
    public Integer loginChatPet(HttpServletResponse response, String wxPubAppId, String wxFanOpenId) throws Exception{

        return null;
    }

}
