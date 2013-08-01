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
//bugzilla 1494
String errorDateComparison = "";

String path = "";
String basePath = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}
//bugzilla 1494
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
errorDateComparison =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "error.project.begindate.lessthan.enddate");
%>

<script language="JavaScript1.2">
function validateForm(form) {
    var validated = validateProjectForm(form);
    //validation for completedDate > startedDate
    if (validated) {
      var compDate = form.completedDateForDisplay.value;
      var startDate = form.startedDateForDisplay.value;
      if (compDate != '') {
         var compYear = compDate.substring(6, 10);
         var compMonth = compDate.substring(0, 2);
         var compDay = compDate.substring(3, 5);
         var startYear = startDate.substring(6, 10);
         var startMonth = startDate.substring(0, 2);
         var startDay = startDate.substring(3, 5);
         
         var completedDate = compYear + compMonth + compDay;
         var startedDate = startYear + startMonth + startDay;
        
         if (completedDate < startedDate) {
            alert('<%=errorDateComparison%>');
            validated = false;
         }
      }
   }
   return validated;
}

</script>

<table>
		<tr>
						<td class="label"><%--bugzilla 2438--%>
							<bean:message key="project.localAbbreviation"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="localAbbreviation"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="project.projectName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" size="55" property="projectName" />
						</td>
		 </tr>
         <tr>
						<td class="label">
							<bean:message key="project.description"/>:
						</td>	
						<td>
							<html:text name="<%=formName%>" property="description"/>
						</td>
          </tr>
          <tr>
						<td class="label">
							<bean:message key="project.startedDate"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="startedDateForDisplay" styleClass="dateText" onkeyup="DateFormat(this,this.value,event,false,'1')" onblur="DateFormat(this,this.value,event,true,'1')"/>
					   	    <%--html:text name="<%=formName%>" property="startedDateForDisplay" styleClass="dateText"/--%>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="project.owner"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
					   	 <%--html:text property="sysUserName"/--%>
					   	<html:select name="<%=formName%>" property="sysUserId">
					   	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="sysUsers" 
										label="nameForDisplay" 
										value="id"  
							        	filterProperty="isActive" 
							        	filterValue="N"
							 			allowEdits="true"
							/>
                        
					   </html:select>

						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="project.program.programName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
					   	    <%--html:text property="programCode"/--%>
					   	    
					   	    
					   	  <html:select name="<%=formName%>" property="programCode">
					   	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="programs" 
										label="code" 
										value="code"  />
							        	<%-- filterProperty="isActive" 
							        	filterValue="N" 
							 			allowEdits="true"
							/--%>
                      
					   </html:select>
						</td>
		</tr>
		<tr title="<bean:message key="project.external.reference.tooltip"/>" >
						<td class="label">
						    <bean:message key="project.external.reference"/>:
						</td>	
						<td> 
						 	<html:text name="<%=formName%>" property="referenceTo" />
						</td>
		</tr>
        <tr>
						<td class="label">
							<bean:message key="project.completedDate"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="completedDateForDisplay" styleClass="dateText" onkeyup="DateFormat(this,this.value,event,false,'1')" onblur="DateFormat(this,this.value,event,true,'1')"/>
							<%--html:text name="<%=formName%>" property="completedDateForDisplay" styleClass="dateText" /--%>
						</td>
		</tr>
        <tr>
						<td class="label">
							<bean:message key="project.isActive"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="1"> 
							<html:text name="<%=formName%>" property="isActive" size="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
		</tr>
		<tr>
						<td class="label">
						    <%--bugzilla 1401 removed asterisk for required--%>
							<bean:message key="project.scriptletName"/>:
						</td>	
						<td> 
							<html:text styleId="scriptletName" size="30" name="<%=formName%>" property="scriptletName" /> 
						    <span id="indicator1" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
 	   				         <input id="selectedScriptletName" name="selectedScriptletName" type="hidden" size="30" />
						</td>
		 </tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

  <ajax:autocomplete
  source="scriptletName"
  target="selectedScriptletName"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="scriptletName={scriptletName},provider=ScriptletAutocompleteProvider,fieldName=scriptletName,idName=id"
  indicator="indicator1"
  minimumCharacters="1" />
  
<%--bugzilla 1512 custom JavascriptValidator--%>
<app:javascript formName="projectForm" />

