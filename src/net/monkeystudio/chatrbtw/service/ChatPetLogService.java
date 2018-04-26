package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.PetLog;
import net.monkeystudio.chatrbtw.enums.ChatPetTaskEnum;
import net.monkeystudio.chatrbtw.mapper.PetLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    /**
     * 获取每日宠物日志
     * @param date
     * @return
     */
    public List<PetLog> getDailyPetLogList(Integer chatPetId, Date date){
        Date beginDate = DateUtils.getBeginDate(date);
        Date endDate = DateUtils.getEndDate(date);

        List<PetLog> pls = petLogMapper.selectDailyPetLog(chatPetId,beginDate,endDate);
        return pls;
    }

    /**
     * 获取当前粉丝总代币数  代币是跟粉丝挂钩的
     * @return
     */
    public Float getFanTotalCoin(String wxPubOriginId,String wxFanOpenId){

        Float totalCoin = petLogMapper.countFanTotalCoin(wxPubOriginId,wxFanOpenId,new Date());
        return totalCoin;
    }

    /**
     * 保存宠物日志
     * @param petLog
     */
    public void savePetLog(PetLog petLog){
        PetLog pl = new PetLog();

        pl.setCoin(petLog.getCoin());
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
     * 粉丝完成
     * @return
     */
    public void completeChatPetDailyTask(String wxPubOriginId,String wxFanOpenId,ChatPetTaskEnum taskEnum){
        //判断粉丝当天宠物陪聊任务是否已经完成
        //完成则插入一条宠物日志
        if(isDailyTaskDone(wxPubOriginId,wxFanOpenId,taskEnum)){
            return;
        }
        PetLog pl = new PetLog();
        pl.setTaskCode(taskEnum.getCode());
        pl.setCoin(taskEnum.getCoinValue());
        pl.setCreateTime(new Date());
        pl.setContent("完成"+taskEnum.getName());
        ChatPet chatPet = chatPetService.getChatPetByFans(wxPubOriginId, wxFanOpenId);
        if(chatPet != null){
            pl.setChatPetId(chatPet.getId());
        }
        pl.setWxPubOriginId(wxPubOriginId);
        pl.setWxFanOpenId(wxFanOpenId);

        this.savePetLog(pl);
    }

    public boolean isDailyTaskDone(String wxPubOriginId,String wxFanOpenId,ChatPetTaskEnum taskEnum){
        Date now = new Date();
        Date beginDate = DateUtils.getBeginDate(now);
        Date endDate = DateUtils.getEndDate(now);

        //每日阅读任务code
        int code = taskEnum.getCode();

        Integer count = petLogMapper.countTaskLog(wxPubOriginId, wxFanOpenId, beginDate, endDate, code);

        return count > 0;

    }

}
