package net.monkeystudio.chatrbtw.service;

import com.google.zxing.WriterException;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
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
import net.monkeystudio.chatrbtw.service.bean.chatpetappearence.Appearance;
import net.monkeystudio.chatrbtw.service.bean.chatpetappearence.ZombiesCatAppearance;
import net.monkeystudio.chatrbtw.service.bean.chatpetlevel.ExperienceProgressRate;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.TodayMissionItem;
import net.monkeystudio.wx.service.WxOauthService;
import net.monkeystudio.wx.service.WxPubService;
import net.monkeystudio.wx.vo.oauth.WxOauthAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private RWxPubProductService rWxPubProductService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private ChatPetAppearenceService chatPetAppearenceService;

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

        String appearanceCode = chatPetAppearenceService.getChatPetAppearenceCodeFromPool();
        chatPet.setAppearanceCode(appearanceCode);


        this.save(chatPet);

        Integer chatPetId = chatPet.getId();

        chatPetLogService.savePetBornLog(wxPubOriginId,wxFanOpenId,chatPetId);
        return chatPetId;
    }

    /**
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
        List<TodayMissionItem> todayMissionList = chatPetMissionPoolService.getTodayMissionList(chatPetId);
        chatPetBaseInfo.setTodayMissions(todayMissionList);


        String appearanceCode = chatPet.getAppearanceCode();
        ZombiesCatAppearance zombiesCatAppearance = chatPetAppearenceService.getZombiesCatAppearence(appearanceCode);
        Appearance appearance = new Appearance();
        appearance.setChatPetType(ChatPetTypeService.CHAT_PET_TYPE_ZOMBIES_CAT);
        appearance.setObject(zombiesCatAppearance);
        chatPetBaseInfo.setAppearance(appearance);

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
        List<TodayMissionItem> todayMissionList = chatPetMissionPoolService.getTodayMissionList(chatPetId);
        chatPetBaseInfo.setTodayMissions(todayMissionList);



        return chatPetBaseInfo;
    }

    public ChatPetInfo rewardHandle(Integer wxFanId,Integer itemId) throws BizException{
        ChatPet chatPet = this.getChatPetByWxFanId(wxFanId);

        if(chatPet == null){
            throw new BizException("尚未领取宠物");
        }

        Integer chatPetId = chatPet.getId();

        //获取当前任务领取状态
        ChatPetPersonalMission cppm = chatPetMissionPoolService.getById(itemId);
        Integer nowState = cppm.getState();

        if(Integer.valueOf(MissionStateEnum.GOING_ON.getCode()).equals(nowState)){
            throw new BizException("请完成任务后再领取奖励");
        }
        if(Integer.valueOf(MissionStateEnum.FINISH_AND_AWARD.getCode()).equals(nowState)){
            throw new BizException("您已领取过奖励");
        }

        if(isAble2Reward(nowState)){
            this.missionReward(chatPetId,itemId);
        }

        ChatPetInfo info = this.getInfoAfterReward(chatPetId);

        return info;
    }

    /**
     * 点击"领取"时判断当前是否能够领取
     * @param nowState : 当前任务状态
     * @return
     */
    public boolean isAble2Reward(Integer nowState){

        Integer shouldState = MissionStateEnum.FINISH_NOT_AWARD.getCode();//当前任务领取状态应为 已完成未领取

        return shouldState.equals(nowState);
    }

    /**
<<<<<<< Updated upstream
=======
     *   TODO
     * 加入奖励池后  修改
     * 完成每日任务领取奖励
     * @param rewardItemId:奖励池表主键  missionItemId:任务池表主键
     */
    @Transactional
    public void missionReward(Integer rewardItemId,Integer chatPetId,Integer missionItemId) {
        //更新任务池记录
        //chatPetMissionPoolService.updateMissionWhenReward(missionItemId);

        //判断rewardItem的state是否为未领奖  &&  missionItemId判断该任务已经完成

        //增加金币  金币是一定会增加的
        ChatPetPersonalMission cppm = chatPetMissionPoolService.getById(missionItemId);
        Integer missionCode = cppm.getMissionCode();

        Float incrCoin = ChatPetTaskEnum.codeOf(missionCode).getCoinValue();
        this.increaseCoin(chatPetId,incrCoin);


        //根据missionItemId == null 判断该奖励是否为完成任务后的奖励. 每日可领取奖励
        //增加经验
        //increaseExperienceHandle(missionItemId,missionCode);
        ChatPet chatPet = this.getById(chatPetId);
        Integer oldExperience = chatPet.getExperience();

        Integer addExperience = incrCoin.intValue();
        this.increaseExperience(chatPetId,addExperience);

        Integer newExprience = this.getChatPetExperience(chatPetId);

        //插入日志
        //需要问清楚一件事:悬浮的奖励是不是只标明金币数,不会说这个是什么金币???  如果需要的话就要搞一个金币枚举类了.
        //填充奖励池,首先去missionPool里面看有没有已经完成了的任务,有可能第一次进H5的时候已经把当日聊天任务给完成了.
        //传到前端需要数据: coinValue  rewardItemId  missionItemId  RewardTypeEnum.type(当日聊天,阅读任务,...奖励 , missionCode)
        //问:金币上需不需要展示是什么类型奖励,
        chatPetLogService.savePetLogWhenReward(chatPetId,missionCode,oldExperience, newExprience);
    }


    /**
>>>>>>> Stashed changes
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



    /**
     * 粉丝是否拥有宠物
     * @param wxFanOpenId
     * @return
     */
    public boolean isFansOwnChatPet(String wxPubOriginId,String wxFanOpenId){
        ChatPet chatPet = this.getChatPetByFans(wxPubOriginId, wxFanOpenId);
        return chatPet != null;
    }

    /**
     * 是否满足宠陪聊要求 1:公众号开通产品线 2:粉丝拥有宠物
     * @param wxPubOriginId
     * @param wxFanOpenId
     * @return
     */
    public boolean isSatisfyChatPetRequire(String wxPubOriginId,String wxFanOpenId){
        boolean isFanOwnChatPet = false;

        ChatPet chatPet = this.getChatPetByFans(wxPubOriginId, wxFanOpenId);

        if(chatPet != null){
            isFanOwnChatPet = true;
        }

        boolean isProductEnable = rWxPubProductService.isEnable(ProductService.CHAT_PET, wxPubOriginId);

        return isFanOwnChatPet && isProductEnable;
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

    public Integer getChatPetIdByFans(String wxPubOriginId,String wxFanOpenId){
        Integer ret = null;
        ChatPet chatPet = this.getChatPetByFans(wxPubOriginId, wxFanOpenId);
        if(chatPet != null){
            ret = chatPet.getId();
        }
        return ret;
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
    public ChatPetSessionVo wxOauthHandle(String code,String wxPubAppId) throws BizException{

        WxOauthAccessToken wxOauthAccessToken = this.getOauthAccessTokenResponse(code, wxPubAppId);

        if(wxOauthAccessToken == null){
            throw  new BizException("未能成功获取wxOauthAccessToken!");
        }

        String wxFanOpenId = wxOauthAccessToken.getWxFanOpenId();

        boolean isRedirectPoster = false;
        //未关注公众号或未领取宠物
        if(!isUserFollowWxPub(wxPubAppId,wxFanOpenId) || !isFanOwnChatPet(wxPubAppId,wxFanOpenId)){
            isRedirectPoster = true;
        }

        //准备跳转宠物日志h5数据
        ChatPetSessionVo vo = this.createChatPetSessionVo(wxPubAppId, wxFanOpenId,isRedirectPoster);

        return vo;

    }

    /**
     * 获取存入chatPet session的fanId 以及跳转宠物日志页面所需参数wxPubId
     * @param wxPubAppId
     * @param wxFanOpenId
     * @return
     */
    private ChatPetSessionVo createChatPetSessionVo(String wxPubAppId,String wxFanOpenId,Boolean isRedirectPoster){

        WxPub wxPub = wxPubService.getWxPubByAppId(wxPubAppId);

        String wxPubOriginId = wxPub.getOriginId();

        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);

        ChatPetSessionVo sessionVo = new ChatPetSessionVo();

        sessionVo.setWxFanId(wxFan.getId());

        sessionVo.setWxPubId(wxPub.getId());

        sessionVo.setRedirectPoster(isRedirectPoster);

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

        boolean isFans = wxFanService.isFans(wxPubOriginId, wxFanOpenId);

        return isFans;
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

    public String getHomePageUrl(Integer wxPubId){
        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        String url = "http://" + domain + "/api/chat-pet/pet/home-page?id=" + wxPubId;
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
     * 通过微信粉丝id获取二级族群排行
     * @param wxFanId
     * @param pageSize
     * @return
     */
    public ChatPetExperinceRank getChatPetExperinceRankByWxFan(Integer wxFanId , Integer pageSize){
        WxFan wxFan = wxFanService.getById(wxFanId);
        String wxFanOpenId = wxFan.getWxFanOpenId();
        String wxPubOriginId = wxFan.getWxPubOriginId();

        ChatPet chatPet = this.getChatPetByFans(wxPubOriginId, wxFanOpenId);

        if(chatPet == null){
            return null;
        }

        Integer chatPetId = chatPet.getId();

        ChatPetExperinceRank chatPetExperinceRank = new ChatPetExperinceRank();

        //获取排行
        List<ChatPetExperinceRankItem> chatPetExperinceRankList = this.getChatPetExperinceRankByPet(chatPetId,pageSize);
        chatPetExperinceRank.setChatPetExperinceRankItemList(chatPetExperinceRankList);

        Integer count = this.countSecondEthnicGroupsById(chatPet.getSecondEthnicGroupsId());
        chatPetExperinceRank.setTotal(count);

        return chatPetExperinceRank;
    }

    /**
     * 通过宠物id获取二级族群排行
     * @param chatPetId
     * @param pageSize
     * @return
     */
    private List<ChatPetExperinceRankItem> getChatPetExperinceRankByPet(Integer chatPetId , Integer pageSize){
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
        String picUrl = "http://" + domain + "/res/wedo/images/kitties_normal_cover.jpg";

        return picUrl;
    }

    private Integer countSecondEthnicGroupsById(Integer secondEthnicGroupsId){
        return chatPetMapper.countSecondEthnicGroupsById(secondEthnicGroupsId);
    }

    /**
     * 获取创世海报url
     * @return
     */
    public String getChatPetPosterUrl(){
        //"https://test.keendo.com.cn/res/wedo/poster.html"
        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        String posterUrl = "http://" + domain + "/res/wedo/poster.html";

        return posterUrl;
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
