package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RandomUtil;
import net.monkeystudio.chatrbtw.entity.ChatPetAppearenceMaterial;
import net.monkeystudio.chatrbtw.entity.ChatPetAppearenceSite;
import net.monkeystudio.chatrbtw.entity.RChatPetAppearenceSiteColor;
import net.monkeystudio.chatrbtw.mapper.ChatPetAppearenceMaterialMapper;
import net.monkeystudio.chatrbtw.mapper.ChatPetAppearenceSiteMapper;
import net.monkeystudio.chatrbtw.mapper.ChatPetMapper;
import net.monkeystudio.chatrbtw.mapper.RChatPetAppearenceSiteColorMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpetappearence.ZombiesCatAppearance;
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

    @Autowired
    private CfgService cfgService;

    //外观池里面常驻的个数
    private final static Long CODE_PERMANENT_COUNT = 100L;
    //每次补充的个数
    private final static Integer SUPPLEMENTS_CONT_EACH_TIME = 5;

    @PostConstruct
    public void init(){

        Long zombiesCatCount = this.countAppearenceCodeInPool(ChatPetTypeService.CHAT_PET_TYPE_ZOMBIES_CAT);
        if(zombiesCatCount.longValue() < CODE_PERMANENT_COUNT){
            Long change = CODE_PERMANENT_COUNT.longValue() - zombiesCatCount.longValue();
            this.generateChatPetAppearence(ChatPetTypeService.CHAT_PET_TYPE_ZOMBIES_CAT ,change.intValue());
        }

        Long luckyCatCount = this.countAppearenceCodeInPool(ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT);
        if(luckyCatCount.longValue() < CODE_PERMANENT_COUNT){
            Long change = CODE_PERMANENT_COUNT.longValue() - luckyCatCount.longValue();
            this.generateChatPetAppearence(ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT ,change.intValue());
        }
    }


    /**
     *
     * @param chatPetType
     * @return
     */
    public String getAppearanceCodeFromPool(Integer chatPetType){
        String key = this.getAppearenceCodePoolKey(chatPetType);

        if(key == null){
            return null;
        }

        String appearenceCode = redisCacheTemplate.lpop(key);

        Long count = this.countAppearenceCodeInPool(chatPetType);
        if(count.longValue() < CODE_PERMANENT_COUNT){
            this.generateChatPetAppearence(chatPetType,SUPPLEMENTS_CONT_EACH_TIME);
        }

        return appearenceCode;
    }

    /**
     * 从外观池取出
     * @return
     */
    /*public String getZombiesCatAppearanceCodeFromPool(){
        String key = this.getAppearenceCodePoolKey();

        String appearenceCode = redisCacheTemplate.lpop(key);

        Long count = this.countAppearenceCodeInPool();
        if(count.longValue() < CODE_PERMANENT_COUNT){
            this.generateChatPetAppearence(SUPPLEMENTS_CONT_EACH_TIME);
        }

        return appearenceCode;
    }*/

    private String getAppearenceCodePoolKey(Integer chatPetType){

        if(chatPetType.intValue() == ChatPetTypeService.CHAT_PET_TYPE_ZOMBIES_CAT){
            return RedisTypeConstants.KEY_LIST_TYPE_PREFIX + "chat-pet:zombies-cat-appearence-code:pool";
        }

        if(chatPetType.intValue() == ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT){
            return RedisTypeConstants.KEY_LIST_TYPE_PREFIX + "chat-pet:lucky-cat-appearence-code:pool";
        }

        Log.e("the chatPetType is not supported ,chatPetType :" + chatPetType);
        return null;
    }

    private List<String> getAllAppearenceCodeFromPool(Integer chatPetType){

        String key = this.getAppearenceCodePoolKey(chatPetType);

        return redisCacheTemplate.lrange(key, 0L, -1L);
    }

    /**
     * 批量生成外观
     * @param chatPetType
     * @param count
     */
    private void generateChatPetAppearence(Integer chatPetType,Integer count){
        while (count.intValue() > 0){
            this.generateAChatPetAppearence(chatPetType);
            count--;
        }
    }

    /**
     * 生成一个宠物外观放入外观池
     */
    private void generateAChatPetAppearence(Integer chatPetType){

        Boolean flag = false;
        String appearanceCode = null;
        while (!flag){
            appearanceCode = this.randomToalAppearence(chatPetType);
            if(!this.appearenceIsExist(chatPetType,appearanceCode)){
                flag = true;
            }
        }
        this.pushAppearence(chatPetType , appearanceCode);
    }


    private void pushAppearence(Integer chatPetType, String appearenceCode){
        String key = this.getAppearenceCodePoolKey(chatPetType);
        redisCacheTemplate.rpush(key, appearenceCode);
    }



    /**
     * 随机生成一个外观
     * @param chatPetType 宠物类型
     * @return
     */
    private String randomToalAppearence(Integer chatPetType){

        List<ChatPetAppearenceSite> chatPetAppearenceSiteList = this.getChatPetAppearenceSite(chatPetType);

        StringBuilder stringBuilder = new StringBuilder();
        for(ChatPetAppearenceSite chatPetAppearenceSite : chatPetAppearenceSiteList){
            Integer site = chatPetAppearenceSite.getSite();
            String siteKey = this.randomSite(chatPetType,site);
            stringBuilder.append(siteKey);
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

        Integer count = chatPetMapper.countByAppearceCode(chatPetType, appearenceCode);

        if(count.intValue() != 0){
            return true;
        }

        List<String> all = this.getAllAppearenceCodeFromPool(chatPetType);

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
    private Long countAppearenceCodeInPool(Integer chatPetType){

        String key = this.getAppearenceCodePoolKey(chatPetType);

        Long count = redisCacheTemplate.llen(key);

        return count;
    }

    /**
     * 生成魔鬼猫外观
     * @param code
     * @return
     */
    /*public ZombiesCatAppearance getZombiesCatAppearence(String code){
        ZombiesCatAppearance zombiesCatAppearance = this.getAppearance(code, ZombiesCatAppearance.class);
        return zombiesCatAppearance;
    }*/


    /*private<T> T getAppearance(String code ,Class<T> clazz){
            ZombiesCatAppearance zombiesCatAppearance = this.getAppearance(code, clazz);
            return (T) zombiesCatAppearance;
        }

        if(chatPetType.intValue() == ChatPetTypeService.CHAT_PET_TYPE_LUCKY_CAT.intValue()){

        }
    }*/

    public  <T> T getAppearance(String code ,Class<T> clazz){
        T newInstance = null;
        try {
            newInstance = (T)clazz.newInstance();
        } catch (InstantiationException e) {
            Log.e(e);
        } catch (IllegalAccessException e) {
            Log.e(e);
        }

        Field[] fields = clazz.getDeclaredFields();

        for(int i=0;i<fields.length;i++){
            Field field = fields[i];

            net.monkeystudio.chatrbtw.annotation.chatpet.ChatPetAppearanceCodeSite chatPetAppearanceCodeSite = field.getAnnotation(net.monkeystudio.chatrbtw.annotation.chatpet.ChatPetAppearanceCodeSite.class);
            int[] siteArray = chatPetAppearanceCodeSite.value();

            String key = "";
            for(int j=0;j<siteArray.length;j++){
                Integer site = siteArray[j];
                key = key + code.substring(site - 1, site);
            }

            BeanUtils.setFieldValue(field ,newInstance ,key);
        }

        return newInstance;
    }


    /**
     * 以太猫形象文件url转换
     * @param source
     * @return
     */
    public String convertCryptoKittiesUrl(String source){

        String fileName = source.substring(source.lastIndexOf("/"));

        String demain = cfgService.get(GlobalConfigConstants.CHAT_PET_WEB_DOMAIN_KEY);
        return "https:" + demain + "/" + "static/crypto-kitty/" + fileName;
    }
}
