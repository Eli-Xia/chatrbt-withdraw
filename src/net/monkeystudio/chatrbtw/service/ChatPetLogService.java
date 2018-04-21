package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.chatrbtw.entity.PetLog;
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

        petLogMapper.insert(pl);
    }
}
