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
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.TodayMission;
import net.monkeystudio.wx.service.WxOauthService;
import net.monkeystudio.wx.service.WxPubService;
import net.monkeystudio.wx.vo.customerservice.CustomerNewsItem;
import net.monkeystudio.wx.vo.oauth.WxOauthAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ChatPetBackgroundService chatPetBackgroundService;


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
    private ChatPetRewardService chatPetRewardService;
    @Autowired
    private RWxPubChatPetTypeService rWxPubChatPetTypeService;

    @Autowired
    private ChatPetTypeConfigService chatPetTypeConfigService;

    @Autowired
    private ChatPetTypeService chatPetTypeService;

    /**
     * 生成宠物
     * @param wxPubOriginId
     * @param wxFanOpenId
     * @return 生成的宠物的id
     */
    public Integer generateChatPet(String wxPubOriginId , String wxFanOpenId ,Integer ethnicGroupsId ,Integer secondEthnicGroupsId ,Integer parentId){

        ChatPet chatPet = new ChatPet();
        //Integer appearance = this.ramdomGenerateAppearence();

        Integer chatPetType = rWxPubChatPetTypeService.getChatPetType(wxPubOriginId);

        //如果是魔鬼猫
        if(ChatPetTypeService.CHAT_PET_TYPE_ZOMBIES_CAT.intValue() == chatPetType.intValue()){
            String appearanceCode = chatPetAppearenceService.getZombiesCatAppearanceCodeFromPool();
            chatPet.setAppearanceCode(appearanceCode);
        }

        //如果是以太猫
        if(ChatPetTypeService.CHAT_PET_TYPE_CRYPTO_KITTIES.intValue() == chatPetType.intValue()){
            cryptoKittiesService.designateKitty(wxPubOriginId,wxFanOpenId);
            CryptoKitties cryptoKitties = cryptoKittiesService.getKittyByOwner(wxPubOriginId, wxFanOpenId);
            Integer appearance = cryptoKitties.getId();
            chatPet.setTempAppearence(appearance);
        }

        chatPet.setWxFanOpenId(wxFanOpenId);
        chatPet.setWxPubOriginId(wxPubOriginId);
        chatPet.setEthnicGroupsId(ethnicGroupsId);
        chatPet.setSecondEthnicGroupsId(secondEthnicGroupsId);
        chatPet.setCreateTime(new Date());
        chatPet.setParentId(parentId);

        this.save(chatPet);

        Integer chatPetId = chatPet.getId();

        chatPetLogService.savePetBornLog(wxPubOriginId,wxFanOpenId,chatPetId);
        return chatPetId;
    }


    public ChatPet getById(Integer id){
        return chatPetMapper.selectById(id);
    }

    public Integer save(ChatPet chatPet){
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

        //宠物背景图信息
        ChatPetBackgroundInfo chatPetBackgroundInfo = chatPetBackgroundService.getChatPetBackgroundInfo(chatPetId);
        chatPetBaseInfo.setChatPetBackgroundInfo(chatPetBackgroundInfo);

        //宠物基因
        String geneticCode = this.calculateGeneticCode(chatPet.getCreateTime().getTime());
        chatPetBaseInfo.setGeneticCode(geneticCode);

        //今日宠物日志
        List<PetLogResp> resps = chatPetLogService.getDailyPetLogList(chatPetId);
        chatPetBaseInfo.setPetLogs(resps);

        //粉丝拥有代币
        Float fansTotalCoin = this.getChatPetTotalCoin(chatPetId);
        chatPetBaseInfo.setFanTotalCoin(fansTotalCoin);

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
        TodayMission todayMission = chatPetMissionPoolService.getTodayMissionWall(chatPetId);
        chatPetBaseInfo.setTodayMission(todayMission);

        //奖励
        List<ChatPetGoldItem> chatPetGoldItems = chatPetRewardService.getChatPetGoldItems(chatPetId);
        chatPetBaseInfo.setGoldItems(chatPetGoldItems);

        Integer chatPetType = rWxPubChatPetTypeService.getChatPetType(wxPubOriginId);

        //如果是魔鬼猫
        if(ChatPetTypeService.CHAT_PET_TYPE_ZOMBIES_CAT.intValue() == chatPetType.intValue()){
            String appearanceCode = chatPet.getAppearanceCode();
            ZombiesCatAppearance zombiesCatAppearance = chatPetAppearenceService.getZombiesCatAppearence(appearanceCode);

            Appearance appearance = new Appearance();
            appearance.setChatPetType(ChatPetTypeService.CHAT_PET_TYPE_ZOMBIES_CAT);
            appearance.setObject(zombiesCatAppearance);
            chatPetBaseInfo.setAppearance(appearance);
        }


        //如果是以太猫
        if(ChatPetTypeService.CHAT_PET_TYPE_CRYPTO_KITTIES.intValue() == chatPetType.intValue()){
            Appearance appearance = new Appearance();
            appearance.setChatPetType(ChatPetTypeService.CHAT_PET_TYPE_CRYPTO_KITTIES);

            CryptoKitties cryptoKitties = cryptoKittiesService.getKittyByOwner(wxPubOriginId, wxFanOpenId);
            String appearanceUrl = cryptoKitties.getUrl();
            appearance.setObject(appearanceUrl);

            chatPetBaseInfo.setAppearance(appearance);
        }

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
        chatPetRewardService.createInitRewardItems(chatPet.getId());
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
        changeInfo.setTodayMission(info.getTodayMission());
        //族群排名
        ChatPetExperinceRank chatPetExperinceRankByWxFan = this.getChatPetExperinceRankByWxFan(wxFanId, 1);
        changeInfo.setGroupRank(chatPetExperinceRankByWxFan);

        List<ChatPetGoldItem> chatPetGoldItems = chatPetRewardService.getChatPetGoldItems(chatPetId);
        changeInfo.setGoldItems(chatPetGoldItems);

        return changeInfo;

    }

    /**
     * 领取奖励处理
     * @param wxFanId        粉丝id
     * @param rewardItemId  领取奖励记录id
     * @return
     * @throws BizException
     */
    public ChatPetRewardChangeInfo rewardHandle(Integer wxFanId,Integer rewardItemId) throws BizException{
        try {
            ChatPet chatPet = this.getChatPetByWxFanId(wxFanId);//粉丝的宠物
            ChatPetRewardItem chatPetRewardItem = chatPetRewardService.getChatPetRewardItemById(rewardItemId);//奖励对象

            if(chatPet == null || chatPetRewardItem == null){
                throw new BizException("无法领取");
            }

            //判断领取的奖励是否为该粉丝的奖励,通过对比宠物id
            Integer fansChatPetId = chatPet.getId();
            Integer rewardChatPetId = chatPetRewardItem.getChatPetId();
            if(!fansChatPetId.equals(rewardChatPetId)){
                throw new BizException("无法领取");
            }

            if(ChatPetRewardService.HAVE_AWARD.equals(chatPetRewardItem.getRewardState())){
                throw new BizException("您已领取过奖励");
            }

            Integer chatPetPersonalMissionId = chatPetRewardItem.getMissionItemId();

            //任务类型奖励判断
            if(chatPetPersonalMissionId != null){
                ChatPetPersonalMission chatPetPersonalMission = chatPetMissionPoolService.getById(chatPetPersonalMissionId);
                //任务状态,判断任务状态是否为完成可领取奖励状态
                Integer state = chatPetPersonalMission.getState();

                if(MissionStateEnum.GOING_ON.equals(state)){
                    throw new BizException("请完成任务后再来领取");
                }

                if(MissionStateEnum.FINISH_AND_AWARD.equals(state)){
                    throw new BizException("您已领取过奖励");
                }
            }

            //领取奖励
            chatPetRewardService.reward(rewardItemId);

            Integer chatPetId = chatPet.getId();
            ChatPetRewardChangeInfo info = this.getInfoAfterReward(wxFanId,chatPetId);
            return info;
        }catch(Exception e){
            Log.e(e);
        }

        return null;


    }



    /**
     * 领取奖励后相关操作
     * @param chatPetRewardItemId 领取奖励对象id
     * @throws BizException
     *//*
    @Transactional
    public void reward(Integer chatPetRewardItemId) throws BizException{

        ChatPetRewardItem chatPetRewardItem = chatPetRewardItemService.getChatPetRewardItemById(chatPetRewardItemId);

        Integer missionItemId = chatPetRewardItem.getMissionItemId();

        //是否为任务类型奖励
        Boolean isMissionReward = false;

        //根据missionItemId == null 判断该奖励是否为完成任务后的奖励. 区分每日可领取奖励
        if(missionItemId != null){
            isMissionReward = true;
        }

        //增加金币
        Integer chatPetId = chatPetRewardItem.getChatPetId();
        Float incrCoin = chatPetRewardItem.getGoldValue();
        this.increaseCoin(chatPetId,incrCoin);

        //增加经验
        //Float oldExperience = null;
        //Float newExperience = null;

        Boolean isUpgrade = false;
        //如果是从任务来的奖励
        if(isMissionReward){

            ChatPet chatPet = this.getById(chatPetId);
            Float oldExperience = chatPet.getExperience();

            this.increaseExperience(chatPetId,incrCoin);

            Float newExperience = this.getChatPetExperience(chatPetId);

            isUpgrade = chatPetLevelService.isUpgrade(oldExperience, newExperience);

            //更新状态
            chatPetMissionPoolService.updateMissionWhenReward(missionItemId);

            ChatPetPersonalMission chatPetPersonalMission = chatPetMissionPoolService.getById(missionItemId);
            if(chatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(chatPetPersonalMission.getMissionCode())){
                chatPetMissionPoolService.dispatchMission(chatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE,chatPetId);
            }
        }

        //插入日志
        if(isMissionReward){
            chatPetLogService.savePetLog4MissionReward(missionItemId,isUpgrade);
        }else{
            //chatPetLogService.saveDailyFixedCoinLog(chatPetRewardItemId);
        }
    }*/

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
            throw new BizException("未能成功获取wxOauthAccessToken!");
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

        Boolean ret = isUserFollowWxPub(wxPubAppId,wxFanOpenId) && isFanOwnChatPet(wxPubAppId,wxFanOpenId);

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
        String url = "https://" + domain + "/static/chat-pet/#/?id=" + wxPubId;
        return url;
    }

    public String getHomePageUrl(Integer wxPubId){
        String domain = cfgService.get(GlobalConfigConstants.CHAT_PET_WEB_DOMAIN_KEY);
        String url = "https://" + domain + "/api/chat-pet/pet/home-page?id=" + wxPubId;
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

        String domain = cfgService.get(GlobalConfigConstants.CHAT_PET_WEB_DOMAIN_KEY);
        if(ChatPetTypeService.CHAT_PET_TYPE_ZOMBIES_CAT.equals(chatPetType)){
            return "https://" + domain + "/static/chat-pet/#/?id=" + wxPubId;
        }

        if(ChatPetTypeService.CHAT_PET_TYPE_CRYPTO_KITTIES.equals(chatPetType)){
            return "https://" + domain + "/static/chat-pet/#/?id=" + wxPubId;
        }

        return null;
    }

    public String getWxOauthUrl(Integer wxPubId){
        String domain = cfgService.get(GlobalConfigConstants.CHAT_PET_WEB_DOMAIN_KEY);
        String url = "https://" + domain + "/api/wx/oauth/redirect?id=" + wxPubId;
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
    public void increaseExperience(Integer chatPetId ,Float experience){
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
        String picUrl = "https://" + domain + "/res/wedo/images/kitties_normal_cover.jpg";

        return picUrl;
    }

    private Integer countSecondEthnicGroupsById(Integer secondEthnicGroupsId){
        return chatPetMapper.countSecondEthnicGroupsById(secondEthnicGroupsId);
    }

    /**
     * 获取创世海报url
     * https://test.keendo.com.cn/static/chat-pet/#/poster
     * @return
     */
    public String getChatPetPosterUrl(){
        String domain = cfgService.get(GlobalConfigConstants.CHAT_PET_WEB_DOMAIN_KEY);
        String posterUrl = "https://" + domain + "/static/chat-pet/#/poster";

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

    /**
     * 根据宠物id获取金币名称
     * @param chatPetId
     * @return
     */
    public String getChatPetCoinName(Integer chatPetId){

        ChatPet chatPet = this.getById(chatPetId);

        String wxPubOriginId = chatPet.getWxPubOriginId();
        Integer chatPetType = rWxPubChatPetTypeService.getChatPetType(wxPubOriginId);

        ChatPetTypeConfig chatPetTypeConfig = chatPetTypeConfigService.getChatPetTypeConfig(chatPetType);

        return chatPetTypeConfig.getCoinName();

    }


    /**
     * 获取宠物的图文卡片
     * @param chatPetId
     * @return
     */
    public CustomerNewsItem getChatNewsItem(Integer chatPetId){

        ChatPet chatPet = this.getById(chatPetId);

        String wxPubOriginId = chatPet.getWxPubOriginId();
        Integer chatPetType = rWxPubChatPetTypeService.getChatPetType(wxPubOriginId);

        ChatPetTypeConfig chatPetTypeConfig = chatPetTypeConfigService.getChatPetTypeConfig(chatPetType);

        CustomerNewsItem customerNewsItem = new CustomerNewsItem();

        String description = chatPetTypeConfig.getNewsDescription();

        Integer chatPetParentId = chatPet.getParentId();

        //替换邀请人
        if(chatPetParentId == null){
            String founderName = chatPetTypeConfig.getFounderName();
            description = description.replace("#{parentName}", founderName);
        }else {
            ChatPet parentChatPet = this.getById(chatPetParentId);
            String parentOpenId = parentChatPet.getWxFanOpenId();
            WxFan parentOwner = wxFanService.getWxFan(wxPubOriginId, parentOpenId);

            String parentWxFanNickname = parentOwner.getNickname();
            description = description.replace("#{parentName}", parentWxFanNickname);
        }

        //替换粉丝名称
        String wxFanOpenId = chatPet.getWxFanOpenId();
        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
        String wxFanNickname = wxFan.getNickname();

        if(wxFanNickname == null){
            wxFanService.reviseWxPub(wxPubOriginId,wxFanOpenId);
            wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
            wxFanNickname = wxFan.getNickname();
        }
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
        customerNewsItem.setPicUrl("https://" + domain + coverUrl);

        Integer wxPubId = wxPubService.getByOrginId(wxPubOriginId).getId();
        String url = this.getHomePageUrl(wxPubId);
        customerNewsItem.setUrl(url);

        return customerNewsItem;
    }


    /**
     * 获取宠物类型
     * @param chatPetId
     * @return
     */
    public Integer getChatPetType(Integer chatPetId){
        ChatPet chatPet = this.getById(chatPetId);
        String wxPubOriginId = chatPet.getWxPubOriginId();

        Integer chatPetType = rWxPubChatPetTypeService.getChatPetType(wxPubOriginId);

        return chatPetType;
    }
}
