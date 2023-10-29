package cn.hnit.common.resultx.constant;

/**
 * system code
 *
 * @author Admin
 */
public class SysCode {

    private SysCode() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * token 失效
     */
    public static final int INVAILD_USER_TOKRN = 301;
    /**
     * sign 签名无效
     */
    public static final int INVAILD_SIGN = 302;
    /**
     * 处在防沉迷状态
     */
    public static final int PROTECT_CODE = 303;
    /**
     * 用户被ban提示
     */
    public static final int USER_BAN_CODE = 304;

    /**
     * 用户没有权限
     */
    public static final int USER_NOT_PERMISSION = 305;

    /**
     * 用户没有角色
     */
    public static final int USER_NOT_ROLE = 306;
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
     * 失败，不可展示提示信息
     */
    public static final int FORBIDDEN = 403;
    /**
     * 登录 失效 账号在其他设备登陆
     */
    public static final int INVAILD_LOGIN_TYPE = 999;
}
