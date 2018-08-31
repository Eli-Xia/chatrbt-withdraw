package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.JsonHelper;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.AccountSetting;
import net.monkeystudio.chatrbtw.mapper.AccountSettingMapper;
import net.monkeystudio.chatrbtw.mapper.BankMapper;
import net.monkeystudio.chatrbtw.mapper.CityMapper;
import net.monkeystudio.chatrbtw.mapper.ProvinceMapper;
import net.monkeystudio.portal.controller.req.accountsetting.AccountSettingReq;
import net.monkeystudio.portal.controller.resp.accountsetting.AccountSettingResp;
import net.monkeystudio.portal.controller.resp.accountsetting.AccountSettingVO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaxin
 */
@Service
public class AccountSettingService {

    //结算设置
    private final static String[] CERTIFICATION_ALLOW_FILE_SUFFIX = {"jpg","jpeg","png"};	//委托个人收款证明支持文件后缀
    private final static long CERTIFICATION_FILE_SIZE_LIMIT = 5*1048576L ;					//委托个人收款证明图片max size
    public final static Integer PERSONAL_ACCOUNT_TYPE = 1;	                    //个人账户
    public final static Integer COMPANY_ACCOUNT_TYPE = 0;	                    //公司账户
    public static final String ACCOUNT_SETTING_DIRECTORY = "/account-setting/";//委托个人收款证明存放目录
    public static final String ACCOUNT_SETTING_DATE_FORMAT = "yyyyMMddHHmmss";//时间格式,用于生成文件名
    public static final String CERTIFICATION_COS_PATH = "/company-doc/委托个人收款证明.docx";//委托个人收款证明文件在cos中的存放路径

    @Autowired
    private COSService cosService;

    @Autowired
    private AccountSettingMapper accountSettingMapper;

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private ProvinceMapper provinceMapper;

    @Autowired
    private BankMapper bankMapper;



    //判断文件类型是否符合要求
    public boolean checkFileSuffix(MultipartFile file){
        String fileName = file.getOriginalFilename();
        String suffix = CommonUtils.getFilenamePostfix(fileName);
        return Arrays.asList(CERTIFICATION_ALLOW_FILE_SUFFIX).contains(suffix);
    }

    public boolean isInLimitSize(MultipartFile file){
        long size = file.getSize();
        return size <= CERTIFICATION_FILE_SIZE_LIMIT;
    }

    //COS上传并返回图片url
    private String uploadFile(MultipartFile file,Integer userId){
        String cosPath = this.createPicPath(file, userId);
        String cosPicUrl = null;
        try {
            String pathJson = cosService.uploadSensitiveFile(cosPath, IOUtils.toByteArray(file.getInputStream()));
            String dataJson = JsonHelper.getStringFromJson(pathJson, "data");
            cosPicUrl = JsonHelper.getStringFromJson(dataJson,"access_url");
        } catch (IOException e) {
            Log.e(e);
        }
        return cosPicUrl;
    }

    //文件名: 年月日时分秒-用户id.后缀
    private String createPicPath(MultipartFile file,Integer userId){
        String fileName = file.getOriginalFilename();
        String suffix = CommonUtils.getFilenamePostfix(fileName);
        String sFormat = CommonUtils.dateFormat(new Date(), ACCOUNT_SETTING_DATE_FORMAT);
        StringBuilder sb = new StringBuilder();
        sb.append(ACCOUNT_SETTING_DIRECTORY).append(sFormat).append("-").append(userId).append(".").append(suffix);
        return sb.toString();
    }


    public InputStream getPicInputStream(Integer userId){

        AccountSetting accountSetting = accountSettingMapper.selectByUserId(userId);

        String picUrl = null;

        if(accountSetting != null){
            picUrl = accountSetting.getLetterOfDelegationImgUrl();
        }

        if(picUrl == null){
            return null;
        }

        int index = picUrl.lastIndexOf(ACCOUNT_SETTING_DIRECTORY);

        String cosPath = null;
        if(index == -1){
            cosPath = "";
        }else{
            cosPath = picUrl.substring(picUrl.lastIndexOf(ACCOUNT_SETTING_DIRECTORY));
        }

        InputStream is = cosService.getSensitivePicInputStream(cosPath);

        return is;
    }

    public void saveOrUpdateAccountSettings(AccountSettingReq req,Integer userId){

        AccountSetting accountSetting = new AccountSetting();

        BeanUtils.copyProperties(req,accountSetting,"image","bankId","cityId","provinceId");

        String filePath = null;

        if(req.getImage() != null){//账户类型为公司时值为null
            filePath = this.uploadFile(req.getImage(),userId);//上传COS并返回图片路径
        }

        accountSetting.setLetterOfDelegationImgUrl(filePath);

        accountSetting.setBankName(bankMapper.selectByPrimaryKey(req.getBankId()).getBankName());

        Map<String,Object> cityParam = new HashMap<>();
        cityParam.put("code",req.getCityCode());
        Map<String,Object> provinceParam = new HashMap<>();
        provinceParam.put("code",req.getProvinceCode());

        accountSetting.setCity((cityMapper.selectByParamMap(cityParam)).getName());

        accountSetting.setProvince((provinceMapper.selectByParamMap(provinceParam)).getName());

        accountSetting.setUserId(userId);

        if(req.getId() == null){
            accountSettingMapper.insert(accountSetting);
        }else{
            accountSettingMapper.updateByPrimaryKey(accountSetting);
        }
    }


    public AccountSettingVO getAccountSettingVO(Integer userId) {

        AccountSettingVO vo = new AccountSettingVO();

        AccountSetting accountSetting = accountSettingMapper.selectByUserId(userId);

        AccountSettingResp resp = new AccountSettingResp();

        if(accountSetting != null){

            BeanUtils.copyProperties(accountSetting,resp,"bankName","province","city");

            String bankName = accountSetting.getBankName();
            String provinceName = accountSetting.getProvince();
            String cityName = accountSetting.getCity();

            resp.setBank(bankMapper.selectByName(bankName));
            resp.setProvince(provinceMapper.selectByName(provinceName));
            resp.setCity(cityMapper.selectByName(cityName));
        }

        vo.setAccountSettingResp(resp);

        vo.setProvinces(provinceMapper.selectAll());

        vo.setCities(cityMapper.selectAll());

        vo.setBanks(bankMapper.selectAll());

        return vo;
    }

    //判断用户是否绑定银行卡
    public boolean isBindBank(Integer userId){
        return accountSettingMapper.selectByUserId(userId) != null;
    }

    public boolean isPersonalAccountType(Integer accountType){
        return accountType.equals(PERSONAL_ACCOUNT_TYPE);
    }

    //委托个人收款证明附件下载
    public void downloadCertification(HttpServletResponse response){
        InputStream is = cosService.getSensitivePicInputStream(CERTIFICATION_COS_PATH);

        //response.setHeader("Content-Disposition","attachment;filename="+ new String( fileName.getBytes("gb2312"), "ISO8859-1" ) );

        String fileName = CERTIFICATION_COS_PATH.substring(CERTIFICATION_COS_PATH.lastIndexOf("/")+1);

        try{
            response.setHeader("Content-Disposition","attachment;filename="+ MimeUtility.encodeWord(fileName) );

            byte[] bytes = IOUtils.toByteArray(is);

            ServletOutputStream os = response.getOutputStream();

            os.write(bytes);

            os.flush();

            os.close();
        }
        catch(Exception e){
            Log.e(e);
        }

    }


    public void handleSensitivePic(Integer userId,HttpServletResponse response){

        InputStream is = this.getPicInputStream(userId);

        if(is == null){
            return ;
        }

        try{
            byte[] bytes = IOUtils.toByteArray(is);

            ServletOutputStream os = response.getOutputStream();

            os.write(bytes);

            os.flush();

            os.close();

        }catch(Exception e){
            Log.e(e);
        }
    }

    public AccountSetting getAccountSettingByUserId(Integer userId){
        return this.accountSettingMapper.selectByUserId(userId);
    }

    //判断是否为个人账户
    public boolean isPersonalAccount(AccountSetting setting){
        Integer accountType = null;
        if(setting != null){
            accountType = setting.getAccountType();
        }
        return PERSONAL_ACCOUNT_TYPE.equals(accountType);
    }


}