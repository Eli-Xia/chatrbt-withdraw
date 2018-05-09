package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.ChatPetColor;
import net.monkeystudio.chatrbtw.mapper.ChatPetColorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by bint on 2018/5/7.
 */
@Service
public class ChatPetColorService {

    public final static String NONE_COLOR_KEY = "0";

    @Autowired
    private ChatPetColorMapper chatPetColorMapper;

    /**
     * 获取所有的颜色
     * @return
     */
    public List<ChatPetColor> getAll() {
        List<ChatPetColor> chatPetColorList = chatPetColorMapper.selectAll();
        return chatPetColorList;
    }



}
