package net.monkeystudio.admin.controller;

import net.monkeystudio.admin.controller.req.QueryKeywordList;
import net.monkeystudio.base.BaseController;
import net.monkeystudio.base.RespBase;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.entity.KrKeyword;
import net.monkeystudio.chatrbtw.service.KeywordRespMgrService;
import net.monkeystudio.chatrbtw.service.bean.kr.KeywordResponse;
import net.monkeystudio.chatrbtw.service.bean.kr.SetBaseKeywordResponse;
import net.monkeystudio.portal.controller.req.kr.UpdateKeywordResponse;
import net.monkeystudio.utils.RespHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 关键字回复管理接口
 * @author hebo
 *
 */
@Controller
@RequestMapping(value = "/admin/kr")
public class KeywordRespMgrController extends BaseController{

    @Autowired
    private RespHelper respHelper;
    
    @Autowired
    private KeywordRespMgrService keywordRespMgrService;
    
    /**
     * 关键字列表
     * @param request
     * @param queryKeywordList
     * @return
     */
    @RequestMapping(value = "/base/list", method = RequestMethod.POST)
    @ResponseBody
    public RespBase keywordList(HttpServletRequest request, @RequestBody QueryKeywordList queryKeywordList){
        
        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        Integer page = queryKeywordList.getPage();
        Integer pageSize = queryKeywordList.getPageSize();
        
        if ( page == null || page < 1 ){
            return respHelper.cliParamError("page error.");
        }
        
        if ( pageSize == null || pageSize < 1 ){
            return respHelper.cliParamError("pageSize error.");
        }

        Map<String,Object> queryKeyword = queryKeywordList.getMap();
        queryKeyword.put("keywords",queryKeywordList.getKeywords());

        List<KeywordResponse> keywords = keywordRespMgrService.getBaseKeywordResponseList(queryKeyword);
        Integer total = keywordRespMgrService.countBase(queryKeyword);
        
        return respHelper.ok(keywords, total);
        
    }

    /**
     * 设置关键字-回复
     * @param request
     * @return
     */
    @RequestMapping(value = "/base/set", method = RequestMethod.POST)
    @ResponseBody
    public RespBase add(HttpServletRequest request, @RequestBody SetBaseKeywordResponse setKeywordResponse){

        Integer userId = this.getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if ( setKeywordResponse.getKeywords() == null || setKeywordResponse.getKeywords().size() == 0 ){
            return respHelper.failed("关键字不能为空。");
        }

        if ( StringUtils.isBlank( setKeywordResponse.getResponse() ) ){
            return respHelper.failed("响应内容不能为空。");
        }

        Integer rule = setKeywordResponse.getRule();
        if ( rule == null ){
            return respHelper.failed("rule参数不能为空。");
        }

        if ( !ruleValid(rule) ){
            return respHelper.failed("rule参数值不对持。");
        }

        keywordRespMgrService.addKeywordsResponse(null, setKeywordResponse.getKeywords(), setKeywordResponse.getResponse(), setKeywordResponse.getRule());
        return respHelper.ok();
    }


    /**
     * 删除关键字
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value = "/base/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RespBase delete(HttpServletRequest request, @PathVariable("id") Integer id){

        Integer userId = getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        KrKeyword kr = keywordRespMgrService.getKrKeyword(id);

        if(kr == null){
            return respHelper.failed("找不到该关键字");
        }

        keywordRespMgrService.deleteKeyword(id);

        return respHelper.ok();
    }


    /**
     * 更新关键字-回复
     * @param request
     * @return
     */
    @RequestMapping(value = "/base/update", method = RequestMethod.POST)
    @ResponseBody
    public RespBase update(HttpServletRequest request, @RequestBody UpdateKeywordResponse updateKeywordResponse){

        Integer userId = this.getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        if ( updateKeywordResponse.getId() == null ){
            return respHelper.failed("id不能为空。");
        }

        if ( updateKeywordResponse.getKeywords() == null || updateKeywordResponse.getKeywords().size() == 0 ){
            return respHelper.failed("关键字不能为空。");
        }

        if ( StringUtils.isBlank( updateKeywordResponse.getResponse() ) ){
            return respHelper.failed("响应内容不能为空。");
        }

        Integer rule = updateKeywordResponse.getRule();
        if ( rule == null ){
            return respHelper.failed("rule参数不能为空。");
        }
        if ( !ruleValid(rule) ){
            return respHelper.failed("rule参数值不对持。");
        }

        KrKeyword krKeyword = keywordRespMgrService.getKrKeyword(updateKeywordResponse.getId());
        if ( krKeyword == null ){
            return respHelper.failed("配置不存在。");
        }

        keywordRespMgrService.updateKeywordsResponse(updateKeywordResponse.getId(), updateKeywordResponse.getKeywords(), updateKeywordResponse.getResponse(), updateKeywordResponse.getRule());
        return respHelper.ok();
    }


    private boolean ruleValid(Integer rule){

        for (int i = 0; i < AppConstants.KEYWORD_RESPONSE_RULES.length; i++ ){
            if ( rule.intValue() == AppConstants.KEYWORD_RESPONSE_RULES[i] ){
                return true;
            }
        }

        return false;
    }

    /**
     * excel批量插入关键字及回复
     * @return
     */
    @RequestMapping(value= "/importKwResp", method =RequestMethod.POST , consumes = "multipart/form-data" )
    @ResponseBody
    public RespBase batchInsertKwAndRespFromExcel(MultipartFile excelFile,HttpServletRequest request){

        Integer userId = this.getUserId();
        if ( userId == null ){
            return respHelper.nologin();
        }

        keywordRespMgrService.batchInsertKwRespFromExcel(excelFile);

        return respHelper.ok();
    }

}
