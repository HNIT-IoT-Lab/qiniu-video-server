package com.qiniu.video.service.impl;

import cn.hnit.common.exception.base.AppException;
import cn.hnit.common.redis.operator.RedisOperator;
import cn.hnit.core.LoginFactory;
import cn.hnit.entity.LoginVO;
import cn.hnit.sdk.orm.mongodb.entity.BaseEntity;
import cn.hnit.utils.AssertUtil;
import cn.hnit.utils.context.UserContext;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.qiniu.video.component.OSSOperator;
import com.qiniu.video.component.TencentSmsOperator;
import com.qiniu.video.dao.FollowDao;
import com.qiniu.video.dao.UserDao;
import com.qiniu.video.entity.model.Follow;
import com.qiniu.video.entity.model.User;
import com.qiniu.video.service.FilesService;
import com.qiniu.video.service.UserService;
import com.qiniu.video.utils.AESUtil;
import com.qiniu.video.utils.RandomUsernameGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cn.hnit.video.kodo.service.QiniuKodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/27 21:03
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    /**
     * 验证码的有效时间，单位是分钟
     */
    @Value("${tencent-cloud.expires}")
    private Integer expires;
    @Value("${oss.storePath}")
    private String storePath;

    @Autowired
    private UserDao userDao;

    @Autowired
    private FollowDao followDao;

    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private ThreadPoolExecutor asyncExecutor;
    @Autowired
    private OSSOperator ossOperator;
    @Autowired
    private TencentSmsOperator smsOperator;
    @Autowired
    private QiniuKodoService qiniuKodoService;
    @Autowired
    private FilesService userFileService;


    public User FindByUserName(String userName) {
        return userDao.findOne(Query.query(Criteria.where(User.Fields.userName).is(userName)));
    }

    public LoginVO Login(Map<String, Object> paramMap) {
        Object loginSign = paramMap.get("loginSign");
        AssertUtil.notNull(loginSign, "请选择登录类型");
        return LoginFactory.getLoginStrategy(String.valueOf(loginSign)).login(paramMap);
    }

    @Override
    public LoginVO LogOut(Map<String, Object> paramMap) {
        Object loginSign = paramMap.get("loginSign");
        AssertUtil.notNull(loginSign, "请选择登出类型");
        return LoginFactory.getLoginStrategy(String.valueOf(loginSign)).logOut(paramMap);
    }

    public User AddOrDefault(String phoneNumber) {
        User user = FindByPhoneNumber(phoneNumber);
        return user == null ? RegisterByPhoneNumber(phoneNumber) : user;
    }

    public User Register(User user) {
        return userDao.save(user);
    }

    public User RegisterByPhoneNumber(String phoneNumber) {
        String DEFAULT_AVATAR = "https://cdn.fengxianhub.top/resources-master/OIP.jpg";
        return userDao.save(User.builder()
                .phoneNumber(AESUtil.aesEncrypt(phoneNumber))
                .userName(RandomUsernameGenerator.generateRandomUsername())
                .avatar(DEFAULT_AVATAR)
                .build());
    }

    public User UpdateUserName(String userName) {
        User user = FindById(UserContext.getUserId());
        userDao.updateById(user.setUserName(userName));
        return user;
    }


    public String SendSmsCode(String phoneNumber) {
        String md5Hex = DigestUtil.md5Hex(phoneNumber);
        if (!StringUtils.isEmpty(redisOperator.get(md5Hex))) {
            throw AppException.pop("验证码已发送");
        }
        // 生成验证码
        String code = RandomUtil.randomNumbers(6);
        asyncExecutor.execute(() -> {
            try {
                smsOperator.sendSms(phoneNumber, code);
                redisOperator.set(md5Hex, code, expires * 60);
            } catch (Exception e) {
                log.error("验证码发送失败", e);
                throw AppException.pop("验证码发送失败");
            }
        });
        return code;
    }


    public User FindByPhoneNumber(String phoneNumber) {
        return userDao.findOne(Query.query(Criteria.where(User.Fields.phoneNumber).is(AESUtil.aesEncrypt(phoneNumber))));
    }

    public User FindById(Long id) {
        return userDao.findOne(Query.query(Criteria.where(BaseEntity.Fields.id).is(id)));
    }

    public User updateAvatar(MultipartFile file) {
        String avatarUrl = ossOperator.uploadObjectOSS(storePath, file);
        userDao.updateByCriteria(
                Update.update(User.Fields.avatar, avatarUrl),
                Criteria.where(BaseEntity.Fields.id).is(UserContext.getUserId()));
        return FindById(UserContext.getUserId());
    }

    @Override
    public String upload(MultipartFile file) {
        AssertUtil.notNull(file, "上传文件不能为空");

        String filePath;
        try {
            filePath = qiniuKodoService.uploadFromInputStream(file.getInputStream(), file.getOriginalFilename(), file.getSize());
        } catch (Exception e) {
            log.error("上传失败, {}", e.getMessage());
            throw new AppException("上传失败");
        }
        // 保存用户上传得文件
        String finalFilePath = filePath;
        asyncExecutor.submit(() -> userFileService.SaveUserFile(finalFilePath));
        return filePath;
    }

    @Override
    public User updateUserName(String userName) {
        AssertUtil.notBlank(userName, "用户名不能为空");
        userDao.updateByCriteria(
                Update.update(User.Fields.userName, userName),
                Criteria.where(BaseEntity.Fields.id).is(UserContext.getUserId()));
        return FindById(UserContext.getUserId());
    }

    @Override
    public List<User> FindByIds(List<Long> userId) {
        return userDao.find(Query.query(Criteria.where(BaseEntity.Fields.id).in(userId)));
    }

    @Override
    public Follow followUser(String followUserId) {
        //拿到当前用户
        Long userId = UserContext.getUserId();
        return followDao.save(Follow.builder()
                .userId(String.valueOf(userId))
                .followUserId(followUserId)
                .build());
    }

    @Override
    public List<User> getFollowUserList() {
        Long userId = UserContext.getUserId();
        List<Follow> followList = followDao.find(Query.query(Criteria.where(Follow.Fields.userId).is(userId.toString())));
        //过滤
        List<String> followUserId = followList.stream().map(Follow::getFollowUserId).collect(Collectors.toList());

        List<Long> longList = followUserId.stream().map(Long::parseLong).collect(Collectors.toList());

        //去用户表拿数据返回即可
        return userDao.find(Query.query(Criteria.where(BaseEntity.Fields.id).in(longList)));
    }
}
