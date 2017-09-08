package us.mn.state.health.lims.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;

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

	HashSet<String> getParamWhiteList;
	HashSet<String> getParamBlackList;
	
	public SecurityFilter() {
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
		
		//CSRF check for any "action" pages
		if (httpRequest.getMethod().equals("POST") || httpRequest.getRequestURI().contains("Update")
				|| httpRequest.getRequestURI().contains("Save")) {
			String referer = httpRequest.getHeader("Referer");
			String scheme = httpRequest.getScheme();
			String host = httpRequest.getHeader("Host");
			String contextPath = httpRequest.getContextPath();
			String baseURL = scheme + "://" + host + contextPath;			
			if  (referer == null) {
				suspectedAttack = true;
				attackList.add("CSRF- null referer");
			} else if (!referer.startsWith(baseURL)) {
				suspectedAttack = true;
				attackList.add("CSRF- " + referer);
			} 
		}
			
		//Body Parameters in query check currently on blacklist
		String query = httpRequest.getQueryString();
		if (query != null) {
			String[] urlParams = query.split("&");
			for (int i = 0; i < urlParams.length; i++) {
				String urlParamName = urlParams[i].split("=")[0];
				if (urlParamName.contains(".")) {
					urlParamName = urlParamName.split(".")[0];
				}
				if (urlParamName.contains("[")) {
					urlParamName = urlParamName.split("[")[0];
				}

				//if (!getParamWhiteList.contains(urlParamName)) {
				if (getParamBlackList.contains(urlParamName)) {
					suspectedAttack = true;
					attackList.add("Body Parameter in query- " + urlParamName);
				}
			}
		}
			
		//XSS check 
		if (httpRequest.getMethod().equals("POST") || httpRequest.getRequestURI().contains("Update")
				|| httpRequest.getRequestURI().contains("Save")) {
			Enumeration<String> parameterNames = httpRequest.getParameterNames();
			 while (parameterNames.hasMoreElements()) {
				 String param = httpRequest.getParameter(parameterNames.nextElement());
				 String paramValue = java.net.URLDecoder.decode(param, "UTF-8");
				 paramValue = paramValue.replaceAll("\\s", "");
				 if (paramValue.contains("<script>") || paramValue.contains("</script>")) {
					 suspectedAttack = true;
					 attackList.add("XSS- " + param);
				 }
			 }
		}
		
		//Adding security headers to response
		httpResponse.addHeader("Content-Security-Policy","default-src 'none'; script-src 'self' 'unsafe-inline' 'unsafe-eval';" 
		        + "connect-src 'self'; img-src 'self'; style-src 'self' 'unsafe-inline';");//defines where content is allowed to be loaded from
		//httpResponse.addHeader("Strict-Transport-Security", "max-age=31536000"); //enforces communication must be over https
		httpResponse.addHeader("X-Content-Type-Options","nosniff"); //prevents MIME sniffing errors
		httpResponse.addHeader("X-Frame-Options", "SAMEORIGIN");//enforces whether page is allowed to be an iframe in another website
		httpResponse.addHeader("X-XSS-Protection","1"); //provides browser xss protection. attempts to cleanse.
	
		if (!suspectedAttack) {
			chain.doFilter(request, httpResponse);
		} else {
			StringBuilder attackMessage = new StringBuilder();
			String separator = "";
			attackMessage.append(httpRequest.getRequestURI());
			attackMessage.append(" suspected attack(s) of type: ");
			for (String attack : attackList) {
				attackMessage.append(separator);
				separator = ",";
				attackMessage.append(attack);
			}
			
			//should log suspected attempt
			LogEvent.logWarn("SecurityFilter", "doFilter()", attackMessage.toString());
			System.out.println(attackMessage.toString());
			//send to safe page
			httpResponse.sendRedirect("Dashboard.do");
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		getParamWhiteList = new HashSet<String>();
		//createWhiteList();
		getParamBlackList = new HashSet<String>();
		createBlackList();
	}
	
	//less restrictive, less likely to stop valid traffic
	private void createBlackList() {
		getParamBlackList.add("englishValue");
		getParamBlackList.add("frenchValue");
		getParamBlackList.add("loginName");
		getParamBlackList.add("observations");
		getParamBlackList.add("password");
		getParamBlackList.add("patientProperties");
		getParamBlackList.add("ProjectData");
		getParamBlackList.add("qaEvents");
		getParamBlackList.add("referralItems");
		getParamBlackList.add("resultList");
		getParamBlackList.add("sampleOrderItems");
		getParamBlackList.add("selectedIDs");
		getParamBlackList.add("selectedRoles");
		getParamBlackList.add("testResult");
	}

	//more restrictive, more likely to stop valid traffic
	public void createWhiteList() {
		getParamWhiteList.add("accessionNumber");
		getParamWhiteList.add("accessionNumberSearch");
		getParamWhiteList.add("blank");
		getParamWhiteList.add("cacheBreaker");
		getParamWhiteList.add("date");
		getParamWhiteList.add("field");
		getParamWhiteList.add("fieldId");
		getParamWhiteList.add("firstName");
		getParamWhiteList.add("forward");
		getParamWhiteList.add("guid");
		getParamWhiteList.add("ID");
		getParamWhiteList.add("labNo");
		getParamWhiteList.add("labNumber");
		getParamWhiteList.add("lang");
		getParamWhiteList.add("lastName");
		getParamWhiteList.add("NationalID");
		getParamWhiteList.add("nationalID");
		getParamWhiteList.add("patientID");
		getParamWhiteList.add("personKey");
		getParamWhiteList.add("provider");
		getParamWhiteList.add("regionId");
		getParamWhiteList.add("relativeToNow");
		getParamWhiteList.add("report");
		getParamWhiteList.add("sampleType");
		getParamWhiteList.add("selectedSearchID");
		getParamWhiteList.add("selectedValue");
		getParamWhiteList.add("startingRecNo");
		getParamWhiteList.add("STNumber");
		getParamWhiteList.add("subjectNumber");
		getParamWhiteList.add("suppressExternalSearch");
		getParamWhiteList.add("test");
		getParamWhiteList.add("testSectionId");
		getParamWhiteList.add("type");
		getParamWhiteList.add("value");
		getParamWhiteList.add("ver");
	}

}
