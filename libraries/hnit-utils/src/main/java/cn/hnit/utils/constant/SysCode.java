package cn.hnit.utils.constant;

/**
 * system code
 *
 * @author Admin
 */
public class SysCode {

    /**
     * 重新同步游戏
     */
    public static final int RELOAD_GAME = 4001;

    /**
     * 重新加载词语
     */
    public static final int RELOAD_WORD = 4002;


    /**
     * 需要重新加载麦位
     */
    public static final int RELOAD_MIC_AND_GAME = 4003;

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
     * 登录 失效 账号在其他设备登陆
     */
    public static final int INVAILD_LOGIN_TYPE = 999;
    /**
     * 不可用的家族名称
     */
    public static final int UNUSEFUL_CLAN_NAME = 600;

    /**
     * 用户申请签约/解约家族失败
     */
    public static final int APPLY_CLAN_FAIL = 601;

    /**
     * 币值不足
     */
    public static final int BALANCE_NOT_ENOUGH = 604;

    /**
     * 用户ID输入错误，查无此人
     */
    public static final int CLAN_USER_NOT_EXITS = 606;

    /**
     * 此主播已绑定家族，暂不可邀约
     */
    public static final int CLAN_USER_ALREADY_JOIN = 605;

    /**
     * 对该主播邀请过于频繁，休息下
     */
    public static final int CLAN_INVITE_LIMITED = 607;

    /**
     * 用户已在本家族
     */
    public static final int USER_AREADY_JOIN_CLAN = 608;

    /**
     * 用户已在别的家族
     */
    public static final int USER_AREADY_OTHER_CLAN = 608;

    /**
     * 达到家族上限
     */
    public static final int CLAN_OUT_LIMIT = 609;

    /**
     * 需要加入家族
     * */
    public static final int NEED_JOIN_CLAN = 610;

    /** 合规限制pk团战*/
    public static final int LIMIT_PK = 700;

    /** 合规限制跨房pk*/
    public static final int LIMIT_ACROSS_PK = 701;


    /**
     * PC不能进入移动端
     */
    public static final int PC_NO_MOBILE = 1010;


    /**
     * 移动端不能进入pc
     */
    public static final int MOBILE_NO_PC = 1011;

    /**
     * 非当前设备，操作非法
     */
    public static final int NOT_CURR_DEVICE = 1012;

    /**
     * 确认是否新设备进房
     */
    public static final int ENSURE_SWITCH_DEVICE = 1013;

    /**
     * 用户注销时间小于指定时间
     */
    public static  final  int CANCEL_USER_BEFORE_DATE = 1014;


    /**
     * 用户不存在
     */
    public static final int USER_NOT_EXITS = 2001;

    /**
     * 不允许发言
     */
    public static final int SPEAKING_NOT_ALLOWED=2001;

    /**
     * 需要实名
     */
    public static final int NEED_ID_REAL_NAME = 3001;      // 需要二要素返回
    public static final int NEED_FACE_REAL_NAME = 3002;    // 需要三要素
}
