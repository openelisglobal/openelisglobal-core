package us.mn.state.health.lims.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.log.LogEvent;

public class SecurityFilter implements Filter {

	public SecurityFilter() {
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
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		boolean suspectedAttack = false;
		ArrayList<String> attackList = new ArrayList<String>();
		
		//CSRF check
		if (httpRequest.getMethod().equals("POST")) {
			String referer = httpRequest.getHeader("Referer");
			String scheme = httpRequest.getScheme();
			String host = httpRequest.getHeader("Host");
			String contextPath = httpRequest.getContextPath();
			String baseURL = scheme + "://" + host + contextPath;			
			if  (referer == null) {
				suspectedAttack = true;
				attackList.add("CSRF");
			} else if (!referer.startsWith(baseURL)) {
				suspectedAttack = true;
				attackList.add("CSRF");
			} 
		}
		
		//Body Parameters in query check
		String[] blacklist = {"loginName=", "password=", "selectedIDs=", "patientProperties.", "sampleOrderItems.",
				"selectedRoles=", "account", "expirationDate=0", "qaEvents"};
		String query = httpRequest.getQueryString();
		if (query != null) {
			String[] urlParams = query.split("&");
			for (int i = 0; i < urlParams.length; i++) {
				String urlParam = urlParams[i];
				for (int j = 0; j < blacklist.length; j++) {
					if (urlParam.startsWith(blacklist[j])) {
						suspectedAttack = true;
						attackList.add("Body Parameter in query");
					}
				}
			}
		}
		
		//Adding security headers to response
		httpResponse.addHeader("Content-Security-Policy","default-src 'none'; script-src 'self';" 
		        + "connect-src 'self'; img-src 'self'; style-src 'self';");//defines where content is allowed to be loaded from
		//httpResponse.addHeader("Strict-Transport-Security", "max-age=31536000"); //enforces communication must be over https
		httpResponse.addHeader("X-Content-Type-Options","nosniff"); //prevents MIME sniffing errors
		httpResponse.addHeader("X-Frame-Options", "SAMEORIGIN");//enforces whether page is allowed to be an iframe in another website
		httpResponse.addHeader("X-XSS-Protection","1"); //provides browser xss protection. attempts to cleanse.
		
		if (!suspectedAttack) {
			chain.doFilter(httpRequest, httpResponse);
		} else {
			StringBuilder attacks = new StringBuilder();
			for (String attack : attackList) {
				attacks.append(attack);
				attacks.append("\t");
			}
			//should log suspected attempt
			LogEvent.logWarn("SecurityFilter", "doFilter()", "Suspected Attack of type:" + attacks.toString());
			//send to safe page
			httpResponse.sendRedirect("Dashboard.do");
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

}
