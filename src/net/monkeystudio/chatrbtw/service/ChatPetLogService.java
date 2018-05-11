package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.ChatPetMission;
import net.monkeystudio.chatrbtw.entity.PetLog;
import net.monkeystudio.chatrbtw.enums.mission.RewardMethodEnum;
import net.monkeystudio.chatrbtw.mapper.PetLogMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.PetLogResp;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ChatPetService chatPetService;

    @Autowired
    private ChatPetMissionService chatPetMissionService;

    @Autowired
    private ChatPetLevelService chatPetLevelService;

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

        for(PetLog pl:pls){
            PetLogResp resp = new PetLogResp();
            resp.setCreateTime(pl.getCreateTime());
            resp.setContent(pl.getContent());

            Integer taskCode = pl.getTaskCode();
            ChatPetMission cpm = chatPetMissionService.getByMissionCode(taskCode);

            if(taskCode == null){
                //不是奖励类型的日志
                resp.setRewardType(RewardMethodEnum.NULL_REWARD.getType());
            }

            Integer rewardType = pl.getRewardType();

            if(rewardType != null){
                if(RewardMethodEnum.GOLD_REWARD.getType() == rewardType){
                    resp.setCoin(cpm.getCoin().intValue());
                }
                if(RewardMethodEnum.EXPERIENCE_REWARD.getType() == rewardType){
                    resp.setCoin(cpm.getExperience());
                }
            }

            resp.setRewardType(pl.getRewardType());

            resps.add(resp);
        }
        return resps;
    }


    /**
     * 保存宠物日志
     * @param petLog
     */
    public void savePetLog(PetLog petLog){
        PetLog pl = new PetLog();

        pl.setCreateTime(petLog.getCreateTime());
        pl.setContent(petLog.getContent());
        pl.setChatPetId(petLog.getChatPetId());
        pl.setWxPubOriginId(petLog.getWxPubOriginId());
        pl.setWxFanOpenId(petLog.getWxFanOpenId());
        pl.setTaskCode(petLog.getTaskCode());

        petLogMapper.insert(pl);
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
     * 完成任务领取奖励后插入宠物日志
     */
    @Transactional
    public void savePetLogWhenReward(Integer chatPetId,Integer missionCode,Integer oldExperience,Integer newExperience){
        ChatPet chatPet = chatPetService.getById(chatPetId);
        String wxPubOriginId = chatPet.getWxPubOriginId();
        String wxFanOpenId = chatPet.getWxFanOpenId();

        ChatPetMission cpm = chatPetMissionService.getByMissionCode(missionCode);
        Float coin = cpm.getCoin();
        Integer experience = cpm.getExperience();

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

            if(experience != 0){
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
                if(chatPetLevelService.isUpgrade(oldExperience,newExperience)){
                    //升级后的等级
                    Integer level = chatPetLevelService.calculateLevel(newExperience);

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


    }

}
