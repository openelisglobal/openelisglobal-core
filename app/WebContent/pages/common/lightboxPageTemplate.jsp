<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page 
language="java"
contentType="text/html; charset=UTF-8"
import="us.mn.state.health.lims.common.action.IActionConstants"
import="us.mn.state.health.lims.common.util.Versioning"
%>

<%!
String path = "";
String basePath = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<link rel="stylesheet" type="text/css" href="<%=basePath%>css/openElisCore.css?ver=<%= Versioning.getBuildNumber() %>" />
<script language="JavaScript1.2" src="<%=basePath%>scripts/utilities.jsp"></script>
<script type="text/javascript" src="<%=basePath%>scripts/prototype-1.5.1.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/scriptaculous.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/overlibmws.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ajaxtags-1.2.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/Tooltip-0.6.0.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/lightbox.js?ver=<%= Versioning.getBuildNumber() %>"></script>


  <% if (request.getAttribute(IActionConstants.ACTION_KEY) != null) { %>
       <form name='<%=(String)request.getAttribute(IActionConstants.FORM_NAME) %>' action='<%=(String)request.getAttribute(IActionConstants.ACTION_KEY) %>' onsubmit="return submitForm(this);" method="POST">
  <% } %>
    <table width="100%" cellpadding="0" cellspacing="0">
		<tr>
			<td class="popuplistheader"><bean:write name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>" scope='request' /></td>
		</tr>
		<tr>
			<td class="popuplistdata"><tiles:insert attribute="body"/></td>
		</tr>
		<tr>
		    <td><tiles:insert attribute="footer"/></td>
		</tr>
	</table>
	<% if (request.getAttribute(IActionConstants.ACTION_KEY) != null) { %>
	</form>
	<% } %>

</form>

