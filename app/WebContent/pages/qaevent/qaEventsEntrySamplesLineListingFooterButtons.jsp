<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants,org.apache.struts.Globals"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<!--bugzilla 2504-->      
<center>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tbody valign="middle">
	    <tr height="22"><td>&nbsp;</td></tr>
		<tr>
		   <% 	
		    String allowEdits = "true";
		    if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
    		  	allowEdits = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.ALLOW_EDITS_KEY);
    		}
           %>
		<td width="47%">&nbsp;</td>
		<td width="6%">
  			<html:button onclick="cancelQaEventsEntryLineListing();"  property="cancel" >
  			   <bean:message key="label.button.exit"/>
  			</html:button>
	    </td>
		<td width="47%">&nbsp;</td>
 	    </tr>
	 </tbody>
</table>
</center>