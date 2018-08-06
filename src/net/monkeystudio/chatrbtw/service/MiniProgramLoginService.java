package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.ChatPetLoginLog;
import net.monkeystudio.chatrbtw.entity.ChatPetPersonalMission;
import net.monkeystudio.chatrbtw.entity.WxFan;
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


    /**
     * key: token
     * value:   miniprogramId:openId:sessionKey
     * 登陆处理
     * @param miniProgramId : 小程序id
     * @param jsCode
     * @return
     */
    @Transactional
    public String loginHandle(Integer miniProgramId,String jsCode) throws BizException{
        if(miniProgramId == null){
            miniProgramId = 1;
        }
        LoginVerifyInfo loginVerifyInfo = wxMiniProgramHelper.fetchLoginVerifyInfo(miniProgramId,jsCode);

        String openId = loginVerifyInfo.getOpneId();
        Log.i("========== mini login openid = {?} ============",openId);

        String sessionKey = loginVerifyInfo.getSessionKey();
        Log.i("=========== mini login sessionKey = {?}===========",sessionKey);

        String token = CommonUtils.randomUUID();
        Log.i("=========== mini login generate token = {?}===========",token);

        sessionTokenService.saveToken(token,miniProgramId,openId,sessionKey);

        //非注册登录
        WxFan wxFan = wxFanService.getWxFan(openId, miniProgramId);

        if(wxFan != null){
            ChatPet chatPet = chatPetService.getByWxFanId(wxFan.getId());

            if(chatPet != null){
                this.dailyFirstLoginHandle(chatPet.getId());

                //登录记录
                ChatPetLoginLog chatPetLoginLog = new ChatPetLoginLog();
                chatPetLoginLog.setLoginTime(new Date());
                chatPetLoginLog.setWxFanId(wxFan.getId());
                chatPetLoginLogService.save(chatPetLoginLog);
            }
        }

        return token;
    }


    /**
     * 宠物每天第一次登录处理
     * @param chatPetId
     */
    public void dailyFirstLoginHandle(Integer chatPetId) throws BizException{

        String cacheKey = this.getFanDailyLoginCountCacheKey(chatPetId);

        Long loginCount = redisCacheTemplate.incr(cacheKey);

        if(loginCount.intValue() == 1){

            redisCacheTemplate.expire(cacheKey, DateUtils.getCacheSeconds());
            //派发小游戏点击任务
            List<Integer> wxMiniGameIds = wxMiniGameService.getWxMiniGameIds();

            for (Integer id:wxMiniGameIds){

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
     * 获取小程序用户每天登录次数缓存key
     * @param chatPetId
     * @return
     */
    private String getFanDailyLoginCountCacheKey(Integer chatPetId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "miniAppFanDailyLoginCount:" + chatPetId;
    }


}
