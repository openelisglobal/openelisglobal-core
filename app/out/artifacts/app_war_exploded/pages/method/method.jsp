<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<%!

String allowEdits = "true";
//bugzilla 1494
String errorDateComparison = "";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}
//bugzilla 1494
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
errorDateComparison =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "error.method.begindate.lessthan.enddate");
%>

<script language="JavaScript1.2">
function validateForm(form) {
    var validated = validateMethodForm(form);
    //validation for activeEndDate > activeBeginDate
    if (validated) {
      var compDate = form.activeEndDateForDisplay.value;
      var startDate = form.activeBeginDateForDisplay.value;
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
						<td class="label">
							<bean:message key="method.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="method.methodName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="methodName" />
						</td>
		 </tr>
         <tr>
						<td class="label">
							<bean:message key="method.description"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="description"/>
						</td>
          </tr>
         <tr>
						<td class="label">
							<bean:message key="method.reportingDescription"/>:
						</td>	
						<td>
							<html:text name="<%=formName%>" property="reportingDescription"/>
						</td>
          </tr>
          <tr>
						<td class="label">
							<bean:message key="method.activeBeginDate"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="activeBeginDateForDisplay" styleClass="dateText" onkeyup="DateFormat(this,this.value,event,false,'1')" onblur="DateFormat(this,this.value,event,true,'1')"/>
					   	    <%--html:text name="<%=formName%>" property="startedDateForDisplay" styleClass="dateText"/--%>
						</td>
		</tr>
        <tr>
						<td class="label">
							<bean:message key="method.activeEndDate"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="activeEndDateForDisplay" styleClass="dateText" onkeyup="DateFormat(this,this.value,event,false,'1')" onblur="DateFormat(this,this.value,event,true,'1')"/>
							<%--html:text name="<%=formName%>" property="completedDateForDisplay" styleClass="dateText" /--%>
						</td>
		</tr>
        <tr>
						<td class="label">
							<bean:message key="method.isActive"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width=1"> 
							<html:text name="<%=formName%>" property="isActive" size="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
		</tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<%--bugzilla 1512 custom JavascriptValidator--%>
<app:javascript formName="methodForm" />

