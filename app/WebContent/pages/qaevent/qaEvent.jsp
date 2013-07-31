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
    return validateQaEventForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="qaevent.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
 		<tr>
						<td class="label">
							<bean:message key="qaevent.name"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="qaEventName" onblur="this.value=this.value.toUpperCase()"/>
						</td>
		</tr>
 		<tr>
						<td class="label">
							<bean:message key="qaevent.description"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> <%--bugzilla 2548 extend length of description--%>
							<app:text name="<%=formName%>" size="120" property="description"/>
						</td>
		</tr>
 		<tr>
						<td class="label">
							<bean:message key="qaevent.type"/>:<span class="requiredlabel">*</span>
						</td>	
						 <%--bugzilla 2246 changed name type to selectedTypeId --%>	
						<td> 
					    	<html:select name="<%=formName%>" property="selectedTypeId">
					       	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="dictionaries" 
										label="dictEntry" 
										value="id"  
							        	allowEdits="true"
					    		/>
                           </html:select>
						</td>
		</tr>
 		<tr>
						<td class="label">
							<bean:message key="qaevent.category"/>:
						</td>	
						 <%--bugzilla 2506 --%>	
						<td> 
					    	<html:select name="<%=formName%>" property="selectedCategoryId">
					       	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="dictionaries2" 
										label="dictEntry" 
										value="id"  
							        	allowEdits="true"
					    		/>
                           </html:select>
						</td>
		</tr>
 		<tr>
						<td class="label">
							<bean:message key="qaevent.isBillable"/>:
						</td>	
						<td width=1"> 
							<app:text name="<%=formName%>" property="isBillable" size="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="qaevent.isHoldable"/>:<span class="requiredlabel">*</span>
						</td>	
						<td width=1"> 
							<app:text name="<%=formName%>" property="isHoldable" size="1" onblur="this.value=this.value.toUpperCase()"/>
						</td>
		</tr>
 		<tr>
						<td class="label">
							<bean:message key="qaevent.reportingSequence"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="reportingSequence"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="qaevent.reportingText"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="reportingText"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="qaevent.testId"/>:
						</td>	
						<td> 
				
						<html:select name="<%=formName%>" property="selectedTestId">
					   	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="tests" 
										label="testName" 
										value="id"  
							        	allowEdits="true"
							/>
                       </html:select>
                      
						</td>
		</tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="qaEventForm"/>

