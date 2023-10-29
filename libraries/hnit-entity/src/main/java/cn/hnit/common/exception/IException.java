package cn.hnit.common.exception;


import cn.hnit.common.resultx.constant.SysCode;

/**
 * 异常类统一行为接口
 *
 * @author 梁峰源
 * @since 2022-08-22 21:54
 **/
public interface IException {
    /**
     * 获取异常信息
     *
     * @return 异常信息
     */
    String getMsg();

    /**
     * 获取异常状态码
     *
     * @return 异常状态码
     * @see SysCode
     */
    int getCode();

    /**
     * 获取异常体信息
     *
     * @return 异常体信息
     */
    Object getData();
}
