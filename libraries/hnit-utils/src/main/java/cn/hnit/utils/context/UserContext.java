package cn.hnit.utils.context;

import cn.hnit.common.enums.AppSourceEnums;
import cn.hnit.common.exception.base.AppException;
import cn.hnit.utils.LocalThreadUtil;
import cn.hnit.utils.common.bean.HeaderParam;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户上下文，从当前线程的{@link InheritableThreadLocal}中到请求参数
 *
 * @author 梁峰源
 * @date 2022年9月22日19:43:07
 * @see LocalThreadUtil
 * @see InheritableThreadLocal
 */
public class UserContext {


    /**
     * 用户id
     */
    public static Long getUserId() {
        return LocalThreadUtil.getUid();
    }


    /**
     * 必须要userId
     */
    public static Long mustUserId() {
        Long userId = getUserId();
        if (userId == null) {
            throw AppException.pop("请登录后重试");
        }
        return userId;
    }


    /**
     * 得到精简用户信息
     *
     * @return SimpleUserDTO
     */
    public static SimpleUserDTO getSimpleUser() {
        return LocalThreadUtil.getLocalObj(SimpleUserDTO.class);
    }

    public static String getUserToken() {
        return LocalThreadUtil.getHeader().getUserToken();
    }


    /**
     * 得到version
     *
     * @return version
     */
    public static String getVersion() {
        return LocalThreadUtil.getHeader().getVersion();
    }

    public static String getVersionName() {
        return LocalThreadUtil.getHeader().getVersionName();
    }

    public static Integer getFrom() {
        String source = LocalThreadUtil.getHeader().getPlatform();
        if (StringUtils.isBlank(source)) {
            return null;
        }
        return Integer.parseInt(source);
    }

    public static HeaderParam getHeader() {
        return LocalThreadUtil.getHeader();
    }

    public static String getSign() {
        String source = LocalThreadUtil.getHeader().getSign();
        if (StringUtils.isBlank(source)) {
            return null;
        }
        return source;
    }

    public static String getUserNumber() {
        return LocalThreadUtil.getHeader().getUserNumber();
    }


    public static boolean isPc() {
        return AppSourceEnums.PC.getCode().equals(UserContext.getFrom());
    }

    public static boolean isNewVersion(String newAdrVer, String newIosVer, String newPCVer) {
        if (isPc()) { // 是PC端
            String pcVersion = UserContext.getPcVersion();
            pcVersion = extraVersion(pcVersion);
            return checkVersion(newPCVer, pcVersion);
        } else if (isAndroid()) { // adr端
            String version = UserContext.getVersionName();
            version = extraVersion(version);
            return checkVersion(newAdrVer, version);
        } else { // ios端
            String version = UserContext.getVersionName();
            version = extraVersion(version);
            return checkVersion(newIosVer, version);
        }
    }

    private static String extraVersion(String version) {
        int index = version.indexOf("-");
        if (index != -1) {
            return version.substring(0, index);
        }
        return version;
    }

    private static boolean checkVersion(String checkVersion, String version) {
        // 有版本号, 那么直接进行校验
        String[] pcVersions = version.split("\\.");
        int currentVersion = 10000 * Integer.parseInt(pcVersions[0]) + 100 * Integer.parseInt(pcVersions[1]) + Integer.parseInt(pcVersions[2]);
        String[] checkVersions = checkVersion.split("\\.");
        int needVersion = 10000 * Integer.parseInt(checkVersions[0]) + 100 * Integer.parseInt(checkVersions[1]) + Integer.parseInt(checkVersions[2]);
        return currentVersion >= needVersion;
    }

    public static Map<String, Object> getDeviceMap() {
        Map<String, Object> map = new HashMap<>();
        HeaderParam headerParam = LocalThreadUtil.getHeader();
        String deviceId = headerParam.getDeviceId();
        if (StringUtils.isNotBlank(deviceId)) {
            map.put(HeaderParam.Fields.deviceId, deviceId);
        }
        String trackingDeviceId = headerParam.getTrackingDeviceId();
        if (StringUtils.isNotBlank(trackingDeviceId)) {
            map.put(HeaderParam.Fields.TrackingDeviceId, trackingDeviceId);
        }
        String smDeviceId = headerParam.getSmDeviceId();
        if (StringUtils.isNotBlank(smDeviceId)) {
            map.put(HeaderParam.Fields.smDeviceId, smDeviceId);
        }
        return map;
    }

    public static boolean isSimulate() {
        return LocalThreadUtil.getHeader().isEmulator();
    }

    public static String getPlatform() {
        return LocalThreadUtil.getHeader().getPlatform();
    }

    public static String getPcVersion() {
        return LocalThreadUtil.getHeader().getPcVersion();
    }

    public static boolean isIos() {
        return AppSourceEnums.IOS.getCode().equals(UserContext.getFrom());
    }

    public static String getDeviceId() {
        return LocalThreadUtil.getHeader().getDeviceId();
    }

    public static boolean isAndroid() {
        return AppSourceEnums.ANDROID.getCode().equals(UserContext.getFrom());
    }
}
