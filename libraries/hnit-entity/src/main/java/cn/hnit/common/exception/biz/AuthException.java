package cn.hnit.common.exception.biz;

import cn.hnit.common.exception.base.BaseException;
import cn.hnit.common.resultx.constant.SysCode;

/**
 * 认证异常
 *
 * @author Admin
 */
public class AuthException extends BaseException {

    private static final long serialVersionUID = -5875371379845226068L;

    public AuthException() {
        super(SysCode.FAILED, "认证失败");
    }

    public AuthException(String msg) {
        super(msg);
    }

    public AuthException(int code, String msg) {
        super(code, msg);
    }
}
