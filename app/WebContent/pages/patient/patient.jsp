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
    return validatePatientForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="patient.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<%--tr>
						<td class="label">
							<bean:message key="patient.personId"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="person.id" />
						</td>
		 </tr--%>
	 	<%--tr>
						<td class="label">
							<bean:message key="patient.personId"/>:<span class="requiredlabel">*</span>
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
							<bean:message key="patient.personId"/>:<span class="requiredlabel">*</span>
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
		<%--currently not needed--%>
		 <%--tr>
						<td class="label">
							<bean:message key="patient.race"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="race" />
						</td>
		 </tr--%>
		<tr>
						<td class="label">
							<bean:message key="patient.gender"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
					   	 			   	    
					   	  <html:select name="<%=formName%>" property="gender">
					   	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="genders" 
										label="genderType" 
										value="genderType"  />
                     
					   </html:select>
						</td>
		</tr>
		 <%--tr>
						<td class="label">
							<bean:message key="patient.gender"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="gender" />
						</td>
		 </tr--%>
		 <tr>
						<td class="label">
							<bean:message key="patient.birthDate"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="birthDateForDisplay" onkeyup="DateFormat(this,this.value,event,false,'1')" onblur="DateFormat(this,this.value,event,true,'1')"/>
						</td>
		 </tr>
		 <%--epi names not needed for this form--%>
		 <%--tr>
						<td class="label">
							<bean:message key="patient.epiFirstName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="epiFirstName" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="patient.epiMiddleName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="epiMiddleName" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="patient.epiLastName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="epiLastName" />
						</td>
		 </tr--%>
		 <%--not needed at all--%>
		 <%--tr>
						<td class="label">
							<bean:message key="patient.birthTime"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="birthTimeForDisplay" />
						</td>
		 </tr--%>
		 <tr>
						<td class="label">
							<bean:message key="patient.deathDate"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="deathDateForDisplay" onkeyup="DateFormat(this,this.value,event,false,'1')" onblur="DateFormat(this,this.value,event,true,'1')"/>
						</td>
		 </tr>
		 <%--tr><%-- the next six properties are future enhancements
						<td class="label">
							<bean:message key="patient.nationalId"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="nationalId" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="patient.ethnicity"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="ethnicity" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="patient.schoolAttend"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="schoolAttend" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="patient.medicareId"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="medicareId" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="patient.medicaidId"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="medicaidId" />
						</td>
		 </tr>
		 <tr>
						<td class="label">
							<bean:message key="patient.birthPlace"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="birthPlace" />
						</td>
		 </tr--%>
 		 <tr>
						<td class="label">
							<bean:message key="patient.externalId"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="externalId" />
						</td>
		 </tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<%--bugzilla 1512 custom JavascriptValidator--%>
<app:javascript formName="patientForm"/>

