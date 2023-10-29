package com.qiniu.video.service.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

/**
 * @author: 梁峰源
 * @date: 2022年2月13日20:23:55
 * Description: 发送qq邮件
 */
@Component
@Slf4j
public class MailService {

    @Resource
    private JavaMailSender mailSender;


    /**
     * 1、发送普通文本邮件
     *
     * @param mailFrom     发件人邮箱
     * @param mailFromNick 发件人昵称
     * @param mailTo       收件人邮箱，可以多个，传入一个String数组
     * @param cc           抄送人邮箱(可为空，方法内部处理)
     * @param subject      主题(邮件标题)
     * @param content      内容
     */
    public void sendSimpleMail(String mailFrom, String mailFromNick, String[] mailTo, String cc,
                               String subject, String content) {
        try {
            // 多个收件人之间用英文逗号分隔
            for (String address : mailTo) {
                // 简单邮件信息类
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                // HTML邮件信息类
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                // 昵称
                mimeMessageHelper.setFrom(new InternetAddress(mailFromNick + " <" + mailFrom + ">"));
                mimeMessageHelper.setTo(address);
                if (!StringUtils.isEmpty(cc)) {
                    mimeMessageHelper.setCc(cc);
                }
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(content);

                mailSender.send(mimeMessage);
            }
        } catch (Exception e) {
            log.error("发送邮件失败：{}", e.getMessage());
        }
    }

    /**
     * 2、发送带附件的邮件
     *
     * @param mailFrom     发件人
     * @param mailFromNick 发件人昵称
     * @param mailTo       收件人
     * @param cc           抄送人
     * @param subject      主题
     * @param content      content
     * @param files        附件的地址集合
     */
    public void sendMailWithAttachments(String mailFrom, String mailFromNick, String mailTo, String cc,
                                        String subject, String content, List<File> files) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            /*
            第二个参数true表示构造一个multipart message类型的邮件，multipart message类型的邮件包含多个正文、附件以及内嵌资源，
            邮件的表现形式更丰富
             */
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(new InternetAddress(mailFromNick + " <" + mailFrom + ">"));
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content);

            // 设置多个收件人
            String[] toAddress = mailTo.split(",");
            mimeMessageHelper.setTo(toAddress);
            if (!StringUtils.isEmpty(cc)) {
                mimeMessageHelper.setCc(cc);
            }
            // 添加附件
            if (null != files) {
                for (File file : files) {
                    // 通过addAttachment方法添加附件
                    mimeMessageHelper.addAttachment(file.getName(), file);
                }
            }
        } catch (javax.mail.MessagingException e) {
            e.printStackTrace();
        }
        //发送邮件
        mailSender.send(mimeMessage);

    }

    /**
     * 3、正文内容带图片
     *
     * @param mailFrom     发件人
     * @param mailFromNick 发件人昵称
     * @param mailTo       摘抄人
     * @param cc           抄送人
     * @param subject      主题
     * @param content      内容
     * @param imagePaths   图片地址
     * @param imageId      图片编号
     */
    public void sendMailWithImage(String mailFrom, String mailFromNick, String mailTo, String cc, String subject,
                                  String content, String[] imagePaths, String[] imageId) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(new InternetAddress(mailFromNick + " <" + mailFrom + ">"));
            // 设置多个收件人
            String[] toAddress = mailTo.split(",");
            mimeMessageHelper.setTo(toAddress);
            if (!StringUtils.isEmpty(cc)) {
                mimeMessageHelper.setCc(cc);
            }
            mimeMessageHelper.setSubject(subject);
            // 第二个参数为true表示邮件正文是html格式的，默认是false
            mimeMessageHelper.setText(content, true);

            // 添加图片
            if (imagePaths != null && imagePaths.length != 0) {
                for (int i = 0; i < imagePaths.length; i++) {
                    // 通过FileSystemResource构造静态资源
                    FileSystemResource fileSystemResource = new FileSystemResource(imagePaths[i]);
                    // 调用addInline方法将资源加入邮件对象中
                    mimeMessageHelper.addInline(imageId[i], fileSystemResource);
                }
            }

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            log.error("邮件发送失败：{}",e.getMessage());
        }
    }

    /**
     * 4、使用Themeleaf构建邮件模板。需额外加spring-boot-starter-thymeleaf依赖
     *
     * @param mailFrom
     * @param mailFromNick
     * @param mailTo
     * @param cc
     * @param subject
     * @param content
     */
    public void sendHtmlMailThymeLeaf(String mailFrom, String mailFromNick, String mailTo, String cc,
                                      String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(new InternetAddress(mailFromNick + " <" + mailFrom + ">"));
            // 设置多个收件人
            String[] toAddress = mailTo.split(",");
            mimeMessageHelper.setTo(toAddress);
            if (!StringUtils.isEmpty(cc)) {
                mimeMessageHelper.setCc(cc);
            }
            mimeMessageHelper.setSubject(subject);
            // 第二个参数为true表示邮件正文是html格式的，默认是false
            mimeMessageHelper.setText(content, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("邮件发送失败：{}",e.getMessage());
        }
    }
}

