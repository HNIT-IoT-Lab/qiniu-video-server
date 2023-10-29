package com.qiniu.video.entity.vo;

import lombok.Data;

import javax.validation.constraints.Pattern;

import static cn.hnit.utils.regex.RegularUtil.PhoneNumberRegularExpression;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/5/3 14:09
 */
@Data
public class PhoneVO {

    @Pattern(regexp = PhoneNumberRegularExpression, message = "电话号码格式不正确")
    private String phoneNumber;
}
