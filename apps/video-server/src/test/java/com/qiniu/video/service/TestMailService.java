package com.qiniu.video.service;

import com.qiniu.video.service.common.MailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/5/6 21:17
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMailService {
    @Value("${spring.mail.username}")
    private String mailForm;//发件人邮箱
    @Value("${spring.mail.mailFromNick}")
    private String mailFromNick;//发件人昵称
    @Autowired
    private MailService mailService;

    @Test
    public void test01() {
        sendOverTimeAlarm(new String[]{"fengyuan-liang@foxmail.com"});
    }

    /**
     * 发送邮件给逾期用户
     * @param mailTo 收件人邮箱
     */
    public void sendOverTimeAlarm(String[] mailTo){
        String subject = "爱心雨伞借取超时提醒";//主题
        //邮件内容
        String content = "您好，您借取爱心雨伞的时间已经超过48小时了，请您尽快将爱心雨伞归还哦，借取时间超过60小时将会有管理员联系您，并且以后将不能再借取爱心雨伞了哦！";
        mailService.sendSimpleMail(mailForm,mailFromNick,mailTo,null,subject,content);
    }
}
