package com.qiniu.video.component;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;

/**
 * 阿里oss工具类
 */
@Configuration
@ConfigurationProperties(prefix = "oss")
@Slf4j
public class OSSOperator implements InitializingBean {

    // Endpoint，创建Bucket的时候所选择的
    @Value("${oss.endpoint}")
    private String endpoint;
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
    @Value("${oss.accessKeyId}")
    private String accessKeyId;
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${oss.bucketName}")
    private String bucketName;
    @Value("${oss.bucketDomain}")
    private String bucketDomain;
    @Value("${oss.myOssUrl}")
    private String myOssUrl;
    private OSS ossClient = null;

    @Override
    public void afterPropertiesSet() {
        //设置超时机制和重试机制
        ClientBuilderConfiguration builderConfiguration = new ClientBuilderConfiguration();
        // 超时时长
        builderConfiguration.setConnectionTimeout(5000);
        // 重试次数
        builderConfiguration.setMaxErrorRetry(3);

        // 构建OSS客户端对象
        ossClient= new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret,builderConfiguration);
    }

    /**
     * 创建存储空间
     *
     * @param bucketName 存储空间
     * @return
     */
    private String createBucketName(String bucketName) {
        // 存储空间
        final String bucketNames = bucketName;
        if (!ossClient.doesBucketExist(bucketName)) {
            // 创建存储空间
            Bucket bucket = ossClient.createBucket(bucketName);
            log.info("创建存储空间成功");
            return bucket.getName();
        }
        return bucketNames;
    }

    /**
     * 删除存储空间buckName
     *
     * @param bucketName 存储空间
     */
    public void deleteBucket(String bucketName) {
        ossClient.deleteBucket(bucketName);
        log.info("删除" + bucketName + "Bucket成功");
    }

    /**
     * 创建模拟文件夹
     *
     * @param bucketName 存储空间
     * @param folder 模拟文件夹名如"example_folder/"
     * @return 文件夹名
     */
    private String createFolder(String bucketName, String folder) {
        // 文件夹名
        final String keySuffixWithSlash = folder;
        // 判断文件夹是否存在，不存在则创建
        if (!ossClient.doesObjectExist(bucketName, keySuffixWithSlash)) {
            // 创建文件夹
            ossClient.putObject(bucketName, keySuffixWithSlash, new ByteArrayInputStream(new byte[0]));
            // 得到文件夹名
            OSSObject object = ossClient.getObject(bucketName, keySuffixWithSlash);
            String fileDir = object.getKey();
            return fileDir;
        }
        return keySuffixWithSlash;
    }


    /**
     * 根据key删除OSS服务器上的文件
     *
     * @param bucketName 存储空间
     * @param folder 模拟文件夹名 如"qj_nanjing/"
     * @param key Bucket下的文件的路径名+文件名 如："upload/cake.jpg"
     */
    public void deleteFile(String bucketName, String folder, String key) {
        ossClient.deleteObject(bucketName, folder + key);
        log.info("删除" + bucketName + "下的文件" + folder + key + "成功");
    }


    /**
     * 上传图片至OSS
     *
     * @param storePath 文件保存的路径(如: volunteer/)
     * @param file  文件
     * @return  可以访问此文件的url
     */
    public String uploadObjectOSS(String storePath, File file) {
        return upload(storePath, file, UUID.fastUUID().toString().replace("-",""),"."+ FileUtil.extName(file));
    }

    /**
     * 上传图片至OSS
     *
     * @param storePath 文件保存的路径(如: volunteer/)
     * @param file  文件
     * @return  可以访问此文件的url
     */
    public String uploadObjectOSS(String storePath, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        log.info("上传的文件名：" + originalFilename);
//        String fileName =
        return upload(storePath, file, UUID.randomUUID(true).toString(true),"." + FileUtil.extName(originalFilename));
    }

    /**
     * 上传图片至OSS并指定文件名
     *
     * @param storePath 文件保存的路径(如: volunteer/)
     * @param file  文件对象
     * @param fileName  自定义的文件名
     * @return  可以访问此文件的url
     */
    public String uploadObjectOSS(String storePath, File file, String fileName) {
        String extName = "." + FileUtil.extName(fileName);
        return upload(storePath,file,fileName,extName);
    }

    /**
     * 上传图片至OSS并指定文件名
     *
     * @param storePath 文件保存的路径(如: volunteer/)
     * @param file  文件对象
     * @param fileName  自定义的文件名
     * @return  可以访问此文件的url
     */
    public String uploadObjectOSS(String storePath, MultipartFile file, String fileName) {
        String extName = "." + FileUtil.extName(file.getOriginalFilename());
        //图片
        return upload(storePath, file, fileName, extName);
    }

    /**
     * 上传文件至OSS
     *
     * @param storePath 上传的路径
     * @param file  文件对象（File或者MultipartFile类型）
     * @param fileName  文件名
     * @param extName  文件后缀
     * @return  可以公网访问的url
     */
    private String upload(String storePath, Object file, String fileName, String extName) {
        String url = null;
        createFolder(bucketName, storePath);
        InputStream is=null;
        try {
            // 文件输入流
            is = getInputStream(file);
            // 文件大小
            Long fileSize = getFileSize(file);
            // 创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            // 上传的文件的长度
            metadata.setContentLength(is.available());
            // 指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            // 指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            // 指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            // 如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(file));
            // 指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filename/filesize=" + fileName + extName + "/" + fileSize + "Byte.");
            // 相对路径
            String relativePath = storePath + fileName +extName;
            // 上传文件 (上传文件流的形式)
            PutObjectResult putResult = ossClient.putObject(bucketName, relativePath, is, metadata);
            // 解析结果
            url = myOssUrl + relativePath;
            log.info("putResult.getETag():"+putResult.getETag());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        } finally {
            if(ObjectUtil.isNotNull(is)){
                try {
                    assert is != null;
                    is.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        return url;
    }


    private InputStream getInputStream(Object file) throws IOException {
        InputStream inputStream = null;
        if (file instanceof File) {
            inputStream = Files.newInputStream(((File) file).toPath());
        } else if (file instanceof MultipartFile){
            inputStream = ((MultipartFile) file).getInputStream();
        } else {
            throw new RuntimeException("参数必须为 File 或 MultipartFile 类型");
        }
        return inputStream;
    }

    private Long getFileSize(Object file) {
        Long size = null;
        if (file instanceof File) {
            size = ((File)file).length();
        } else if (file instanceof MultipartFile){
            size = ((MultipartFile) file).getSize();
        } else {
            throw new RuntimeException("参数必须为 File 或 MultipartFile 类型");
        }
        return size;
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * param fileName 文件名
     * return 文件的contentType
     */
    private String getContentType(Object file) {
        // 文件的后缀名
        String fileExtension = null;
        if (file instanceof File) {
            fileExtension = FileUtil.extName((File) file);
        } else if (file instanceof MultipartFile){
            fileExtension = FileUtil.extName(((MultipartFile) file).getOriginalFilename());
        } else {
            throw new RuntimeException("参数必须为 File 或 MultipartFile 类型");
        }

        if ("bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if ("gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if ("jpeg".equalsIgnoreCase(fileExtension) || "jpg".equalsIgnoreCase(fileExtension)
                || ".png".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        }
        if ("png".equalsIgnoreCase(fileExtension)) {
            return "image/png";
        }
        if ("html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if ("txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if ("vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if ("ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if ("doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if ("xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        // 默认返回类型
        return "";
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public void setBucketDomain(String bucketDomain) {
        this.bucketDomain = bucketDomain;
    }
}