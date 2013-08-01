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
<logic:notEmpty name="<%=formName%>" property="sampleXmlHelper">
<bean:define id="sample" name="<%=formName%>" property="sampleXmlHelper" type="us.mn.state.health.lims.reports.valueholder.audittrail.SampleXmlHelper" />
<logic:notEmpty name="sample" property="historyRecords">
<table>
 	<tr>
     <td>
      <table width="1206" border="1">
        <td colspan="6" scope="col" bgcolor="#CCCC99">
         <strong><bean:message key="report.audit.trail.sample.section.title"/></strong> </td>
	      <tr>
	        <th width="201"><bean:message key="report.audit.trail.record.changedBy"/></th>
	        <th width="201"><bean:message key="report.audit.trail.record.changedDate"/></th>
	        <th width="201"><bean:message key="report.audit.trail.record.activity"/></th>
	        <th width="201"><bean:message key="report.audit.trail.record.column"/></th>
	        <th width="201"><bean:message key="report.audit.trail.record.value.before"/></th>
	        <th width="201"><bean:message key="report.audit.trail.record.value.description"/></th>
	      </tr> 
	  	   <logic:iterate id="sampleHistory" name="sample" property="historyRecords">
	  	    <tr>
	  	     <td valign="top" width="201"><app:writeHtmlFragment name="sampleHistory" property="userName"/></td>
  	         <td valign="top" width="201"><app:writeHtmlFragment name="sampleHistory" property="date"/></td>
	  	     <td valign="top" width="201"><app:writeHtmlFragment name="sampleHistory" property="activity"/></td>
	  	     <td valign="top" colspan="3"><table width="100%" border="1"><app:writeHtmlFragment name="sampleHistory" property="change"/></table></td>
    	    </tr>
	  	   </logic:iterate>
  	   </table>
  	  </td>
  	 </tr>
 </table>
</logic:notEmpty>
</logic:notEmpty>
<html:javascript formName="auditTrailReportBySampleForm"/>
