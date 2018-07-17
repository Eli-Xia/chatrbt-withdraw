package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.ArithmeticUtils;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.chatrbtw.annotation.chatpet.ChatPetType;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.mapper.PetLogMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.PetLogResp;
import net.monkeystudio.chatrbtw.service.bean.chatpetlog.SaveChatPetLogParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaxin
 */
@Service
public class ChatPetLogService {
    @Autowired
    private PetLogMapper petLogMapper;

    @Autowired
    private ChatPetRewardService chatPetRewardService;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private ChatPetLevelService chatPetLevelService;

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private ChatPetMissionEnumService chatPetMissionEnumService;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private ChatPetTypeConfigService chatPetTypeConfigService;


    /**
     * 获取每日宠物日志
     * @param date
     * @return
     */
    /*public List<PetLogResp> getDailyPetLogList(Integer chatPetId, Date date){
        Date beginDate = DateUtils.getBeginDate(date);
        Date endDate = DateUtils.getEndDate(date);

        List<PetLog> pls = petLogMapper.selectDailyPetLog(chatPetId,beginDate,endDate);

        List<PetLogResp> resps = new ArrayList<>();

        for(PetLog petLog: pls){
            Integer rewardType = petLog.getRewardType();
            Integer chatPetRewardItemId = petLog.getRewardItemId();
            ChatPetRewardItem chatPetRewardItem = chatPetRewardService.getChatPetRewardItemById(chatPetRewardItemId);

            PetLogResp petLogResp = new PetLogResp();

            petLogResp.setRewardType(rewardType);
            petLogResp.setContent(petLog.getContent());
            petLogResp.setCreateTime(petLog.getCreateTime());

            if(RewardMethodEnum.GOLD_REWARD.getType().equals(rewardType)){
                petLogResp.setCoin(chatPetRewardItem.getGoldValue());
            }

            if(RewardMethodEnum.EXPERIENCE_REWARD.getType().equals(rewardType)){
                petLogResp.setCoin(chatPetRewardItem.getExperience());
            }

            if(RewardMethodEnum.NULL_REWARD.getType().equals(rewardType)){
                petLogResp.setCoin(null);
            }

            resps.add(petLogResp);
        }

        return resps;
    }*/

    /**
     * 获取今日宠物日志
     * @param chatPetId 宠物id
     * @return
     */
    public List<PetLogResp> getDailyPetLogList(Integer chatPetId){
        Date date = new Date();
        Date beginDate = DateUtils.getBeginDate(date);
        Date endDate = DateUtils.getEndDate(date);

        List<PetLog> pls = petLogMapper.selectDailyPetLog(chatPetId,beginDate,endDate);

        List<PetLogResp> resps = new ArrayList<>();

        for(PetLog petLog:pls){
            PetLogResp petLogResp = new PetLogResp();
            petLogResp.setCreateTime(petLog.getCreateTime());
            petLogResp.setContent(petLog.getContent());

            resps.add(petLogResp);
        }

        return resps;
    }


    /*public void savePetLog4MissionReward(Integer chatPetRewardItemId,Boolean isUpgrade){
        ChatPetRewardItem item = chatPetRewardService.getChatPetRewardItemById(chatPetRewardItemId);
        Integer chatPetId = item.getChatPetId();
        Integer chatPetPersonalMissionId = item.getMissionItemId();

        Float goldValue = item.getGoldValue();
        Float experience = item.getExperience();

        //金币名称
        String chatPetCoinName = chatPetService.getChatPetCoinName(chatPetId);

        StringBuilder missisonNameSb = new StringBuilder();

        missisonNameSb.append(this.getPetLogMissionNameByChatPetPersonalMissionId(chatPetPersonalMissionId));
        missisonNameSb.append(chatPetCoinName + "+" + goldValue + ",");
        missisonNameSb.append("算力" + "+" + experience);

        PetLog petLog1 = new PetLog();
        petLog1.setContent(missisonNameSb.toString());
        petLog1.setChatPetId(chatPetId);
        petLog1.setCreateTime(new Date());
        this.savePetLog(petLog1);

        if(isUpgrade){
            Integer level = chatPetLevelService.calculateLevel(experience);

            PetLog petLog2 = new PetLog();
            petLog2.setCreateTime(new Date());
            petLog2.setChatPetId(chatPetId);
            petLog2.setContent("恭喜你,升级到lv." + level + "啦");
            this.savePetLog(petLog2);
        }

    }*/

    /**
     * 保存宠物动态
     */
    public void saveChatPetDynamic(SaveChatPetLogParam param){
        Integer chatPetLogType = param.getChatPetLogType();
        if(ChatPetLogTypeService.CHAT_PET_LOG_TYPE_BORN.equals(chatPetLogType)){
            Integer chatPetId = param.getChatPetId();
            this.savePetBornLog(chatPetId);
        }

        if(ChatPetLogTypeService.CHAT_PET_LOG_TYPE_LEVEL_REWARD.equals(chatPetLogType)){
            Integer chatPetRewardItemId = param.getChatPetRewardItemId();
            this.saveLevelRewardLog(chatPetRewardItemId);
        }

        if(ChatPetLogTypeService.CHAT_PET_LOG_TYPE_JOIN_AUTION.equals(chatPetLogType)){
            Integer chatPetId = param.getChatPetId();
            this.saveJoinAuctionLog(chatPetId);
        }

        if(ChatPetLogTypeService.CHAT_PET_LOG_TYPE_AUTION_SUCCESS.equals(chatPetLogType)){
            Integer chatPetId = param.getChatPetId();
            this.saveAuctionSuccessLog(chatPetId);
        }

        if(ChatPetLogTypeService.CHAT_PET_LOG_TYPE_MISSION_REWARD.equals(chatPetLogType)){
            Integer chatPetRewardItemId = param.getChatPetRewardItemId();
            Integer chatPetId = param.getChatPetId();

            ChatPet chatPet = chatPetService.getById(chatPetId);
            Integer chatPetType = chatPet.getChatPetType();

            if(ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT.equals(chatPetType)){
                this.saveLuckyCatMissionLog(chatPetRewardItemId,param.getUpgrade());
            }/*else{
                this.savePetLog4MissionReward(chatPetRewardItemId,param.getUpgrade());
            }*/
        }
    }



    /**
     * 保存招财猫任务日志
     * @param chatPetRewardItemId
     * @param isUpgrade
     */
    public void saveLuckyCatMissionLog(Integer chatPetRewardItemId,Boolean isUpgrade){

        ChatPetRewardItem item = chatPetRewardService.getChatPetRewardItemById(chatPetRewardItemId);
        Integer chatPetId = item.getChatPetId();
        Integer chatPetPersonalMissionId = item.getMissionItemId();
        ChatPetPersonalMission chatPetPersonalMission = chatPetMissionPoolService.getById(chatPetPersonalMissionId);
        Integer missionCode = chatPetPersonalMission.getMissionCode();
        Float experience = item.getExperience();
        String displayExpStr = ArithmeticUtils.keep2DecimalPlace(experience);

        //经验值日志
        PetLog addExperienceLog = new PetLog();
        addExperienceLog.setChatPetId(chatPetId);

        if(ChatPetMissionEnumService.DAILY_CHAT_MISSION_CODE.equals(missionCode)){
            addExperienceLog.setContent("完成公众号打招呼,经验值+" + displayExpStr);
            addExperienceLog.setCreateTime(new Date());
            this.savePetLog(addExperienceLog);
        }

        if(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){
            addExperienceLog.setContent("赠送一只猫六六,经验值+" + displayExpStr);
            addExperienceLog.setCreateTime(new Date());
            this.savePetLog(addExperienceLog);
        }

        if(ChatPetMissionEnumService.DAILY_PLAY_MINI_GAME_CODE.equals(missionCode)){
            addExperienceLog.setContent("体验游戏,经验值+" + displayExpStr);
            addExperienceLog.setCreateTime(new Date());
            this.savePetLog(addExperienceLog);
        }

        if(ChatPetMissionEnumService.DAILY_LOGIN_MINI_PROGRAM_CODE.equals(missionCode)){
            addExperienceLog.setContent("每日登录,经验值+" + displayExpStr);
            addExperienceLog.setCreateTime(new Date());
            this.savePetLog(addExperienceLog);
        }

        //升级日志
        if(isUpgrade!=null && isUpgrade){

            ChatPet chatPet = chatPetService.getById(chatPetId);
            Integer level = chatPetLevelService.calculateLevel(chatPet.getExperience());

            PetLog upgradeLog = new PetLog();
            upgradeLog.setChatPetId(chatPetId);
            upgradeLog.setContent("恭喜你,升级到lv." + level + "啦");
            upgradeLog.setCreateTime(new Date());
            this.savePetLog(upgradeLog);
        }

    }

    /**
     * 参加竞标日志
     */
    private void saveJoinAuctionLog(Integer chatPetId){
        PetLog joinAuctionLog = new PetLog();

        joinAuctionLog.setContent("参与竞拍活动");
        joinAuctionLog.setCreateTime(new Date());
        joinAuctionLog.setChatPetId(chatPetId);

        this.savePetLog(joinAuctionLog);
    }

    /**
     * 竞标成功日志
     */
    public void saveAuctionSuccessLog(Integer chatPetId){
        PetLog auctionSuccessLog= new PetLog();

        auctionSuccessLog.setContent("恭喜你中标啦");
        auctionSuccessLog.setCreateTime(new Date());
        auctionSuccessLog.setChatPetId(chatPetId);

        this.savePetLog(auctionSuccessLog);
    }

    private void savePetLog4MissionReward(Integer chatPetRewardItemId,Boolean isUpgrade){
        ChatPetRewardItem item = chatPetRewardService.getChatPetRewardItemById(chatPetRewardItemId);
        Integer chatPetId = item.getChatPetId();
        Integer chatPetPersonalMissionId = item.getMissionItemId();

        Float goldValue = item.getGoldValue();
        Float experience = item.getExperience();

        //金币名称
        String chatPetCoinName = "猫饼";

        StringBuilder missisonNameSb = new StringBuilder();

        missisonNameSb.append(this.getPetLogMissionNameByChatPetPersonalMissionId(chatPetPersonalMissionId));
        missisonNameSb.append(chatPetCoinName + "+" + goldValue + ",");
        missisonNameSb.append("算力" + "+" + experience);

        PetLog petLog1 = new PetLog();
        petLog1.setContent(missisonNameSb.toString());
        petLog1.setChatPetId(chatPetId);
        petLog1.setCreateTime(new Date());
        this.savePetLog(petLog1);

        if(isUpgrade){
            Integer level = chatPetLevelService.calculateLevel(experience);

            PetLog petLog2 = new PetLog();
            petLog2.setCreateTime(new Date());
            petLog2.setChatPetId(chatPetId);
            petLog2.setContent("恭喜你,升级到lv." + level + "啦");
            this.savePetLog(petLog2);
        }

    }

    /**
     * 根据任务记录id获取记录在宠物日志中的任务完成信息
     * @param chatPetPersonalMissionId
     * @return
     */
    private String getPetLogMissionNameByChatPetPersonalMissionId(Integer chatPetPersonalMissionId){
        StringBuilder sb = new StringBuilder();

        ChatPetPersonalMission chatPetPersonalMission = chatPetMissionPoolService.getById(chatPetPersonalMissionId);
        Integer missionCode = chatPetPersonalMission.getMissionCode();
        String missionName = chatPetMissionEnumService.getMissionByCode(missionCode).getMissionName();
        //Date createTime = chatPetPersonalMission.getCreateTime();//任务广告派发时间

//        if(ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){
//            sb.append("完成" + ChatPetMissionNoUtil.getMissionNo(createTime) + missionName);
//        }

        if(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){
            Integer inviteeWxFanId = chatPetPersonalMission.getInviteeWxFanId();
            WxFan wxFan = wxFanService.getById(inviteeWxFanId);
            sb.append("邀请" + wxFan.getNickname() + "加入");
        }

        if(ChatPetMissionEnumService.DAILY_CHAT_MISSION_CODE.equals(missionCode) || ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){
            sb.append(missionName);
        }

        sb.append(",");

        return sb.toString();
    }

    /**
     * 保存任务类型奖励日志
     * @param chatPetRewardItemId 领取奖励对象id
     * @param isUpgrade             是否升级
     */
    /*public void savePetLog4MissionReward(Integer chatPetRewardItemId,Boolean isUpgrade){
        ChatPetRewardItem item = chatPetRewardService.getChatPetRewardItemById(chatPetRewardItemId);
        Integer chatPetId = item.getChatPetId();
        Integer chatPetPersonalMissionId = item.getMissionItemId();

        ChatPetPersonalMission chatPetPersonalMission = chatPetMissionPoolService.getById(chatPetPersonalMissionId);
        Integer missionCode = chatPetPersonalMission.getMissionCode();

        PetLog goldPetLog = new PetLog();
        goldPetLog.setCreateTime(new Date());
        goldPetLog.setChatPetId(chatPetId);
        goldPetLog.setTaskCode(missionCode);
        goldPetLog.setRewardType(RewardMethodEnum.GOLD_REWARD.getType());
        goldPetLog.setRewardItemId(chatPetRewardItemId);
        goldPetLog.setContent(getPetLogContentByPersonalMission(chatPetPersonalMissionId));
        this.savePetLog(goldPetLog);

        PetLog experiencePetLog = new PetLog();
        experiencePetLog.setCreateTime(new Date());
        experiencePetLog.setChatPetId(chatPetId);
        experiencePetLog.setTaskCode(missionCode);
        experiencePetLog.setRewardType(RewardMethodEnum.EXPERIENCE_REWARD.getType());
        experiencePetLog.setRewardItemId(chatPetRewardItemId);
        experiencePetLog.setContent("增加经验值");
        this.savePetLog(experiencePetLog);

        if(isUpgrade){
            ChatPet chatPet = chatPetService.getById(chatPetId);
            Float experience = chatPet.getExperience();
            Integer level = chatPetLevelService.calculateLevel(experience);

            PetLog upgradelog = new PetLog();
            upgradelog.setCreateTime(new Date());
            upgradelog.setChatPetId(chatPetId);
            upgradelog.setTaskCode(missionCode);
            upgradelog.setRewardType(RewardMethodEnum.NULL_REWARD.getType());
            upgradelog.setRewardItemId(chatPetRewardItemId);
            upgradelog.setContent("恭喜你,升级到等级lv." + level + "啦");
            this.savePetLog(upgradelog);
        }

    }*/


    /**
     * 根据宠物任务派发记录id获取宠物日志内容
     * @return
     */
    /*private String getPetLogContentByPersonalMission(Integer chatPetPersonalMissionId){
        ChatPetPersonalMission chatPetPersonalMission = chatPetMissionPoolService.getById(chatPetPersonalMissionId);
        Date createTime = chatPetPersonalMission.getCreateTime();//派发时间
        Integer missionCode = chatPetPersonalMission.getMissionCode();//任务类型

        String content = "";

        //咨询获取任务
        if(chatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){
            content = "完成NO." + getRandomNum(createTime) + chatPetMissionEnumService.getMissionByCode(missionCode).getMissionName();
        }

        //每日打招呼任务
        if(chatPetMissionEnumService.DAILY_CHAT_MISSION_CODE.equals(missionCode)){
            content = "完成" + chatPetMissionEnumService.getMissionByCode(missionCode).getMissionName();
        }

        //邀请人任务
        if(chatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){
            Integer wxFanId = chatPetPersonalMission.getInviteeWxFanId();
            WxFan wxFan = wxFanService.getById(wxFanId);

            content = "成功邀请" + wxFan.getNickname() + "加入族群";
        }

        return content;
    }*/


    /**
     * TODO
     * 保存宠物日志
     * @param petLog
     */
    public void savePetLog(PetLog petLog){
        petLogMapper.insert(petLog);
    }

    //每日可领取奖励日志
    private void saveLevelRewardLog(Integer chatPetRewardItemId){
        ChatPetRewardItem item = chatPetRewardService.getChatPetRewardItemById(chatPetRewardItemId);
        Integer chatPetId = item.getChatPetId();
        //金币名称
        String chatPetCoinName = "猫饼";
        //String chatPetCoinName = chatPetService.getChatPetCoinName(chatPetId);

        PetLog pl = new PetLog();

        pl.setChatPetId(chatPetId);
        pl.setCreateTime(new Date());
        pl.setContent("等级奖励," + "猫饼" + "+" + ArithmeticUtils.keep2DecimalPlace(item.getGoldValue()));

        //this.savePetLog(pl);
    }

    private void savePetBornLog(Integer chatPetId){
        PetLog pl = new PetLog();

        pl.setContent("我出生啦!!");
        pl.setCreateTime(new Date());
        pl.setChatPetId(chatPetId);

        this.savePetLog(pl);
    }




}
