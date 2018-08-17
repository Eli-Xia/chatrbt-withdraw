package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.chatrbtw.MiniProgramChatPetService;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.ChatPetLoginLog;
import net.monkeystudio.chatrbtw.entity.ChatPetPersonalMission;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.enums.mission.MissionStateEnum;
import net.monkeystudio.chatrbtw.sdk.wx.WxMiniProgramHelper;
import net.monkeystudio.chatrbtw.sdk.wx.bean.miniapp.LoginVerifyInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.DispatchMissionParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author xiaxin
 */
@Service
public class MiniProgramLoginService {

    @Autowired
    private SessionTokenService sessionTokenService;

    @Autowired
    private WxMiniProgramHelper wxMiniProgramHelper;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private WxMiniGameService wxMiniGameService;

    @Autowired
    private ChatPetLoginLogService chatPetLoginLogService;

    @Autowired
    private MiniProgramChatPetService miniProgramChatPetService;

    @Autowired
    private ChatPetRewardService chatPetRewardService;

    @Autowired
    private CfgService cfgService;

    /**
     * 登陆
     *
     * @param wxFanId
     * @throws BizException
     */
    @Transactional
    public void login(Integer wxFanId) {
        ChatPet chatPet = chatPetService.getByWxFanId(wxFanId);

        if (chatPet != null) {
            this.dailyFirstLoginHandle(chatPet.getId());

            //登录记录
            ChatPetLoginLog chatPetLoginLog = new ChatPetLoginLog();
            chatPetLoginLog.setLoginTime(new Date());
            chatPetLoginLog.setWxFanId(wxFanId);
            chatPetLoginLogService.save(chatPetLoginLog);
        }
    }


    /**
     * 注册
     *
     * @param parentFanId
     * @param openId
     * @param unionId
     * @throws BizException
     */
    @Transactional
    public void register(Integer parentFanId, String openId, String unionId) throws BizException {
        //新增用户
        WxFan wxFan = new WxFan();

        wxFan.setWxFanOpenId(openId);
        wxFan.setUnionId(unionId);
        wxFan.setCreateAt(TimeUtil.getCurrentTimestamp());
        wxFan.setMiniProgramId(wxFanService.LUCK_CAT_MINI_APP_ID);
        wxFan.setWxServiceType(wxFanService.WX_SERVICE_TYPE_MINI_APP);
        wxFan.setHeadImgUrl(this.getDefaultHeadImgUrl());
        wxFan.setNickname(this.getDefaultNickname());

        //当不存在于数据库时保存
        wxFanService.saveIfNotExist(wxFan);

        //获取刚insert的wxFan的id
        Integer wxFanId = wxFan.getId();

        //生成宠物
        //如果是通过分享卡注册的宠物,父亲完成赠送猫六六任务,获得奖励
        Integer chatPetId = null;

        if (parentFanId == null) {

            chatPetId = miniProgramChatPetService.generateChatPet(wxFanId, ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT, null);

        } else {

            ChatPet parentChatPet = chatPetService.getByWxFanId(parentFanId);

            Integer parentId = parentChatPet.getId();

            chatPetId = miniProgramChatPetService.generateChatPet(wxFanId, ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT, parentId);

            //如果父亲宠物当天邀请任务未完成
            ChatPetPersonalMission chatPetPersonalMissionParam = new ChatPetPersonalMission();
            chatPetPersonalMissionParam.setState(MissionStateEnum.GOING_ON.getCode());
            chatPetPersonalMissionParam.setMissionCode(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE);
            chatPetPersonalMissionParam.setCreateTime(DateUtils.getBeginDate(new Date()));
            chatPetPersonalMissionParam.setChatPetId(parentId);

            ChatPetPersonalMission inviteMission = chatPetMissionPoolService.getPersonalMissionByParam(chatPetPersonalMissionParam);

            if (inviteMission != null) {
                //父亲宠物完成邀请任务
                chatPetMissionPoolService.completeChatPetMission(wxFanId, inviteMission.getId());
            }
        }

        //新注册用户生成0.01猫币奖励
        chatPetRewardService.generateRegisterReward(chatPetId);

        //第一次登录任务数据准备
        this.dailyFirstLoginHandle(chatPetId);

        //登录记录
        ChatPetLoginLog chatPetLoginLog = new ChatPetLoginLog();
        chatPetLoginLog.setLoginTime(new Date());
        chatPetLoginLog.setWxFanId(wxFan.getId());
        chatPetLoginLogService.save(chatPetLoginLog);
    }


    /**
     * 登陆注册入口方法
     *
     * @param parentFanId:父亲粉丝id
     * @param miniProgramId:小程序id
     * @param jsCode:前端传过来的jsCode
     * @throws BizException
     * @return:用户会话token
     */
    public String loginHandle(Integer parentFanId, Integer miniProgramId, String jsCode) throws BizException {

        if (miniProgramId == null) {
            miniProgramId = 1;
        }
        LoginVerifyInfo loginVerifyInfo = wxMiniProgramHelper.fetchLoginVerifyInfo(miniProgramId, jsCode);

        String unionId = loginVerifyInfo.getUnionId();

        String openId = loginVerifyInfo.getOpneId();

        String sessionKey = loginVerifyInfo.getSessionKey();

        String token = CommonUtils.randomUUID();

        Log.i("==> mini login : openid = {?} , session_key = {?} , token = {?} ", openId, sessionKey, token);

        sessionTokenService.saveToken(token, miniProgramId, openId, sessionKey);

        //非注册登录
        WxFan wxFan = wxFanService.getWxFan(openId, miniProgramId);

        if (wxFan == null) {
            this.register(parentFanId, openId, unionId);
        } else {
            this.login(wxFan.getId());
        }

        return token;
    }


    /**
     * 宠物每天第一次登录处理
     *
     * @param chatPetId
     */
    @Transactional
    public void dailyFirstLoginHandle(Integer chatPetId) {

        String cacheKey = this.getFanDailyLoginCountCacheKey(chatPetId);

        Long loginCount = redisCacheTemplate.incr(cacheKey);//登陆次数

        //是否为第一次派发
        if (loginCount.intValue() == 1) {

            redisCacheTemplate.expire(cacheKey, DateUtils.getCacheSeconds());
            //派发小游戏点击任务
            List<Integer> wxMiniGameIds = wxMiniGameService.getWxMiniGameIds();

            for (Integer id : wxMiniGameIds) {

                DispatchMissionParam diapatchMiniGameMissionParam = new DispatchMissionParam();
                diapatchMiniGameMissionParam.setChatPetId(chatPetId);
                diapatchMiniGameMissionParam.setMissionCode(ChatPetMissionEnumService.DAILY_PLAY_MINI_GAME_CODE);
                diapatchMiniGameMissionParam.setWxMiniGameId(id);

                chatPetMissionPoolService.dispatchMission(diapatchMiniGameMissionParam);
            }

            //派发一个登录任务
            DispatchMissionParam dispatchLoginMisionParam = new DispatchMissionParam();
            dispatchLoginMisionParam.setMissionCode(ChatPetMissionEnumService.DAILY_LOGIN_MINI_PROGRAM_CODE);
            dispatchLoginMisionParam.setChatPetId(chatPetId);
            chatPetMissionPoolService.dispatchMission(dispatchLoginMisionParam);

            //登录任务完成
            ChatPetPersonalMission loginMission = chatPetMissionPoolService.getChatPetOngoingMissionByMissionType(chatPetId, ChatPetMissionEnumService.DAILY_LOGIN_MINI_PROGRAM_CODE);
            chatPetMissionPoolService.completeChatPetMission(loginMission.getId());


            //派发邀请任务
            DispatchMissionParam dispatchInviteMissionParam = new DispatchMissionParam();
            dispatchInviteMissionParam.setMissionCode(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE);
            dispatchInviteMissionParam.setChatPetId(chatPetId);
            chatPetMissionPoolService.dispatchMission(dispatchInviteMissionParam);

        }
    }


    /**
     * 判断是否为第一次派发任务
     * 依据: (登陆次数缓存) loginCount == 1   ||  (loginCount > 1 && notDispatch)
     *
     * @param chatPetId:宠物id
     * @param loginCount:缓存登陆次数
     * @return
     */
    private Boolean isFirstDispatch(Integer chatPetId, Long loginCount) {
        Boolean isFirstDispatch = false;

        if (loginCount == 1) {
            isFirstDispatch = true;
        }

        if (!chatPetMissionPoolService.isDispatchMission(chatPetId)) {
            isFirstDispatch = true;
        }

        return isFirstDispatch;
    }

    /**
     * 获取小程序用户每天登录次数缓存key
     *
     * @param chatPetId
     * @return
     */
    private String getFanDailyLoginCountCacheKey(Integer chatPetId) {
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "miniAppFanDailyLoginCount:" + chatPetId;
    }

    /**
     * 获取猫六六用户默认昵称
     *
     * @return
     */
    private String getDefaultNickname() {
        return cfgService.get(GlobalConfigConstants.LUCKY_CAT_DEFAULT_NICKNAME_KEY);
    }

    /**
     * 获取猫六六用户默认昵称
     *
     * @return
     */
    private String getDefaultHeadImgUrl() {
        return cfgService.get(GlobalConfigConstants.LUCKY_CAT_DEFAULT_HEADIMG_URL_KEY);
    }


}