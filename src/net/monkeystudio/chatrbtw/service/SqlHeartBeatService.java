package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.mapper.GlobalConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaxin
 */
@Service
public class SqlHeartBeatService {
    @Autowired
    private GlobalConfigMapper globalConfigMapper;

    /**
     * 每隔50S的心跳测试
     */
    public void sqlHeartBeatTask(){
        globalConfigMapper.selectTest();
    }
}
