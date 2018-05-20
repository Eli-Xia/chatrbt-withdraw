package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.chatrbtw.entity.PushMessageConfig;
import net.monkeystudio.chatrbtw.mapper.PushMessageConfigMapper;
import net.monkeystudio.chatrbtw.service.bean.ad.AdConfigReq;
import net.monkeystudio.chatrbtw.service.bean.ad.AdConfigResp;
import net.monkeystudio.chatrbtw.service.bean.ad.AdProbabilityStrategyConfigReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Created by bint on 2017/11/13.
 */
@Service
public class PushMessageConfigService {

    public final static String PUSH_RECOVER_CONTENT_KEY = "PUSH_RECOVER_CONTENT";
    //决定是否需要推送的开关
    public final static String PUSH_RECOVER_MESSAGE_SWITCH_KEY = "PUSH_RECOVER_MESSAGE_SWITCH";

    //推送广告的比率
    public final static String PUSH_AD_RATIO_KEY = "PUSH_AD_RATIO";
    //推送广告的id
    public final static String PUSH_AD_ID_KEY = "PUSH_AD_ID";
    //推送聊天广告的开关
    public final static String PUSH_AD_SWITCH_KEY = "PUSH_AD_SWITCH";
    //聊天过程到达的次数
    public final static String CHAT_PUSH_AD_COUNT_KEY = "CHAT_PUSH_AD_COUNT";

    //概率触发:每次聊天触发广告的概率
    public final static String PROBABILITY_STRATEGY_PUSH_AD_RATIO_KEY = "PROBABILITY_STRATEGY_PUSH_AD_RATIO";

    //概率触发:推送聊天广告的开关
    public final static String PROBABILITY_STRATEGY_PUSH_AD_SWITCH_KEY = "PROBABILITY_STRATEGY_PUSH_AD_SWITCH";

    private final static String PUSH_MESSAGE_CONFIG_KEY = RedisTypeConstants.KEY_HASH_TYPE_PREFIX + "pushMessageConfig";

    private final static Integer PUSH_MESSAGE_CONFIG_SURVIVAL_PERIOD = 60 * 30;

    @Autowired
    private PushMessageConfigMapper pushMessageConfigMapper;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @PostConstruct
    private void init(){
        List<PushMessageConfig> allConfig = this.getAllConfigFromDb();

        for(PushMessageConfig pushMessageConfigItem : allConfig){
            redisCacheTemplate.hsetObject(PUSH_MESSAGE_CONFIG_KEY,pushMessageConfigItem.getKey(),pushMessageConfigItem);
        }

        redisCacheTemplate.expire(PUSH_MESSAGE_CONFIG_KEY, PUSH_MESSAGE_CONFIG_SURVIVAL_PERIOD);
    }

    public String getByKey(String key){

        PushMessageConfig pushMessageConfig = redisCacheTemplate.hgetObject(PUSH_MESSAGE_CONFIG_KEY,key);

        if(pushMessageConfig != null){
            return pushMessageConfig.getValue();
        }

        this.init();

        pushMessageConfig = redisCacheTemplate.hgetObject(PUSH_MESSAGE_CONFIG_KEY,key);

        return pushMessageConfig.getValue();
    }


    private List<PushMessageConfig> getAllConfigFromDb(){

        return pushMessageConfigMapper.selectAll();
    }

    /**
     * 获取广告相关的配置信息
     * @return
     */
    public AdConfigResp getAdConfig(){

        List<PushMessageConfig> allConfig = this.getAllConfigFromDb();

        AdConfigResp adConfigResp = new AdConfigResp();

        for(PushMessageConfig pushMessageConfig : allConfig){

            if(PUSH_AD_RATIO_KEY.equals(pushMessageConfig.getKey())){
                adConfigResp.setPushAdRatio(Float.valueOf(pushMessageConfig.getValue()));
            }

            if(PUSH_AD_ID_KEY.equals(pushMessageConfig.getKey())){
                adConfigResp.setPushAdId(Integer.valueOf(pushMessageConfig.getValue()));
            }

            if(PUSH_AD_SWITCH_KEY.equals(pushMessageConfig.getKey())){
                adConfigResp.setPushAdSwitch(Integer.valueOf(pushMessageConfig.getValue()));
            }

            if(CHAT_PUSH_AD_COUNT_KEY.equals(pushMessageConfig.getKey())){
                adConfigResp.setChatPushAdCount(Integer.valueOf(pushMessageConfig.getValue()));
            }

            if(PROBABILITY_STRATEGY_PUSH_AD_RATIO_KEY.equals(pushMessageConfig.getKey())){
                adConfigResp.setProbabilityStrategyPushAdRatio(Float.valueOf(pushMessageConfig.getValue()));
            }

            if(PROBABILITY_STRATEGY_PUSH_AD_SWITCH_KEY.equals(pushMessageConfig.getKey())){
                adConfigResp.setProbabilityStrategyPushAdSwitch(Integer.valueOf(pushMessageConfig.getValue()));
            }
        }

        return adConfigResp;
    }

    /**
     * 更新广告的配置信息
     * @param adConfigReq
     * @return
     */
    public void updateAdConfig(AdConfigReq adConfigReq){

        Integer chatPushAdCount = adConfigReq.getChatPushAdCount();
        this.updateItem(CHAT_PUSH_AD_COUNT_KEY, chatPushAdCount);

        Float pushAdRatios = adConfigReq.getPushAdRatio();
        this.updateItem(PUSH_AD_RATIO_KEY, pushAdRatios);

        Integer adId = adConfigReq.getPushAdId();
        this.updateItem(PUSH_AD_ID_KEY, adId);

        Integer pushAdSwitch = adConfigReq.getPushAdSwitch();
        this.updateItem(PUSH_AD_SWITCH_KEY, pushAdSwitch);

        this.init();
    }

    /**
     * 更新广告配置-概率触发
     * @param adProbabilityStrategyConfigReq
     */
    public void updateAdProbabilityStrategyConfig(AdProbabilityStrategyConfigReq adProbabilityStrategyConfigReq){
        Integer probabilityStrategyPushAdSwitch = adProbabilityStrategyConfigReq.getProbabilityStrategyPushAdSwitch();
        this.updateItem(PROBABILITY_STRATEGY_PUSH_AD_SWITCH_KEY,probabilityStrategyPushAdSwitch);

        Float probabilityStrategyPushAdRatio = adProbabilityStrategyConfigReq.getProbabilityStrategyPushAdRatio();
        this.updateItem(PROBABILITY_STRATEGY_PUSH_AD_RATIO_KEY,probabilityStrategyPushAdRatio);

        this.init();
    }



    private Integer updateItem(String key ,Float value){
        String valueStr = String.valueOf(value);
        return this.updateItem(key, valueStr);
    }

    private Integer updateItem(String key ,Integer value){

        String valueStr = String.valueOf(value);
        return this.updateItem(key, valueStr);
    }

    private Integer updateItem(String key ,String value){
        return pushMessageConfigMapper.updateItem(key, value);
    }
}
