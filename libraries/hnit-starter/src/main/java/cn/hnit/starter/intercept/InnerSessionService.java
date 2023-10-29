package cn.hnit.starter.intercept;

import javax.servlet.http.HttpServletRequest;

public interface InnerSessionService {


    boolean isLogin(HttpServletRequest request);

}
