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
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.TodayMission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ChatPetRewardService chatPetRewardService;
    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;
    @Autowired
    private ChatPetAppearenceService chatPetAppearenceService;


    /**
     * 获取宠物的信息
     * @param wxFanId
     * @return
     */
    public ChatPetInfo getInfoByFanId(Integer wxFanId){

        WxFan wxFan = wxFanService.getById(wxFanId);

        String wxFanOpenId = wxFan.getWxFanOpenId();

        ChatPet chatPet = this.getChatPetByMiniProgramFanId(wxFanOpenId);

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

    public ChatPet getChatPetByMiniProgramFanId(String fanOpenId){
        ChatPet param = new ChatPet();
        param.setWxFanOpenId(fanOpenId);
        param.setChatPetType(ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT);
        ChatPet chatPet = chatPetMapper.selectByParam(param);
        return chatPet;
    }

    public ChatPet getChatPetById(Integer chatPetId){
        ChatPet chatPet = chatPetService.getById(chatPetId);
        return chatPet;
    }

    /**
     * 小程序为用户生成一只宠物
     * @param wxFanOpenId
     * @param chatPetType
     * @param parentId
     * @return
     */
    public void generateChatPet(String wxFanOpenId,Integer chatPetType,Integer parentId){
        String appearanceCode = chatPetAppearenceService.getAppearanceCodeFromPool(chatPetType);

        ChatPet chatPet = new ChatPet();
        chatPet.setWxFanOpenId(wxFanOpenId);
        chatPet.setCreateTime(new Date());
        chatPet.setParentId(parentId);
        chatPet.setAppearanceCode(appearanceCode);
        chatPet.setChatPetType(chatPetType);

        chatPetService.save(chatPet);
    }

    /**
     * 分享卡生成孩子宠物
     * @param fanId
     * @param parentId
     */
    public void generateChatPetFromShareCard(Integer fanId,Integer parentId){
        WxFan miniAppFan = wxFanService.getById(fanId);
        String fanOpenId = miniAppFan.getWxFanOpenId();
        this.generateChatPet(fanOpenId,ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT,parentId);
    }




}
