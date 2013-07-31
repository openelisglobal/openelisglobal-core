<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<%--bugzilla 2061-2063--%>
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
 return validateDictionaryCategoryForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="dictionarycategory.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
        <tr>
						<td class="label">
							<bean:message key="dictionarycategory.categoryname"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
						    <%--bugzilla 1482 don't allow lowercase--%>
							<html:text name="<%=formName%>" property="categoryName" size="50" onblur="this.value=this.value.toUpperCase()"/>
						</td>
        </tr>
        <tr>
						<td class="label">
							<bean:message key="dictionarycategory.localAbbreviation"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
						    <%--bugzilla 1482 don't allow lowercase--%>
							<html:text name="<%=formName%>" property="localAbbreviation" size="10" onblur="this.value=this.value.toUpperCase()"/>
						</td>
          </tr>
		<tr>
						<td class="label">
							<bean:message key="dictionarycategory.description"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
						    <%--bugzilla 1482 don't allow lowercase--%>
							<html:text name="<%=formName%>" property="description" size="60" onblur="this.value=this.value.toUpperCase()"/>
						</td>
		 </tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="dictionaryCategoryForm"/>

