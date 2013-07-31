<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%--bugzilla 1908 changed some disabled values for Vietnam tomcat/linux--%>

<script>

function setMyMenuAction(button, form, action, validate, parameters) {
   //alert("I am in setMyMenuAction" );
   var parms = parameters + '&Test=' + document.getElementById("selectedTestId").value;
   setMenuAction(button, form, action, validate, parms);
}
</script>
<% 	
	   String deactivateDisabled = "true";
       if (request.getAttribute(IActionConstants.DEACTIVATE_DISABLED) != null) {
            deactivateDisabled = (String)request.getAttribute(IActionConstants.DEACTIVATE_DISABLED);
       }
       
       //This is added for testAnalyteTestResult (we need to disable ADD until test is selected
       String allowEdits = "true";
       if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
            allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
       }
 
%>
<%
	if(null != request.getAttribute(IActionConstants.FORM_NAME))
	{
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0" >
	<tr>
		<td class="pageTitle">
			<b> &nbsp;&nbsp;&nbsp;&nbsp; 
				<logic:notEmpty
					name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>">
					<bean:write name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>" />
				</logic:notEmpty> 
		 	&nbsp;&nbsp; 
		 	</b>
		</td>		
	</tr>
</table>
<br>
<%
	}
%>


<table border="0" cellpadding="0" cellspacing="0">
	<tbody valign="left">
	<tr>
	<td>
  	   <html:button onclick="setMyMenuAction(this, window.document.forms[0], '', 'yes', '?ID=0');return false;" property="add" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">
  		  <bean:message key="label.button.add"/>
  	   </html:button>
  	</td>	
	</tr>
 	<tr>
		<td>
			<bean:message key="label.form.selectand"/>
			&nbsp;
  			<html:button onclick="setMenuAction(this, window.document.forms[0], '', 'yes', '?ID=');return false;" property="edit" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">
  			   <bean:message key="label.button.edit"/>
  			</html:button>
	     </td>
		 <td align="left">
  			<html:button onclick="setMenuAction(this, window.document.forms[0], 'Delete', 'yes', '?ID=');return false;" property="deactivate" disabled="<%=Boolean.valueOf(deactivateDisabled).booleanValue()%>" >
  			   <bean:message key="label.button.deactivate"/>
  			</html:button>
	     </td>
	</tr>
    </tbody>
</table>