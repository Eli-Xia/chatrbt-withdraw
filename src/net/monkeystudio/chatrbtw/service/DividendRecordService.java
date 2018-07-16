package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.service.TaskExecutor;
import net.monkeystudio.base.utils.ArithmeticUtils;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.DividendDetailRecord;
import net.monkeystudio.chatrbtw.entity.DividendRecord;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.mapper.DividendRecordMapper;
import net.monkeystudio.chatrbtw.service.bean.dividendrecord.DividendQueueValueVO;
import net.monkeystudio.chatrbtw.utils.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by bint on 2018/7/10.
 */
@Service
public class DividendRecordService {

    @Autowired
    private DividendRecordMapper dividendRecordMapper;

    public Integer save(DividendRecord dividendRecord){
        return dividendRecordMapper.insert(dividendRecord);
    }
}
