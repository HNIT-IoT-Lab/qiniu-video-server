package cn.hnit.common.exception.base;


import cn.hnit.common.exception.IException;
import cn.hnit.common.resultx.constant.RspEnum;
import cn.hnit.common.resultx.constant.SysCode;

/**
 * 异常基类
 *
 * @author 梁峰源
 * @since 2022-08-22 21:54
 **/
public class BaseException extends RuntimeException implements IException {

    private static final long serialVersionUID = -8262387578498619440L;


    /**
     * 异常信息
     */
    private final String msg;
    /**
     * 具体异常码
     */
    private final int code;

    /**
     * 异常体
     */
    private Object data;

    public BaseException() {
        super("系统异常");
        this.code = SysCode.FAILED;
        this.msg = "系统异常";
    }

    public BaseException(String msg) {
        super(msg);
        this.code = SysCode.FAILED;
        this.msg = msg;
    }

    public BaseException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BaseException(int code, String msg, Object data) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    public BaseException(RspEnum exp) {
        super(exp.getMsg());
        this.code = exp.getCode();
        this.msg = exp.getMsg();
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public Object getData() {
        return data;
    }

}
