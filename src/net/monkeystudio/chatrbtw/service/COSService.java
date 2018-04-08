package net.monkeystudio.chatrbtw.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.request.GetFileInputStreamRequest;
import com.qcloud.cos.request.UpdateFileRequest;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.sign.Credentials;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.service.bean.cos.COSUploadSuccessResp;
import net.monkeystudio.service.CfgService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;

/**
 * Created by bint on 2017/12/12.
 */
@Service
public class COSService {

    private static String SECRET_ID ;
    private static String SECRET_KEY ;
    private static Long APP_ID ;
    private static String REGION ;
    private static String ORDINARY_BUCKET_NAME;
    private static String SENSITIVE_BUCKET_NAME;

    private COSClient cosClient = null;

    @Autowired
    private CfgService cfgService;

    @PostConstruct
    public void init(){
        SECRET_ID = cfgService.get(GlobalConfigConstants.COS_SECRET_ID_KEY);
        SECRET_KEY = cfgService.get(GlobalConfigConstants.COS_SECRET_KEY_KEY);
        APP_ID = cfgService.getLong(GlobalConfigConstants.COS_APP_ID_KEY);
        REGION = cfgService.get(GlobalConfigConstants.COS_REGION_KEY);


        Credentials cred = new Credentials(APP_ID, SECRET_ID, SECRET_KEY);

        // 初始化客户端配置
        ClientConfig clientConfig = new ClientConfig();
        // 设置bucket所在的区域，比如华南园区：gz； 华北园区：tj；华东园区：sh ；
        clientConfig.setRegion(REGION);
        //clientConfig.setDownCosEndPointDomain("gz.file.myqcloud.com");

        // 初始化cosClient

        cosClient = new COSClient(clientConfig, cred);
        ORDINARY_BUCKET_NAME = cfgService.get(GlobalConfigConstants.COS_BUCKET_NAME_ORDINARY_KEY);
        SENSITIVE_BUCKET_NAME = cfgService.get(GlobalConfigConstants.COS_BUCKET_NAME_SENSITIVE_KEY);
    }


    private String upload(String path , byte[] contentBufer , String bufferName){
        UploadFileRequest request = new UploadFileRequest(bufferName, path,contentBufer );

        String uploadFileRet = cosClient.uploadFile(request);

        if(uploadFileRet.indexOf("SUCCESS") != -1){
            COSUploadSuccessResp cosUploadSuccessResp = JsonUtil.readValue(uploadFileRet, COSUploadSuccessResp.class);
        }

        UpdateFileRequest updateFileRequest = new UpdateFileRequest(bufferName, path);
        updateFileRequest.setContentDisposition("inline;filename=*");
        String updateResult = cosClient.updateFile(updateFileRequest);

        return uploadFileRet;
    }

    public String uploadFile(String path , byte[] contentBufer ){

        return this.upload(path,contentBufer,ORDINARY_BUCKET_NAME);

    }

    public String uploadSensitiveFile(String path , byte[] contentBufer){
        return this.upload(path,contentBufer,SENSITIVE_BUCKET_NAME);
    }

    //获取下载文件的输入流
    public InputStream getSensitivePicInputStream(String cosPath){

        GetFileInputStreamRequest request = new GetFileInputStreamRequest(SENSITIVE_BUCKET_NAME,cosPath);

        request.setUseCDN(false);

        InputStream fileInputStream = null;

        try {
            fileInputStream = cosClient.getFileInputStream(request);
        } catch (Exception e) {
            Log.e(e);
        }

        return fileInputStream;
    }




}
