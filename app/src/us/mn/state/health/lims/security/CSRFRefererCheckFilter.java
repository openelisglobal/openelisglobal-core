package us.mn.state.health.lims.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CSRFRefererCheckFilter implements Filter {

	public CSRFRefererCheckFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		if (httpRequest.getMethod().equals("POST")) {
			String referer = httpRequest.getHeader("Referer");
			String scheme = httpRequest.getScheme();
			String host = httpRequest.getHeader("Host");
			String contextPath = httpRequest.getContextPath();
			String baseURL = scheme + "://" + host + contextPath;
			boolean validRequest = true;
			
			if  (referer == null) {
				validRequest = false;
			} else if (referer.startsWith(baseURL)) {
				validRequest = true;
			} 
			
			if (validRequest) 
				chain.doFilter(request, response);
			else {
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.sendRedirect("Dashboard.do");
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

}
