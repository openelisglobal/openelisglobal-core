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
    String okDisabled = (String)request.getAttribute(IActionConstants.SAVE_DISABLED);
%>
	   	<td>
            <html:button  onclick="if(checkClicked()) 
			    {
				    return false;
				} 
				else
				{
				    setAction(window.document.forms[0], 'Process', 'yes', '?ID=');
				}"
				property="save" disabled="<%=Boolean.valueOf(okDisabled).booleanValue()%>">
  			    <bean:message key="label.button.process"/>
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