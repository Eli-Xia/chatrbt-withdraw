package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.service.TaskExecutor;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.enums.chatpet.ChatPetTaskEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by bint on 2018/4/20.
 */
@Service
public class ChatPetMissionService {
    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ChatPetLogService chatPetLogService;

    private final static String MESSAGE_KEY = "chat_pet_mission";



    @PostConstruct
    private void initSubscribe(){
        //起一条独立的线程去监听
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                /*JedisPubSub jedisPubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {

                        Log.d("receive channel[?] , the message [?]",channel,message);
                        String str[] = message.split(":");
                        Integer wxFanId = Integer.valueOf(str[0]);
                        String wxFanOpenId = str[1];
                        Integer adId = Integer.valueOf(str[2]);

                        if(str.length != 3){
                            Log.d("chatpet mission message errror." + str);
                            return ;
                        }

                        if(validatedWxFan(wxFanId,wxFanOpenId)){
                            WxFan wxFan = wxFanService.getById(wxFanId);

                            chatPetLogService.completeChatPetDailyTask(wxFan.getWxPubOriginId(),wxFanOpenId, ChatPetTaskEnum.DAILY_READ_NEWS);
                        }
                    }
                };

                redisCacheTemplate.subscribe(jedisPubSub,SUBSCRIBE_CHANNEL);*/


                while(true){
                    List<String> list = redisCacheTemplate.brpop(0,MESSAGE_KEY);
                    String string = list.get(1);
                    Log.d("receive the message [?]",string);
                    String str[] = string.split(":");
                    Integer wxFanId = Integer.valueOf(str[0]);
                    String wxFanOpenId = str[1];
                    Integer adId = Integer.valueOf(str[2]);

                    if(str.length != 3){
                        Log.d("chatpet mission message errror." + str);
                        return ;
                    }

                    if(validatedWxFan(wxFanId,wxFanOpenId)){
                        WxFan wxFan = wxFanService.getById(wxFanId);

                        chatPetLogService.completeChatPetDailyTask(wxFan.getWxPubOriginId(),wxFanOpenId, ChatPetTaskEnum.DAILY_READ_NEWS);
                    }


                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        Log.d("finished Subscribe");
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
