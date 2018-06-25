package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.JsonHelper;
import net.monkeystudio.base.utils.Log;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by bint on 2018/6/14.
 */
@Service
public class UploadService {

    @Autowired
    private COSService cosService;

    /**
     * 上传非敏感的图片
     * @param file
     * @param directory
     * @return
     */
    public String uploadPic(MultipartFile file, String directory ,String fileName){

        String path = this.connect(directory, fileName);

        String cosPicUrl = null;
        try {
            String pathJson = cosService.uploadFile(path , IOUtils.toByteArray(file.getInputStream()));
            String dataJson = JsonHelper.getStringFromJson(pathJson, "data");
            cosPicUrl = JsonHelper.getStringFromJson(dataJson,"access_url");
        } catch (IOException e) {
            Log.e(e);
        }

        return cosPicUrl;
    }


    /**
     * 上传敏感的图片
     * @param file
     * @param directory
     * @return
     */
    public String uploadSensitiveFile(MultipartFile file, String directory ,String fileName){

        String path = this.connect(directory, fileName);

        String cosPicUrl = null;
        try {
            String pathJson = cosService.uploadSensitiveFile(path , IOUtils.toByteArray(file.getInputStream()));
            String dataJson = JsonHelper.getStringFromJson(pathJson, "data");
            cosPicUrl = JsonHelper.getStringFromJson(dataJson,"access_url");
        } catch (IOException e) {
            Log.e(e);
        }

        return cosPicUrl;
    }



    private String connect(String directory  , String fileName){

        if(directory.endsWith("/")){
            String path = directory + fileName;
            return path;
        }

        return directory + "/" + fileName;
    }



}
