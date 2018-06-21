package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.auctionitem.AuctionItemPageReq;
import net.monkeystudio.admin.controller.req.auctionitem.AuctionItemShipState;
import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.AuctionItem;
import net.monkeystudio.chatrbtw.service.AuctionItemService;
import net.monkeystudio.chatrbtw.service.bean.UploadFile;
import net.monkeystudio.chatrbtw.service.bean.auctionitem.AddAuctionItem;
import net.monkeystudio.chatrbtw.service.bean.auctionitem.AuctionItemDetail;
import net.monkeystudio.chatrbtw.service.bean.auctionitem.UpdateAuctionItem;
import net.monkeystudio.chatrbtw.service.bean.chatpetautionitem.AdminAuctionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2018/6/12.
 */
@RequestMapping(value = "/admin/auction-item")
@Controller
public class AuctionItemController extends BaseController{

    @Autowired
    private AuctionItemService auctionItemService;

    @Autowired
    private RespHelper respHelper;


    /**
     * 获取
     * @param auctionItemPageReq
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RespBase getAuctionItemPage(@RequestBody AuctionItemPageReq auctionItemPageReq){

        Integer userId = this.getUserId();

        if(userId == null){
            return respHelper.nologin();
        }

        Integer page = auctionItemPageReq.getPage();
        Integer pageSize = auctionItemPageReq.getPageSize();

        List<AdminAuctionItem> result = auctionItemService.getAdminAuctionItemList(page,pageSize);

        return respHelper.ok(result);
    }

    /**
     * 新增竞拍商品
     * @param addAuctionItem
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public RespBase addAuctionItem(@RequestBody AddAuctionItem addAuctionItem){

        Integer userId = this.getUserId();
        if(userId == null){
            return respHelper.nologin();
        }

        Date startTime = addAuctionItem.getStartTime();
        Date eneTime = addAuctionItem.getEndTime();

        if(startTime.compareTo(eneTime) > 0){
            return respHelper.failed("开始时间不能在结束时间后面");
        }

        Date newDate = new Date();
        if(newDate.compareTo(startTime) > 0){
            return respHelper.ok("开始时间不能早于当前时间");
        }

        auctionItemService.add(addAuctionItem);

        return respHelper.ok();
    }


    /**
     * 上传展示图片
     * @param uploadFile
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/pic-image", method = RequestMethod.POST)
    public RespBase addAuctionItem(UploadFile uploadFile){

        String url = auctionItemService.uploadShowPic(uploadFile.getMultipartFile());

        return respHelper.ok(url);
    }

    /**
     * 更新竞拍品
     * @param updateAuctionItem
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public RespBase updateAuctionItem(@RequestBody UpdateAuctionItem updateAuctionItem){

        Integer userId = this.getUserId();

        if(userId == null){
            return respHelper.nologin();
        }

        Integer auctionId = updateAuctionItem.getId();

        AuctionItem auctionItem = auctionItemService.getById(auctionId);

        if(auctionItem == null){
            return respHelper.failed("找不到该竞拍品");
        }

        if(auctionItem.getState().intValue() != AuctionItemService.HAS_NOT_STARTED.intValue()){
            return respHelper.failed("非预投放状态不能更改");
        }

        auctionItemService.updateAuctionItem(updateAuctionItem);

        return respHelper.ok();
    }

    @ResponseBody
    @RequestMapping(value = "/update/ship-state", method = RequestMethod.POST)
    public RespBase updateShipState(@RequestBody AuctionItemShipState auctionItemShipState){


        Integer userId = this.getUserId();

        if(userId == null){
            return respHelper.nologin();
        }

        Integer shipState = auctionItemShipState.getShipState();
        Integer auctionItemId = auctionItemShipState.getId();

        auctionItemService.updateAuctionItemShipState(auctionItemId, shipState);

        return respHelper.ok();
    }


    /**
     * 获取竞拍品的详细信息
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/get-info", method = RequestMethod.POST)
    public RespBase getDetailInfo(@RequestParam Integer id){


        Integer userId = this.getUserId();

        if(userId == null){
            return respHelper.nologin();
        }

        AuctionItemDetail auctionItemDetail = auctionItemService.getAuctionItemDetail(id);

        return respHelper.ok(auctionItemDetail);
    }




    /**
     * 删除id
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public RespBase delete(@RequestParam Integer id){


        Integer userId = this.getUserId();

        if(userId == null){
            return respHelper.nologin();
        }

        AuctionItem auctionItem = auctionItemService.getById(id);

        if(auctionItem == null){
            return respHelper.failed("找不到该竞拍品");

        }

        //如果非未开始的状态的,不能修改
        if(auctionItem.getState().intValue() != AuctionItemService.HAS_NOT_STARTED){
            return respHelper.failed("不是未开始状态下的不能删除");
        }

        auctionItemService.deleteById(id);

        return respHelper.ok();
    }
}
