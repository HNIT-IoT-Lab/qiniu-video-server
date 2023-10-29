package cn.hnit.common.exception.base;


import cn.hnit.common.resultx.constant.RspEnum;
import cn.hnit.common.resultx.constant.SysCode;

/**
 * 参数异常
 *
 * @author Admin
 */
public class ParamDefectException extends BaseException {

    private static final long serialVersionUID = -5875371379845226068L;

    public ParamDefectException() {
        super(SysCode.FAILED, "参数非法");
    }

    public ParamDefectException(String msg) {
        super(msg);
    }

    public ParamDefectException(int code, String msg) {
        super(code, msg);
    }

    public ParamDefectException(RspEnum exp) {
        super(exp);
    }
}
