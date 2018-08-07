package net.monkeystudio.chatrbtw;

import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.mapper.ChatPetMapper;
import net.monkeystudio.chatrbtw.service.*;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetGoldItem;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.OwnerInfo;
import net.monkeystudio.chatrbtw.service.bean.chatpet.PetLogResp;
import net.monkeystudio.chatrbtw.service.bean.chatpetappearence.Appearance;
import net.monkeystudio.chatrbtw.service.bean.chatpetappearence.LuckyCatAppearance;
import net.monkeystudio.chatrbtw.service.bean.chatpetlevel.ExperienceProgressRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author xiaxin
 */
@Service
public class MiniProgramChatPetService {

    @Autowired
    private ChatPetMapper chatPetMapper;
    @Autowired
    private WxFanService wxFanService;
    @Autowired
    private ChatPetService chatPetService;
    @Autowired
    private ChatPetLogService chatPetLogService;
    @Autowired
    private ChatPetLevelService chatPetLevelService;
    @Autowired
    private ChatPetAppearenceService chatPetAppearenceService;
    @Autowired
    private ChatPetRewardService chatPetRewardService;
    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;


    /**
     * 获取宠物的信息
     * @param wxFanId
     * @return
     */
    public ChatPetInfo getInfoByFanId(Integer wxFanId){

        ChatPet chatPet = chatPetService.getChatPetByWxFanId(wxFanId);

        if(chatPet == null){
            return null;
        }

        Integer chatPetId = chatPet.getId();

        return this.getInfo(chatPetId);

    }

    /**
     * 获取宠物的信息
     * @param chatPetId
     * @return
     */
    public ChatPetInfo getInfo(Integer chatPetId){
        ChatPetInfo chatPetBaseInfo = new ChatPetInfo();

        ChatPet chatPet = chatPetService.getById(chatPetId);

        if(chatPet == null){
            return null;
        }

        //用户信息
        OwnerInfo ownerInfo = new OwnerInfo();
        String wxFanOpenId = chatPet.getWxFanOpenId();
        WxFan owner = wxFanService.getWxFan(wxFanOpenId, WxFanService.LUCK_CAT_MINI_APP_ID);
        String nickname = owner.getNickname();
        String headImgUrl = owner.getHeadImgUrl();

        ownerInfo.setHeadImg(headImgUrl);
        ownerInfo.setNickname(nickname);

        chatPetBaseInfo.setOwnerInfo(ownerInfo);

        String owerId = wxFanOpenId.substring(wxFanOpenId.length() - 6, wxFanOpenId.length() - 1);
        chatPetBaseInfo.setOwnerId(owerId);

        //wxFanId
        Integer wxFanId = chatPet.getWxFanId();
        chatPetBaseInfo.setOwerWxFanId(wxFanId);

        //宠物基因
        String geneticCode = chatPetService.calculateGeneticCode(chatPet.getCreateTime().getTime());
        chatPetBaseInfo.setGeneticCode(geneticCode);

        //今日宠物日志
        List<PetLogResp> resps = chatPetLogService.getDailyPetLogList(chatPetId);
        chatPetBaseInfo.setPetLogs(resps);

        //粉丝拥有代币
        Float fansTotalCoin = chatPetService.getChatPetTotalCoin(chatPetId);
        chatPetBaseInfo.setFanTotalCoin(fansTotalCoin);

        //宠物的经验
        Float experience = chatPet.getExperience();
        chatPetBaseInfo.setExperience(experience);

        //宠物分红
        Float money = chatPet.getMoney();
        chatPetBaseInfo.setMoney(money);

        //经验条进度
        ExperienceProgressRate experienceProgressRate = chatPetLevelService.getProgressRate(experience);
        chatPetBaseInfo.setExperienceProgressRate(experienceProgressRate);

        //宠物等级
        Integer chatPetLevel = chatPetLevelService.calculateLevel(experience);
        chatPetBaseInfo.setChatPetLevel(chatPetLevel);

        //外观
        String appearanceCode = chatPet.getAppearanceCode();
        LuckyCatAppearance luckyCatAppearance = chatPetAppearenceService.getAppearance(appearanceCode , LuckyCatAppearance.class);

        Appearance appearance = new Appearance();
        appearance.setChatPetType(ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT);
        appearance.setObject(luckyCatAppearance);
        chatPetBaseInfo.setAppearance(appearance);

        //奖励
        List<ChatPetGoldItem> chatPetGoldItems = chatPetRewardService.getChatPetGoldItems(chatPetId);
        chatPetBaseInfo.setGoldItems(chatPetGoldItems);

        return chatPetBaseInfo;
    }


    /**
     * 生成一只招财猫宠物
     * @param wxFanId
     * @param chatPetType
     * @param parentId
     * @return
     */
    public Integer generateChatPet(Integer wxFanId,Integer chatPetType,Integer parentId){
        ChatPet chatPet = new ChatPet();
        String appearanceCode = chatPetAppearenceService.getAppearanceCodeFromPool(chatPetType);
        chatPet.setAppearanceCode(appearanceCode);

        WxFan wxFan = wxFanService.getById(wxFanId);
        String wxFanOpenId = wxFan.getWxFanOpenId();
        chatPet.setWxFanOpenId(wxFanOpenId);
        chatPet.setWxFanId(wxFanId);

        Integer miniProgramId = wxFan.getMiniProgramId();
        chatPet.setMiniProgramId(miniProgramId);

        chatPet.setCreateTime(new Date());

        chatPet.setParentId(parentId);

        chatPet.setChatPetType(chatPetType);

        chatPetService.save(chatPet);

        return chatPet.getId();
    }



}
