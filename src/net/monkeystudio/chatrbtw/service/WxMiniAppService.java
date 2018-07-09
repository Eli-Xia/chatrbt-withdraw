package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.chatrbtw.entity.WxMiniApp;
import net.monkeystudio.chatrbtw.mapper.WxMiniAppMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author xiaxin
 */
@Service
public class WxMiniAppService {
    @Autowired
    private WxMiniAppMapper wxMiniAppMapper;
    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @PostConstruct
    private void init(){

    }

    private WxMiniApp getLuckyCatMiniApp(){
        return wxMiniAppMapper.selectByPrimaryKey(1);
    }

    private String getLuckyCatMiniAppCacheKey(){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "wx_mini_app_id" ;
    }
}
