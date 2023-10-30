package org.cn.hnit.video.kodo.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.cn.hnit.video.kodo.config.QiniuKodoConfig;
import org.cn.hnit.video.kodo.service.QiniuKodoService;
import org.cn.hnit.video.kodo.utils.FileNameGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class QiniuKodoServiceImpl implements QiniuKodoService {

    /**
     * 获取七牛云存储相关变量
     */
    @Value("${qiniu.kodo.accessKey}")
    String accessKey;
    @Value("${qiniu.kodo.secretKey}")
    String secretKey;
    @Value("${qiniu.kodo.bucket}")
    String bucketName;
    @Value("${qiniu.kodo.domain}")
    String domain;

    /**
     * 构造一个带指定 Region 对象的配置类
     * 因为配置的是华东机房，所以为Region.region0()
     */
    Configuration cfg = new Configuration(Region.region0());

    /**
     * 文件名前缀
     */
    String prefix = "";
    /**
     * 每次迭代的长度限制，最大1000，推荐值 1000
     */
    int limit = 1000;
    /**
     * 指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
     */
    String delimiter = "";


    private final UploadManager uploadManager = new UploadManager(cfg);

    /**
     * 列举空间文件列表
     */
    @Override
    public void listSpaceFiles() {
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(bucketName, prefix, limit, delimiter);
        while (fileListIterator.hasNext()) {
            //处理获取的file list结果
            FileInfo[] items = fileListIterator.next();
            for (FileInfo item : items) {
                System.out.println(item.key);
                System.out.println(item.fsize / 1024 + "kb");
                System.out.println(item.mimeType);
            }
        }
    }

    /**
     * 上传本地文件
     */
    @Override
    public void upload(String localFilePath) {
        /**
         * 如果是Windows情况下，格式是 D:\\qiniu\\test.png
         * 以文件最低级目录名作为文件名
         */
        String[] strings = localFilePath.split("\\\\");
        String key = strings[strings.length - 1];

        try {
            Response response = uploadManager.put(localFilePath, key, genUploadToken());
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }

    public String uploadFromInputStream(InputStream in, String filePath, long fileSize) throws QiniuException {
        String generateFileName = bucketName + "/" + FileNameGenerator.generateFileName(filePath);
        Response response = uploadManager.put(in, fileSize, generateFileName, genUploadToken(), null, "", false);
        if (response.statusCode == 200) {
            return "http://" + domain + "/" + generateFileName;
        }
        throw new RuntimeException(String.format("上传失败，%s", response.bodyString()));
    }

    private String genUploadToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucketName);
    }

    /**
     * 获取下载文件的链接
     *
     * @param fileName 文件名称
     * @return 下载文件的链接
     */
    @Override
    public String getFileUrl(String fileName) {
        String encodedFileName = null;
        try {
            encodedFileName = URLEncoder.encode(fileName, "utf-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String finalUrl = String.format("%s/%s", "http://" + domain, encodedFileName);
        System.out.println(finalUrl);
        return finalUrl;
    }

    /**
     * 批量删除空间中的文件
     *
     * @param fileList 文件名称列表
     */
    @Override
    public void deleteFile(String[] fileList) {
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            //单次批量请求的文件数量不得超过1000
            BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
            batchOperations.addDeleteOp(bucketName, fileList);
            Response response = bucketManager.batch(batchOperations);
            BatchStatus[] batchStatusList = response.jsonToObject(BatchStatus[].class);
            for (int i = 0; i < fileList.length; i++) {
                BatchStatus status = batchStatusList[i];
                String key = fileList[i];
                System.out.print(key + "\t");
                if (status.code == 200) {
                    System.out.println("delete success");
                } else {
                    System.out.println(status.data.error);
                }
            }
        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
    }
}
