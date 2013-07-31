<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants,
	org.apache.struts.*"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<center>
<table border="0">
	<tbody valign="middle">
		<tr>
<% 	
    String saveDisabled = (String)request.getAttribute(IActionConstants.SAVE_DISABLED);    
%>
	   	<td>      
   			<html:button onclick="setAction(window.document.forms[0], 'Update', 'yes', '');" property="save" styleId="save" disabled="<%=Boolean.valueOf(saveDisabled).booleanValue()%>">
  			   <bean:message key="label.button.save"/>
  			</html:button>
 	    </td>
	           
		<td>&nbsp;</td>
		<td>
  			<html:button  onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');" property="cancel" >
  			   <bean:message key="label.button.exit"/>
  			</html:button>
	    </td>
	    <td>&nbsp;</td>
 	    </tr>
	 </tbody>
</table>