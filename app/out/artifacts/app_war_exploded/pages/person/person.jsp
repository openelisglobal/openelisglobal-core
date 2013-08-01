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
    return validatePersonForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="person.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="person.lastName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="lastName" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="person.firstName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="firstName" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="person.middleName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="middleName" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="person.streetAddress"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="streetAddress" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="person.city"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="city" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="person.state"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="state" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="person.zipCode"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="zipCode" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="person.country"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="country" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="person.workPhone" />:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="workPhone" onkeyup="javascript:getIt(this,event)" size="15" styleClass="text" />
						</td>
		 </tr>
		 
		 
		 <tr> 
                      <td class="label">
				       <bean:message key="person.workPhoneExtension" />:
		              </td>
                      <td> 
          	              <html:text name="<%=formName%>" property="workPhoneExtension" size="4" styleClass="text" />
   		    		  </td>
        </tr>
		 
		 
		 
		 
		 
		 <tr>
						<td class="label">
							<bean:message key="person.homePhone"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="homePhone" onkeyup="javascript:getIt(this,event)" size="15" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="person.cellPhone"/>:
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="cellPhone" onkeyup="javascript:getIt(this,event)" size="15" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="person.fax"/>:
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="fax" onkeyup="javascript:getIt(this,event)" size="15" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="person.email"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="email" />
						</td>
		 </tr>
		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="personForm"/>

