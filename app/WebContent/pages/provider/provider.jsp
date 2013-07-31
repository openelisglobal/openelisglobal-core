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
    return validateProviderForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="provider.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
 		<%--tr>
						<td class="label">
							<bean:message key="provider.personId"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
				
						<html:select name="<%=formName%>" property="selectedPersonId">
					   	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="persons" 
										label="firstName" 
										value="id"  
							        	allowEdits="true"
							/>
                       </html:select>
                      
						</td>
		</tr--%>
 		<tr>
						<td class="label">
							<bean:message key="provider.personId"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
				
						<html:select name="<%=formName%>" property="selectedPersonId">
					   	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="persons" 
										label="id" 
										value="id"  
							        	allowEdits="true"
							/>
                       </html:select>
                      
						</td>
		</tr>
		 <tr>
						<td class="label">
							<bean:message key="provider.npi"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="npi" />
						</td>
		 </tr>

		 <tr>
						<td class="label">
							<bean:message key="provider.externalId"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="externalId" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="provider.providerType"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width="1"> 
							<html:text name="<%=formName%>" property="providerType" size="1"/>
						</td>
		 </tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="providerForm"/>

