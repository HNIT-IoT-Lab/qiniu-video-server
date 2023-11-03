package com.qiniu.video.service.impl;

import cn.hnit.utils.context.UserContext;
import com.qiniu.video.dao.UserFilesDao;
import com.qiniu.video.entity.model.UserFile;
import com.qiniu.video.entity.constant.UserFileConstant;
import com.qiniu.video.service.FilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class FilesServiceImpl implements FilesService {


    @Autowired
    private UserFilesDao userFilesDao;


    @Override
    public void SaveUserFile(String filePath) {
        UserFileConstant.UserFileKind userFileKind = UserFileConstant.UserFileKind.of(filePath);
        userFilesDao.upsert(UserFile.builder()
                .userId(UserContext.getUserId())
                .fileKind(userFileKind)
                .cover(GenCover(filePath, userFileKind))
                .filePath(filePath)
                .build());
    }

    private String GenCover(String filePath, UserFileConstant.UserFileKind userFileKind) {
        if (userFileKind == UserFileConstant.UserFileKind.VIDEO) {
            // 用视频第一帧作为封面
            return filePath + "?vframe/jpg/offset/0";
        }
        return "";
    }
}
