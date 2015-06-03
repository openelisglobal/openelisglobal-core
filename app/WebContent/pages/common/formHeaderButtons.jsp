<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%
	if(null != request.getAttribute(IActionConstants.FORM_NAME))
	{
%>
<h1>
	<logic:notEmpty	name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>">
		<bean:write name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>" />
	</logic:notEmpty> 
	<logic:empty name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>">
		<% if ("0".equals(request.getParameter("ID"))) { %>
		  <bean:message key="default.add.title" />
		<% } else { %>
		  <bean:message key="default.edit.title" />
		<%}%>
	</logic:empty> 
</h1>
<%
	}
%>


