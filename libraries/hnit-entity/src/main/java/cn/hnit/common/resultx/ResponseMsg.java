package cn.hnit.common.resultx;

import cn.hnit.common.resultx.constant.RspEnum;
import cn.hnit.common.resultx.constant.SysCode;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 这种方式作为实体类的好处是有泛型的返回，可以一目了然看到接口的结构<br/>
 * Spring官方也是采用这种形式
 *
 * @author 梁峰源
 * @see org.springframework.http.ResponseEntity
 * @since 2022-08-22 21:54
 **/
@Data
@Accessors(chain = true)
public class ResponseMsg<T> implements Serializable {
    private static final long serialVersionUID = 4250719891313555820L;
    private static final String SUCCESS_MSG = "success";
    @SuppressWarnings("unchecked")
    public ResponseMsg() {
        code = SysCode.SUCCESSED;
        msg = SUCCESS_MSG;
        data = (T) "";
    }
    @SuppressWarnings("unchecked")
    public ResponseMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        data = (T) "";
    }

    public ResponseMsg(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    @SuppressWarnings("unchecked")
    public ResponseMsg(RspEnum exp) {
        code = exp.getCode();
        msg = exp.getMsg();
        data = (T) "";
    }

    public ResponseMsg(T t) {
        data = t;
        code = SysCode.SUCCESSED;
        msg = SUCCESS_MSG;
    }

    public static <T> ResponseMsg<T> success(T t) {
        return new ResponseMsg<>(t);
    }

    public static ResponseMsg<Object> success() {
        ResponseMsg<Object> responseMsg = new ResponseMsg<>();
        responseMsg.setCode(SysCode.SUCCESSED);
        responseMsg.setMsg(SUCCESS_MSG);
        responseMsg.setData(StrUtil.EMPTY_JSON);
        return responseMsg;
    }

    public static <T> ResponseMsg<T> successWithEmpty() {
        ResponseMsg<T> responseMsg = new ResponseMsg<>();
        responseMsg.setCode(SysCode.SUCCESSED);
        responseMsg.setMsg(SUCCESS_MSG);
        return responseMsg;
    }

    public static ResponseMsg<Void> success(Void v) {
        ResponseMsg<Void> responseMsg = new ResponseMsg<>();
        responseMsg.setCode(SysCode.SUCCESSED);
        responseMsg.setMsg(SUCCESS_MSG);
        responseMsg.setData(v);
        return responseMsg;
    }

    public static ResponseMsg<Void> fail() {
        ResponseMsg<Void> responseMsg = new ResponseMsg<>();
        responseMsg.setCode(SysCode.FAILED);
        responseMsg.setMsg("失败");
        return responseMsg;
    }

    /**
     * 返回结果集
     */
    private T data;
    /**
     * 返回消息
     */
    private String msg;
    /**
     * 响应码
     */
    private Integer code;
    /**
     * 链路id
     */
    private String traceId;
}
