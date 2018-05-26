package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.enums.mission.RewardMethodEnum;
import net.monkeystudio.chatrbtw.mapper.PetLogMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.PetLogResp;
import net.monkeystudio.chatrbtw.utils.ChatPetMissionNoUtil;
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


    public void savePetLog4MissionReward(Integer chatPetRewardItemId,Boolean isUpgrade){
        ChatPetRewardItem item = chatPetRewardService.getChatPetRewardItemById(chatPetRewardItemId);
        Integer chatPetId = item.getChatPetId();
        Integer chatPetPersonalMissionId = item.getMissionItemId();

        Float goldValue = item.getGoldValue();
        Float experience = item.getExperience();

        StringBuilder missisonNameSb = new StringBuilder();

        missisonNameSb.append(this.getPetLogMissionNameByChatPetPersonalMissionId(chatPetPersonalMissionId));
        missisonNameSb.append("获取POP币+" + goldValue + ",");
        missisonNameSb.append("经验值+" + experience);

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
        Date createTime = chatPetPersonalMission.getCreateTime();//任务广告派发时间
        String missionName = chatPetMissionEnumService.getMissionByCode(missionCode).getMissionName();

        if(ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){
            sb.append("完成NO." + this.getRandomNum(createTime) + missionName);
        }

<<<<<<< HEAD
        if(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){
            sb.append("邀请好友加入族群");
=======
        if(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode) || ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){
            sb.append("NO." + ChatPetMissionNoUtil.getMissionNo(createTime));
>>>>>>> tttttttest
        }

        if(ChatPetMissionEnumService.DAILY_CHAT_MISSION_CODE.equals(missionCode)){
            sb.append("完成" + missionName);
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
    private String getPetLogContentByPersonalMission(Integer chatPetPersonalMissionId){
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
    }

    //时间戳后四位,作为任务编码
    private String getRandomNum(Date date){
        Long time = date.getTime();
        String timeStr = time.toString();
        int startIndex = timeStr.length() - 4;
        String ret = timeStr.substring(startIndex);
        return ret;
    }



    /**
     * 保存宠物日志
     * @param petLog
     */
    public void savePetLog(PetLog petLog){
        petLogMapper.insert(petLog);
    }

    //每日可领取奖励日志
    public void saveLevelRewardLog(Integer chatPetRewardItemId){
        ChatPetRewardItem item = chatPetRewardService.getChatPetRewardItemById(chatPetRewardItemId);
        Integer chatPetId = item.getChatPetId();

        PetLog pl = new PetLog();

        pl.setChatPetId(chatPetId);
        pl.setCreateTime(new Date());
        pl.setContent("领取等级奖励,获得POP币+" + item.getGoldValue());

        this.savePetLog(pl);
    }

    public void savePetBornLog(String wxPubOpenId,String wxFanOpenId,Integer chatPetId){
        PetLog pl = new PetLog();

        pl.setWxPubOriginId(wxPubOpenId);
        pl.setWxFanOpenId(wxFanOpenId);
        pl.setContent("我出生啦!!");
        pl.setCreateTime(new Date());
        pl.setChatPetId(chatPetId);

        this.savePetLog(pl);

    }


    /**
     * 领取奖励产生日志处理
     * @param chatPetPersonalMissionId
     * @param isUpgrade
     *//*
    public void savePetLogWhenReward(Integer chatPetPersonalMissionId , Boolean isUpgrade){

        ChatPetPersonalMission chatPetPersonalMission = chatPetMissionPoolService.getById(chatPetPersonalMissionId);
        Integer chatPetId = chatPetPersonalMission.getId();

        ChatPet chatPet = chatPetService.getById(chatPetId);
        String wxPubOriginId = chatPet.getWxPubOriginId();
        String wxFanOpenId = chatPet.getWxFanOpenId();


        Integer missionCode = chatPetPersonalMission.getMissionCode();
        ChatPetMission cpm = chatPetMissionService.getByMissionCode(missionCode);

        Float coin = cpm.getCoin();
        Float experience = cpm.getExperience();

        List<PetLog> pls = new ArrayList<>();

        if(coin != 0F){
            PetLog pl1 = new PetLog();

            pl1.setWxPubOriginId(wxPubOriginId);
            pl1.setWxFanOpenId(wxFanOpenId);
            pl1.setContent("完成"+cpm.getMissionName());
            pl1.setCreateTime(new Date());
            pl1.setChatPetId(chatPetId);
            pl1.setRewardType(RewardMethodEnum.GOLD_REWARD.getType());
            pl1.setTaskCode(missionCode);

            pls.add(pl1);

            if(experience != 0F){
                PetLog pl2 = new PetLog();

                pl2.setWxPubOriginId(wxPubOriginId);
                pl2.setWxFanOpenId(wxFanOpenId);
                pl2.setContent("经验值");
                pl2.setCreateTime(new Date());
                pl2.setChatPetId(chatPetId);
                pl2.setRewardType(RewardMethodEnum.EXPERIENCE_REWARD.getType());
                pl2.setTaskCode(missionCode);

                pls.add(pl2);
                //是否升级
                if(isUpgrade){

                    //升级后的等级
                    Integer level = chatPetLevelService.calculateLevel(chatPet.getExperience());

                    PetLog pl3 = new PetLog();

                    pl3.setWxPubOriginId(wxPubOriginId);
                    pl3.setWxFanOpenId(wxFanOpenId);
                    pl3.setContent("恭喜你,升级到等级lv."+level+"啦");
                    pl3.setCreateTime(new Date());
                    pl3.setChatPetId(chatPetId);

                    pls.add(pl3);
                }
            }
            this.petLogMapper.batchInsert(pls);


        }else{
            //没有金币奖励一定有经验值奖励   "完成...任务,奖励经验..."
        }
    }*/



}
