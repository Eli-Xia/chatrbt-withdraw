package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.MiniProgramChatPetService;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.sdk.wx.WxMiniProgramHelper;
import net.monkeystudio.chatrbtw.sdk.wx.bean.miniapp.LoginVerifyInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.CompleteMissionParam;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.DispatchMissionParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xiaxin
 */
@Service
public class MiniProgramLoginService {
    @Autowired
    private WxMiniProgramHelper wxMiniProgramHelper;
    @Autowired
    private RedisCacheTemplate redisCacheTemplate;
    @Autowired
    private WxFanService wxFanService;
    @Autowired
    private ChatPetService chatPetService;
    @Autowired
    private MiniProgramChatPetService miniProgramChatPetService;
    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;
    @Autowired
    private WxMiniGameService wxMiniGameService;
    @Autowired
    private ChatPetMissionEnumService chatPetMissionEnumService;
    @Autowired
    private ChatPetRewardService chatPetRewardService;
    @Autowired
    private ChatPetLogService chatPetLogService;

    //token失效时间
    private final static Integer SESSION_TOKEN_EXPIRE = 3600 * 2 ;


    /**
     * key: token
     * value:   miniprogramId:openId:sessionKey
     * 登陆处理
     * @param appSign
     * @param jsCode
     * @return
     */
    public String loginHandle(String appSign,String jsCode){
        /*if(appSign == null){

        }*/
        Log.d(" ================ miniProgram login =============");
        LoginVerifyInfo loginVerifyInfo = wxMiniProgramHelper.fetchLoginVerifyInfo(jsCode);

        String openId = loginVerifyInfo.getOpneId();

        String sessionKey = loginVerifyInfo.getSessionKey();

        String token = CommonUtils.randomUUID();

        String key = this.getSessionTokenCacheKey(token);

        String value = openId + ":" + sessionKey;

        Integer remainSecond = DateUtils.getCacheSeconds();//距离第二天凌晨多少秒

        Integer cacheSecond = SESSION_TOKEN_EXPIRE;

        redisCacheTemplate.setString(key,value);

        //如果距离第二天凌晨小于2小时,token失效时间为距离凌晨的时间,保证每天都会刷新token,用于判断用户每天第一登录.
        if(remainSecond.intValue() < cacheSecond.intValue()){
            cacheSecond = remainSecond;
        }

        redisCacheTemplate.expire(key,cacheSecond);

        //判断openid是否存在于db,不存在则insert
        /*WxFan wxFan = wxFanService.getWxFan(openId, wxFanService.LUCK_CAT_MINI_APP_ID);
        if(wxFan == null){
            Log.d("=============== 小程序登录,wxfan不存在于db,是新用户 ===============");
            WxFan miniAppFan = new WxFan();
            miniAppFan.setWxFanOpenId(openId);
            miniAppFan.setMiniProgramId(wxFanService.LUCK_CAT_MINI_APP_ID);
            miniAppFan.setWxServiceType(wxFanService.WX_SERVICE_TYPE_MINI_APP);
            wxFanService.save(miniAppFan);

            //为新用户生成招财猫,同一事务
            Log.d("========= 为新用户生成一只招财猫 ============");
            miniProgramChatPetService.generateChatPet(wxFan.getId(),ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT,null);
        }*/

        this.dailyFirstLoginHandle(openId);

        return token;
    }

    /**
     * 获取小程序用户每天登录次数缓存key
     * @param chatPetId
     * @return
     */
    private String getFanDailyLoginCountCacheKey(Integer chatPetId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "miniAppFanDailyLoginCount:" + chatPetId;
    }

    @Transactional
    public void dailyFirstLoginHandle(String fanOpenId){
        WxFan wxFan = wxFanService.getWxFan(fanOpenId, wxFanService.LUCK_CAT_MINI_APP_ID);

        if(wxFan == null){
            return;//注册流程不处理
        }

        ChatPet chatPet = chatPetService.getByWxFanId(wxFan.getId());
        if(chatPet == null){
            return;//没有宠物不处理
        }

        Integer chatPetId = chatPet.getId();

        Log.d(" ============ 宠物当天首次登录处理 =============");
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
            CompleteMissionParam completeLoginMissionParam = new CompleteMissionParam();
            completeLoginMissionParam.setMissionCode(ChatPetMissionEnumService.DAILY_LOGIN_MINI_PROGRAM_CODE);
            completeLoginMissionParam.setChatPetId(chatPetId);
            chatPetMissionPoolService.completeChatPetMission(completeLoginMissionParam);

        }
    }

    /**
     * 获取redis sesion的key值
     * @param token
     * @return
     */
    public String getSessionTokenCacheKey(String token){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "miniAppSessionToken:" + token;
    }


}
