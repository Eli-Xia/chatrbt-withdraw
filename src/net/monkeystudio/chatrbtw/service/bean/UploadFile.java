package net.monkeystudio.chatrbtw.service.bean;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by bint on 2018/6/14.
 */
public class UploadFile {

    private MultipartFile multipartFile;

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }
}
