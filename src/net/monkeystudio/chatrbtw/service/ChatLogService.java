package net.monkeystudio.chatrbtw.service;

import java.util.*;

import net.monkeystudio.admin.controller.req.QueryChatLogs;
import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.chatrbtw.service.bean.chatlog.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.monkeystudio.chatrbtw.entity.ChatLog;
import net.monkeystudio.chatrbtw.mapper.ChatLogMapper;

@Service
public class ChatLogService {

    @Autowired
    private ChatLogMapper chatLogMapper;



    /**
     * 记录接收到的聊天信息
     *
     * @param wxPubOriginId 公众号原始ID
     * @param userOpenid    公众号获取到的用户openid
     * @param content       内容
     * @return 记录ID
     */
    public Integer saveReceive(String wxPubOriginId, String userOpenid, String content) {

        ChatLog cl = new ChatLog();
        cl.setWxPubOriginId(wxPubOriginId);
        cl.setUserOpenid(userOpenid);
        cl.setContent(content);
        cl.setCreateTime(new Date());

        chatLogMapper.insert(cl);

        return cl.getId();
    }

    /**
     * 记录系统回复的聊天信息，replyId为被回复的信息的记录ID
     *
     * @param wxPubOriginId 公众号原始ID
     * @param userOpenid    公众号获取到的用户openid
     * @param content       内容
     * @param replyId       被回复的信息的记录ID
     * @param replySrc      回复来源
     * @return 记录ID
     */
    public Integer saveResponse(String wxPubOriginId, String userOpenid, String content, Integer replyId, String replySrc) {

        ChatLog cl = new ChatLog();
        cl.setWxPubOriginId(wxPubOriginId);
        cl.setUserOpenid(userOpenid);
        cl.setContent(content);
        cl.setCreateTime(new Date());
        cl.setReplyId(replyId);
        cl.setReplySrc(replySrc);

        chatLogMapper.insert(cl);

        return cl.getId();
    }

    public List<ChatLog> getByCreateTime(Long startTime, Long endTime) {
        Date startDate = new Date(startTime * 1000);
        Date endDate = new Date(endTime * 1000);

        return this.getByCreateTime(startDate, endDate);
    }

    private List<ChatLog> getByCreateTime(Date startTime, Date endTime) {
        return chatLogMapper.selectByCreateTime(startTime, endTime);
    }

    public Integer countByTime(Long startTime, Long endTime, String wxPubOpneId, String userOpenId) {

        Integer count = chatLogMapper.countByTime(startTime, endTime, wxPubOpneId, userOpenId);

        return count;
    }

    /**
     * 日志查询
     *
     * @param queryChatLogs
     * @return List<ChatLog>
     */
    public List<ChatLog> getLogs(QueryChatLogs queryChatLogs) {

        Map<String, Object> params = queryChatLogs.getMap();

        Integer startIndex = CommonUtils.page2startIndex(queryChatLogs.getPage(), queryChatLogs.getPageSize());
        params.put("startIndex", startIndex);

        return chatLogMapper.selectByPage(params);
    }


    public List<ChatLogListItem> getFuzzyChatLogList(Integer page, Integer pageSize, String wxPubOriginId) {
        List<ChatLog> chatLogList = this.getLogs(page, pageSize, wxPubOriginId);

        List<ChatLogListItem> chatLogListItemList = new ArrayList<>();
        for (ChatLog chatLog : chatLogList) {
            ChatLogListItem chatLogListItem = BeanUtils.copyBean(chatLog, ChatLogListItem.class);

            chatLogListItem.setWxFanOpenId(chatLog.getUserOpenid());

            String wxFanOpenId = chatLog.getUserOpenid();

            String wxFanOpenIdFuzzy = null;

            wxFanOpenIdFuzzy = wxFanOpenId.substring(0, 4) + "**********" + wxFanOpenId.substring(wxFanOpenId.length() - 4, wxFanOpenId.length());
            chatLogListItem.setWxFanOpenId(wxFanOpenIdFuzzy);

            if (chatLog.getReplyId() == null) {
                chatLogListItem.setType(1);
            } else {
                chatLogListItem.setType(2);
            }

            chatLogListItemList.add(chatLogListItem);
        }

        return chatLogListItemList;
    }

    /**
     * 日志总数
     *
     * @return
     */
    public Integer getCount() {

        return chatLogMapper.count();
    }


    /**
     * 根据公众号原始id获取聊天记录
     *
     * @param page
     * @param pageSize
     * @param wxPubOriginId
     * @return
     */
    public List<ChatLog> getLogs(Integer page, Integer pageSize, String wxPubOriginId) {

        Integer startIndex = CommonUtils.page2startIndex(page, pageSize);

        List<ChatLog> chatLogList = chatLogMapper.selectByWxPubOriginId(startIndex, pageSize, wxPubOriginId);

        return chatLogList;
    }

    /**
     * 根据公众号原始id统计聊天记录
     *
     * @param wxPubOriginId
     * @return
     */
    public Integer getCount(String wxPubOriginId) {

        return chatLogMapper.countByWxPubOriginId(wxPubOriginId);
    }


    /**
     * 获取指定公众号昨日聊天人数
     *
     * @return
     */
    public Long getTotalChatMan(String wxPubOriginId) {

        return chatLogMapper.getYstdTotalChatMan(wxPubOriginId);

    }


    /**
     * 获取指定公众号昨日聊天次数
     *
     * @return
     */
    public Long getTotalChatNum(String wxPubOriginId) {

        return chatLogMapper.getYstdTotalChatNum(wxPubOriginId);

    }


    //获取公众号昨天相比前天新增聊天人数
    private Long getBoostChatMan(String originId,Long yesterdayChatMan) {

        Long beforeYesterdayChatMan = chatLogMapper.get2DaysAgoTotalChatMan(originId);
        return yesterdayChatMan-beforeYesterdayChatMan;
    }

    //获取公众号昨天相比前天新增聊天次数
    private Long getBoostChatNum(String originId,Long yesterdayChatNum) {

        Long beforeYesterdayChatNum = chatLogMapper.get2DaysAgoTotalChatNum(originId);
        return yesterdayChatNum-beforeYesterdayChatNum;
    }

    //开始时间应该为当天0点,结束时间为当天最后一秒
    private void handleQoDate(ChatStatisticsChartQueryObject qo){
        qo.setBeginDate(DateUtils.getBeginDate(qo.getBeginDate()));
        qo.setEndDate(DateUtils.getEndDate(qo.getEndDate()));

    }

    /**
     * 聊天人数vo
     * @param qo
     * @param originId
     * @return
     */
    public YesterdayChatManVO getYesterdayChatManVO(ChatStatisticsChartQueryObject qo,String originId){
        List<Long> dates = this.getDates(qo);

        this.handleQoDate(qo);

        List<Map<String, Object>> list = chatLogMapper.totalChatManChartByDate(qo.getBeginDate(),qo.getEndDate(),originId);//key chatlogtime chatman

        List<ChatLogChartNode> nodes = new ArrayList<>();

        paramMapToNodeList(list,"chatlogtime","chatman",nodes);

        List<ChatLogChartNode> allNodes = createNodeListWithAllDates(nodes, dates);

        Long yesterdayChatMan =  this.getTotalChatMan(originId);

        Long boostChatMan = this.getBoostChatMan(originId, yesterdayChatMan);


        YesterdayChatManVO  vo = new YesterdayChatManVO();

        vo.setChatManNum(yesterdayChatMan);
        vo.setBoostManNum(boostChatMan);
        vo.setNodes(allNodes);

        return vo;
    }


    /**
     * 聊天次数vo
     * @param qo
     * @param originId
     * @return
     */
    public YesterdayChatNumVO getYesterdayChatNumVO(ChatStatisticsChartQueryObject qo,String originId){
        List<Long> dates = this.getDates(qo);

        this.handleQoDate(qo);

        List<Map<String, Object>> list = chatLogMapper.totalChatNumChartByDate(qo.getBeginDate(),qo.getEndDate(),originId);//key chatlogtime chatman

        List<ChatLogChartNode> nodes = new ArrayList<>();

        paramMapToNodeList(list,"chatlogtime","chatnum",nodes);

        List<ChatLogChartNode> allNodes = createNodeListWithAllDates(nodes, dates);

        Long yesterdayChatNum =  this.getTotalChatNum(originId);

        Long boostChatNum = this.getBoostChatNum(originId, yesterdayChatNum);


        YesterdayChatNumVO  vo = new YesterdayChatNumVO();

        vo.setChatNum(yesterdayChatNum);
        vo.setBoostNum(boostChatNum);
        vo.setNodes(allNodes);

        return vo;
    }


    // 没有聊天记录的日期并记为0
    private List<ChatLogChartNode> createNodeListWithAllDates(List<ChatLogChartNode> originNodes, List<Long> dates){
        //1 将originNodes转为map并得到keySet
        //2 判断date是否在keySet中,否则记0

        Map<Long,Long> nodeMap = new HashMap<>();//key为日期,value为次数
        for(ChatLogChartNode oldNode:originNodes){
            nodeMap.put(oldNode.getDate(),oldNode.getAmount());
        }
        Set<Long> keySet = nodeMap.keySet();

        List<ChatLogChartNode> nodesWithAllDates = new ArrayList<>();

        for (Long date:dates){
            ChatLogChartNode node = new ChatLogChartNode();
            node.setDate(date);
            if(!keySet.contains(date)){
                node.setAmount(0L);
            }else{
                node.setAmount(nodeMap.get(date));
            }
            nodesWithAllDates.add(node);
        }

        return nodesWithAllDates;
    }


    //从开始时间到结束时间每一天集合
    private List<Long> getDates(ChatStatisticsChartQueryObject qo){
        List<Long> dates = new ArrayList<>();
        Date now = DateUtils.getBeginDate(qo.getBeginDate());
        while(now.getTime()<=qo.getEndDate().getTime()){
            dates.add(now.getTime());
            Date d = new Date(now.getTime()+3600*24*1000);//第二天的时间
            now = d;
        }
        return dates;
    }

    //将查询数据组装为List<ChatLogChartNode>
    private void paramMapToNodeList(List<Map<String, Object>> list, String xKey,String yKey, List<ChatLogChartNode> nodes) {
        for(Map<String,Object> param:list){
            ChatLogChartNode node = new ChatLogChartNode();
            node.setDate(((Date)param.get(xKey)).getTime());
            node.setAmount((Long) param.get(yKey));
            nodes.add(node);
        }
    }

    public List<Map<String,Long>> totalChatManAndNum(ChatStatisticsChartQueryObject qo){

        handleQoDate(qo);

        List<Map<String, Object>> maps = chatLogMapper.totalChatManAndNumByDateFromAllWxPub(qo.getBeginDate(), qo.getEndDate());

        List<Map<String,Long>> dateToLong = new ArrayList<>();

        for(Map<String,Object> param:maps){
            Map<String,Long> newMap = new HashMap<>();
            newMap.put("createtime",((Date)param.get("createtime")).getTime());
            newMap.put("totalchatman",(Long)param.get("totalchatman"));
            newMap.put("totalchatnum",(Long) param.get("totalchatnum"));
            dateToLong.add(newMap);
        }

        return dateToLong;
    }









}
