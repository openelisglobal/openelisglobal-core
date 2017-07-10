package us.mn.state.health.lims.common.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class URLParamFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		if (paramInQuery(httpRequest)) {
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.sendRedirect("LoginPage.do");
			return;
		}	
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	public boolean paramInQuery(HttpServletRequest httpRequest) {
		String[] blacklist = {"loginName=", "password=", "selectedIDs=", "patientProperties.", "sampleOrderItems.", "value=",
				"selectedRoles=", "account", "expirationDate="};
		String query = httpRequest.getQueryString();
		if (query != null) {
			String[] urlParams = query.split("&");
			for (int i = 0; i < urlParams.length; i++) {
				String urlParam = urlParams[i];
				for (int j = 0; j < blacklist.length; j++) {
					if (urlParam.startsWith(blacklist[j])) {
						return true;
					}
				}
			}
		}
		return false;
	}

}