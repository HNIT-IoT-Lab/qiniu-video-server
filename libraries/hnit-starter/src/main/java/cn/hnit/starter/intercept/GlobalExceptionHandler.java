package cn.hnit.starter.intercept;

import cn.hnit.common.exception.base.AppException;
import cn.hnit.common.exception.base.ParamDefectException;
import cn.hnit.common.exception.biz.AuthException;
import cn.hnit.common.resultx.ResponseMsg;
import cn.hnit.starter.intercept.ban.exeception.BanException;
import cn.hnit.utils.LocalThreadUtil;
import cn.hnit.utils.common.bean.HeaderParam;
import cn.hnit.utils.constant.SysCode;
import cn.hnit.utils.logutil.LogUtils;
import cn.hutool.core.text.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 全局异常拦截
 *
 * @author 梁峰源
 * @see cn.hnit.common.exception.base.BaseException
 * @see cn.hnit.common.exception.base.AppException
 * @see org.springframework.validation.BindException
 * @since 2022年9月25日17:28:36
 */
@Slf4j
@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 处理自定义异常 BanException 验证码验证失败异常
     */
    @ExceptionHandler(value = BanException.class)
    public ResponseMsg<String> banExceptionHandler(BanException e) {
        logErr(e);
        ResponseMsg<String> msg = new ResponseMsg<>(e.getCode(), e.getMsg());
        msg.setData(StrPool.EMPTY_JSON);
        return msg;
    }


    /**
     * 处理自定义异常 AuthException
     */
    @ExceptionHandler(value = AuthException.class)
    public ResponseMsg<String> authExceptionErrorHandler(AuthException e) {
        logErr(e);
        ResponseMsg<String> msg = new ResponseMsg<>(e.getCode(), e.getMsg());
        msg.setData(StrPool.EMPTY_JSON);
        msg.setTraceId(LogUtils.getTraceId());
        return msg;
    }

    /**
     * 处理自定义参数确实异常 paramDefectException
     */
    @ExceptionHandler(value = ParamDefectException.class)
    public ResponseMsg<String> paramDefectExceptionErrorHandler(ParamDefectException e) {
        logErr(e);
        ResponseMsg<String> msg = new ResponseMsg<>(e.getCode(), e.getMsg());
        msg.setData(StrPool.EMPTY_JSON);
        msg.setTraceId(LogUtils.getTraceId());
        return msg;
    }


    /**
     * 处理自定义异常
     * 统一处理为可展示异常
     */
    @ExceptionHandler(AppException.class)
    public ResponseMsg<Object> appExceptionHandler(AppException e) {
        logErr(e);
        ResponseMsg<Object> msg = new ResponseMsg<>(e.getCode(), e.getMsg());
        msg.setData(e.getData() != null ? e.getData() : StrPool.EMPTY_JSON);
        msg.setTraceId(LogUtils.getTraceId());
        return msg;
    }

    /**
     * 处理绑定属性效验不通过异常
     */
    @ExceptionHandler(value = {BindException.class, MethodArgumentNotValidException.class})
    public ResponseMsg<String> bindExceptionErrorHandler(Exception e) {
        logErr(e);
        ResponseMsg<String> ex;
        if (e instanceof BindException) {
            ex = new ResponseMsg<>(SysCode.FAILED, ((BindException) e).getFieldError().getDefaultMessage());
        } else if (e instanceof MethodArgumentNotValidException) {
            String msg = Optional.ofNullable(((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage()).orElse("Illegal request");
            ex = new ResponseMsg<>(SysCode.FAILED, msg);
        } else {
            ex = new ResponseMsg<>(SysCode.FAILED, e.getMessage());
        }
        ex.setData(StrPool.EMPTY_JSON);
        ex.setTraceId(LogUtils.getTraceId());
        return ex;
    }

    /**
     * 请求不合法处理
     */
    @ExceptionHandler(value = {ServletException.class, HttpMessageNotReadableException.class})
    public ResponseMsg<String> servletExceptionHandler(Exception e) {
        logErr(e);
        ResponseMsg<String> msg = new ResponseMsg<>(SysCode.FAILED, "Illegal request");
        msg.setData(StrPool.EMPTY_JSON);
        msg.setTraceId(LogUtils.getTraceId());
        return msg;
    }

    /**
     * ClientAbortException
     */
    @ExceptionHandler(value = ClientAbortException.class)
    public void handler(ClientAbortException e) {
        logErr(e);
    }

    /**
     * 处理绑定属性效验不通过异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseMsg<String> runtimeExceptionErrorHandler(Exception ex, HttpServletRequest request) {
        printErr(ex);
        ResponseMsg<String> msg = new ResponseMsg<>(SysCode.FAILED, ex.getMessage());
        msg.setData(StrPool.EMPTY_JSON);
        msg.setTraceId(LogUtils.getTraceId());
        return msg;
    }

    /**
     * 处理绑定属性效验不通过异常
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseMsg<String> exceptionErrorHandler(Exception ex, HttpServletRequest request) {
        printErr(ex);
        ResponseMsg<String> msg = new ResponseMsg<>(SysCode.FAILED, "服务器异常");
        msg.setData(StrPool.EMPTY_JSON);
        msg.setTraceId(LogUtils.getTraceId());
        return msg;
    }

    /**
     * 出错后打印交易记录与请求头
     */
    private void printErr(Exception e) {
        log.error("服务器异常======>>> 请求参数:{} \n请求头信息:{}", LocalThreadUtil.getTrade(), LocalThreadUtil.getLocalObj(HeaderParam.class), e);
    }

    /**
     * 授权异常处理
     */
    private void logErr(Exception e) {
        log.error("unexpected error:{}  msg:{}\n请求参数:{} \n请求头信息:{},异常信息", e.getClass(), e.getMessage(), LocalThreadUtil.getTrade(), LocalThreadUtil.getLocalObj(HeaderParam.class), e);
    }
}
