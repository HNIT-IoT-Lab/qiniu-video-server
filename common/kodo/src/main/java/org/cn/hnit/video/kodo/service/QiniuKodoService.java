package org.cn.hnit.video.kodo.service;

import com.qiniu.common.QiniuException;

import java.io.InputStream;

public interface QiniuKodoService {

    void upload(String localFilePath);

    void listSpaceFiles();

    String getFileUrl(String fileName);

    void deleteFile(String[] fileList);

    String uploadFromInputStream(InputStream in, String filePath, long fileSize) throws QiniuException;
}
