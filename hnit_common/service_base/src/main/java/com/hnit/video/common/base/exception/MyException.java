package com.hnit.video.common.base.exception;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常类抛出异常处理类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class MyException extends RuntimeException {

    @ApiModelProperty(value = "状态码")
    private Integer code;

    @ApiModelProperty(value = "提示消息")
    private String message;

    public MyException(String message) {
        this.code = 20001;
        this.message = message;
    }
}