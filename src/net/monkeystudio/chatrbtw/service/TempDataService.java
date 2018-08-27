package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by bint on 2018/8/23.
 */
@Service
public class TempDataService {

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;


    public void increaseClickGame(){

        Date date = TimeUtil.getStartTimestamp();
        String format = TimeUtil.dateFormat(date, "yyyy-MM-dd");
        redisCacheTemplate.incr("click-game-count:" + format);
    }


    public void increaseClickMore(){
        Date date = TimeUtil.getStartTimestamp();
        String format = TimeUtil.dateFormat(date, "yyyy-MM-dd");
        redisCacheTemplate.incr("click-more-count:" + format);
    }

}
