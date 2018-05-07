package net.monkeystudio.chatrbtw.service;

import com.google.zxing.WriterException;
import net.monkeystudio.base.Constants;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.HttpsHelper;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RandomUtil;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.enums.chatpet.ChatPetTaskEnum;
import net.monkeystudio.chatrbtw.enums.mission.MissionStateEnum;
import net.monkeystudio.chatrbtw.mapper.ChatPetMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.*;
import net.monkeystudio.chatrbtw.service.bean.chatpetlevel.ExperienceProgressRate;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.OwnerInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.PetLogResp;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.TodayMissionItem;
import net.monkeystudio.wx.service.WxOauthService;
import net.monkeystudio.wx.service.WxPubService;
import net.monkeystudio.wx.vo.oauth.WxOauthAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ChatPetMissionPoolService chatPetMissionPoolService;

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

    @Autowired
    private CfgService cfgService;

    @Autowired
    private ChatPetLevelService chatPetLevelService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    /**
     * 生成宠物
     * @param wxPubOriginId
     * @param wxFanOpenId
     * @return 生成的宠物的id
     */
    public Integer generateChatPet(String wxPubOriginId , String wxFanOpenId ,Integer ethnicGroupsId ,Integer secondEthnicGroupsId ,Integer parentId){

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
        chatPet.setParentId(parentId);

        String appearenceCode = this.getChatPetAppearenceCodeFromPool();
        chatPet.setAppearenceCode(appearenceCode);


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
     * @param wxFanId
     * @return
     */
    public ChatPetInfo getInfo(Integer wxFanId){
        ChatPetInfo chatPetBaseInfo = new ChatPetInfo();

        ChatPet chatPet = this.getChatPetByWxFanId(wxFanId);

        if(chatPet == null){
            return null;
        }

        Integer chatPetId = chatPet.getId();

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

        //今日宠物日志
        List<PetLogResp> resps = chatPetLogService.getDailyPetLogList(chatPetId, new Date());
        chatPetBaseInfo.setPetLogs(resps);

        //粉丝拥有代币
        Float fansTotalCoin = this.getChatPetTotalCoin(chatPetId);
        chatPetBaseInfo.setFanTotalCoin(fansTotalCoin);

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

        //宠物的经验
        Integer experience = chatPet.getExperience();
        chatPetBaseInfo.setExperience(experience);

        //经验条进度
        ExperienceProgressRate experienceProgressRate = chatPetLevelService.getProgressRate(experience);
        chatPetBaseInfo.setExperienceProgressRate(experienceProgressRate);

        //宠物等级
        Integer chatPetLevel = chatPetLevelService.calculateLevel(experience);
        chatPetBaseInfo.setChatPetLevel(chatPetLevel);

        //第一进入H5填充任务池任务
        chatPetMissionPoolService.createMissionWhenFirstChatOrComeH5(wxPubOriginId,wxFanOpenId);

        //今日任务
        List<TodayMissionItem> todayMissionList = chatPetMissionPoolService.getTodayMissionList(wxPubOriginId, wxFanOpenId);
        chatPetBaseInfo.setTodayMissions(todayMissionList);

        return chatPetBaseInfo;
    }

    /**
     * 获取宠物的信息
     * @param chatPetId
     * @return

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

        //今日宠物日志
        List<PetLogResp> resps = chatPetLogService.getDailyPetLogList(chatPetId, new Date());
        chatPetBaseInfo.setPetLogs(resps);

        //粉丝拥有代币
        Float fansTotalCoin = this.getChatPetTotalCoin(chatPetId);
        chatPetBaseInfo.setFanTotalCoin(fansTotalCoin);

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

        //宠物的经验
        Integer experience = chatPet.getExperience();
        chatPetBaseInfo.setExperience(experience);

        //经验条进度
        ExperienceProgressRate experienceProgressRate = chatPetLevelService.getProgressRate(experience);
        chatPetBaseInfo.setExperienceProgressRate(experienceProgressRate);

        //宠物等级
        Integer chatPetLevel = chatPetLevelService.calculateLevel(experience);
        chatPetBaseInfo.setChatPetLevel(chatPetLevel);

        //第一进入H5填充任务池任务
        chatPetMissionPoolService.createMissionWhenFirstChatOrComeH5(wxPubOriginId,wxFanOpenId);

        //今日任务
        List<TodayMissionItem> todayMissionList = chatPetMissionPoolService.getTodayMissionList(wxPubOriginId, wxFanOpenId);
        chatPetBaseInfo.setTodayMissions(todayMissionList);

        return chatPetBaseInfo;
    }
     */

    /**
     * 获取宠物的信息
     * @param chatPetId
     * @return
     */
    public ChatPetInfo getInfoAfterReward(Integer chatPetId){
        ChatPetInfo chatPetBaseInfo = new ChatPetInfo();

        ChatPet chatPet = this.getById(chatPetId);

        if(chatPet == null){
            return null;
        }

        String wxPubOriginId = chatPet.getWxPubOriginId();
        String wxFanOpenId = chatPet.getWxFanOpenId();

        //今日宠物日志
        List<PetLogResp> resps = chatPetLogService.getDailyPetLogList(chatPetId, new Date());
        chatPetBaseInfo.setPetLogs(resps);

        //粉丝拥有代币
        Float fansTotalCoin = this.getChatPetTotalCoin(chatPetId);
        chatPetBaseInfo.setFanTotalCoin(fansTotalCoin);

        //宠物的经验
        Integer experience = chatPet.getExperience();
        chatPetBaseInfo.setExperience(experience);

        //经验条进度
        ExperienceProgressRate experienceProgressRate = chatPetLevelService.getProgressRate(experience);
        chatPetBaseInfo.setExperienceProgressRate(experienceProgressRate);

        //宠物等级
        Integer chatPetLevel = chatPetLevelService.calculateLevel(experience);
        chatPetBaseInfo.setChatPetLevel(chatPetLevel);

        //今日任务
        List<TodayMissionItem> todayMissionList = chatPetMissionPoolService.getTodayMissionList(wxPubOriginId, wxFanOpenId);
        chatPetBaseInfo.setTodayMissions(todayMissionList);



        return chatPetBaseInfo;
    }

    public ChatPetInfo rewardHandle(Integer chatPetId,Integer itemId) throws BizException{

        if(!isAble2Reward(itemId)){
            throw new BizException("请完成任务后再领取奖励");
        }
        this.missionReward(chatPetId,itemId);
        ChatPetInfo info = this.getInfoAfterReward(chatPetId);
        return info;
    }

    /**
     * 点击"领取"时判断当前是否能够领取
     * @param itemId 任务池记录id
     * @return
     */
    public boolean isAble2Reward(Integer itemId){
        ChatPetPersonalMission cppm = chatPetMissionPoolService.getById(itemId);

        Integer nowState = cppm.getState();//当前任务领取状态
        Integer shouldState = MissionStateEnum.FINISH_NOT_AWARD.getCode();//当前任务领取状态应为 已完成未领取

        return shouldState.equals(nowState);
    }

    /**
     * 完成每日任务领取奖励
     * @param itemId
     */
    @Transactional
    public void missionReward(Integer chatPetId,Integer itemId) {
        //更新任务池记录
        chatPetMissionPoolService.updateMissionWhenReward(itemId);


        //增加金币
        ChatPetPersonalMission cppm = chatPetMissionPoolService.getById(itemId);
        Integer missionCode = cppm.getMissionCode();

        Float incrCoin = ChatPetTaskEnum.codeOf(missionCode).getCoinValue();
        this.increaseCoin(chatPetId,incrCoin);


        //增加经验
        ChatPet chatPet = this.getById(chatPetId);
        Integer oldExperience = chatPet.getExperience();

        Integer addExperience = incrCoin.intValue();
        this.increaseExperience(chatPetId,addExperience);

        Integer newExprience = this.getChatPetExperience(chatPetId);

        //插入日志
        chatPetLogService.savePetLogWhenReward(chatPetId,missionCode,oldExperience, newExprience);
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
     * 得到宠物主页的URL
     * @param chatPetId
     * @return
     */
    public String getChatPetHomeUrl(Integer chatPetId){
        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        String uri = "/res/wedo/zebra.html?id=" + chatPetId;
        String url = domain + uri;
        return url;
    }


    /**
     * 根据wxFanId获取宠物对象
     * @param fanId
     * @return
     */
    public ChatPet getChatPetByWxFanId(Integer fanId){
        WxFan wxFan = wxFanService.getById(fanId);
        String wxPubOriginId = wxFan.getWxPubOriginId();
        String wxFanOpenId = wxFan.getWxFanOpenId();

        ChatPet chatPetByFans = this.getChatPetByFans(wxPubOriginId, wxFanOpenId);
        return chatPetByFans;
    }


    /**
     * 微信网页授权处理
     * @param code
     */
    public ChatPetSessionVo wxOauthHandle(HttpServletResponse response,String code,String wxPubAppId) throws Exception{

        WxOauthAccessToken wxOauthAccessToken = this.getOauthAccessTokenResponse(code, wxPubAppId);

        if(wxOauthAccessToken == null){
            throw  new BizException("未能成功获取wxOauthAccessToken!");
        }

        String wxFanOpenId = wxOauthAccessToken.getWxFanOpenId();

        //未关注公众号或未领取宠物
        if(!isUserFollowWxPub(wxPubAppId,wxFanOpenId) || !isFanOwnChatPet(wxPubAppId,wxFanOpenId)){
            response.sendRedirect(this.getChatPetPosterUrl());
        }

        //准备跳转宠物日志h5数据
        ChatPetSessionVo vo = this.createChatPetSessionVo(wxPubAppId, wxFanOpenId);

        return vo;

    }

    /**
     * 获取存入chatPet session的fanId 以及跳转宠物日志页面所需参数wxPubId
     * @param wxPubAppId
     * @param wxFanOpenId
     * @return
     */
    private ChatPetSessionVo createChatPetSessionVo(String wxPubAppId,String wxFanOpenId){

        WxPub wxPub = wxPubService.getWxPubByAppId(wxPubAppId);

        String wxPubOriginId = wxPub.getOriginId();

        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);

        ChatPetSessionVo sessionVo = new ChatPetSessionVo();

        sessionVo.setWxFanId(wxFan.getId());

        sessionVo.setWxPubId(wxPub.getId());

        return sessionVo;

    }

    /**
     * 通过code获取access_token及openid
     * @return
     */
    private WxOauthAccessToken getOauthAccessTokenResponse(String code,String wxPubAppId) throws BizException{

        String fetchAccessTokenUrl = wxOauthService.getAccessTokenUrl(code,wxPubAppId);
        String response = HttpsHelper.get(fetchAccessTokenUrl);

        if(response == null || response.indexOf("errorcode") != -1){
            return null;
        }

        WxOauthAccessToken wxOauthAccessToken = JsonUtil.readValue(response, WxOauthAccessToken.class);

        if(wxOauthAccessToken != null){
            Log.d("============ wxoauth access_token = {?} , openid = {?} =============",wxOauthAccessToken.getAccessToken(),wxOauthAccessToken.getWxFanOpenId());
        }

        return wxOauthAccessToken;
    }

    /**
     * 判断用户是否关注公众号
     * @param wxPubAppId
     * @param wxFanOpenId
     * @return
     */
    private boolean isUserFollowWxPub(String wxPubAppId,String wxFanOpenId){
        String wxPubOriginId = wxPubService.getWxPubOriginIdByAppId(wxPubAppId);

        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);

        if(wxFan != null){
            return true;
        }
        return false;
    }

    /**
     * 判断粉丝是否已经领取陪聊宠
     * @param wxPubAppId
     * @param wxFanOpenId
     * @return
     */
    private boolean isFanOwnChatPet(String wxPubAppId,String wxFanOpenId){
        String wxPubOriginId = wxPubService.getWxPubOriginIdByAppId(wxPubAppId);

        ChatPet chatPet = this.getChatPetByFans(wxPubOriginId, wxFanOpenId);

        if(chatPet != null){
            return true;
        }
        return false;
    }

    /**
     * 宠物日志页面url
     *
     * @return
     */
    public String getZebraHtmlUrl(Integer wxPubId){
        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        String url = "http://" + domain + "/res/wedo/zebra.html?id=" + wxPubId;
        return url;
    }

    public String getCheckLoginUrl(Integer wxPubId){
        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        String url = "http://" + domain + "/api/chat-pet/pet/check-login" + wxPubId;
        return url;
    }

    public String getWxOauthUrl(Integer wxPubId){
        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        String url = "http://" + domain + "/api/wx/oauth/redirect?id=" + wxPubId;
        return url;
    }

    /**
     * 增加金币
     * @param chatPetId 宠物id
     * @param coin  增加的金币
     */
    public void increaseCoin(Integer chatPetId ,Float coin){
        chatPetMapper.increaseCoin(chatPetId,coin);
    }

    /**
     * 获取宠物的总金币
     * @return
     */
    public Float getChatPetTotalCoin(Integer chatPetId){
        ChatPet chatPet = this.getById(chatPetId);

        return chatPet.getCoin();
    }

    /**
     * TODO
     * 获取一个微信用户在多个公众号中的总金币 unionId
     * @return
     */
    public Float getFansTotalCoin(){
        return null;
    }

    /**
     * 增加经验
     * @param chatPetId 宠物id
     * @param experience  增加的经验值
     */
    public void increaseExperience(Integer chatPetId ,Integer experience){
        chatPetMapper.increaseExperience(chatPetId,experience);
    }




    /**
     * 获得宠物的经验
     * @param chatPetid
     * @return
     */
    public Integer getChatPetExperience(Integer chatPetid ){
        ChatPet chatPet = this.getById(chatPetid);

        return chatPet.getExperience();
    }

    /**
     * 获取经验排行
     * @param secondEthnicGroupsId
     * @param page
     * @param pageSize
     * @return
     */
    private List<ChatPet> getListByExperience(Integer secondEthnicGroupsId ,Integer page,Integer pageSize){

        Integer startIndex = (page - 1) * pageSize;

        return chatPetMapper.selectListByExperience(secondEthnicGroupsId,startIndex,pageSize);
    }

    /**
     * 通过宠物id获取二级族群排行
     * @param chatPetId
     * @param pageSize
     * @return
     */
    public List<ChatPetExperinceRankItem> getChatPetExperinceRankByPet(Integer chatPetId , Integer pageSize){
        ChatPet chatPet = this.getById(chatPetId);

        if(chatPet == null){
            return null;
        }

        Integer secondEthnicGroupsId = chatPet.getSecondEthnicGroupsId();
        return this.getChatPetExperinceRank(secondEthnicGroupsId, pageSize);
    }

    private List<ChatPetExperinceRankItem> getChatPetExperinceRank(Integer secondEthnicGroupsId ,Integer pageSize){
        List<ChatPet> chatPetList = this.getListByExperience(secondEthnicGroupsId, 1, pageSize);

        List<ChatPetExperinceRankItem> chatPetExperinceRankItemList = new ArrayList<>();

        for(ChatPet chatPet : chatPetList){

            ChatPetExperinceRankItem chatPetExperinceRankItem = new ChatPetExperinceRankItem();

            //宠物的等级
            Integer experience = chatPet.getExperience();
            Integer level = chatPetLevelService.calculateLevel(experience);
            chatPetExperinceRankItem.setLevel(level);


            String wxFanOpenId = chatPet.getWxFanOpenId();
            String wxPubOriginId = chatPet.getWxPubOriginId();
            WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
            //粉丝头像
            String headImgUrl = wxFan.getHeadImgUrl();
            chatPetExperinceRankItem.setWxFanHeadImgUrl(headImgUrl);

            //粉丝的昵称
            chatPetExperinceRankItem.setWxFanNickname(wxFan.getNickname());

            chatPetExperinceRankItemList.add(chatPetExperinceRankItem);
        }

        return chatPetExperinceRankItemList;
    }



    /**
     * 得到封面图的url
     * @return
     */
    public String getNewsMessageCoverUrl(){

        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        String picUrl = "http://" + domain + "/res/wedo/images/kitties_normal_cover.png";

        return picUrl;
    }

    /**
     * 获取创世海报url
     * @return
     */
    public String getChatPetPosterUrl(){
        //"https://test.keendo.com.cn/res/wedo/poster.html"
        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        String posterUrl = "http://" + domain + "res/wedo/poster.html";

        return posterUrl;
    }

    public RedisCacheTemplate getRedisCacheTemplate() {
        return redisCacheTemplate;
    }

    private void generateChatPetAppearence(Integer count){
        while (count.intValue() > 0){
            this.generateChatPetAppearence();
            count--;
        }
    }
    /**
     * 生成一个宠物外观放入外观池
     */
    private void generateChatPetAppearence(){

        Boolean flag = false;
        String appearanceCode = null;
        while (!flag){
            String[] appearance = this.randomAppearence();
            appearanceCode = appearance.toString();
            if(!this.appearenceIsExist(null,appearanceCode)){
                flag = true;
            }
        }

        this.pushAppearence(appearanceCode);
    }


    private void pushAppearence(String appearenceCode){
        String key = this.getAppearenceCodePoolKey();

        redisCacheTemplate.rpush(key, appearenceCode);
    }

    /**
     * 外貌是否已经存在
     * @param appeareanceType 外观的类型
     * @param appearenceCode 外观的组成码
     * @return
     */
    private Boolean appearenceIsExist (Integer appeareanceType ,String appearenceCode){

        Integer count = chatPetMapper.countByAppearceCode(appearenceCode);

        if(count.intValue() != 0){
            return true;
        }

        return false;
    }


    private String[] randomAppearence(){

        return null;
    }


    private String getChatPetAppearenceCodeFromPool(){
        String key = this.getAppearenceCodePoolKey();

        String appearenceCode = redisCacheTemplate.lpop(key);

        Long count = redisCacheTemplate.llen(key);
        if(count.longValue() < 100L){
            this.generateChatPetAppearence(10);
        }

        return appearenceCode;
    }

    private String getAppearenceCodePoolKey(){
        String key = RedisTypeConstants.KEY_LIST_TYPE_PREFIX + "chat-pet:appearence-code:pool";
        return key;
    }

    //用户未授权跳转到授权页面
    /*public String getNoAuthRedirectUrl(Integer wxFanId){
        WxFan wxFan = wxFanService.getById(wxFanId);
        String wxPubOriginId = wxFan.getWxPubOriginId();
        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);

        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        String picUrl = "http://" + domain + "/api/wx/oauth/redirect/?id="+5;
        return null;
    }*/
}
