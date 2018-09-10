package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.minigamead.IdReq;
import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.controller.bean.RespBase;
import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.entity.MiniGameAd;
import net.monkeystudio.chatrbtw.service.MiniGameAdService;
import net.monkeystudio.chatrbtw.service.bean.UploadFile;
import net.monkeystudio.chatrbtw.service.bean.minigamead.AddMiniGameAd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping(value = "/admin/minigame-ad")
@Controller
public class MiniGameAdController extends BaseController {
    @Autowired
    private MiniGameAdService miniGameAdService;

    @Autowired
    private RespHelper respHelper;

    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public RespBase add(@RequestBody AddMiniGameAd addMiniGameAd){

        miniGameAdService.add(addMiniGameAd);
        return respHelper.ok();
    }

    @ResponseBody
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public RespBase get(@RequestBody IdReq idReq){

        MiniGameAd miniGameAd = miniGameAdService.getById(idReq.getId());
        return respHelper.ok(miniGameAd);
    }


    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public RespBase update(@RequestBody MiniGameAd miniGameAd){

        miniGameAdService.update(miniGameAd);
        return respHelper.ok();
    }

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RespBase list(){
        List<MiniGameAd> miniGameList = miniGameAdService.getMiniGameList();
        return respHelper.ok(miniGameList);
    }

    /**
     * 上传展示图片
     * @param uploadFile
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public RespBase addAuctionItem(UploadFile uploadFile){

        String url = miniGameAdService.uploadPic(uploadFile.getMultipartFile());

        return respHelper.ok(url);
    }

}
