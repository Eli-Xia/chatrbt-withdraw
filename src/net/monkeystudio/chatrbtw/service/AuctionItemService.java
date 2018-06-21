package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.service.FixedScheduling;
import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.AuctionItem;
import net.monkeystudio.chatrbtw.entity.AuctionRecord;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.mapper.AuctionItemMapper;
import net.monkeystudio.chatrbtw.service.bean.auctionitem.*;
import net.monkeystudio.chatrbtw.service.bean.chatpetautionitem.AdminAuctionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by bint on 2018/6/11.
 */
@Service
public class AuctionItemService {

    @Autowired
    private AuctionItemMapper auctionItemMapper;

    @Autowired
    private FixedScheduling fixedScheduling;

    @Autowired
    private AuctionRecordService auctionRecordService;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private ChatPetService chatPetService;

    //竞拍品的状态
    public final static Integer HAS_NOT_STARTED = 0; //未开始
    public final static Integer PROCESSING = 1;      //进行中
    public final static Integer HAVE_FINISHED = 2;   //已完成
    private final static Integer UNCLAIMED = 3;       //流拍

    //发货状态
    public final static Integer HAS_NOT_SHIP = 0;//未发货
    public final static Integer HAS_SHIP = 1;   //已经发货

    private final static String DIRCTORY_NAME = "/chat_pet/auction_item";


    private Map<Integer,Thread> threadMap = new HashMap<>();


    @PostConstruct
    private void init(){

        Date nowDate = new Date();
        List<AuctionItem> auctionItemList = this.getByEndTime(nowDate);

        if(ListUtil.isEmpty(auctionItemList)){
            return ;
        }

        for(AuctionItem auctionItem : auctionItemList){

            this.setFixedScheduling(auctionItem);
        }

    }

    /**
     * 设定竞拍品生成对应的定时任务，并放入线程map
     * @param auctionItem
     */
    private void setFixedScheduling(AuctionItem auctionItem ){
        Runnable runnable = this.createRunnable(auctionItem);


        Integer state = auctionItem.getState();

        Date date = null;
        if(state.intValue() == HAS_NOT_STARTED.intValue()){
            date = auctionItem.getStartTime();
        }

        if(state.intValue() ==  PROCESSING.intValue()){
            date = auctionItem.getEndTime();
        }

        Thread thread = fixedScheduling.createFixedScheduling(date ,runnable);

        //放入到线程map里面
        if(thread != null){
            threadMap.put(auctionItem.getId(),thread);
        }

    }

    /**
     * 通过id获取竞拍品
     * @param id
     * @return
     */
    public AuctionItem getById(Integer id){
        return auctionItemMapper.selectById(id);
    }


    private Runnable createRunnable(AuctionItem auctionItem ){

        Integer auctionItemId = auctionItem.getId();

        Runnable runnable = null;
        if(auctionItem.getState().intValue() == PROCESSING.intValue()){
            runnable = new Runnable() {

                @Override
                public void run() {
                    processing2haveFinished(auctionItemId);
                }
            };
            return runnable;
        }


        //如果本来是未开始的，时间到了，修改状态为进行中
        if(auctionItem.getState().intValue() == HAS_NOT_STARTED.intValue()){
            runnable = new Runnable() {
                @Override
                public void run() {

                    Log.i("It is time to perform translate status to PROCESSING from HAS_NOT_STARTED" );

                    Integer result = translateStatus(auctionItemId , HAS_NOT_STARTED , PROCESSING , null);

                    //如果已经处理，则返回
                    if(result == null || result.intValue() == 0){
                        return ;
                    }

                    AuctionItem auctionItemFrom = getById(auctionItemId);
                    //设定一个线程定时任务
                    setFixedScheduling(auctionItemFrom);

                    //test(auctionItemId);
                }
            };

            return runnable;
        }


        return null;
    }

    /**
     * 把竞拍品从进行中状态专程已经结束状态
     * @param auctionItemId
     */
    public void processing2haveFinished(Integer auctionItemId){
        Log.i("It is time to run perform fixed tasks , auctionItemId :" + auctionItemId);

        List<AuctionRecord> maxPriceAuctionItemList = auctionRecordService.getMaxPriceAuctionItemList(auctionItemId);

        //如果没人竞价,设置竞拍品状态为无人竞拍
        if(ListUtil.isEmpty(maxPriceAuctionItemList)){
            Integer result = translateStatus(auctionItemId , PROCESSING , UNCLAIMED , null);
            return ;
        }

        Boolean hasOwner = false;
        for(AuctionRecord maxPriceAuctionItem : maxPriceAuctionItemList){

            Integer wxFanId = maxPriceAuctionItem.getWxFanId();
            ChatPet chatPet = chatPetService.getChatPetByWxFanId(wxFanId);

            //如果所拥有的钱,比出价高
            Float priceFloat = maxPriceAuctionItem.getPrice();
            if(priceFloat.floatValue() <= chatPet.getCoin().floatValue()){

                //修改状态为已经结束和填充获得人
                Integer result = translateStatus(auctionItemId , PROCESSING , HAVE_FINISHED , wxFanId);

                //如果已经处理，则返回
                if(result == null || result.intValue() == 0){
                    return ;
                }

                //减去所得者的金币
                Integer chatPetId = chatPet.getId();
                chatPetService.decreaseCoin(chatPetId , priceFloat);
                hasOwner = true;
                break;
            }
        }

        //线程从map里面删除
        threadMap.remove(auctionItemId);

        //如果所有出价的人都没有对应的金币,则流拍
        if(!hasOwner){
            Integer result = translateStatus(auctionItemId , PROCESSING , UNCLAIMED , null);
            return ;
        }
    }

    /*public void test(Integer auctionItemId){
        Log.i("It is time to perform translate status to PROCESSING from HAS_NOT_STARTED" );

        Integer result = translateStatus(auctionItemId , HAS_NOT_STARTED , PROCESSING , null);

        //如果已经处理，则返回
        if(result == null || result.intValue() == 0){
            return ;
        }


        AuctionItem auctionItemFrom = getById(auctionItemId);
        //设定一个线程定时任务
        setFixedScheduling(auctionItemFrom);
    }*/

    public Integer translateStatus(Integer id , Integer originState ,Integer taregetState ,Integer wxFanId){
        return auctionItemMapper.updateStateAndWxFanId(id, originState, taregetState, wxFanId);
    }

    /**
     * 获取后台管理的列表
     * @param page
     * @param pageSize
     * @return
     */
    public List<AdminAuctionItem> getAdminAuctionItemList(Integer page ,Integer pageSize ){

        List<AuctionItem>  auctionItemList = this.getAuctionItemList(page, pageSize);

        List<AdminAuctionItem> adminAuctionItemList = new ArrayList<>();

        for(AuctionItem auctionItem : auctionItemList){

            Integer id = auctionItem.getId();

            AdminAuctionItem adminAuctionItem = BeanUtils.copyBean(auctionItem, AdminAuctionItem.class);

            Integer participantNumber = auctionRecordService.countParticipant(id);

            adminAuctionItem.setParticipantNumber(participantNumber);

            adminAuctionItemList.add(adminAuctionItem);
        }

        return adminAuctionItemList;
    }


    private List<AuctionItem> getAuctionItemList(Integer page ,Integer pageSize ){

        Integer startIndex = CommonUtils.page2startIndex(page, pageSize);

        return auctionItemMapper.selectPage(startIndex, pageSize);
    }

    /**
     * 获取指定宠物类型的拍卖品
     * @param chatPetType
     * @param page
     * @param pageSize
     * @return
     */
    public ChatPetAuctionItemResp getAuctionItemListByChatPetType(Integer chatPetType , Integer page , Integer pageSize , Integer wxFanId){

        Integer startIndex = CommonUtils.page2startIndex(page, pageSize);

        List<AuctionItem> auctionItemList = auctionItemMapper.selectPageByChatPetType(startIndex, pageSize, chatPetType);

        List<ChatPetAuctionItemListResp> chatPetAuctionItemListRespList = new ArrayList<>();

        for(AuctionItem auctionItem : auctionItemList){

            Integer auctionItemId = auctionItem.getId();

            ChatPetAuctionItemListResp chatPetAuctionItemListResp = BeanUtils.copyBean(auctionItem,ChatPetAuctionItemListResp.class);

            Integer participantNumber = auctionRecordService.countParticipant(auctionItemId);

            chatPetAuctionItemListResp.setNumber(participantNumber);

            chatPetAuctionItemListResp.setAuctionItemName(auctionItem.getName());
            chatPetAuctionItemListResp.setAuctionItemPic(auctionItem.getAuctionItemPic());

            Integer state = chatPetAuctionItemListResp.getState();
            if(state.intValue() == HAVE_FINISHED.intValue()){

                //中标的出价记录
                Integer ownerId = auctionItem.getWxFanId();
                AuctionRecord auctionRecord = auctionRecordService.getAuctionRecordByWxFan(ownerId ,auctionItemId);

                //中标人
                WxFan wxFan = wxFanService.getById(ownerId);
                String nickName = wxFan.getNickname();
                chatPetAuctionItemListResp.setWxFanNickname(nickName);

                //成交时间
                chatPetAuctionItemListResp.setDealTime(auctionRecord.getBidTime());

                //是否为中标人
                if(wxFanId.intValue() == ownerId.intValue()){
                    chatPetAuctionItemListResp.setWinner(Boolean.TRUE);
                }else {
                    chatPetAuctionItemListResp.setWinner(Boolean.FALSE);
                }

                AuctionRecord maxPriceRecord = auctionRecordService.getAuctionRecordByWxFan(ownerId, auctionItemId);
                chatPetAuctionItemListResp.setDealPrice(maxPriceRecord.getPrice());

            }

            if(state.intValue() == PROCESSING.intValue()){

                //获取粉丝的出价
                AuctionRecord auctionRecord = auctionRecordService.getAuctionRecordByWxFan(wxFanId ,auctionItemId);

                if(auctionRecord != null){
                    chatPetAuctionItemListResp.setBidPrice(auctionRecord.getPrice());
                    chatPetAuctionItemListResp.setBidTime(auctionRecord.getBidTime());
                }

            }

            chatPetAuctionItemListRespList.add(chatPetAuctionItemListResp);
        }

        ChatPetAuctionItemResp chatPetAuctionItemResp = new ChatPetAuctionItemResp();
        chatPetAuctionItemResp.setList(chatPetAuctionItemListRespList);

        ChatPet chatPet = chatPetService.getChatPetByWxFanId(wxFanId);
        chatPetAuctionItemResp.setCoin(chatPet.getCoin());

        return chatPetAuctionItemResp;
    }

    public void add(AddAuctionItem addAuctionItem){


        AuctionItem auctionItem = BeanUtils.copyBean(addAuctionItem, AuctionItem.class);

        auctionItem.setState(HAS_NOT_STARTED);
        auctionItem.setShipState(HAS_NOT_SHIP);

        Integer result = this.save(auctionItem);

        //如果没有保存成功
        if(result == null || result.intValue() == 0) {
            return ;
        }

       this.setFixedScheduling(auctionItem);
    }

    private Integer save(AuctionItem auctionItem){
        return auctionItemMapper.insert(auctionItem);
    }


    private List<AuctionItem> getByEndTime(Date endTime ){
        return auctionItemMapper.selectByEndTime(endTime);
    }

    /**
     * 上传展示图片
     * @param multipartFile
     * @return
     */
    public String uploadShowPic(MultipartFile multipartFile ){
        return uploadService.uploadPic(multipartFile, DIRCTORY_NAME ,String.valueOf(System.currentTimeMillis()));
    }


    /**
     * 更新竞拍品
     * @param updateAuctionItem
     * @return
     */
    public Integer updateAuctionItem(UpdateAuctionItem updateAuctionItem){

        Integer id = updateAuctionItem.getId();

        Date startTime = updateAuctionItem.getStartTime();
        Date endTime = updateAuctionItem.getEndTime();
        Integer auctionType = updateAuctionItem.getAuctionType();
        String name = updateAuctionItem.getName();
        Integer chatPetType = updateAuctionItem.getChatPetType();
        String auctionItemPic = updateAuctionItem.getAuctionItemPic();

        Integer result = auctionItemMapper.updateAuctionItem(startTime ,endTime ,id ,chatPetType ,auctionType ,name ,auctionItemPic);

        if(result == null){
            return null;
        }

        AuctionItem auctionItem = this.getById(id);

        if(result.intValue() == 1){

            //把线程停止
            Thread thread = threadMap.get(id);
            thread.interrupted();

            //重新建立线程
            setFixedScheduling(auctionItem);
        }

        return result;
    }


    /**
     * 修改竞拍品的发货状态
     * @return
     */
    public Integer updateAuctionItemShipState(Integer auctionItemId , Integer shipState){
        return auctionItemMapper.updateShipState(auctionItemId , shipState);
    }


    /**
     * 获取竞拍品的详细信息
     * @param auctionItemId
     * @return
     */
    public AuctionItemDetail getAuctionItemDetail(Integer auctionItemId){

        AuctionItem auctionItem = this.getById(auctionItemId);

        if(auctionItem == null){
            return null;
        }

        AuctionItemDetail auctionItemDetail = BeanUtils.copyBean(auctionItem, AuctionItemDetail.class);

        Integer participantNumber = auctionRecordService.countParticipant(auctionItemId);
        auctionItemDetail.setParticipantNumber(participantNumber);

        //如果已经结束且不流拍
        if(auctionItem.getState().intValue() == HAVE_FINISHED.intValue()){
            AuctionResultInfo auctionResultInfo = new AuctionResultInfo();

            Integer wxFanId = auctionItem.getWxFanId();

            WxFan wxFan = wxFanService.getById(wxFanId);

            String nickname = wxFan.getNickname();
            auctionResultInfo.setOwnerNickname(nickname);

            String wxFanOpenId = wxFan.getWxFanOpenId();
            String owerId = wxFanOpenId.substring(wxFanOpenId.length() - 6, wxFanOpenId.length() - 1);
            auctionResultInfo.setOpenId(owerId);

            AuctionRecord maxPriceAuctionItem = auctionRecordService.getAuctionRecordByWxFan(wxFanId, auctionItemId);
            Date bidTime = maxPriceAuctionItem.getBidTime();
            auctionResultInfo.setBidTime(bidTime);

            Float price = maxPriceAuctionItem.getPrice();
            auctionResultInfo.setPrice(price);

            Integer shipState = auctionItem.getShipState();
            if(shipState == null){
                shipState = HAS_NOT_SHIP;
            }
            auctionResultInfo.setShipState(shipState);

            auctionItemDetail.setAuctionResultInfo(auctionResultInfo);
        }

        return auctionItemDetail;
    }


    /**
     * 通过id删除
     * @param id
     * @return
     */
    public Integer deleteById(Integer id){
        return auctionItemMapper.delete(id);
    }
}
