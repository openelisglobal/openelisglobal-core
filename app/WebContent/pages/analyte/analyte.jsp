<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>


<div id="sound"></div>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />


<%!

String allowEdits = "true";

String path = "";
String basePath = "";
%>
<%

path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

%>

<script language="JavaScript1.2">
function validateForm(form) {
 return validateAnalyteForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="analyte.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<%--NOT CURRENTLY NEEDED tr>
						<td class="label">
							<bean:message key="analyte.parent"/>:
						</td>	
						<td> 
	   			<html:text styleId="parentAnalyteName" size="30" name="<%=formName%>" property="parentAnalyteName" /> 
	   			<span id="indicator1" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
 	   				<input id="selectedAnalyteId" name="selectedAnalyteId" type="hidden" size="30" />
						 <%--html:select name="<%=formName%>" property="selectedAnalyteId">
							   	   <app:optionsCollection 
										name="<%=formName%>" 
							    		property="parentAnalytes" 
										label="analyteName" 
										value="id"  
							        	filterProperty="isActive" 
							        	filterValue="N"
							 			allowEdits="true"
							/>
                         </html:select--%>							
							<%--html:text styleId="analyteName" styleClass="form-autocomplete" size="30" name="<%=formName%>" property="analyteName" /> &nbsp;&nbsp;&nbsp;&nbsp;Analyte ID: <input id="selectedAnalyteId" name="selectedAnalyteId" type="text" size="30" /--%>
			                     
						</td>
		</tr--%>
		<tr>
						<td class="label">
							<bean:message key="analyte.analyteName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="60"> 
							<html:text name="<%=formName%>" property="analyteName" size="60"/>
						</td>
		</tr>
        <tr>
						<td class="label">
							<bean:message key="analyte.isActive"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="1"> 
							<html:text name="<%=formName%>" property="isActive" size="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="analyte.externalId"/>:
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="externalId" />
						</td>
		</tr>
        <%--bugzilla 2432--%>
		<tr>
						<td class="label">
							<bean:message key="analyte.localAbbreviation"/>:
						</td>	
						<td> 
						    <html:text name="<%=formName%>" property="localAbbreviation" size="10" onblur="this.value=this.value.toUpperCase()" />
						</td>
		</tr>	
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

 
  <%--ajax:autocomplete
  source="parentAnalyteName"
  target="selectedAnalyteId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="parentAnalyteName={parentAnalyteName},provider=AnalyteAutocompleteProvider,fieldName=analyteName,idName=id"
  indicator="indicator1"
  minimumCharacters="1" /--%>
  

<html:javascript formName="analyteForm"/>


