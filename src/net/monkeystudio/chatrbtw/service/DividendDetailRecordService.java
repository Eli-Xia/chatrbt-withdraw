package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.DividendDetailRecord;
import net.monkeystudio.chatrbtw.mapper.DividendDetailRecordMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * Created by bint on 2018/7/10.
 */
@Service
public class DividendDetailRecordService {

    @Autowired
    private DividendDetailRecordMapper dividendDetailRecordMapper;


    public Integer save(DividendDetailRecord dividendDetailRecord){
        return dividendDetailRecordMapper.insert(dividendDetailRecord);
    }

}
