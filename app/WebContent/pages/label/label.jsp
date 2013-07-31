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

<script language="JavaScript1.2">
 
function validateForm(form) {
     return validateLabelForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="label.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
						 <%--bugzilla 1402 do not require scriptlet--%>
							<bean:message key="label.scriptletName"/>:
						</td>	
						<td> 
						   <%--AIS - bugzilla 1562--%>
							<html:select name="<%=formName%>" property="scriptletName" >
					 	   	  <app:optionsCollection 
						    	name="<%=formName%>" 
								property="scriptlets" 
								label="scriptletName" 
								value="scriptletName" 
					          />											        
					   	   </html:select>   	   
						</td>
		 </tr>
         <tr>
						<td class="label">
							<bean:message key="label.labelName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="labelName" />
						</td>
		 </tr>
		<tr>
						<td class="label">
							<bean:message key="label.description"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="description" />
						</td>
		 </tr>
          <tr>
						<td class="label">
							<bean:message key="label.printerType"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="1">
							<html:text name="<%=formName%>" property="printerType" size="1" />
						</td>
          </tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<%--AIS - bugzilla 1562 ( removed ajax as scriptlet is now drop-down) --%>
<html:javascript formName="labelForm"/>

