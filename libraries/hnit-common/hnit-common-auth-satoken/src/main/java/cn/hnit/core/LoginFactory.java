package cn.hnit.core;

import cn.hnit.handle.LoginHandle;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录工厂
 *
 * @author king
 * @since 2023/10/26 23:07
 */
@Component
public class LoginFactory {
    private static final Map<String, LoginHandle> loginStrategyFactoryMap = new ConcurrentHashMap<>();

    public static LoginHandle getLoginStrategy(String loginSign) {
        LoginHandle loginHandle = loginStrategyFactoryMap.get(loginSign);
        if (loginHandle == null) {
            throw new RuntimeException(String.format("has no loginHandle[%s]", loginSign));
        }
        return loginHandle;
    }

    public static void register(String loginSign, LoginHandle loginHandle) {
        if (loginSign == null || loginSign.isEmpty() || Objects.isNull(loginHandle)) {
            throw new RuntimeException("登录策略注册失败，参数错误");
        }
        // 将策略注册到工厂中
        loginStrategyFactoryMap.put(loginSign, loginHandle);
    }
}
