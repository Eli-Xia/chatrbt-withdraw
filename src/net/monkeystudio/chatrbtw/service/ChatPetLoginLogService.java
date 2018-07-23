package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.chatrbtw.entity.ChatPetLoginLog;
import net.monkeystudio.chatrbtw.mapper.ChatPetLoginLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author xiaxin
 */
@Service
public class ChatPetLoginLogService {
    @Autowired
    private ChatPetLoginLogMapper chatPetLoginLogMapper;

    public void save(ChatPetLoginLog chatPetLoginLog){
        chatPetLoginLogMapper.insert(chatPetLoginLog);
    }

    /**
     * 获取昨日登录人数
     * @return
     */
    public Integer getYesterdayLoginNumCount(){
        Date yesterday = CommonUtils.dateOffset(new Date(), -1);

        Date beginTime = CommonUtils.dateStartTime(yesterday);

        Date endTime = CommonUtils.dateEndTime(yesterday);

        return chatPetLoginLogMapper.countLoginNum(beginTime, endTime);
    }
}
