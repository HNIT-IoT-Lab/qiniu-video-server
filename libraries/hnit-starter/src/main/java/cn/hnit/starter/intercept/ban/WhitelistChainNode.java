package cn.hnit.starter.intercept.ban;

import javax.servlet.http.HttpServletRequest;

public interface WhitelistChainNode {
    /**
     * 识别是否是白名单
     * @param request
     * @return
     */
    boolean identifyWhitelist(HttpServletRequest request);
}
