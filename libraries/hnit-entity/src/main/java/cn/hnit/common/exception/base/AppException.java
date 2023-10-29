package cn.hnit.common.exception.base;


import cn.hnit.common.exception.IException;
import cn.hnit.common.resultx.constant.RspEnum;
import cn.hnit.common.resultx.constant.SysCode;

/**
 * 服务端应用异常，此异常为直接返回给移动端的异常
 *
 * @author 梁峰源
 * @date 2022年9月22日19:43:07
 * @see IException
 * @see BaseException
 * @see ExceptionHelper
 */
public class AppException extends BaseException implements IException {

    private static final long serialVersionUID = -5875371379845226068L;

    public AppException() {
    }

    public AppException(String msg) {
        super(msg);
    }


    public AppException(int code, String msg) {
        super(code, msg);
    }

    public AppException(int code, String msg, Object data) {
        super(code, msg, data);
    }


    public AppException(RspEnum rspEnum) {
        super(rspEnum.getCode(), rspEnum.getMsg());
    }

    /**
     * 需要弹窗，此方法返回的异常消息会直接反馈给用户
     *
     * @param msg 文本信息
     * @return 异常
     */
    public static AppException pop(String msg) {
        return new AppException(SysCode.WARN, msg);
    }
}
