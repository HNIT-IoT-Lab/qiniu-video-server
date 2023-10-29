package cn.hnit.handle;

import cn.hnit.entity.LoginVO;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;

/**
 * 策略接口
 *
 * @author king
 * @since 2022/9/25 11:59
 */
public interface LoginHandle extends InitializingBean {
    /**
     * 具体的登录逻辑
     * @return
     */
    LoginVO login(Map<String, Object> params);

}
