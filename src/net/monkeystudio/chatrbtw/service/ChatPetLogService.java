package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.enums.mission.RewardMethodEnum;
import net.monkeystudio.chatrbtw.enums.mission.RewardTypeEnum;
import net.monkeystudio.chatrbtw.mapper.PetLogMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.PetLogResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ChatPetRewardItemService chatPetRewardItemService;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private ChatPetMissionService chatPetMissionService;

    @Autowired
    private ChatPetLevelService chatPetLevelService;

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private MissionEnumService missionEnumService;
    /**
     * 获取每日宠物日志
     * @param date
     * @return
     */
    public List<PetLogResp> getDailyPetLogList(Integer chatPetId, Date date){
        Date beginDate = DateUtils.getBeginDate(date);
        Date endDate = DateUtils.getEndDate(date);

        List<PetLog> pls = petLogMapper.selectDailyPetLog(chatPetId,beginDate,endDate);

        List<PetLogResp> resps = new ArrayList<>();

        for(PetLog petLog : pls){
            Integer rewardType = petLog.getRewardType();
            Integer chatPetRewardItemId = petLog.getRewardItemId();
            ChatPetRewardItem chatPetRewardItem = chatPetRewardItemService.getChatPetRewardItemById(chatPetRewardItemId);

            PetLogResp petLogResp = new PetLogResp();

            petLogResp.setRewardType(rewardType);
            petLogResp.setContent(petLog.getContent());
            petLogResp.setCreateTime(petLog.getCreateTime());

            if(RewardMethodEnum.GOLD_REWARD.equals(rewardType)){
                petLogResp.setCoin(chatPetRewardItem.getGoldValue());
            }

            if(RewardMethodEnum.EXPERIENCE_REWARD.equals(rewardType)){
                petLogResp.setCoin(chatPetRewardItem.getExperience());
            }

            if(RewardMethodEnum.NULL_REWARD.equals(rewardType)){
                petLogResp.setCoin(null);
            }

            resps.add(petLogResp);
        }

        return resps;
    }

    public void savePetLog4MissionReward(Integer chatPetRewardItemId,Boolean isUpgrade){
        ChatPetRewardItem item = chatPetRewardItemService.getChatPetRewardItemById(chatPetRewardItemId);
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
        this.savePetLog(goldPetLog);

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

    }

    private void savePetLog(Date createTime,Integer chatPetId,Integer missionCode,Integer rewardType,String content){
        PetLog petLog = new PetLog();
        petLog.setCreateTime(new Date());
        petLog.setChatPetId(chatPetId);
        petLog.setTaskCode(missionCode);
        petLog.setRewardType(rewardType);
        petLog.setContent(content);
        this.savePetLog(petLog);
    }

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
        if(MissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){
            content = "完成NO." + getRandomNum(createTime) + missionEnumService.getMissionByCode(missionCode).getMissionName();
        }

        //每日打招呼任务
        if(MissionEnumService.DAILY_CHAT_MISSION_CODE.equals(missionCode)){
            //完成每日打招呼任务
            content = "完成" + missionEnumService.getMissionByCode(missionCode).getMissionName();
        }

        //邀请人任务
        if(MissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){

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
    public void saveDailyFixedCoinLog(Integer chatPetRewardItemId){
        ChatPetRewardItem item = chatPetRewardItemService.getChatPetRewardItemById(chatPetRewardItemId);
        Integer chatPetId = item.getChatPetId();

        PetLog pl = new PetLog();

        pl.setRewardType(RewardMethodEnum.GOLD_REWARD.getType());
        pl.setChatPetId(chatPetId);
        pl.setCreateTime(new Date());
        pl.setContent("领取等级奖励");
        pl.setRewardItemId(chatPetRewardItemId);

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
