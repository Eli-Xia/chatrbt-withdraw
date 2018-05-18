package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.service.TaskExecutor;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.ChatPetMission;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.enums.chatpet.ChatPetTaskEnum;
import net.monkeystudio.chatrbtw.mapper.ChatPetMissionMapper;
import net.monkeystudio.chatrbtw.mapper.ChatPetPersonalMissionMapper;
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

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private ChatPetMissionMapper chatPetMissionMapper;


    private final static String MESSAGE_KEY = "chat_pet_mission";



    @PostConstruct
    private void initSubscribe(){
        //起一条独立的线程去监听
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
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

                        chatPetMissionPoolService.completeDailyReadMission(wxFanId,adId);
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

    /**
     * 后台新增任务类型
     * @param chatPetMission
     */
    public void save(ChatPetMission chatPetMission){
        ChatPetMission cpm = new ChatPetMission();

        cpm.setCoin(chatPetMission.getCoin());
        cpm.setIsActive(chatPetMission.getIsActive());
        cpm.setMissionCode(chatPetMission.getMissionCode());
        cpm.setMissionName(chatPetMission.getMissionName());

        chatPetMissionMapper.insert(cpm);
    }

    /**
     * 更新任务类型
     */
    public void update(ChatPetMission chatPetMission){
        chatPetMissionMapper.updateByPrimaryKey(chatPetMission);
    }

    /**
     * 根据任务code获取任务对象
     * @param missionCode
     * @return
     */
    public ChatPetMission getByMissionCode(Integer missionCode){
        return chatPetMissionMapper.selectByMissionCode(missionCode);
    }

    public ChatPetMission getById(Integer id){
        return chatPetMissionMapper.selectByPrimaryKey(id);
    }

    public List<ChatPetMission> getActiveMissions(){
        return chatPetMissionMapper.selectActiveMissionList();
    }




}
