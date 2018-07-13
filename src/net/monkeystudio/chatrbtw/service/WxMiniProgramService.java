package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.chatrbtw.entity.WxMiniProgram;
import net.monkeystudio.chatrbtw.mapper.WxMiniProgramMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author xiaxin
 */
@Service
public class WxMiniProgramService {
    @Autowired
    private WxMiniProgramMapper wxMiniProgramMapper;
    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    public static final Integer LUCKY_CAT_MINI_PROGRAM_ID = 1;
}
