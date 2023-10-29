package cn.hnit.utils.common.bean;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 请求头参数
 * 参见showdoc文档说明
 * https://www.showdoc.cc/page/edit/196122837868599/4032133553620653
 */
@Data
@FieldNameConstants
public class HeaderParam {

    private String deviceId;

    private String platform;

    private String ip;

    private String userToken;

    private String sign;

    private Long timestamp;

    private String comeFrom;

    private String comeUserId;

    private String platformId;

    private String platformkey;

    private String platformCode;

    private String version;


    private String versionName;

    private String tsign;
    /**
     * 请求开始时间
     */
    private Long requestTime;

    private String activityId;

    private String tdid;

    /**
     * ios封禁标识标识
     */
    private String udid;

    /**
     * 参数加密验证
     */
    private String paramSign;


    private String TrackingDeviceId;

    /**
     * 智投 跟踪设备号
     */
    private String smartThrowDeviceId;


    /**
     * 数美处获取的设备标识号
     */
    private String smDeviceId;


    private String port;


    private String requestId;


    private String userNumber;

    private String userid;


    /**
     * 是否模拟器
     */
    private boolean isEmulator;

    /** pc 版本号*/
    private String pcVersion;
}
