package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.mapper.GlobalConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaxin
 */
@Service
public class SqlHeartBeatService {
    @Autowired
    private GlobalConfigMapper globalConfigMapper;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    private final static String HEART_BEAT_MESSAGE_KEY = "heart_beat_task";
    /**
     * 每隔50S的心跳测试
     */
    public void sqlHeartBeatTask(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    List<String> list = redisCacheTemplate.brpop(0,HEART_BEAT_MESSAGE_KEY);
                    String msg = list.get(1);
                    Log.i("receive the message [?]",msg);
                    globalConfigMapper.selectTest();
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("sqlHeartBeatTask");
        thread.start();
        Log.d("finished task");

    }


}
