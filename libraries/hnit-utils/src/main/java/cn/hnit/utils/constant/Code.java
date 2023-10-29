package cn.hnit.utils.constant;

public class Code {

    private Code(){

    }

    /**
     * token 失效
     */
    public static final int INVAILD_USER_TOKRN = 301;

    /**
     *  sign 签名无效
     */
    public static final int INVAILD_SIGN = 302;

	/**
	 * 处在防沉迷状态
	 */
	public static final int PROTECT_CODE = 303;
    /**
     * 成功
     */
    public static final int SUCCESSED = 200;
    /**
     * 失败
     */
    public static final int FAILED = -1;

    /**
     * 失败，可展示提示信息
     */
    public static final int WARN = 400;

    /**
     * 失败，不可展示提示信息
     */
    public static final int WARN_NO_SHOW = 401;

    /**
     * 失败，需要进房密码
     */
    public static final int WARN_NEED_PASSWORD = 402;

    /**
     * 用户余额不足状态信息
     */
    public static final int INSUFFICIENT_BALANCE = 208;

    /**
     * 登录 失效  账号在其他设备登陆
     */
    public static  final int INVAILD_LOGIN_TYPE = 999;

    /**
     * 密码错误
     */
    public static final  int PWD_ERR_CODE = 410;

    /**
     * 用户锁定
     */
    public static final  int USER_LOCKED = 415;

    /**
     * 动态删除提示
     */
    public static final  int POST_DEL_CODE = 420;
}
