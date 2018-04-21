package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.WxFan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.PostConstruct;

/**
 * Created by bint on 2018/4/20.
 */
@Service
public class ChatPetMissionService {
    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private WxFanService wxFanService;

    private final static String SUBSCRIBE_CHANNEL = "chat_pet_mission";

    @PostConstruct
    private void initSubscribe(){

        JedisPubSub jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {

                String str[] = message.split(":");
                Integer wxFanId = Integer.valueOf(str[0]);
                String wxFanOpenId = str[1];
                Integer adId = Integer.valueOf(str[2]);

                if(str.length != 3){
                    Log.d("chatpet mission message errror." + str);
                    return ;
                }

                if(validatedWxFan(wxFanId,wxFanOpenId)){




                }

            }
        };

        redisCacheTemplate.subscribe(jedisPubSub,SUBSCRIBE_CHANNEL);
    }



    private Boolean validatedWxFan(Integer wxFanId ,String wxFanOpenId){
        WxFan wxFan = wxFanService.getById(wxFanId);

        if(wxFan == null){
            return false;
        }

        if(wxFanOpenId == null){
            return false;
        }

        String wxFanOpenIdFromDb = wxFan.getWxFanOpenId();

        if(wxFanOpenId.equals(wxFanOpenIdFromDb)){
            return true;
        }

        return false;
    }


}
