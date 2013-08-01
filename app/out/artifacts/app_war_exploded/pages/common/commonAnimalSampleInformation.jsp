<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<%!

String allowEdits = "true";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

%>
<%--bugzilla 2501--%>
<logic:notEmpty name="<%=formName%>" property="accessionNumber">
<table>
     <tr>
     <td height="86" colspan="2"><table width="1207">
     <td colspan="6" scope="col" bgcolor="#CCCC99">
         <strong><bean:message key="resultsentry.sample.title"/></strong> </td>
      <tr>
        <td width="105"><bean:message key="resultsentry.sampleType"/>:</td>
        <td width="277">&nbsp;<app:text name="<%=formName%>" property="typeOfSample.description" allowEdits="false"/></td>
        <td width="135"><bean:message key="resultsentry.patientLastName"/>:</td>
        <td width="338">&nbsp;<app:text name="<%=formName%>" property="patientLastName" allowEdits="false"/></td>
        <td width="75"><bean:message key="resultsentry.submitter"/>:</td>
        <td width="237">&nbsp;<app:text name="<%=formName%>" property="organization.organizationName" allowEdits="false"/></td>
      </tr>
      <tr>
        <td><bean:message key="resultsentry.sampleSource"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="sourceOfSample.description" allowEdits="false"/></td>
        <td><bean:message key="resultsentry.patientFirstName"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="patientFirstName" allowEdits="false"/></td>
        <td><bean:message key="resultsentry.project"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="project.projectName" allowEdits="false"/></td>
      </tr>
      <tr>
        <td><bean:message key="resultsentry.sourceOther"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="sourceOther" allowEdits="false"/></td>
        <td><bean:message key="resultsentry.patientId"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="patientId" allowEdits="false"/></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
</table>
</logic:notEmpty>
