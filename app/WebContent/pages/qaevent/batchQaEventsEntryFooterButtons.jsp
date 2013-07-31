<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%--bugzilla 1664--%>
<center>
<table border="0" cellpadding="0" cellspacing="0">
	<tbody valign="middle">
		<tr>
		   <% 	
		    String allowEdits = "true";
		    if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
		      		  	allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
    		}
    		String saveDisabled = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
    		String addDisabled = (String)request.getAttribute(IActionConstants.ADD_DISABLED);
    		
           %>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>	
	    <td><%--bugzilla 2180 fixed to use Boolean--%>
  			<html:button  onclick="saveThis();" property="save" disabled="<%=Boolean.valueOf(addDisabled).booleanValue()%>">
  			   <bean:message key="label.button.save"/>
  			</html:button>
	    </td>
        
		<td>&nbsp;</td>
		<td>
  			<html:button  onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');" property="cancel" >
  			   <bean:message key="label.button.exit"/>
  			</html:button>
	    </td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	 </tbody>
</table>
</center>