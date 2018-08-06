package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.annotation.chatpet.ChatPetType;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.mapper.ChatPetRewardItemMapper;
import net.monkeystudio.chatrbtw.mapper.RMiniProgramProductMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpet.ChatPetGoldItem;
import net.monkeystudio.chatrbtw.service.bean.chatpetlog.SaveChatPetLogParam;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.MissionReward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 陪聊宠奖励
 * @author xiaxin
 */
@Service
public class ChatPetRewardService{


    @Autowired
    private ChatPetMissionEnumService chatPetMissionEnumService;
    @Autowired
    private ChatPetRewardItemMapper chatPetRewardItemMapper;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private ChatPetLogService chatPetLogService;

    @Autowired
    private ChatPetLevelService chatPetLevelService;

    @Autowired
    private RWxPubProductService rWxPubProductService;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private OpLogService opLogService;

    @Autowired
    private EthnicGroupsService ethnicGroupsService;

    @Autowired
    private ChatPetTypeConfigService chatPetTypeConfigService;

    @Autowired
    private RMiniProgramProductMapper rMiniProgramProductMapper;

    @Autowired
    private ChatPetExpFlowService chatPetExpFlowService;

    @Autowired
    private ChatPetCoinFlowService chatPetCoinFlowService;

    @Autowired
    private WxMiniGameService wxMiniGameService;

    @Autowired
    private ChatPetMissionRewardService chatPetMissionRewardService;


    public static final Integer NOT_AWARD = 0;//未领取
    public static final Integer HAVE_AWARD = 1;//已经领取
    public static final Integer EXPIRED_AWARD = 2;//已经过期

    private static final Integer MAX_NOT_AWARD_COUNT = 12;//等级奖励最大个数

    //定时产生等级奖励队列key
    private final static String LEVEL_REWARD_MESSAGE_KEY = "generate_level_reward_task";
    //定时删除过时奖励
    private final static String DELETE_EXPIRE_REWARD_MESSAGE_KEY = "delete_expire_reward_task";
    //出生奖励数量
    private final static Integer BORN_REWARD_NUM = 5;




    public ChatPetRewardItem getChatPetRewardItemById(Integer id){
        return chatPetRewardItemMapper.selectByPrimaryKey(id);
    }

    public void save(ChatPetRewardItem item){
        chatPetRewardItemMapper.insert(item);
    }

    public void update(ChatPetRewardItem item){
        chatPetRewardItemMapper.updateByPrimaryKey(item);
    }


    /**
     * 获取宠物奖励展示
     * @param chatPetId
     * @return
     */
    public List<ChatPetGoldItem> getChatPetGoldItems(Integer chatPetId){

        ChatPetRewardItem param = new ChatPetRewardItem();
        param.setRewardState(NOT_AWARD);
        param.setChatPetId(chatPetId);
        //param.setCreateTime(DateUtils.getBeginDate(new Date()));

        List<ChatPetRewardItem> items = this.chatPetRewardItemMapper.selectByParam(param);

        if(ListUtil.isEmpty(items)){
            return Collections.EMPTY_LIST;
        }

        List<ChatPetGoldItem> goldItems = new ArrayList<>();

        for(ChatPetRewardItem item : items){

            ChatPetGoldItem goldItem = new ChatPetGoldItem();

            goldItem.setGoldValue(item.getGoldValue());
            goldItem.setRewardItemId(item.getId());

            goldItems.add(goldItem);
        }

        return goldItems;

    }

    /**
     * 宠物领取奖励
     * @param chatPetRewardItemId   领取奖励对象id
     */
    public void reward(Integer chatPetRewardItemId){

        ChatPetRewardItem chatPetRewardItem = this.getChatPetRewardItemById(chatPetRewardItemId);

        Integer missionItemId = chatPetRewardItem.getMissionItemId();

        //是否为任务类型奖励
        Boolean isMissionReward = missionItemId != null;

        if(isMissionReward){

            this.missionRewardHandle(chatPetRewardItemId);

        }else{

            this.levelRewardHandle(chatPetRewardItemId);

        }
    }




    /**
     * 每日可领取奖励(等级奖励)处理
     * @param chatPetRewardItemId 领取奖励对象id
     */
    private void levelRewardHandle(Integer chatPetRewardItemId){

        ChatPetRewardItem chatPetRewardItem = this.getChatPetRewardItemById(chatPetRewardItemId);
        Integer chatPetId = chatPetRewardItem.getChatPetId();

        //修改奖励对象的领取状态为已领取
        Integer updateCount = this.updateRewardState(chatPetRewardItemId);
        if(updateCount.intValue() <= 0){
            return;
        }

        //加金币
        Float addGoldValue = chatPetRewardItem.getGoldValue();//新增金币值
        chatPetService.increaseCoin(chatPetId,addGoldValue);

        //宠物日志
        SaveChatPetLogParam param = new SaveChatPetLogParam();
        param.setChatPetRewardItemId(chatPetRewardItemId);
        param.setChatPetLogType(ChatPetLogTypeService.CHAT_PET_LOG_TYPE_LEVEL_REWARD);
        chatPetLogService.saveChatPetDynamic(param);

        //日常领取等级奖励猫饼流水
        chatPetCoinFlowService.dailyRewardFlow(chatPetId,addGoldValue);

    }

    /**
     * 任务类型奖励处理
     * @param chatPetRewardItemId   领取奖励对象id
     */
    private void missionRewardHandle(Integer chatPetRewardItemId){
        //修改金币状态
        Integer updateCount = this.updateRewardState(chatPetRewardItemId);

        if(updateCount <= 0){
            return;
        }

        //修改任务记录状态
        ChatPetRewardItem chatPetRewardItem = this.getChatPetRewardItemById(chatPetRewardItemId);
        Integer missionItemId = chatPetRewardItem.getMissionItemId();
        chatPetMissionPoolService.updateMissionWhenReward(missionItemId);

        //加金币
        Integer chatPetId = chatPetRewardItem.getChatPetId();
        chatPetService.increaseCoin(chatPetId,chatPetRewardItem.getGoldValue());

        //加经验
        ChatPet chatPet = chatPetService.getById(chatPetId);
        Float oldExperience = chatPet.getExperience();
        //增加经验值
        Float addExperience = chatPetRewardItem.getExperience();
        chatPetService.increaseExperience(chatPetId,addExperience);

        //是否升级
        chatPet = chatPetService.getById(chatPetId);
        Float newExperience = chatPet.getExperience();
        boolean isUpgrade = chatPetLevelService.isUpgrade(oldExperience, newExperience);

        //宠物日志
        SaveChatPetLogParam saveChatPetLogParam = new SaveChatPetLogParam();
        saveChatPetLogParam.setChatPetId(chatPetId);
        saveChatPetLogParam.setChatPetLogType(ChatPetLogTypeService.CHAT_PET_LOG_TYPE_MISSION_REWARD);
        saveChatPetLogParam.setChatPetRewardItemId(chatPetRewardItemId);
        saveChatPetLogParam.setUpgrade(isUpgrade);
        chatPetLogService.saveChatPetDynamic(saveChatPetLogParam);

        //经验值流水
        ChatPetPersonalMission chatPetPersonalMission = chatPetMissionPoolService.getById(missionItemId);
        Integer missionCode = chatPetPersonalMission.getMissionCode();
        if(ChatPetMissionEnumService.DAILY_PLAY_MINI_GAME_CODE.equals(missionCode)){
            chatPetExpFlowService.playGameFlow(chatPetId,addExperience);
        }
        if(ChatPetMissionEnumService.DAILY_CHAT_MISSION_CODE.equals(missionCode)){
            chatPetExpFlowService.wxPubSayHiFlow(chatPetId,addExperience);
        }
        if(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){
            chatPetExpFlowService.presentCatFlow(chatPetId,addExperience);
        }
        if(ChatPetMissionEnumService.DAILY_LOGIN_MINI_PROGRAM_CODE.equals(missionCode)){
            chatPetExpFlowService.dailyLoginFlow(chatPetId,addExperience);
        }
        /*Integer chatPetType = chatPet.getChatPetType();

        if(ChatPetTypeService.CHAT_PET_TYPE_ZOMBIES_CAT.equals(chatPetType)){
            chatPetLogService.savePetLog4MissionReward(chatPetRewardItemId,isUpgrade);
        }

        if(ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT.equals(chatPetType)){
            chatPetLogService.saveLuckyCatMissionLog(chatPetRewardItemId,isUpgrade);
        }*/

    }

    /**
     * 完成宠物任务后生成奖励
     * @param chatPetPersonalMissionId  宠物任务id
     */
    public void generateRewardWhenMissionDone(Integer chatPetPersonalMissionId){

        ChatPetPersonalMission cppm = chatPetMissionPoolService.getById(chatPetPersonalMissionId);
        Integer missionCode = cppm.getMissionCode();
        Integer chatPetId = cppm.getChatPetId();

        this.saveRewardItemByMission(missionCode,chatPetId,chatPetPersonalMissionId);
    }

    /**
     * 根据任务类型生成一个奖励
     * @param missionCode
     */
    public void saveRewardItemByMission(Integer missionCode, Integer chatPetId, Integer chatPetPersonalMissionId){
        ChatPetRewardItem item = new ChatPetRewardItem();

        ChatPet chatPet = chatPetService.getById(chatPetId);
        Integer chatPetType = chatPet.getChatPetType();

        item.setChatPetId(chatPetId);
        item.setRewardState(NOT_AWARD);
        item.setMissionItemId(chatPetPersonalMissionId);
        item.setCreateTime(new Date());
        item.setChatPetType(chatPetType);

        MissionReward missionReward = null;

        if(ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){
            missionReward = chatPetMissionRewardService.getSearchNewsMissionReward();
        }

        if(ChatPetMissionEnumService.DAILY_CHAT_MISSION_CODE.equals(missionCode)){
            missionReward = chatPetMissionRewardService.getDailyChatMissionReward();
        }

        if(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){
            missionReward = chatPetMissionRewardService.getPresentCatMissionReward(chatPetId);
        }

        if(ChatPetMissionEnumService.DAILY_PLAY_MINI_GAME_CODE.equals(missionCode)){
            ChatPetPersonalMission miniGameMission = chatPetMissionPoolService.getById(chatPetPersonalMissionId);
            Integer miniGameId = miniGameMission.getWxMiniGameId();

            WxMiniGame miniGame = wxMiniGameService.getById(miniGameId);
            Integer needSign = miniGame.getNeedSign();

            Float playMiniGameExperience = 0F;

            if(WxMiniGameService.WX_MINI_GAME_NEED_SIGN.equals(needSign)){
               missionReward = chatPetMissionRewardService.getPlayNewMiniGameMissionReward(chatPetId);
            }

            if(WxMiniGameService.WX_MINI_GAME_NO_SIGN.equals(needSign)){
                missionReward = chatPetMissionRewardService.getPlayOldMiniGameMissionReward(chatPetId);
            }
        }

        if(ChatPetMissionEnumService.DAILY_LOGIN_MINI_PROGRAM_CODE.equals(missionCode)){
            missionReward = chatPetMissionRewardService.getLoginMiniProgramMisisonReward(chatPetId);
        }

        Float coin = missionReward.getCoin();
        Float experience = missionReward.getExperience();

        item.setExperience(experience);
        item.setGoldValue(coin);

        this.save(item);

    }


    /**
     * 根据任务类型生成一个奖励
     */
    /*private void saveRewardItemByMission(Integer missionCode, Integer chatPetId, Integer chatPetPersonalMissionId){

        ChatPetRewardItem item = new ChatPetRewardItem();

        ChatPet chatPet = chatPetService.getById(chatPetId);
        Integer chatPetType = chatPet.getChatPetType();

        item.setChatPetId(chatPetId);
        item.setRewardState(NOT_AWARD);
        item.setMissionItemId(chatPetPersonalMissionId);
        item.setCreateTime(new Date());
        item.setChatPetType(chatPetType);

        BigDecimal ethnicGroupsAdditionRadio = ethnicGroupsService.getEthnicGroupsAdditionRadio(chatPetId);

        if(ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE.equals(missionCode)){

            item.setExperience(this.getSearchNewMissionRandomExperience());//1.5 ~ 2.5
            item.setGoldValue(this.getSearchNewMissionRandomCoin());//0.38 ~ 0.63

            this.save(item);
        }

        if(ChatPetMissionEnumService.DAILY_CHAT_MISSION_CODE.equals(missionCode)){

            item.setGoldValue(chatPetMissionEnumService.getMissionByCode(missionCode).getCoin());
            item.setExperience(chatPetMissionEnumService.getMissionByCode(missionCode).getExperience());

            this.save(item);
        }

        if(ChatPetMissionEnumService.INVITE_FRIENDS_MISSION_CODE.equals(missionCode)){


            ChatPetTypeConfig chatPetTypeConfig = chatPetTypeConfigService.getChatPetTypeConfig(chatPetType);

            if(chatPetTypeConfig.getRewardType().equals(ChatPetTypeConfigService.Constants.MANUALLY_EXPERIENCE_COIN)){
                Float coin = chatPetMissionEnumService.getMissionByCode(missionCode).getCoin();
                item.setGoldValue(coin);
                item.setExperience(chatPetMissionEnumService.getMissionByCode(missionCode).getExperience());
            }

            if(chatPetTypeConfig.getRewardType().equals(ChatPetTypeConfigService.Constants.MANUALLY_ONLY_EXPERIENCE)){
                item.setGoldValue(0F);

                Float experience = chatPetMissionEnumService.getMissionByCode(missionCode).getExperience();
                BigDecimal experienceBD = ethnicGroupsAdditionRadio.multiply(new BigDecimal(experience));
                item.setExperience(experienceBD.floatValue());
            }

            Float coin = chatPetMissionEnumService.getMissionByCode(missionCode).getCoin();
            item.setGoldValue(coin);
            item.setExperience(chatPetMissionEnumService.getMissionByCode(missionCode).getExperience());

            this.save(item);
        }

        if(ChatPetMissionEnumService.DAILY_PLAY_MINI_GAME_CODE.equals(missionCode)){
            ChatPetPersonalMission miniGameMission = chatPetMissionPoolService.getById(chatPetPersonalMissionId);
            Integer miniGameId = miniGameMission.getWxMiniGameId();

            WxMiniGame miniGame = wxMiniGameService.getById(miniGameId);
            Integer needSign = miniGame.getNeedSign();

            Float playMiniGameExperience = 0F;

            if(WxMiniGameService.WX_MINI_GAME_NEED_SIGN.equals(needSign)){
                playMiniGameExperience = this.getPlayNewMiniGameExperience();
            }

            if(WxMiniGameService.WX_MINI_GAME_NO_SIGN.equals(needSign)){
                playMiniGameExperience = this.getPlayOldMiniGameExperience();
            }

            //族群加成之后经验值
            BigDecimal bd = ethnicGroupsAdditionRadio.multiply(new BigDecimal(playMiniGameExperience));
            item.setExperience(bd.floatValue());
            item.setGoldValue(0F);

            this.save(item);
        }

        if(ChatPetMissionEnumService.DAILY_LOGIN_MINI_PROGRAM_CODE.equals(missionCode)){

            item.setExperience(chatPetMissionEnumService.getMissionByCode(missionCode).getExperience());

            //每日登陆的奖励为1
            BigDecimal bd = ethnicGroupsAdditionRadio.multiply(new BigDecimal(1));
            item.setExperience(bd.floatValue());

            item.setGoldValue(0F);

            this.save(item);
        }
    }*/

//    //获取资讯任务随机奖励经验值  1.5 ~ 2.5
//    private Float getSearchNewMissionRandomExperience(){
//        Random random = new Random();
//        Float f = ( random.nextInt(10) + 15 ) / 10F;
//        return f;
//    }
//
//    //获取资讯任务随机奖励金币值  0.38 ~ 0.63
//    private Float getSearchNewMissionRandomCoin(){
//        // ( 0 ~ 25 + 38 ) / 100
//        Random random = new Random();
//        int i = random.nextInt(25) + 38;
//        Float f = i / 100F;
//        return f;
//    }
//
//    /**
//     * 获取NEW小游戏经验值
//     * 1.0 ~ 2.0随机
//     * @return
//     */
//    private Float getPlayNewMiniGameExperience(){
//        Random random = new Random();
//        Float f = ( random.nextInt(100) + 100 ) / 100F;
//        return f;
//    }
//
//    /**
//     * 获取没有NEW小游戏经验值
//     * @return
//     */
//    private Float getPlayOldMiniGameExperience(){
//        Float f = 0.2F;
//        return f;
//    }


    /**
     * 领取奖励之后修改奖励状态,防止多个领奖请求同时update同一奖励,加入状态判断
     * @param chatPetRewardItemId
     * @return   返回被修改的个数,若updateCount <= 0 则修改失败
     */
    private Integer updateRewardState(Integer chatPetRewardItemId){

        ChatPetRewardItem chatPetRewardItem = this.getChatPetRewardItemById(chatPetRewardItemId);

        chatPetRewardItem.setRewardState(HAVE_AWARD);

        return this.chatPetRewardItemMapper.updateRewarded(chatPetRewardItem);
    }


    /**
     * 判断奖励是否被领取
     * @param rewardItemId
     * @return
     */
    public boolean isGoldAwarded(Integer rewardItemId){
        boolean ret = false;
        ChatPetRewardItem item = this.getChatPetRewardItemById(rewardItemId);
        Integer rewardState = item.getRewardState();
        if(HAVE_AWARD.equals(rewardState)){
            ret = true;
        }
        return ret;
    }

    /**
     *  获取当前宠物等级奖励的奖励值
     * @param chatPetId
     * @return
     */
    public Float getChatPetLevelCoinReward(Integer chatPetId){
        ChatPetRewardItem chatPetRewardItem = this.chatPetRewardItemMapper.selectLevelRewardItem(chatPetId);
        return chatPetRewardItem.getGoldValue();
    }

    //初始化奖励池count cache
    private String getInitRewardItemCountKey(Integer chatPetId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "initRewardItem:" + chatPetId;
    }

    /**
     * 获取奖励列表
     * @param createTime 时间
     * @param chatPetId 宠物id
     * @param rewardState 奖励状态
     * @return
     */
    public List<ChatPetRewardItem> getByChatPetAndState(Date createTime ,Integer chatPetId ,Integer rewardState){
        return chatPetRewardItemMapper.selectByDateAndChatPet(createTime , chatPetId ,rewardState );
    }


    /**
     * 获取所有的开通陪聊宠公众号的粉丝，把粉丝塞入队列
     */
    public void generateLevelReward(){


        //开通陪聊宠的公众号
        /*List<RWxPubProduct> rWxPubProductList = rWxPubProductService.getWxPubListByProduct(ProductService.CHAT_PET);

        for(RWxPubProduct rWxPubProduct : rWxPubProductList){

            String wxPubOriginId = rWxPubProduct.getWxPubOriginId();

            List<WxFan> wxFanList = wxFanService.getListByWxPubOriginid(wxPubOriginId);

            String key = this.getLevelReward();
            for(WxFan wxFan : wxFanList){
                redisCacheTemplate.lpush(key,String.valueOf(wxFan.getId()));
            }
        }*/

        //开通陪聊宠的小程序
        List<RMiniProgramProduct> rMiniProgramProductList = rMiniProgramProductMapper.selectByProductId(ProductService.CHAT_PET);
        for(RMiniProgramProduct rWxMiniProgramProduct : rMiniProgramProductList){
            Integer miniProgramId = rWxMiniProgramProduct.getMiniProgramId();

            List<WxFan> wxFanList = wxFanService.getListByMiniProgramId(miniProgramId);
            String key = this.getLevelReward();
            for(WxFan wxFan : wxFanList){
                redisCacheTemplate.lpush(key,String.valueOf(wxFan.getId()));
            }
        }
    }

    private String getLevelReward(){
        return RedisTypeConstants.KEY_LIST_TYPE_PREFIX + "levelReward:wxFan";
    }

    /**
     * 消费等级奖励的消息队列
     */
    public void comsumeLevelReward(){
        //起一条独立的线程去监听
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    levelRewardHanle();
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("comsumeLevelReward");
        thread.start();
    }


    public void levelRewardHanle(){
        String key = this.getLevelReward();

        List<String> list = redisCacheTemplate.brpop(0,key);
        String wxFanIdStr = list.get(1);

        Integer wxFanId = Integer.valueOf(wxFanIdStr);

        /*String wxPubOriginId = wxFan.getWxPubOriginId();
        String wxFanOpenId = wxFan.getWxFanOpenId();

        ChatPet chatPet = chatPetService.getChatPetByFans(wxPubOriginId, wxFanOpenId);*/
        ChatPet chatPet = chatPetService.getByWxFanId(wxFanId);

        //如果没有宠物，返回
        if(chatPet == null){
            return ;
        }

        Integer chatPetId = chatPet.getId();
        //如果奖励超过了指定个数，不再分发
        List<ChatPetRewardItem> rewardItemList = this.getByChatPetAndMissionId(chatPetId,NOT_AWARD,null);
        if(rewardItemList.size() >= MAX_NOT_AWARD_COUNT.intValue()){
            Log.d("more than max count , don't generate level reward");
            return ;
        }

        this.generateLevelReward(chatPetId);
    }


    /**
     * 给指定宠物生成等级奖励
     * @param chatPetId
     */
    private void generateLevelReward(Integer chatPetId){
        Float levelCoin = this.calculateLevelCoin(chatPetId);

        //定时等级奖励
        ChatPetRewardItem levelReward = new ChatPetRewardItem();

        Integer chatPetType = chatPetService.getChatPetType(chatPetId);

        levelReward.setChatPetType(chatPetType);
        levelReward.setGoldValue(levelCoin);
        levelReward.setRewardState(NOT_AWARD);
        levelReward.setChatPetId(chatPetId);
        levelReward.setCreateTime(new Date());

        this.save(levelReward);
    }


    private Float calculateLevelCoin(Integer chatPetId){


        ChatPet chatPet = chatPetService.getById(chatPetId);

        Float levelExperience = chatPet.getExperience() * 0.01F;

        return levelExperience;
    }

    /**
     * 根据宠物类型获取魔币总产值
     * @param chatPetType
     * @return
     */
    public Float getTotalGoldAmountByChatPetType(Integer chatPetType){
        return this.chatPetRewardItemMapper.countTotalGoldByChatPetType(chatPetType);
    }

    /**
     * 根据宠物类型获取魔币昨日产值
     * @param chatPetType
     * @return
     */
    public Float getYesterdayGoldAmountByChatPetType(Integer chatPetType){
        Date yesterday = DateUtils.getYesterday(new Date());

        Date beginDate = DateUtils.getBeginDate(yesterday);
        Date endDate = DateUtils.getEndDate(yesterday);

        return chatPetRewardItemMapper.countDayGoldByChatPetType(beginDate,endDate,chatPetType);
    }



    /**
     * 根据宠物类型获取昨日经验值总产值
     * @param chatPetType
     * @return
     */
    public Float getYesterExpAmountByChatPetType(Integer chatPetType){
        Date yesterday = CommonUtils.dateOffset(new Date(), -1);
        Date beginDate = CommonUtils.dateStartTime(yesterday);
        Date endDate = CommonUtils.dateEndTime(yesterday);
        return chatPetRewardItemMapper.countDayExpByChatPetType(beginDate,endDate,chatPetType);
    }

    /**
     * 让任务奖励过期
     */
    public void expireAward(){
        chatPetRewardItemMapper.updateMissionRewardState(NOT_AWARD , EXPIRED_AWARD);
    }

    /**
     * 根据宠物id、奖励状态和任务id获取奖励
     * @param chatPetId
     * @param rewardState
     * @param missionItemId
     * @return
     */
    public List<ChatPetRewardItem> getByChatPetAndMissionId(Integer chatPetId , Integer rewardState ,  Integer missionItemId){
        return chatPetRewardItemMapper.selectByChatPetAndMissionId(chatPetId, rewardState, missionItemId);
    }

    /**
     * 每隔两小时分发一次等级奖励
     */
    public void generateLevelRewardTask(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    List<String> list = redisCacheTemplate.brpop(0,LEVEL_REWARD_MESSAGE_KEY);
                    String msg = list.get(1);
                    Log.i("receive the message [?]",msg);
                    opLogService.systemOper(AppConstants.OP_LOG_TAG_S_GENERATE_LEVEL_REWARD, "生成等级奖励");
                    generateLevelReward();
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("generateLevelRewardTask");
        thread.start();
        Log.d("finished task");



    }

    /**
     *当日12点去掉过期的任务奖励
     */
    public void deleteMissionRewardTask(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    List<String> list = redisCacheTemplate.brpop(0,DELETE_EXPIRE_REWARD_MESSAGE_KEY);
                    String msg = list.get(1);
                    Log.i("receive the message [?]",msg);
                    opLogService.systemOper(AppConstants.OP_LOG_TAG_S_DELETE_MISSION_REWARD, "生成等级奖励");
                    expireAward();
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("deleteMissionRewardTask");
        thread.start();
        Log.d("finished task");

    }

    /**
     * 通过任务的id获取对应的奖励
     * @param missionId
     * @return
     */
    public ChatPetRewardItem getByMissionItemId(Integer missionId){
        return chatPetRewardItemMapper.selectByMissionItemId(missionId);
    }

    /**
     * 新注册用户生成5枚0.01的金币奖励
     * @param chatPetId
     */
    public void generateRegisterReward(Integer chatPetId){
        List<ChatPetRewardItem> rewarList = new ArrayList<>();

        for (int i = 0; i < BORN_REWARD_NUM; i++){
            ChatPetRewardItem registerReward = new ChatPetRewardItem();

            registerReward.setChatPetType(ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT);
            registerReward.setGoldValue(0.01F);

            registerReward.setChatPetId(chatPetId);
            registerReward.setRewardState(NOT_AWARD);

            registerReward.setCreateTime(new Date());
            rewarList.add(registerReward);
        }
        chatPetRewardItemMapper.batchInsert(rewarList);

    }


    /**
     * 检查奖励是否为可领取状态
     * @param chatPetRewardItemId
     * @return
     */
    public boolean checkRewardState(Integer chatPetRewardItemId){
        ChatPetRewardItem reward = this.getChatPetRewardItemById(chatPetRewardItemId);
        return NOT_AWARD.equals(reward.getRewardState());
    }

    /**
     * 检查奖励所有者
     * @param wxFanId
     * @param chatPetRewardItemId
     * @return
     */
    public boolean checkRewardOwner(Integer wxFanId,Integer chatPetRewardItemId){
        ChatPetRewardItem reward = this.getChatPetRewardItemById(chatPetRewardItemId);
        Integer chatPetId = reward.getChatPetId();
        ChatPet chatPet = chatPetService.getById(chatPetId);
        return wxFanId.equals(chatPet.getWxFanId());
    }
}
