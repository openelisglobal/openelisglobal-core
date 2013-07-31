<%@ page language="java" contentType="text/html; charset=utf-8" import="java.util.Date,us.mn.state.health.lims.common.action.IActionConstants,us.mn.state.health.lims.common.util.SystemConfiguration"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>


<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<%--AIS - bugzilla 1851--%>
<html:hidden property="selectedProjIdOne" name="<%=formName%>" />
<html:hidden property="selectedProjIdTwo" name="<%=formName%>" />

<%!String allowEdits = "true";

	String errorDate = "";

	%>

<%if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
				allowEdits = (String) request
						.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
			}

			request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, allowEdits);

			java.util.Locale locale = (java.util.Locale) request.getSession()
					.getAttribute(org.apache.struts.Globals.LOCALE_KEY);
			errorDate = us.mn.state.health.lims.common.util.resources.ResourceLocator
					.getInstance().getMessageResources().getMessage(locale,
							"error.date");

			%>


<script language="JavaScript1.2">


function myCheckDate(date, event, dateCheck, onblur) {
 var validDate = DateFormat(date,date.value,event,dateCheck,'1');
 if (dateCheck) { 
   if (!validDate) {
     alert('<%=errorDate%>');
     //var thisField = document.getElementById("selectedReceivedDate");
     date.focus();     
   }
 }
}
</script>

<table>
	<tr>
		<td colspan="4">
			<h2 align="left">
				<bean:message key="sampletracking.subtitle.other" />
			</h2>
		</td>
	</tr>


	<tr>
		<td>
			<bean:message key="sampletracking.popup.submitter" />:
		</td>

		<td>
            <%--bugzilla 2069 using organzationLocalAbbreviation--%>
			<html:select name="<%=formName%>" property="selectedOrgId">
				<app:optionsCollection name="<%=formName%>" 
				property="submitters" label="concatOrganizationLocalAbbreviationName" 
				value="organizationLocalAbbreviation" filterProperty="isActive" filterValue="N" />
			</html:select>

		</td>
	</tr>
	
	
	
	<tr>
		<td>
			<bean:message key="sampletracking.popup.collectionDate" />:
		</td>

		<td>
		    <%--bugzilla 2004--%>
			<app:text name="<%=formName%>" property="selectedCollectionDate" size="20" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" styleClass="text" />
		</td>
	</tr>


	<tr>
		<td>
			<bean:message key="sampletracking.popup.receivedDate" />:
		</td>

		<td>
		    <%--bugzilla 2004--%>
			<app:text name="<%=formName%>" property="selectedReceivedDate" size="20" maxlength="10" onkeyup="myCheckDate(this, event, false,false)" styleClass="text" />
		</td>
	</tr>

	<%--bugzilla 2455--%>
	<tr>
		<td>
			<bean:message key="sample.referredCultureFlag" />:
		</td>

		<td>
			<app:text name="<%=formName%>" property="selectedSpecimenOrIsolate" maxlength="1" size="1" styleClass="text" />
		</td>
	</tr>
	
	<tr>
		<td>
			<bean:message key="sampletracking.popup.sampleType" />:
		</td>

		<td>
			<html:select name="<%=formName%>" property="selectedSampleType">
				<app:optionsCollection name="<%=formName%>" 
				property="types" label="description" value="id" />
			</html:select>
		</td>
	</tr>

	<tr>
		<td>
			<bean:message key="sampletracking.popup.sampleSource" />:
		</td>

		<td>

			<html:select name="<%=formName%>" property="selectedSampleSource">
				<app:optionsCollection name="<%=formName%>" 
				property="sources" label="description" value="id" />
			</html:select>

		</td>
	</tr>
	<%--AIS - bugzilla 1851--%>
	<tr>
		<td>
			<bean:message key="sampletracking.provider.project" />:
		</td>
		<td>		
			<select name="selectedProjId" 
			        id="selectedProjId"			       
			        multiple="multiple" 
			        style="width: 300px" 
			        type="us.mn.state.health.lims.project.valueholder.Project"			        
			        				
			        >
				<logic:iterate id="project" property="projectdetails" name="<%=formName%>">
					<bean:define id="concatProjNameDesc" name="project" property="concatProjNameDesc" />
					<bean:define id="ID" name="project" property="id" />
					<bean:define id="tooltip" name="project" property="concatProjNameDesc" />
					<logic:equal name="project" property="isActive" value="Y">
						<option value="<%=ID%>" title="<%=tooltip%>">
							<bean:write name="project" property="concatProjNameDesc" />
						</option>
					</logic:equal>
				</logic:iterate>
			</select>
		</td>
	</tr>

</table>


