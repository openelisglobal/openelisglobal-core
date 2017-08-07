package us.mn.state.health.lims.common.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class HttpSecurityHeadersFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		httpResponse.addHeader("Content-Security-Policy","default-src 'none'; script-src 'self';" 
		        + "connect-src 'self'; img-src 'self'; style-src 'self';");//defines where content is allowed to be loaded from
		//httpResponse.addHeader("Strict-Transport-Security", "max-age=31536000"); //enforces communication must be over https
		httpResponse.addHeader("X-Content-Type-Options","nosniff"); //prevents MIME sniffing errors
		httpResponse.addHeader("X-Frame-Options", "SAMEORIGIN");//enforces whether page is allowed to be an iframe in another website
		httpResponse.addHeader("X-XSS-Protection","1"); //provides browser xss protection. attempts to cleanse.
		chain.doFilter(request, httpResponse);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
