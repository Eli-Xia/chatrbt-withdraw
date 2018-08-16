package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.MiniGameTag;
import net.monkeystudio.chatrbtw.mapper.MiniGameTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MiniGameTagService {
    @Autowired
    private MiniGameTagMapper miniGameTagMapper;

    public List<MiniGameTag> getMiniGameTagList(){
        return miniGameTagMapper.selectAll();
    }
}
