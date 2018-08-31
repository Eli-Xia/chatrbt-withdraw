package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.chatrbtw.entity.MiniGameTag;
import net.monkeystudio.chatrbtw.entity.WxMiniGame;
import net.monkeystudio.chatrbtw.mapper.MiniGameTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class MiniGameTagService {
    @Autowired
    private MiniGameTagMapper miniGameTagMapper;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @PostConstruct
    private void init(){
        List<MiniGameTag> list = this.getMiniGameTagListFromDb();
        redisCacheTemplate.setObject(this.getMiniGameTagListCacheKey(),list);
    }

    private String getMiniGameTagListCacheKey(){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "miniGameTagList";
    }

    public List<MiniGameTag> getMiniGameTagList(){
        List<MiniGameTag> tags = redisCacheTemplate.getObject(this.getMiniGameTagListCacheKey());

        if(tags == null){
            tags = this.getMiniGameTagListFromDb();
        }

        return tags;
    }

    public List<MiniGameTag> getMiniGameTagListFromDb(){
        return miniGameTagMapper.selectAll();
    }
}
