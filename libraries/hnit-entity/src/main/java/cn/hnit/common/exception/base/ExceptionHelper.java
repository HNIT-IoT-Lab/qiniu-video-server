package cn.hnit.common.exception.base;


import cn.hnit.common.exception.IException;
import cn.hnit.common.resultx.constant.SysCode;

/**
 * 异常抛出帮助类，功能不全，推荐使用断言帮助类 cn.hnit.utils.AssertUtil
 *
 * @author 梁峰源
 * @date 2022年9月22日19:43:07
 * @see IException
 * @see BaseException
 */
public class ExceptionHelper {

    private ExceptionHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static void isTrueWarn(boolean expression, String msg) {
        if (!expression) {
            throwException(SysCode.WARN, msg);
        }
    }

    public static void isTrueFailed(boolean expression, String msg) {
        if (!expression) {
            throwException(SysCode.FAILED, msg);
        }
    }

    public static void isTrue(boolean expression, int code, String msg) {
        if (!expression) {
            throwException(code, msg);
        }
    }

    public static void throwException(int code, String msg) {
        throw new ParamDefectException(code, msg);
    }

}
