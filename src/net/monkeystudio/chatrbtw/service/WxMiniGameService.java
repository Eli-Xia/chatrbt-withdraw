package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.WxMiniGame;
import net.monkeystudio.chatrbtw.mapper.WxMiniGameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaxin
 */
@Service
public class WxMiniGameService {
    @Autowired
    private WxMiniGameMapper wxMiniGameMapper;

    public List<WxMiniGame> getWxMiniGameList(){
        return wxMiniGameMapper.selectAll();
    }

    public List<Integer> getWxMiniGameIds(){
        List<WxMiniGame> wxMiniGameList = this.getWxMiniGameList();
        List<Integer> ids = wxMiniGameList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
        return ids;
    }
}
