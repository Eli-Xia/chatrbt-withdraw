package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.auctionitem.AuctionItemPageReq;
import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.AuctionItem;
import net.monkeystudio.chatrbtw.service.AuctionItemService;
import net.monkeystudio.chatrbtw.service.bean.UploadFile;
import net.monkeystudio.chatrbtw.service.bean.auctionitem.UpdateAuctionItem;
import net.monkeystudio.chatrbtw.service.bean.chatpetautionitem.AdminAuctionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
     *
     * @param auctionItem
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public RespBase addAuctionItem(@RequestBody AuctionItem auctionItem){

        auctionItemService.save(auctionItem);

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
     *
     * @param auctionItem
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public RespBase updateAuctionItem(@RequestBody UpdateAuctionItem auctionItem){

        auctionItemService.updateAuctionItem(auctionItem);

        return respHelper.ok();
    }

}
