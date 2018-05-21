package net.monkeystudio.chatrbtw.service;

import com.google.zxing.WriterException;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.HttpsHelper;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RandomUtil;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.enums.mission.MissionStateEnum;
import net.monkeystudio.chatrbtw.mapper.ChatPetMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.*;
import net.monkeystudio.chatrbtw.service.bean.chatpetappearence.Appearance;
import net.monkeystudio.chatrbtw.service.bean.chatpetappearence.ZombiesCatAppearance;
import net.monkeystudio.chatrbtw.service.bean.chatpetlevel.ExperienceProgressRate;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.TodayMissionItem;
import net.monkeystudio.wx.service.WxOauthService;
import net.monkeystudio.wx.service.WxPubService;
import net.monkeystudio.wx.vo.customerservice.CustomerNewsItem;
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
    private MissionEnumService missionEnumService;

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
    private ChatPetAppearenceService chatPetAppearenceService;

    @Autowired
    private ChatPetRewardItemService chatPetRewardItemService;
    @Autowired
    private RWxPubChatPetTypeService rWxPubChatPetTypeService;

    @Autowired
    private ChatPetTypeConfigService chatPetTypeConfigService;

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
    public ChatPetInfo getInfoByWxFanId(Integer wxFanId){

        ChatPet chatPet = this.getChatPetByWxFanId(wxFanId);

        if(chatPet == null){
            return null;
        }

        Integer chatPetId = chatPet.getId();

        return this.getInfo(chatPetId);
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
        Float experience = chatPet.getExperience();
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

        //奖励
        List<ChatPetGoldItem> chatPetGoldItems = chatPetRewardItemService.getChatPetGoldItems(chatPetId);
        chatPetBaseInfo.setGoldItems(chatPetGoldItems);


        String appearanceCode = chatPet.getAppearanceCode();
        ZombiesCatAppearance zombiesCatAppearance = chatPetAppearenceService.getZombiesCatAppearence(appearanceCode);
        Appearance appearance = new Appearance();
        appearance.setChatPetType(ChatPetTypeService.CHAT_PET_TYPE_ZOMBIES_CAT);
        appearance.setObject(zombiesCatAppearance);
        chatPetBaseInfo.setAppearance(appearance);

        return chatPetBaseInfo;
    }

    /**
     * 首次登录数据准备  任务池
     * @param wxFanId
     * @param wxPubId
     */
    public void dataPrepared(Integer wxFanId,Integer wxPubId){
        WxFan wxFan = wxFanService.getById(wxFanId);
        String wxFanOpenId = wxFan.getWxFanOpenId();

        WxPub wxPub = wxPubService.getWxPubById(wxPubId);
        String originId = wxPub.getOriginId();
        //第一次登录需要准备任务池数据
        chatPetMissionPoolService.createMissionWhenFirstChatOrComeH5(originId,wxFanOpenId);

        ChatPet chatPet = this.getChatPetByFans(originId, wxFanOpenId);
        //奖励池数据
        chatPetRewardItemService.createInitRewardItems(chatPet.getId());
    }

    /**
     * 领取奖励后获取宠物修改的信息
     * @param chatPetId
     * @return
     */
    public ChatPetRewardChangeInfo getInfoAfterReward(Integer wxFanId,Integer chatPetId){

        ChatPet chatPet = this.getById(chatPetId);

        if(chatPet == null){
            return null;
        }

        ChatPetInfo info = this.getInfo(chatPetId);

        ChatPetRewardChangeInfo changeInfo = new ChatPetRewardChangeInfo();
        changeInfo.setChatPetLevel(info.getChatPetLevel());
        changeInfo.setExperience(info.getExperience());
        changeInfo.setExperienceProgressRate(info.getExperienceProgressRate());
        changeInfo.setFanTotalCoin(info.getFanTotalCoin());
        changeInfo.setPetLogs(info.getPetLogs());
        changeInfo.setTodayMissions(info.getTodayMissions());
        //族群排名
        ChatPetExperinceRank chatPetExperinceRankByWxFan = this.getChatPetExperinceRankByWxFan(wxFanId, 1);
        changeInfo.setGroupRank(chatPetExperinceRankByWxFan);

        List<ChatPetGoldItem> chatPetGoldItems = chatPetRewardItemService.getChatPetGoldItems(chatPetId);
        changeInfo.setGoldItems(chatPetGoldItems);

        return changeInfo;

    }

    public ChatPetRewardChangeInfo rewardHandle(Integer wxFanId,Integer rewardItemId,Integer missionItemId) throws BizException{
        ChatPet chatPet = this.getChatPetByWxFanId(wxFanId);

        if(chatPet == null){
            throw new BizException("尚未领取宠物");
        }

        Integer chatPetId = chatPet.getId();

        //获取当前任务领取状态
        /*ChatPetPersonalMission cppm = chatPetMissionPoolService.getById(missionItemId);
        Integer nowState = cppm.getState();

        if(Integer.valueOf(MissionStateEnum.GOING_ON.getCode()).equals(nowState)){
            throw new BizException("请完成任务后再领取奖励");
        }
        if(Integer.valueOf(MissionStateEnum.FINISH_AND_AWARD.getCode()).equals(nowState)){
            throw new BizException("您已领取过奖励");
        }

        if(isAble2Reward(nowState)){
            this.missionReward(rewardItemId,chatPetId,missionItemId);
        }*/
        this.missionReward(rewardItemId);

        ChatPetRewardChangeInfo info = this.getInfoAfterReward(wxFanId,chatPetId);

        return info;
    }

    /**
     * 点击"领取"时判断当前是否能够领取
     * @param nowState : 当前任务状态
     * @return
     */
    private boolean isAble2Reward(Integer nowState){

        Integer shouldState = MissionStateEnum.FINISH_NOT_AWARD.getCode();//当前任务领取状态应为 已完成未领取

        return shouldState.equals(nowState);
    }


    /**
     * 加入奖励池后  修改
     * 完成每日任务领取奖励
     * @param chatPetRewardItemId 奖励池表主键
     * @throws BizException
     */
    @Transactional
    public void missionReward(Integer chatPetRewardItemId) throws BizException{

        //判断rewardItem的state是否为未领奖  &&  missionItemId判断该任务已经完成  还没完成任务不能领奖
        ChatPetRewardItem chatPetRewardItem = chatPetRewardItemService.getChatPetRewardItemById(chatPetRewardItemId);

        Integer missionItemId = chatPetRewardItem.getMissionItemId();

        //是否为任务类型奖励
        Boolean isMissionReward = false;

        //根据missionItemId == null 判断该奖励是否为完成任务后的奖励. 区分每日可领取奖励
        if(missionItemId != null){
            isMissionReward = true;
        }

        Integer chatPetId = chatPetRewardItem.getChatPetId();

        //判断领取的是否为自己的奖励
        Integer chatPetIdInDb = chatPetRewardItem.getChatPetId();
        if(!chatPetId.equals(chatPetIdInDb)){
            throw new BizException("无法领取");
        }

        if(chatPetRewardItemService.isGoldAwarded(chatPetRewardItemId)){
            throw new BizException("您已经领取过奖励");
        }

        if(isMissionReward && !chatPetMissionPoolService.isFinishMission(missionItemId)){
            throw new BizException("请完成任务后再领取奖励");
        }

        //增加金币
        Float incrCoin = chatPetRewardItem.getGoldValue();
        this.increaseCoin(chatPetId,incrCoin);


        //增加经验
        Float oldExperience = null;
        Float newExperience = null;

        Boolean isUpgrade = false;
        //如果是从任务来的奖励
        if(isMissionReward){

            ChatPet chatPet = this.getById(chatPetId);
            oldExperience = chatPet.getExperience();

            Integer addExperience = incrCoin.intValue();
            this.increaseExperience(chatPetId,addExperience);

            newExperience = this.getChatPetExperience(chatPetId);

            isUpgrade = chatPetLevelService.isUpgrade(oldExperience, newExperience);

            ChatPetPersonalMission chatPetPersonalMission = chatPetMissionPoolService.getById(missionItemId);
            if(MissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(chatPetPersonalMission.getMissionCode())){
                chatPetMissionPoolService.dispatchMission(MissionEnumService.INVITE_FRIENDS_MISSION_CODE,chatPetId);
            }
        }

        //插入日志
        if(isMissionReward){
            chatPetLogService.savePetLogWhenReward(missionItemId,isUpgrade);
        }else{
            chatPetLogService.saveDailyFixedCoinLog(chatPetId,chatPetRewardItemId);
        }
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

        Float incrCoin = missionEnumService.getMissionByCode(missionCode).getCoin();
        this.increaseCoin(chatPetId,incrCoin);


        //增加经验
        ChatPet chatPet = this.getById(chatPetId);
        Float oldExperience = chatPet.getExperience();

        Integer addExperience = incrCoin.intValue();
        this.increaseExperience(chatPetId,addExperience);

        Float newExprience = this.getChatPetExperience(chatPetId);


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
     * 粉丝是否能进入到宠物页面,判断是否关注,是否领取宠物
     * @param fansId
     * @param wxPubId
     * @return
     */
    public boolean isAble2Access(Integer fansId,Integer wxPubId){

        WxPub wxPub = wxPubService.getWxPubById(wxPubId);
        String wxPubAppId = wxPub.getAppId();

        WxFan wxFan = wxFanService.getById(fansId);

        String wxFanOpenId = wxFan.getWxFanOpenId();

        Boolean ret =   isUserFollowWxPub(wxPubAppId,wxFanOpenId) && isFanOwnChatPet(wxPubAppId,wxFanOpenId);

        return ret;
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

        Boolean isFans = wxFanService.isFans(wxPubOriginId, wxFanOpenId);

        return isFans;
    }

    /**
     * 登录时判断当前访问WxPubId与session中fanId是否匹配
     * 若不匹配需要置空
     * @param userId    粉丝Id
     * @param wxPubId   公众号Id
     * @return
     */
    public boolean isNeed2EmptyUser4Session(Integer userId,Integer wxPubId){
        WxFan wxFan = wxFanService.getById(userId);

        String wxPubOriginId = wxFan.getWxPubOriginId();

        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);//session中的fanId对应的WxPub

        Integer wxPubIdInSession = wxPub.getId();

        return wxPubId.equals(wxPubIdInSession);
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
        String domain = cfgService.get(GlobalConfigConstants.CHAT_PET_WEB_DOMAIN_KEY);
        String url = "http://" + domain + "/res/wedo/zombiescat.html?id=" + wxPubId;
        return url;
    }

    public String getHomePageUrl(Integer wxPubId){
        String domain = cfgService.get(GlobalConfigConstants.CHAT_PET_WEB_DOMAIN_KEY);
        String url = "http://" + domain + "/api/chat-pet/pet/home-page?id=" + wxPubId;
        return url;
    }

    /**
     * 获取宠物对应的html的url
     * @param wxPubId
     * @return
     */
    public String getChatPetPageUrl(Integer wxPubId){

        WxPub wxPub = wxPubService.getWxPubById(wxPubId);
        Integer chatPetType = rWxPubChatPetTypeService.getChatPetType(wxPub.getOriginId());

        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        if(ChatPetTypeService.CHAT_PET_TYPE_ZOMBIES_CAT.equals(chatPetType)){
            return "http://" + domain + "/res/wedo/zombiescat.html?id=" + wxPubId;
        }

        return null;
    }

    public String getWxOauthUrl(Integer wxPubId){
        String domain = cfgService.get(GlobalConfigConstants.CHAT_PET_WEB_DOMAIN_KEY);
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
    public Float getChatPetExperience(Integer chatPetid ){
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
            Float experience = chatPet.getExperience();
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
     * 获取宠物等级
     * @param chatPetId
     * @return
     */
    public Integer getChatPetLevel(Integer chatPetId){
        ChatPet chatPet = this.getById(chatPetId);
        Float experience = chatPet.getExperience();
        Integer level = chatPetLevelService.calculateLevel(experience);
        return level;
    }



    /**
     * 得到封面图的url
     * @return
     */
    public String getNewsMessageCoverUrl(){

        String domain = cfgService.get(GlobalConfigConstants.CHAT_PET_WEB_DOMAIN_KEY);
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
        String domain = cfgService.get(GlobalConfigConstants.CHAT_PET_WEB_DOMAIN_KEY);
        String posterUrl = "http://" + domain + "/res/wedo/poster.html";

        return posterUrl;
    }







    /**
     * 获取创始海报的信息
     * @param wxFanId
     * @return
     */
    public CreationPost getCreationPost(Integer wxFanId){
        CreationPost creationPost = new CreationPost();

        WxFan wxFan = wxFanService.getById(wxFanId);

        String wxPubOriginId = wxFan.getWxPubOriginId();

        try {
            String base64 = ethnicGroupsService.getCreateFounderQrCodeImageBase64(wxPubOriginId);
            creationPost.setWxPubQrCode(base64);
        } catch (BizException e) {
            Log.e(e);
        } catch (IOException e) {
            Log.e(e);
        } catch (WriterException e) {
            Log.e(e);
        }

        Integer type = rWxPubChatPetTypeService.getChatPetType(wxPubOriginId);
        creationPost.setChatPetType(type);

        return creationPost;
    }

    //应该改为传宠物id即可生成
    public CustomerNewsItem getChatNewsItem(Integer chatPetType ,String parentWxFanNickname ,String wxFanNickname ,String wxPubOriginId){
        ChatPetTypeConfig chatPetTypeConfig = chatPetTypeConfigService.getChatPetTypeConfig(chatPetType);

        CustomerNewsItem customerNewsItem = new CustomerNewsItem();

        String description = chatPetTypeConfig.getNewsDescription();

        //替换邀请人
        if(parentWxFanNickname == null){
            String founderName = chatPetTypeConfig.getFounderName();
            description = description.replace("#{parentName}", founderName);
        }else {
            description = description.replace("#{parentName}", parentWxFanNickname);
        }

        //替换粉丝名称
        description = description.replace("#{wxFanNickname}", wxFanNickname);

        //替换出生日期
        Calendar calendar = Calendar.getInstance();
        String date = (calendar.get(Calendar.YEAR)) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + calendar.get(Calendar.HOUR_OF_DAY) + "点" + calendar.get(Calendar.MINUTE) + "分";
        description = description.replace("#{date}", date);

        customerNewsItem.setDescription(description);

        //标题
        String title = chatPetTypeConfig.getNewsTitle();
        customerNewsItem.setTitle(title);

        //图文封面
        String domain = cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        String coverUrl = chatPetTypeConfig.getNewsCoverUrl();
        customerNewsItem.setPicUrl("http://" + domain + coverUrl);

        Integer wxPubId = wxPubService.getByOrginId(wxPubOriginId).getId();
        String url = this.getHomePageUrl(wxPubId);
        customerNewsItem.setUrl(url);

        return customerNewsItem;
    }

}
