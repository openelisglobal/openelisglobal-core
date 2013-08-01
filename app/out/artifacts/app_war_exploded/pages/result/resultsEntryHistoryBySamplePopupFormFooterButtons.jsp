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
		 <td>
		  <center>
    		<html:button onclick="cancelToParentForm();"  property="cancel" >
  			   <bean:message key="label.button.exit"/>
  			</html:button>
  		  </center>
	     </td>
	    </tr>
	 </tbody>
</table>
</center>