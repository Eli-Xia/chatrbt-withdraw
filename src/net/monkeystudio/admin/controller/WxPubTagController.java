package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.AddUpdateWxPubTag;
import net.monkeystudio.base.BaseController;
import net.monkeystudio.base.RespBase;
import net.monkeystudio.base.req.ListPaginationReq;
import net.monkeystudio.chatrbtw.entity.WxPubTag;
import net.monkeystudio.chatrbtw.service.WxPubTagService;
import net.monkeystudio.utils.RespHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author xiaxin
 */
@Controller
@RequestMapping(value = "/admin/wxPub-tag")
public class WxPubTagController extends BaseController{

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private WxPubTagService wxPubTagService;

    /**
     * @return 所有标签
     */
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    @ResponseBody
    public RespBase getAllTags(HttpServletRequest request){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        List<WxPubTag> tags = wxPubTagService.getAllTags();

        return respHelper.ok(tags);

    }

    /**
     * @return 所有标签 分页
     */
    @RequestMapping(value = "/page/list",method = RequestMethod.POST)
    @ResponseBody
    public RespBase getTagsByPageList(HttpServletRequest request, @RequestBody ListPaginationReq paginationReq){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        List<WxPubTag> tags = wxPubTagService.getTagsByPageList(paginationReq);

        Integer count = wxPubTagService.getCount();

        return respHelper.ok(tags,count);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase getById(HttpServletRequest request , @PathVariable("id") Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        WxPubTag tagById = wxPubTagService.getWxPubTagById(id);

        return respHelper.ok(tagById);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase update(HttpServletRequest request , @PathVariable("id") Integer id ,@RequestBody AddUpdateWxPubTag addUpdateWxPubTag){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if(id == null){
            respHelper.failed("id不能为空");
        }

        if(StringUtils.isBlank(addUpdateWxPubTag.getName())){
            respHelper.failed("标签内容不能为空");
        }

        wxPubTagService.update(id,addUpdateWxPubTag);

        return respHelper.ok();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public RespBase add(HttpServletRequest request ,@RequestBody AddUpdateWxPubTag addUpdateWxPubTag){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

       if(StringUtils.isBlank(addUpdateWxPubTag.getName())){
           respHelper.failed("标签内容不能为空");
       }

        wxPubTagService.save(addUpdateWxPubTag);

        return respHelper.ok();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase add(HttpServletRequest request ,@PathVariable("id") Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if(id == null){
            respHelper.failed("id不能为空");
        }

        wxPubTagService.delete(id);

        return respHelper.ok();
    }

    /**
     * 根据微信公众号id获取其对应标签集
     * @param request
     * @return
     */
    @RequestMapping(value = "/{wxPubId}/list",method = RequestMethod.POST)
    @ResponseBody
    public RespBase getTagsByWxPubId(HttpServletRequest request, @PathVariable Integer wxPubId){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        List<WxPubTag> tags = wxPubTagService.getTagsByWxPubId(wxPubId);
        return respHelper.ok(tags);
    }
    /**
     * 新增或更新公众号标签分类
     * @param wxPubId   微信公众号对象id
     * @param
     * @return
     */
    @RequestMapping(value = "/wx-pub/{id}/update",method = RequestMethod.POST)
    @ResponseBody
    public RespBase saveOrUpdate( @RequestParam("ids") List<Integer> tagIds,HttpServletRequest request, @PathVariable("id") Integer wxPubId){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if(tagIds.size() > 2){
            return respHelper.failed("tag size must not more than 2 !!");
        }

        wxPubTagService.cudTagsForWxPub(wxPubId,tagIds);
        return respHelper.ok();
    }
}
