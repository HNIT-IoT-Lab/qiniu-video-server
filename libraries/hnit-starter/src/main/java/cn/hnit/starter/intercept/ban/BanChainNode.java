package cn.hnit.starter.intercept.ban;

import javax.servlet.http.HttpServletRequest;

/**
 * 职责链链节
 */
public interface BanChainNode {
    void ban(HttpServletRequest request, Object handler);
}
