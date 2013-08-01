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

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

%>

<script language="JavaScript1.2">
function validateForm(form) {
 return validateReceiverCodeElementForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="receivercodeelement.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="receivercodeelement.messageOrganization"/>:<span class="requiredlabel">*</span>
						</td>
						<td> 
					   	<html:select name="<%=formName%>" property="selectedMessageOrganizationId">
					   	  <app:optionsCollection 
										name="<%=formName%>"
							    		property="messageOrganizations" 
										label="organization.organizationName" 
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
							<bean:message key="receivercodeelement.codeElementType"/>:<span class="requiredlabel">*</span>
						</td>
						<td> 
					   	<html:select name="<%=formName%>" property="selectedCodeElementTypeId">
					   	  <app:optionsCollection 
										name="<%=formName%>"
							    		property="codeElementTypes" 
										label="text" 
										value="id"  
							   			allowEdits="true"
							/>
                        
					   </html:select>

						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="receivercodeelement.identifier"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="identifier" onkeypress="return noenter()"/>
						</td>
		 </tr>
		<tr>
						<td class="label">
							<bean:message key="receivercodeelement.text"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="text" onkeypress="return noenter()"/>
						</td>
		 </tr>
		<tr>
						<td class="label">
							<bean:message key="receivercodeelement.codesystem"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="codeSystem" onkeypress="return noenter()"/>
						</td>
		 </tr>
		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="receiverCodeElementForm"/>

