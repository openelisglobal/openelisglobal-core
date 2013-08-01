<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<%--bugzilla 1922 make readonly added allowEdits property on tags--%>
<%!

String allowEdits = "true";
//bugzilla 1494
String errorDateComparison = "";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

%>

<script language="JavaScript1.2">
function validateForm(form) {
    var validated = validateNoteForm(form);
    return validated;
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="note.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="note.sysuser"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
					   	<app:select name="<%=formName%>" property="systemUserId">
					   	  <app:optionsCollection 
										name="<%=formName%>" 
							    		property="sysUsers" 
										label="nameForDisplay" 
										value="id"  
							        	filterProperty="isActive" 
							        	filterValue="N"
							 			allowEdits="true"
							/>
                     
					   </app:select>

						</td>
		</tr>
        <tr>
						<td class="label">
							<bean:message key="note.referenceid"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<app:text name="<%=formName%>" property="referenceId"/>
						</td>
          </tr>
         <tr>
						<td class="label">
							<bean:message key="note.referencetable"/>:<span class="requiredlabel">*</span>
						</td>	
						<td><%--bugzilla 2571 go through ReferenceTablesDAO to get reference tables info--%>
							<app:text name="<%=formName%>" property="referenceTables.name"/>
						</td>
						<td><%--bugzilla 2571 go through ReferenceTablesDAO to get reference tables info--%>
						    -&nbsp;<bean:write name="<%=formName%>" property="referenceTables.id"/>
						</td>
          </tr>
          <tr>
						<td class="label">
							<bean:message key="note.notetype" />:<span class="requiredlabel">*</span>
						</td>
						<td> 
			            	<app:select name="<%=formName%>" property="noteType">
		            			<html:option value=""> </html:option>
		            			<html:option value="I"><bean:message key="note.type.internal"/></html:option>
		             			<html:option value="E"><bean:message key="note.type.external"/></html:option>
		             		</app:select>
						</td>
		</tr>
        <tr>
						<td class="label">
							<bean:message key="note.subject"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="subject"/>
						</td>
		</tr>
        <tr>
						<td class="label">
							<bean:message key="note.text"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
						    <app:textarea name="<%=formName%>" property="text" cols="50" rows="4"/>
						</td>
		</tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<app:javascript formName="noteForm" />

