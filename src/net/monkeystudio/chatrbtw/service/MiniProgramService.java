package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.chatrbtw.entity.MiniProgram;
import net.monkeystudio.chatrbtw.mapper.MiniProgramMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author xiaxin
 */
@Service
public class MiniProgramService {
    @Autowired
    private MiniProgramMapper wxMiniProgramMapper;
    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    public static final Integer LUCKY_CAT_MINI_PROGRAM_ID = 1;

    public MiniProgram getById(Integer id){
        return wxMiniProgramMapper.selectByPrimaryKey(id);
    }
}
