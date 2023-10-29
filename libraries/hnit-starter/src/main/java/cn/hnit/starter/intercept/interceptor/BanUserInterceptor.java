package cn.hnit.starter.intercept.interceptor;

import cn.hnit.starter.intercept.ban.BanChainNode;
import cn.hnit.starter.intercept.ban.WhitelistChainNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 封禁拦截器
 */
@Slf4j
@Component
public class BanUserInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private List<BanChainNode> banChainNodeList;
    @Autowired
    private List<WhitelistChainNode> whitelistChainNodes;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("---------检测是否命中封禁规则------");
        for (WhitelistChainNode node : whitelistChainNodes) {
            if (node.identifyWhitelist(request)) {
                // 白名单则直接通过校验
                return true;
            }
        }
        log.debug("---------未命中白名单，开始检测是否命中封禁规则------");
        for (BanChainNode node : banChainNodeList) {
            node.ban(request, handler);
        }
        return true;
    }
}
