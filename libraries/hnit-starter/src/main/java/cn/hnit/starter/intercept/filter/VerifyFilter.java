package cn.hnit.starter.intercept.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 统一过滤
 * @author Admin
 *
 */
@Slf4j
public class VerifyFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("VerifyFilter init");
	}

	/**
	 * 支持gzip压缩
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		ContentCachingResponseWrapper rspWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
		chain.doFilter(request, rspWrapper);
		try{
			rspWrapper.copyBodyToResponse();
		}catch (ClientAbortException e) {
			log.error("unexpected closed by client");
		}
		
	}

	@Override
	public void destroy() {
		log.info("VerifyFilter destroy");
	}
}
