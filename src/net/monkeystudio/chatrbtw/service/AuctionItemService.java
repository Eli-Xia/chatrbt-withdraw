package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.service.FixedScheduling;
import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.chatrbtw.entity.AuctionItem;
import net.monkeystudio.chatrbtw.entity.AuctionRecord;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.mapper.AuctionItemMapper;
import net.monkeystudio.chatrbtw.service.bean.UploadFile;
import net.monkeystudio.chatrbtw.service.bean.auctionitem.ChatPetAuctionItemListResp;
import net.monkeystudio.chatrbtw.service.bean.auctionitem.UpdateAuctionItem;
import net.monkeystudio.chatrbtw.service.bean.chatpetautionitem.AdminAuctionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private final static Integer HAS_NOT_STARTED = 0; //未开始
    private final static Integer PROCESSING = 1;      //进行中
    private final static Integer HAVE_FINISHED = 2;   //已完成
    private final static Integer UNCLAIMED = 3;       //流拍


    private final static String DIRCTORY_NAME = "/chat_pet/auction_item";

    @PostConstruct
    private void init(){

        Date nowDate = new Date();
        List<AuctionItem> auctionItemList = this.getByEndTime(nowDate);

        if(ListUtil.isEmpty(auctionItemList)){
            return ;
        }

        for(AuctionItem auctionItem : auctionItemList){

            Date endDate = auctionItem.getEndTime();

            Runnable runnable = this.createRunnable(auctionItem);

            fixedScheduling.createFixedScheduling(endDate ,runnable);
        }

    }


    private Runnable createRunnable(AuctionItem auctionItem ){

        Integer auctionItemId = auctionItem.getId();

        Runnable runnable = null;
        if(auctionItem.getState().intValue() == PROCESSING.intValue()){
            runnable = new Runnable() {
                @Override
                public void run() {

                    AuctionRecord maxPriceAuctionItem = auctionRecordService.getMaxPriceAuctionItem(auctionItemId);

                    //如果没人竞价,设置竞拍品状态为无人竞拍
                    if(maxPriceAuctionItem == null){
                        Integer result = translateStatus(auctionItemId , PROCESSING , UNCLAIMED , null);
                        return ;
                    }

                    //修改状态为已经结束和填充获得人
                    Integer wxFanId = maxPriceAuctionItem.getWxFanId();

                    Integer result = translateStatus(auctionItemId , PROCESSING , HAVE_FINISHED , wxFanId);

                    //如果已经处理，则返回
                    if(result == null || result.intValue() == 0){
                        return ;
                    }
                }
            };
            return runnable;
        }


        //如果本来是未开始的，时间到了，修改状态为进行中
        if(auctionItem.getState().intValue() == HAS_NOT_STARTED.intValue()){
            runnable = new Runnable() {
                @Override
                public void run() {

                    Integer result = translateStatus(auctionItemId , HAS_NOT_STARTED , PROCESSING , null);

                    //如果已经处理，则返回
                    if(result == null || result.intValue() == 0){
                        return ;
                    }
                }
            };

            return runnable;
        }


        return null;
    }


    private Integer translateStatus(Integer id , Integer originState ,Integer taregetState ,Integer wxFanId){
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
    public List<ChatPetAuctionItemListResp> getAuctionItemListByChatPetType(Integer chatPetType , Integer page , Integer pageSize ,Integer wxFanId){

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
                AuctionRecord auctionRecord = auctionRecordService.getMaxPriceAuctionItem(auctionItemId);

                //中标人
                Integer ownerId = auctionRecord.getWxFanId();
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

        return chatPetAuctionItemListRespList;
    }


    public Integer save(AuctionItem auctionItem){
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

        Date startTime = updateAuctionItem.getStartTime();
        Date endTime = updateAuctionItem.getEndTime();
        Integer auctionType = updateAuctionItem.getAuctionType();
        String name = updateAuctionItem.getName();
        Integer chatPetType = updateAuctionItem.getChatPetType();
        Integer id = updateAuctionItem.getId();

        Integer result = auctionItemMapper.updateAuctionItem(startTime ,endTime ,id ,chatPetType ,auctionType ,name);

        return result;
    }
}
