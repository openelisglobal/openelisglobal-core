<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<center>
<table border="0" cellpadding="0" cellspacing="0">
	<tbody valign="middle">
		<tr>
		   <% 	
		    String allowEdits = "true";
		    if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
    		  	allowEdits = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.ALLOW_EDITS_KEY);
    		}
           %>
	     <td>
    	   <%--bugzilla 1899 check city/state/zip combo before collection date--%>
    	   <%--bugzilla 1968 validate before lightbox--%> 
   		   <html:button  onclick="if (validateForm(window.document.forms[0])) {checkValidCityZipCodeCombination();}" property="save" styleId="save" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">
  		            <bean:message key="label.button.save"/>
  		   </html:button>
         </td>
		<td>&nbsp;</td>
		<td>
  			<app:button property="cancel" onclick="cancelToParentForm();" >
  						<%--AIS - bugzilla 1860--%>
	      	       	   <bean:message key="label.button.exit"/>
	      	</app:button>
       </td>
	    </tr>
	 </tbody>
</table>
</center>