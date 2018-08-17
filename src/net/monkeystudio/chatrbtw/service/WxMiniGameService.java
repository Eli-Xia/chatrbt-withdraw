package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.chatrbtw.entity.WxMiniGame;
import net.monkeystudio.chatrbtw.mapper.WxMiniGameMapper;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.AdminMiniGame;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.AdminMiniGameAdd;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.AdminMiniGameResp;
import net.monkeystudio.chatrbtw.service.bean.gamecenter.AdminMiniGameUpdate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaxin
 */
@Service
public class WxMiniGameService {
    @Autowired
    private WxMiniGameMapper wxMiniGameMapper;
    @Autowired
    private COSService cosService;
    @Autowired
    private UploadService uploadService;

    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private RMiniGameTagService rMiniGameTagService;

    private final static String WX_MINI_GAME_HEAD_IMG_COS_PATH = "/lucky-cat/mini-game/head-img";
    private final static String WX_MINI_GAME_QR_CODE_IMG_COS_PATH = "/lucky-cat/mini-game/qr-code-img";
    private final static String WX_MINI_GAME_COVER_IMG_COS_PATH = "/lucky-cat/mini-game/cover-img";


    private final static Integer WX_MINI_GAME_SHELVE_STATE = 1;//小游戏上架状态
    private final static Integer WX_MINI_GAME_UNSHELVE_STATE = 0;//小游戏下架状态

    public final static Integer WX_MINI_GAME_NO_SIGN = 0;//小游戏没有new
    public final static Integer WX_MINI_GAME_NEED_SIGN = 1;//小游戏有new

    public List<WxMiniGame> getWxMiniGameList() {
        return wxMiniGameMapper.selectAll();
    }

    /**
     * 年月日時分秒-"originFileName"
     *
     * @param originFileName 文件原來的名字
     * @return
     */
    private String generateFileName(String originFileName) {
        return TimeUtil.getCurrentTimestamp() + "-" + originFileName;
    }

    /**
     * 获取已上线,上架小游戏id list
     *
     * @return
     */
    public List<Integer> getWxMiniGameIds() {
        List<WxMiniGame> wxMiniGameList = this.getOnlineMiniGameList();
        List<Integer> ids = wxMiniGameList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
        return ids;
    }

    //精选编辑save
    private Integer saveForHandpicked(AdminMiniGameAdd adminMiniGameAdd) {
        MultipartFile headImg = adminMiniGameAdd.getHeadImg();
        MultipartFile qrCodeImg = adminMiniGameAdd.getQrCodeImg();
        MultipartFile coverImg = adminMiniGameAdd.getCoverImg();

        String headImgFileName = headImg.getOriginalFilename();
        String qrCodeImgFileName = qrCodeImg.getOriginalFilename();
        String coverImgFileName = coverImg.getOriginalFilename();

        String headImgUploadUrl = uploadService.uploadPic(headImg, WX_MINI_GAME_HEAD_IMG_COS_PATH, this.generateFileName(headImgFileName));
        String qrCodeImgUploadUrl = uploadService.uploadPic(qrCodeImg, WX_MINI_GAME_QR_CODE_IMG_COS_PATH, this.generateFileName(qrCodeImgFileName));
        String coverImgUploadUrl = uploadService.uploadPic(coverImg, WX_MINI_GAME_COVER_IMG_COS_PATH, this.generateFileName(coverImgFileName));


        WxMiniGame wxMiniGame = new WxMiniGame();

        BeanUtils.copyProperties(adminMiniGameAdd, wxMiniGame);
        wxMiniGame.setHeadImgUrl(headImgUploadUrl);
        wxMiniGame.setQrCodeImgUrl(qrCodeImgUploadUrl);
        wxMiniGame.setShelveState(WX_MINI_GAME_SHELVE_STATE);//默认为上架状态
        wxMiniGame.setCreateTime(new Date());
        wxMiniGame.setIsHandpicked(adminMiniGameAdd.getIsHandpicked());
        wxMiniGame.setStarNum(adminMiniGameAdd.getStarNum());
        wxMiniGame.setAppId(adminMiniGameAdd.getAppId());
        wxMiniGame.setCoverImgUrl(coverImgUploadUrl);

        wxMiniGameMapper.insert(wxMiniGame);

        return wxMiniGame.getId();
    }

    //非精选编辑save
    private Integer saveForNotHandpicked(AdminMiniGameAdd adminMiniGameAdd) {
        MultipartFile headImg = adminMiniGameAdd.getHeadImg();
        MultipartFile qrCodeImg = adminMiniGameAdd.getQrCodeImg();

        String headImgFileName = headImg.getOriginalFilename();
        String qrCodeImgFileName = qrCodeImg.getOriginalFilename();

        String headImgUploadUrl = uploadService.uploadPic(headImg, WX_MINI_GAME_HEAD_IMG_COS_PATH, this.generateFileName(headImgFileName));
        String qrCodeImgUploadUrl = uploadService.uploadPic(qrCodeImg, WX_MINI_GAME_QR_CODE_IMG_COS_PATH, this.generateFileName(qrCodeImgFileName));

        WxMiniGame wxMiniGame = new WxMiniGame();

        BeanUtils.copyProperties(adminMiniGameAdd, wxMiniGame);
        wxMiniGame.setHeadImgUrl(headImgUploadUrl);
        wxMiniGame.setQrCodeImgUrl(qrCodeImgUploadUrl);
        wxMiniGame.setShelveState(WX_MINI_GAME_SHELVE_STATE);//默认为上架状态
        wxMiniGame.setCreateTime(new Date());
        wxMiniGame.setIsHandpicked(adminMiniGameAdd.getIsHandpicked());
        wxMiniGame.setStarNum(adminMiniGameAdd.getStarNum());

        wxMiniGameMapper.insert(wxMiniGame);

        return wxMiniGame.getId();
    }

    /**
     * 保存小游戏
     *
     * @param adminMiniGameAdd
     * @return:新增小游戏主键id
     */
    @Transactional
    public Integer save(AdminMiniGameAdd adminMiniGameAdd) {
        Boolean isHandpicked = adminMiniGameAdd.getIsHandpicked();

        Integer addMinigameId = null;

        //insert minigame
        if (isHandpicked) {
            addMinigameId = this.saveForHandpicked(adminMiniGameAdd);
        } else {
            addMinigameId = this.saveForNotHandpicked(adminMiniGameAdd);
        }

        //给minigame分配标签
        rMiniGameTagService.saveTagsForMinigame(adminMiniGameAdd.getTagIdList(), addMinigameId);

        return addMinigameId;
    }


    public void delete(Integer wxMiniGameId) {
        wxMiniGameMapper.delete(wxMiniGameId);
    }


    /**
     * 更新精选编辑小游戏
     *
     * @param adminMiniGameUpdate
     */
    private void updateForHandpicked(AdminMiniGameUpdate adminMiniGameUpdate) {
        WxMiniGame wxMiniGame = this.getById(adminMiniGameUpdate.getId());


        Date onlineTime = CommonUtils.dateStartTime(wxMiniGame.getOnlineTime());
        Date nowTime = new Date();

        //未上线可编辑时间,已上线可编辑
        if(nowTime.compareTo(onlineTime) < 0){
            BeanUtils.copyProperties(adminMiniGameUpdate, wxMiniGame);
        }else{
            BeanUtils.copyProperties(adminMiniGameUpdate, wxMiniGame , "onlineTime");
        }

        MultipartFile headImg = adminMiniGameUpdate.getHeadImg();
        MultipartFile qrCodeImg = adminMiniGameUpdate.getQrCodeImg();
        MultipartFile coverImg = adminMiniGameUpdate.getCoverImg();

        if (headImg != null) {
            String headImgFileName = headImg.getOriginalFilename();
            String headImgUploadUrl = uploadService.uploadPic(headImg, WX_MINI_GAME_HEAD_IMG_COS_PATH, this.generateFileName(headImgFileName));
            wxMiniGame.setHeadImgUrl(headImgUploadUrl);

        }
        if (qrCodeImg != null) {
            String qrCodeImgFileName = qrCodeImg.getOriginalFilename();
            String qrCodeImgUploadUrl = uploadService.uploadPic(qrCodeImg, WX_MINI_GAME_QR_CODE_IMG_COS_PATH, this.generateFileName(qrCodeImgFileName));
            wxMiniGame.setQrCodeImgUrl(qrCodeImgUploadUrl);
        }
        if (coverImg != null) {
            String coverImgFilename = coverImg.getOriginalFilename();
            String coverImgUploadUrl = uploadService.uploadPic(coverImg, WX_MINI_GAME_COVER_IMG_COS_PATH, this.generateFileName(coverImgFilename));
            wxMiniGame.setCoverImgUrl(coverImgUploadUrl);
        }

        wxMiniGameMapper.updateByPrimaryKey(wxMiniGame);
    }

    /**
     * 更新未精选编辑小游戏
     *
     * @param adminMiniGameUpdate
     */
    private void updateForNotHandpicked(AdminMiniGameUpdate adminMiniGameUpdate) {
        WxMiniGame wxMiniGame = this.getById(adminMiniGameUpdate.getId());

        BeanUtils.copyProperties(adminMiniGameUpdate, wxMiniGame);

        MultipartFile headImg = adminMiniGameUpdate.getHeadImg();
        MultipartFile qrCodeImg = adminMiniGameUpdate.getQrCodeImg();

        if (headImg != null) {
            String headImgFileName = headImg.getOriginalFilename();
            String headImgUploadUrl = uploadService.uploadPic(headImg, WX_MINI_GAME_HEAD_IMG_COS_PATH, this.generateFileName(headImgFileName));
            wxMiniGame.setHeadImgUrl(headImgUploadUrl);

        }
        if (qrCodeImg != null) {
            String qrCodeImgFileName = qrCodeImg.getOriginalFilename();
            String qrCodeImgUploadUrl = uploadService.uploadPic(qrCodeImg, WX_MINI_GAME_QR_CODE_IMG_COS_PATH, this.generateFileName(qrCodeImgFileName));
            wxMiniGame.setQrCodeImgUrl(qrCodeImgUploadUrl);

        }

        wxMiniGameMapper.updateByPrimaryKey(wxMiniGame);
    }


    /**
     * 更新小游戏
     *
     * @param adminMiniGameUpdate
     */
    @Transactional
    public void update(AdminMiniGameUpdate adminMiniGameUpdate) {
        Boolean isHandpicked = adminMiniGameUpdate.getIsHandpicked();

        if (isHandpicked) {
            this.updateForHandpicked(adminMiniGameUpdate);
        } else {
            this.updateForNotHandpicked(adminMiniGameUpdate);
        }

        rMiniGameTagService.updateTagsForMinigame(adminMiniGameUpdate.getTagIdList(), adminMiniGameUpdate.getId());
    }

    public WxMiniGame getById(Integer miniGameId) {
        return wxMiniGameMapper.selectByPrimaryKey(miniGameId);
    }

    public AdminMiniGame getAdminGameById(Integer miniGameId){
        WxMiniGame wxMiniGame = this.getById(miniGameId);

        AdminMiniGame adminMiniGame = new AdminMiniGame();

        BeanUtils.copyProperties(wxMiniGame,adminMiniGame);

        //根据小游戏id获取其标签集
        List<Integer> tagList = rMiniGameTagService.getTagListByMiniGameId(miniGameId);
        adminMiniGame.setTagIdList(tagList);

        return adminMiniGame;
    }

    /**
     * 小游戏下架
     *
     * @param miniGameId
     */
    public void unshelve(Integer miniGameId) {
        WxMiniGame wxMiniGame = this.getById(miniGameId);
        wxMiniGame.setShelveState(WX_MINI_GAME_UNSHELVE_STATE);
        wxMiniGameMapper.updateByPrimaryKey(wxMiniGame);
    }

    /*public List<AdminMiniGameResp> getAdminMiniGameRespList() {

        List<AdminMiniGameResp> resps = new ArrayList<>();

        List<WxMiniGame> wxMiniGames = wxMiniGameMapper.selectAll();

        for (WxMiniGame item : wxMiniGames) {
            AdminMiniGameResp resp = new AdminMiniGameResp();
            BeanUtils.copyProperties(item, resp);
            //查询这个游戏历史完成任务的数量
            Long miniGamePlayerNum = chatPetMissionPoolService.getMiniGamePlayerNum(item.getId());
            resp.setPlayerNum(miniGamePlayerNum);
            resps.add(resp);
        }

        return resps;
    }*/

    /**
     * 小游戏后台分页列表
     * @param page
     * @param pageSize
     * @return
     */
    public List<AdminMiniGameResp> getAdminMiniGameRespListByPage(Integer page,Integer pageSize){
        Integer startIndex = CommonUtils.page2startIndex(page, pageSize);

        List<AdminMiniGameResp> resps = new ArrayList<>();

        List<WxMiniGame> wxMiniGames = wxMiniGameMapper.selectByPage(startIndex,pageSize);

        for (WxMiniGame item : wxMiniGames) {
            AdminMiniGameResp resp = new AdminMiniGameResp();
            BeanUtils.copyProperties(item, resp);
            //查询这个游戏历史完成任务的数量
            Long miniGamePlayerNum = chatPetMissionPoolService.getMiniGamePlayerNum(item.getId());
            resp.setPlayerNum(miniGamePlayerNum);
            resps.add(resp);
        }

        return resps;
    }

    public Integer getCount(){
        return wxMiniGameMapper.count();
    }

    /**
     * 获取上线的小游戏列表
     *
     * @return
     */
    public List<WxMiniGame> getOnlineMiniGameList() {
        return wxMiniGameMapper.selectOnlineGameList();
    }


    /**
     * 游戏中心展示列表
     * @return
     */
    /*public List<WxMiniGame> getMiniGameInfoList(){
        List<WxMiniGame> wxMiniGameList = this.getWxMiniGameList();

        Iterator<WxMiniGame> iterator = wxMiniGameList.iterator();

        while(iterator.hasNext()){
            WxMiniGame item = iterator.next();
            Long onlineTime = DateUtils.getBeginDate(item.getOnlineTime()).getTime();
            Long nowTime = new Date().getTime();
            Integer shelveState = item.getShelveState();

            //如果小游戏还未上线或者已经下架,则不展示
            if(nowTime.intValue() < onlineTime.intValue() || WX_MINI_GAME_UNSHELVE_STATE.equals(shelveState)){
                iterator.remove();
            }
        }
        return wxMiniGameList;

    }*/

    /**
     * 判断上线时间是否正确,
     *
     * @param onlineTime
     * @return
     */
    public Boolean isOnlineTimeValid(Date onlineTime) {
        Boolean ret = false;

        //上线当天0点
        Date onlineDate = DateUtils.getBeginDate(onlineTime);

        //设置当天23点59分59秒
        Date today = DateUtils.getEndDate(new Date());

        if (onlineDate.compareTo(today) > 0) {
            ret = true;
        }

        return ret;
    }



}
