package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.ChatPetColor;
import net.monkeystudio.chatrbtw.mapper.ChatPetColorMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetColorItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private List<ChatPetColor> getAll() {
        List<ChatPetColor> chatPetColorList = chatPetColorMapper.selectAll();
        return chatPetColorList;
    }


    public List<ChatPetColorItem> getAllColor() {
        List<ChatPetColor> chatPetColorModelList = this.getAll();

        List<ChatPetColorItem> chatPetColorList = new ArrayList<>();

        for(ChatPetColor chatPetColor : chatPetColorModelList){

            ChatPetColorItem chatPetColorItem = BeanUtils.copyBean(chatPetColor, ChatPetColorItem.class);
            chatPetColorItem.setRgbValue(chatPetColor.getRGBValue());

            chatPetColorList.add(chatPetColorItem);
        }

        return chatPetColorList;
    }
}
