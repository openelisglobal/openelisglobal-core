<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>
<%--bugzilla 2502--%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<%!

String allowEdits = "true";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

%>

<%--bugzilla 2502--%>
<logic:notEmpty name="<%=formName%>" property="accessionNumber">
<table>
 <tr>
  <td>
    <table width="1250">
     <tr>
      <td colspan="5" scope="col" bgcolor="#CCCC99">
         <strong><bean:message key="resultsentry.sample.title"/></strong> 
      </td>
     </tr>
     <tr>
        <td width="295"><bean:message key="resultsentry.patientLastName"/>:&nbsp;<app:text name="<%=formName%>" property="patientLastName" allowEdits="false"/></td>
        <td width="295"><bean:message key="resultsentry.patientFirstName"/>:&nbsp;<app:text name="<%=formName%>" property="patientFirstName" allowEdits="false"/></td>
        <td width="190"><bean:message key="patient.birthDate"/>:&nbsp;<app:text name="<%=formName%>" property="birthDateForDisplay" allowEdits="false"/></td>
        <td width="230"><bean:message key="sample.collectionDate"/>:&nbsp;<app:text name="<%=formName%>" property="collectionDateForDisplay" allowEdits="false"/></td>
        <td width="240"><bean:message key="qaeventsentry.accessionNumber"/>:&nbsp;<app:text name="<%=formName%>" property="accessionNumber" allowEdits="false"/></td>
     </tr>
   </table>
  </td>
 </tr>
</table>
</logic:notEmpty>
