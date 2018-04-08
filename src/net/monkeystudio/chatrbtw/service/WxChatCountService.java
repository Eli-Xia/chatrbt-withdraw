package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.TimeUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 统计微信聊天信息
 * Created by bint on 2017/12/14.
 */
@Service
public class WxChatCountService {

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    /**
     * 当天到达指定广告推送次数的人数+1
     */
    public void incrArrivalCount(){
        String key = this.getArrivalCountKey();
        String field = this.getArrivalCountField();

        redisCacheTemplate.hincrBy(key, field, 1L);
    }

    private String getArrivalCountKey(){
        String key = RedisTypeConstants.KEY_HASH_TYPE_PREFIX + "WxFanArrivalCount";
        return key;
    }

    private String getArrivalCountField(){
        Date date = TimeUtil.getStartTimestamp();
        String dateStr = TimeUtil.dateFormat(date,"yyyyMMdd");
        return dateStr;
    }
}
