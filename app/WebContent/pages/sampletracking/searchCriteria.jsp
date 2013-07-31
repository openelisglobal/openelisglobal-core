<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants, 
	us.mn.state.health.lims.common.util.SystemConfiguration" %>   

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>
<%--bugzilla 2028/2037--%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<%!

String allowEdits = "true";

%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

request.setAttribute(IActionConstants.ALLOW_EDITS_KEY, allowEdits);

%>

<script language="JavaScript1.2">

function pageOnLoad() {
    var accn = document.getElementById("accessionNumber");
    accn.focus();
}
</script>


<table>
        <tr> 
          <td colspan="4"> 
             <h2 align="left"><bean:message key="sampletracking.subtitle.search"/> </h2>
          </td>
        </tr>
        
       <tr>
          <td>
			<bean:message key="sampletracking.accessionNumber"/>:
		 </td>

		<td>
			  <%--AIS - bugzilla 1851--%>
			  <app:text name="<%=formName%>" property="accessionNumber" allowEdits="true" maxlength="10" onkeypress="return noenter()"/>
			 
		</td>
		</tr>				
		
		<%--AIS - bugzilla 1853--%>
		<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>
   		<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>
  		<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>
  		<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>
   		<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>
  		<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>
  		
		
		
		<tr>
          <td>
			<bean:message key="sampletracking.provider.sortby"/>:
		 </td>

		<td>
			<html:select name="<%=formName%>" property="selectedSortBy">
				<app:optionsCollection name="<%=formName%>" 
				property="sortby" label="description" value="id" />
			</html:select>
		</td>
		</tr>			
		
 </table>
 

			
