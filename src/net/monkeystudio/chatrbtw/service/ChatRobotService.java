package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.entity.ChatRobotBaseInfo;
import net.monkeystudio.chatrbtw.entity.RRobotCharacter;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.mapper.ChatRobotBaseInfoMapper;
import net.monkeystudio.chatrbtw.mapper.RRobotCharacterMapper;
import net.monkeystudio.chatrbtw.service.bean.chatrobot.UpdateChatRobot;
import net.monkeystudio.chatrbtw.service.bean.chatrobot.resp.ChatRobotInfoResp;
import net.monkeystudio.wx.service.WxPubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bint on 29/12/2017.
 */
@Service
public class ChatRobotService {

    @Autowired
    private ChatRobotBaseInfoMapper chatRobotBaseInfoMapper;

    @Autowired
    private RRobotCharacterMapper rRobotCharacterMapper;

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private OpLogService opLogService;


    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    //删除无效机器人定时任务队列key
    private final static String DELETE_ROBOT_MESSAGE_KEY = "delete_robot_task";

    //机器人性别
    private final Integer CHAT_BOT_GENDER_EMPTY = 0;
    private final Integer CHAT_BOT_GENDER_MELE = 1;
    private final Integer CHAT_BOT_GENDER_FEMELE = 2;




    /**
     * 保存
     * @param chatRobotBaseInfo
     * @return 新增的ChatRobotBaseInfo的Id
     */
    public Integer saveChatRobotBaseInfo(ChatRobotBaseInfo chatRobotBaseInfo ){
        chatRobotBaseInfoMapper.insert(chatRobotBaseInfo);
        return chatRobotBaseInfo.getId();
    }

    private ChatRobotBaseInfo getByWxPubOriginId(String wxPubOriginId){
        return chatRobotBaseInfoMapper.selectByWxPubOriginId(wxPubOriginId);
    }

    private ChatRobotBaseInfo getChatRobotBaseInfoById(Integer id){
        return chatRobotBaseInfoMapper.selectById(id);
    }

    /**
     * 保存聊天机器人和性格对应的关系
     * @param rRobotCharacter
     * @return
     */
    public Integer saveRRobotCharacter(RRobotCharacter rRobotCharacter){
        return rRobotCharacterMapper.insert(rRobotCharacter);
    }

    private Integer updateChatRobotBaseInfo(ChatRobotBaseInfo chatRobotBaseInfo){
        return chatRobotBaseInfoMapper.update(chatRobotBaseInfo);
    }

    private List<RRobotCharacter> getRRobotCharacterList(Integer chatRobotId){
        return rRobotCharacterMapper.selectByChatRobotId(chatRobotId);
    }

    /**
     * 删除机器人基本信息
     * @param wxPubOriginId
     * @return
     */
    private Integer deleteChatRobotBaseInfo(String wxPubOriginId){
        Integer result = chatRobotBaseInfoMapper.deleteByWxPubOriginId(wxPubOriginId);
        return result;
    }

    /**
     * 指定用户知否有该聊天机器人
     * @param chatRobotBaseInfoId
     * @param userId
     * @return
     */
    public Boolean hasChatRobot(Integer chatRobotBaseInfoId , Integer userId){

        ChatRobotBaseInfo chatRobotBaseInfo = this.getChatRobotBaseInfoById(chatRobotBaseInfoId);

        String wxPubOriginId = chatRobotBaseInfo.getWxPubOriginId();

        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);

        if(wxPub == null){
            return false;
        }

        if(wxPub.getUserId().intValue() == userId){
            return true;
        }

        return false;
    }

    @Transactional
    public Integer deleteRobotByWxPubOriginId(String wxPubOriginId){

        //得到即将要删除的robotId
        ChatRobotBaseInfo chatRobotBaseInfo = this.getByWxPubOriginId(wxPubOriginId);

        if(chatRobotBaseInfo == null){
            return null;
        }

        Integer robotId = chatRobotBaseInfo.getId();

        //删除机器人基本信息
        Integer result = this.deleteChatRobotBaseInfo(wxPubOriginId);

        //删除机器人和性格的对应关系
        this.deleteRRobotCharacter(robotId);

        return result;

    }


    private Integer deleteRRobotCharacter(Integer chatRobotId){
        return rRobotCharacterMapper.deleteByChatRobotId(chatRobotId);
    }

    /**
     *
     * 修改聊天机器人信息
     * @param updateChatRobot
     */
    @Transactional
    public void updateChatRobot( UpdateChatRobot updateChatRobot){

        Integer chatRobotId = updateChatRobot.getId();

        ChatRobotBaseInfo chatRobotBaseInfo = BeanUtils.copyBean(updateChatRobot,ChatRobotBaseInfo.class);

        ChatRobotBaseInfo chatRobotBaseInfoFromDb = this.getChatRobotBaseInfoById(chatRobotId);

        chatRobotBaseInfo.setWxPubOriginId(chatRobotBaseInfoFromDb.getWxPubOriginId());

        this.updateChatRobotBaseInfo(chatRobotBaseInfo);

        List<Integer> characterIdList = updateChatRobot.getCharacterList();

        this.deleteRRobotCharacter(chatRobotId);

        for(Integer characterId : characterIdList){
            RRobotCharacter rRobotCharacter = new RRobotCharacter();
            rRobotCharacter.setChatRobotCharacterId(characterId);
            rRobotCharacter.setChatRobotId(chatRobotId);
            this.saveRRobotCharacter(rRobotCharacter);
        }
    }

    /**
     * 获取公众号对应的机器人
     * @param wxPubOriginId
     * @return
     */
    public ChatRobotInfoResp getChatRobotInfoRespByWxPubOriginId(String wxPubOriginId){
        ChatRobotBaseInfo chatRobotBaseInfo = this.getByWxPubOriginId(wxPubOriginId);

        if(chatRobotBaseInfo == null){
            return null;
        }

        Integer chatRobotId = chatRobotBaseInfo.getId();

        List<RRobotCharacter> rRobotCharacterLis = this.getRRobotCharacterList(chatRobotId);

        ChatRobotInfoResp chatRobotInfoResp = new ChatRobotInfoResp();

        chatRobotInfoResp.setNickname(chatRobotBaseInfo.getNickname());
        chatRobotInfoResp.setGender(chatRobotBaseInfo.getGender());
        chatRobotInfoResp.setId(chatRobotBaseInfo.getId());

        List<Integer> rRobotCharacterIdList = new ArrayList<>();

        for(RRobotCharacter rRobotCharacter : rRobotCharacterLis){
            rRobotCharacterIdList.add(rRobotCharacter.getChatRobotCharacterId());
        }

        chatRobotInfoResp.setCharacterList(rRobotCharacterIdList);

        return chatRobotInfoResp;
    }

    /**
     * 删除无用的机器人
     * @return
     */
    public Integer deleteInvalidRobot(){
        List<ChatRobotBaseInfo> list = chatRobotBaseInfoMapper.selectListByWxPubOriginId(null);

        for(ChatRobotBaseInfo chatRobotBaseInfo : list){
            Integer chatRobotBaseInfoid = chatRobotBaseInfo.getId();
            this.deleteRobot(chatRobotBaseInfoid);
        }

        return list.size();
    }


    /**
     * 删除聊天机器人
     * @param chatRobotBaseInfoId
     * @return
     */
    @Transactional
    public void deleteRobot(Integer chatRobotBaseInfoId ){
        //删除机器人基本信息
        chatRobotBaseInfoMapper.deleteById(chatRobotBaseInfoId);
        //删除机器人和性格的对应关系
        this.deleteRRobotCharacter(chatRobotBaseInfoId);
    }

    /**
     * 指派聊天机器人给微信公众号
     * @param chatRobotId
     * @param wxPubOriginId
     * @return
     */
    public Integer designateChatRobot(Integer chatRobotId ,String wxPubOriginId){

        ChatRobotBaseInfo chatRobotBaseInfo = this.getChatRobotBaseInfoById(chatRobotId);

        if(chatRobotBaseInfo == null){
            return null;
        }

        chatRobotBaseInfo.setWxPubOriginId(wxPubOriginId);

        return this.updateChatRobotBaseInfo(chatRobotBaseInfo);
    }

    /**
     * 定时器任务:
     * 删除无效机器人
     */
    public void deleteRobotInfoTask(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    List<String> list = redisCacheTemplate.brpop(0,DELETE_ROBOT_MESSAGE_KEY);
                    String msg = list.get(1);
                    Log.i("receive the message [?]",msg);
                    opLogService.systemOper(AppConstants.OP_LOG_TAG_S_DELETE_INVALID_ROBOT, "删除无效机器人任务启动");
                    deleteInvalidRobot();
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("deleteRobotInfoTask");
        thread.start();
        Log.d("finished task");
    }
}
