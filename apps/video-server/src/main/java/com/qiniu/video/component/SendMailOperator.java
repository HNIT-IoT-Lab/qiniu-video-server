package com.qiniu.video.component;

import cn.hutool.core.util.ObjectUtil;
import com.qiniu.video.service.common.MailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: 梁峰源
 * @date: 2022/2/13 21:54
 * Description: 发送邮件帮助类
 */
@Component
public class SendMailOperator {

    @Value("${spring.mail.username}")
    private String mailForm;//发件人邮箱
    @Value("${spring.mail.mailFromNick}")
    private String mailFromNick;//发件人昵称
    @Autowired
    private MailService mailService;

    /**
     * 发送邮件给逾期用户
     *
     * @param mailTo 收件人邮箱
     */
    public void sendOverTimeAlarm(String[] mailTo) {
        String subject = "爱心雨伞借取超时提醒";//主题
        //邮件内容
        String content = "您好，您借取爱心雨伞的时间已经超过48小时了，请您尽快将爱心雨伞归还哦，借取时间超过60小时将会有管理员联系您，并且以后将不能再借取爱心雨伞了哦！";
        mailService.sendSimpleMail(mailForm, mailFromNick, mailTo, null, subject, content);
    }

    /**
     * 发送邮件给逾期用户
     *
     * @param mailTo 收件人邮箱
     */
    public void sendOverTimeAlarm(String[] mailTo, String subject, String content) {
        if (ObjectUtil.isNull(subject) || StringUtils.isEmpty(subject)) {
            subject = "爱心雨伞借取超时提醒";//主题
        }
        // 邮件内容
        if (ObjectUtil.isNull(content) || StringUtils.isEmpty(content)) {
            content = "您好，您借取爱心雨伞的时间已经超过48小时了，请您尽快将爱心雨伞归还哦，借取时间超过60小时将会有管理员联系您，并且以后将不能再借取爱心雨伞了哦！";
        }
        mailService.sendSimpleMail(mailForm, mailFromNick, mailTo, null, subject, content);
    }


    /**
     * 发送邮件给管理员
     *
     * @param content 逾期用户的信息
     */
    public void send2Admin(String content) {
        String subject = "管理员您好，爱心雨伞借取超时提醒";//主题
        //邮件内容
        mailService.sendSimpleMail(mailForm, mailFromNick, new String[]{mailForm}, null, subject, content);
    }

    /**
     * 锁机运行状态消息发送给管理员
     *
     * @param content 锁机消息
     */
    public void sendLockMsg2Admin(String content) {
        // 主题
        String subject = "管理员您好，爱心雨伞锁机提醒";
        // 邮件内容
        mailService.sendSimpleMail(mailForm, mailFromNick, new String[]{mailForm}, null, subject, content);
    }
}
