package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.RandomUtil;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.PetLog;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.mapper.ChatPetMapper;
import net.monkeystudio.chatrbtw.mapper.PetLogMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.OwnerInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.PetLogResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2018/4/16.
 */
@Service
public class ChatPetService {

    private final static Integer MAX_APPERANCE_RANGE = 9;

    @Autowired
    private ChatPetMapper chatPetMapper;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private ChatPetLogService chatPetLogService;
    /**
     * 生成宠物
     * @param wxPubOriginId
     * @param wxFanOpenId
     * @return 生成的宠物的id
     */
    public Integer generateChatPet(String wxPubOriginId , String wxFanOpenId ,Integer ethnicGroupsId ,Integer secondEthnicGroupsId){

        ChatPet chatPet = new ChatPet();
        Integer appearance = this.ramdomGenerateAppearence();

        chatPet.setTempAppearence(appearance);
        chatPet.setWxFanOpenId(wxFanOpenId);
        chatPet.setWxPubOriginId(wxPubOriginId);
        chatPet.setEthnicGroupsId(ethnicGroupsId);
        chatPet.setSecondEthnicGroupsId(secondEthnicGroupsId);

        return this.save(chatPet);
    }

    /**e
     * 随机生成
     * @return
     */
    public Integer ramdomGenerateAppearence(){
        return RandomUtil.randomIndex(MAX_APPERANCE_RANGE);
    }

    public ChatPet getById(Integer id){
        return chatPetMapper.selectById(id);
    }


    private Integer save(ChatPet chatPet){
        return chatPetMapper.insert(chatPet);
    }


    /**
     * 获取宠物的信息
     * @param chatPetId
     * @return
     */
    public ChatPetInfo getInfo(Integer chatPetId){
        ChatPetInfo chatPetBaseInfo = new ChatPetInfo();

        ChatPet chatPet = this.getById(chatPetId);

        if(chatPet == null){
            return null;
        }

        chatPetBaseInfo.setTempAppearance(chatPet.getTempAppearence());

        String wxPubOriginId = chatPet.getWxPubOriginId();
        String wxFanOpenId = chatPet.getWxFanOpenId();

        OwnerInfo ownerInfo = new OwnerInfo();
        WxFan owner = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
        ownerInfo.setNickname(owner.getNickname());

        String headImgUrl = owner.getHeadImgUrl();
        if(headImgUrl == null){
            wxFanService.reviseWxPub(wxPubOriginId,wxFanOpenId);
        }

        owner = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
        ownerInfo.setHeadImg(owner.getHeadImgUrl());

        chatPetBaseInfo.setOwnerInfo(ownerInfo);

        String owerId = wxFanOpenId.substring(wxFanOpenId.length() - 6, wxFanOpenId.length() - 1);
        chatPetBaseInfo.setOwnerId(owerId);

        //宠物基因
        String geneticCode = this.calculateGeneticCode(chatPet.getCreateTime().getTime());
        chatPetBaseInfo.setGeneticCode(geneticCode);

        //宠物日志
        List<PetLog> dailyPetLogList = chatPetLogService.getDailyPetLogList(chatPetId, new Date());
        List<PetLogResp> resps = new ArrayList<>();

        for(PetLog pl:dailyPetLogList){
            PetLogResp resp = new PetLogResp();
            resp.setChatPetId(pl.getChatPetId());
            resp.setCoin(pl.getCoin());
            resp.setContent(pl.getContent());
            resp.setCreateTime(pl.getCreateTime());
            resp.setId(pl.getId());
            resps.add(resp);
        }
        chatPetBaseInfo.setPetLogs(resps);

        //粉丝拥有代币
        Float fanTotalCoin = chatPetLogService.getFanTotalCoin(wxPubOriginId,wxFanOpenId);
        chatPetBaseInfo.setFanTotalCoin(fanTotalCoin);

        return chatPetBaseInfo;
    }

    private String calculateGeneticCode(Long createTime){

        Calendar calendar = Calendar.getInstance();
        calendar.set(2018,1,1);
        Date date = calendar.getTime();

        Long cusTimestamp = createTime - date.getTime();
        String geneticCode = String.valueOf(cusTimestamp);

        return geneticCode;
    }


}
