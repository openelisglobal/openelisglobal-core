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

	HashSet<String> whiteList;
	HashSet<String> blackList;
	
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
				//if (!whiteList.contains(urlParamName)) {
				if (blackList.contains(urlParamName)) {
					suspectedAttack = true;
					attackList.add("Body Parameter in query- " + urlParamName);
				}
			}
		}
			
		//XSS check 
		if (httpRequest.getMethod().equals("POST")) {
			Enumeration<String> parameterNames = httpRequest.getParameterNames();
			 while (parameterNames.hasMoreElements()) {
				 String paramName = parameterNames.nextElement();
				 String param = httpRequest.getParameter(paramName);
				 String paramValue = java.net.URLDecoder.decode(param, "UTF-8");
				 paramValue = paramValue.replaceAll("\\s", "");
				 if (paramValue.contains("<script>") || paramValue.contains("</script>")) {
					 suspectedAttack = true;
					 attackList.add("XSS- " + param);
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
		whiteList = new HashSet<String>();
		//createWhiteList();
		blackList = new HashSet<String>();
		createBlackList();
	}
	
	private void createBlackList() {
		blackList.add("patientProperties");
		blackList.add("loginName");
		blackList.add("password");
		blackList.add("selectedIDs");
		blackList.add("sampleOrderItems");
		blackList.add("selectedRoles");
		blackList.add("qaEvents");
		blackList.add("englishValue");
		blackList.add("frenchValue");
	}

	public void createWhiteList() {
	whiteList.add("accessionNumber");
	whiteList.add("accessionNumberSearch");
	whiteList.add("blank");
	whiteList.add("cacheBreaker");
	whiteList.add("date");
	whiteList.add("field");
	whiteList.add("fieldId");
	whiteList.add("firstName");
	whiteList.add("forward");
	whiteList.add("guid");
	whiteList.add("ID");
	whiteList.add("labNo");
	whiteList.add("labNumber");
	whiteList.add("lang");
	whiteList.add("lastName");
	whiteList.add("NationalID");
	whiteList.add("nationalID");
	whiteList.add("patientID");
	whiteList.add("personKey");
	whiteList.add("provider");
	whiteList.add("regionId");
	whiteList.add("relativeToNow");
	whiteList.add("report");
	whiteList.add("sampleType");
	whiteList.add("selectedSearchID");
	whiteList.add("selectedValue");
	whiteList.add("startingRecNo");
	whiteList.add("STNumber");
	whiteList.add("subjectNumber");
	whiteList.add("suppressExternalSearch");
	whiteList.add("test");
	whiteList.add("testSectionId");
	whiteList.add("type");
	whiteList.add("value");
	whiteList.add("ver");
	}

}
