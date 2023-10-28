package com.hnit.video.oss.controller;

import com.hnit.video.oss.service.OssService;
import com.hnit.video.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "对象存储接口")
@RestController
@RequestMapping("admin/oss")
public class OssController {
    private OssService ossService;

    @Autowired
    public void setOssService(OssService ossService) {
        this.ossService = ossService;
    }

    @ApiOperation(value = "上传文件头像")
    @PostMapping("uploadAvatarFile")
    public Result uploadAvatarFile(
            @ApiParam(name = "file", value = "要上传的头像文件", required = true)
            @RequestBody MultipartFile file) {

        // url是文件的路径,表中存储的就是路径
        String url = ossService.uploadAvatarFile(file);
        return Result.success(url);
    }
}
