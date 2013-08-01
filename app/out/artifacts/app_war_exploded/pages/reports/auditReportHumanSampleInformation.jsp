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
        <td width="130"><bean:message key="resultsentry.sampleType"/>:</td>
        <td width="277">&nbsp;<app:text name="<%=formName%>" property="typeOfSample.description" allowEdits="false"/></td>
        <td width="138"><bean:message key="resultsentry.patientLastName"/>:</td>
        <td width="338">&nbsp;<app:text name="<%=formName%>" property="patientLastName" allowEdits="false"/></td>
        <td width="130"><bean:message key="resultsentry.submitter"/>:</td>
        <td width="237">&nbsp;<app:text name="<%=formName%>" property="organization.organizationName" allowEdits="false"/></td>
      </tr>
      <tr>
        <td><bean:message key="resultsentry.sampleSource"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="sourceOfSample.description" allowEdits="false"/></td>
        <td><bean:message key="resultsentry.patientFirstName"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="patientFirstName" allowEdits="false"/></td>
        <td><bean:message key="resultsentry.project"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="project.projectName" allowEdits="false" />
             <logic:notEmpty name="<%=formName%>" property="project.projectName">
                /
             </logic:notEmpty>
             <app:text name="<%=formName%>" property="project.id" allowEdits="false" /></td>
      </tr>
      <tr>
        <td><bean:message key="resultsentry.sourceOther"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="sourceOther" allowEdits="false"/></td>
        <td><bean:message key="resultsentry.patientId"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="patientId" allowEdits="false"/></td>
        <logic:notEmpty name="<%=formName%>" property="project2.projectName">
          <td><bean:message key="resultsentry.project2"/>:</td>
          <td>&nbsp;<app:text name="<%=formName%>" property="project2.projectName" allowEdits="false"/>/<app:text name="<%=formName%>" property="project2.id" allowEdits="false" /></td>
        </logic:notEmpty>
        <logic:empty name="<%=formName%>" property="project2.projectName">
          <td>&nbsp;</td>
          <td>&nbsp;</td>
        </logic:empty>
      </tr>
      <tr>
        <td><bean:message key="sample.collectionDate"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="collectionDateForDisplay" allowEdits="false"/>&nbsp;<app:text name="<%=formName%>" property="collectionTimeForDisplay" allowEdits="false"/></td>
        <td><bean:message key="patient.birthDate"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="birthDateForDisplay" allowEdits="false"/></td>
        <td><bean:message key="sample.receivedDate"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="receivedDateForDisplay" allowEdits="false"/></td>
      </tr>
      <%--bugzilla 1855--%>
      <tr>
        <td width="130"><bean:message key="resultsentry.referredCultureFlag"/>:</td>
        <td width="277">&nbsp;<app:text name="<%=formName%>" property="referredCultureFlag" allowEdits="false"/></td>
        <td><bean:message key="patient.gender"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="gender" allowEdits="false"/></td>
        <td><bean:message key="sample.stickerReceivedFlag"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="stickerReceivedFlag" allowEdits="false"/></td>
      </tr>
      <tr>
        <td width="130">&nbsp;</td>
        <td width="277">&nbsp;</td>
         <td><bean:message key="patient.chartNumber"/>:</td>
        <td>&nbsp;<app:text name="<%=formName%>" property="chartNumber" allowEdits="false"/></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
</table>
</logic:notEmpty>
