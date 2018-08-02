package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.chatrbtw.MiniProgramChatPetService;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.ChatPetLoginLog;
import net.monkeystudio.chatrbtw.entity.ChatPetPersonalMission;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.enums.mission.MissionStateEnum;
import net.monkeystudio.chatrbtw.service.bean.miniapp.MiniProgramFanBaseInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import java.util.Date;

/**
 * @author xiaxin
 */
@Service
public class MiniProgramUserInfoService {

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private ChatPetRewardService chatPetRewardService;

    @Autowired
    private MiniProgramChatPetService miniProgramChatPetService;

    @Autowired
    private MiniProgramLoginService miniProgramLoginService;

    @Autowired
    private SessionTokenService sessionTokenService;

    @Autowired
    private ChatPetCoinFlowService chatPetCoinFlowService;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private ChatPetLoginLogService chatPetLoginLogService;

    /**
     * 点击分享卡注册,携带parentFanId
     * 获取用户信息及注册
     * 注册完成之后也就是当天的第一次登录
     *
     * @param iv
     * @throws Exception
     */
    @Transactional
    public void getUserInfoAndRegister(Integer parentFanId, String encryptedData, String iv) throws Exception {
        Log.i("================== encryptedData = {?} , iv = {?} =================", encryptedData, iv);
        //Map<String, Object> ret = new HashMap<>();//注册wxFan及chatPet,返回对应id

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String token = request.getHeader("token");

        if (token != null) {
            String openId = sessionTokenService.getOpenIdFromTokenVal(token);

            String sessionKey = sessionTokenService.getSessionKeyFromTokenVal(token);

            Integer miniProgramId = sessionTokenService.getMiniProgramIdFromTokenVal(token);

            Log.i("mini user info : openId = {?} , sessionKey = {?} , miniProgramId = {?}", openId, sessionKey, miniProgramId.toString());

            MiniProgramFanBaseInfo miniProgramFanBaseInfo = this.getMiniProgramFanBaseInfo(encryptedData, iv, sessionKey);

            String userInfoOpenId = miniProgramFanBaseInfo.getOpenId();

            if (userInfoOpenId.equals(openId)) {

                //通过openId判断是否存在于数据库中,如果存在update
                WxFan dbWxFan = wxFanService.getWxFanFromDb(null, userInfoOpenId, miniProgramId);

                if (dbWxFan != null) {
                    //更新老数据
                    dbWxFan.setCity(miniProgramFanBaseInfo.getCity());
                    dbWxFan.setProvince(miniProgramFanBaseInfo.getProvince());
                    dbWxFan.setCountry(miniProgramFanBaseInfo.getCountry());
                    dbWxFan.setNickname(miniProgramFanBaseInfo.getNickname());
                    dbWxFan.setHeadImgUrl(miniProgramFanBaseInfo.getHeadImgUrl());
                    dbWxFan.setSex(miniProgramFanBaseInfo.getSex());
                    dbWxFan.setUnionId(miniProgramFanBaseInfo.getUnionId());
                    dbWxFan.setCreateAt(TimeUtil.getCurrentTimestamp());
                    dbWxFan.setMiniProgramId(wxFanService.LUCK_CAT_MINI_APP_ID);
                    dbWxFan.setWxServiceType(wxFanService.WX_SERVICE_TYPE_MINI_APP);

                    wxFanService.update(dbWxFan);

                    //ret.put("wxFanId", dbWxFan.getId());
                } else {
                    //新增用户
                    WxFan wxFan = new WxFan();

                    wxFan.setWxFanOpenId(userInfoOpenId);
                    wxFan.setCity(miniProgramFanBaseInfo.getCity());
                    wxFan.setProvince(miniProgramFanBaseInfo.getProvince());
                    wxFan.setCountry(miniProgramFanBaseInfo.getCountry());
                    wxFan.setNickname(miniProgramFanBaseInfo.getNickname());
                    wxFan.setHeadImgUrl(miniProgramFanBaseInfo.getHeadImgUrl());
                    wxFan.setSex(miniProgramFanBaseInfo.getSex());
                    wxFan.setUnionId(miniProgramFanBaseInfo.getUnionId());
                    wxFan.setCreateAt(TimeUtil.getCurrentTimestamp());
                    wxFan.setMiniProgramId(wxFanService.LUCK_CAT_MINI_APP_ID);
                    wxFan.setWxServiceType(wxFanService.WX_SERVICE_TYPE_MINI_APP);

                    //当不存在于数据库时保存
                    Integer count = wxFanService.saveIfNotExist(wxFan);
                    if(count == 0){
                        return;
                    }

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
                    miniProgramLoginService.dailyFirstLoginHandle(chatPetId);

                    //登录记录
                    ChatPetLoginLog chatPetLoginLog = new ChatPetLoginLog();
                    chatPetLoginLog.setLoginTime(new Date());
                    chatPetLoginLog.setWxFanId(wxFan.getId());
                    chatPetLoginLogService.save(chatPetLoginLog);

                    //ret.put("chatPetId", chatPetId);
                    //ret.put("wxFanId", wxFanId);

                }
            }
        }
    }


    /**
     * 解密小程序用户信息
     *
     * @param encryptedData
     * @param iv
     * @param sessionKey
     * @return
     * @throws Exception
     */
    public MiniProgramFanBaseInfo getMiniProgramFanBaseInfo(String encryptedData, String iv, String sessionKey) throws Exception {
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);

        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                System.out.println(result);
                return JsonUtil.readValue(result, MiniProgramFanBaseInfo.class);
            }
        } catch (Exception e) {
            Log.e(e);
        }
        return null;
    }


}
