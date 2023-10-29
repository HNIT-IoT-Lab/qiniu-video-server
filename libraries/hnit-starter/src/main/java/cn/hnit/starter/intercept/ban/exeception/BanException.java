package cn.hnit.starter.intercept.ban.exeception;


import cn.hnit.common.exception.base.BaseException;

/**
 *
 * @author liangfengyuan
 */
public class BanException extends BaseException {
    public BanException(int code,String msg){
        super(code , msg);
    }
}
