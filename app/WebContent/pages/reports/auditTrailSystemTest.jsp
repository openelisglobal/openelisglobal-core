<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>
<%!

String allowEdits = "true";

%>
<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

%>
<script language="JavaScript1.2">


</script>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<logic:notEmpty name="<%=formName%>" property="historyRecords">
<table width="100%">
	 <tr>
	   <th><bean:message key="report.audit.trail.record.changedBy"/></th>
	   <th><bean:message key="report.audit.trail.record.changedDate"/></th>
	   <th><bean:message key="report.audit.trail.record.activity"/></th>
  	   <th><bean:message key="report.audit.trail.systemtest.refTable"/></th>
   	   <th><bean:message key="report.audit.trail.record.reftable.id"/></th>
	   <th><bean:message key="report.audit.trail.systemtest.record.changes"/></th>
	 </tr> 
	 <logic:iterate id="history" name="<%=formName%>" property="historyRecords">
	 <tr>
	    <td valign="top"><bean:write name="history" property="userName"/></td>
  	    <td valign="top"><bean:write name="history" property="date"/></td>
	  	<td valign="top"><bean:write name="history" property="activity"/></td>
	  	<td valign="top"><bean:write name="history" property="referenceTableName"/></td>
	  	<td valign="top"><bean:write name="history" property="referenceTableId"/></td>
	  	<td valign="top"><p><bean:write name="history" property="change"/></p></td>
	 </tr>
   	 </logic:iterate>
 </table>
</logic:notEmpty>
<html:javascript formName="auditTrailSystemTestForm"/>
