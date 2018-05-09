package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.RandomUtil;
import net.monkeystudio.chatrbtw.entity.ChatPetAppearenceMaterial;
import net.monkeystudio.chatrbtw.entity.ChatPetAppearenceSite;
import net.monkeystudio.chatrbtw.entity.RChatPetAppearenceSiteColor;
import net.monkeystudio.chatrbtw.mapper.ChatPetAppearenceMaterialMapper;
import net.monkeystudio.chatrbtw.mapper.ChatPetAppearenceSiteMapper;
import net.monkeystudio.chatrbtw.mapper.ChatPetMapper;
import net.monkeystudio.chatrbtw.mapper.RChatPetAppearenceSiteColorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by bint on 2018/5/8.
 */
@Service
public class ChatPetAppearenceService {

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private ChatPetMapper chatPetMapper;

    @Autowired
    private ChatPetAppearenceMaterialMapper chatPetAppearenceMaterialMapper;

    @Autowired
    private ChatPetAppearenceSiteMapper chatPetAppearenceSiteMapper;

    @Autowired
    private RChatPetAppearenceSiteColorMapper rChatPetAppearenceSiteColorMapper;

    //外观池里面常驻的个数
    private final static Long CODE_PERMANENT_COUNT = 100L;
    //每次补充的个数
    private final static Integer SUPPLEMENTS_CONT_EACH_TIME = 5;

    @PostConstruct
    public void init(){
        Long count = this.countAppearenceCodeInPool();
        if(count.longValue() < CODE_PERMANENT_COUNT){
            Long change = CODE_PERMANENT_COUNT.longValue() - count.longValue();
            this.generateChatPetAppearence(change.intValue());
        }
    }

    /**
     * 从外观池取出
     * @return
     */
    public String getChatPetAppearenceCodeFromPool(){
        String key = this.getAppearenceCodePoolKey();

        String appearenceCode = redisCacheTemplate.lpop(key);

        Long count = this.countAppearenceCodeInPool();
        if(count.longValue() < CODE_PERMANENT_COUNT){
            this.generateChatPetAppearence(SUPPLEMENTS_CONT_EACH_TIME);
        }

        return appearenceCode;
    }

    private String getAppearenceCodePoolKey(){
        String key = RedisTypeConstants.KEY_LIST_TYPE_PREFIX + "chat-pet:appearence-code:pool";
        return key;
    }

    private List<String> getAllAppearenceCodeFromPool(){
        String key = this.getAppearenceCodePoolKey();

        return redisCacheTemplate.lrange(key, 0L, -1L);
    }


    private void generateChatPetAppearence(Integer count){
        while (count.intValue() > 0){
            this.generateChatPetAppearence();
            count--;
        }
    }

    /**
     * 生成一个宠物外观放入外观池
     */
    private void generateChatPetAppearence(){

        Boolean flag = false;
        String appearanceCode = null;
        while (!flag){
            appearanceCode = this.randomToalAppearence(ChatPetTypeService.CHAT_PET_TYPE_ZOMBIES_CAT);
            if(!this.appearenceIsExist(null,appearanceCode)){
                flag = true;
            }
        }
        this.pushAppearence(appearanceCode);
    }


    private void pushAppearence(String appearenceCode){
        String key = this.getAppearenceCodePoolKey();
        redisCacheTemplate.rpush(key, appearenceCode);
    }

    /**
     * 随机生成一个外观
     * @return
     */
    private String randomToalAppearence(Integer chatPetType ){

        List<ChatPetAppearenceSite> chatPetAppearenceSiteList = this.getChatPetAppearenceSite(chatPetType);

        StringBuilder stringBuilder = new StringBuilder();
        for(ChatPetAppearenceSite chatPetAppearenceSite : chatPetAppearenceSiteList){
            Integer site = chatPetAppearenceSite.getSite();
            String siteKey = this.randomSite(chatPetType,site);
            stringBuilder.append(siteKey);

            //颜色的处理
            List<RChatPetAppearenceSiteColor> siteColorList = this.getSiteColorBySite(chatPetType,site);
            if(siteColorList == null || siteColorList.size() == 0){
                stringBuilder.append(ChatPetColorService.NONE_COLOR_KEY);
            }else {
                RChatPetAppearenceSiteColor rChatPetAppearenceSiteColor = RandomUtil.randomPick(siteColorList);

                stringBuilder.append(rChatPetAppearenceSiteColor.getColorKey());
            }
        }
        return stringBuilder.toString();
    }



    /**
     * 随机生成一个部位的key
     * @param chatPetType
     * @param site
     * @return
     */
    private String randomSite(Integer chatPetType , Integer site){
        List<ChatPetAppearenceMaterial> list = this.getSiteMaterial(chatPetType,site);

        ChatPetAppearenceMaterial chatPetAppearenceMaterial = RandomUtil.randomPick(list);

        return chatPetAppearenceMaterial.getKey();
    }


    /**
     * 获取某个宠物由多少个部位组成
     * @param chatPetType
     * @return
     */
    private List<ChatPetAppearenceSite> getChatPetAppearenceSite(Integer chatPetType){
        return chatPetAppearenceSiteMapper.selectByType(chatPetType);
    }


    /**
     * 外貌是否已经存在
     * @param chatPetType 外观的类型
     * @param appearenceCode 外观的组成码
     * @return
     */
    private Boolean appearenceIsExist (Integer chatPetType ,String appearenceCode){

        Integer count = chatPetMapper.countByAppearceCode(appearenceCode);

        if(count.intValue() != 0){
            return true;
        }

        List<String> all = this.getAllAppearenceCodeFromPool();

        for(String string : all){
            if(appearenceCode.equals(string)){
                return true;
            }
        }

        return false;
    }

    private String getAppearenceKey(int num){
        if(num >= 0 && num <= 9){
            return String.valueOf(num);
        }

        if(num >= 10 && num <= 35){
            char c = (char)(num + 87);
            return String.valueOf(c);
        }

        return null;
    }

    /**
     * 获取某部位所有的素材
     * @param chatPetType
     * @param site
     * @return
     */
    private List<ChatPetAppearenceMaterial> getSiteMaterial(Integer chatPetType, Integer site){
        List<ChatPetAppearenceMaterial> list = chatPetAppearenceMaterialMapper.selectListBySite(chatPetType,site);
        return list;
    }

    /**
     * 获取某个部位对应的颜色
     * @param chatPetType
     * @param site
     * @return
     */
    private List<RChatPetAppearenceSiteColor> getSiteColorBySite(Integer chatPetType, Integer site){
        return rChatPetAppearenceSiteColorMapper.selectByAppearenceSite(chatPetType,site);
    }

    /**
     * 获得外观池的个数
     * @return
     */
    private Long countAppearenceCodeInPool(){

        String key = this.getAppearenceCodePoolKey();

        Long count = redisCacheTemplate.llen(key);

        return count;
    }
}
