<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%--bugzilla 1908 changed some disabled values for Vietnam tomcat/linux--%>

	<% 	
		    String previousDisabled = "false";
            String nextDisabled = "false"; 
            if (request.getAttribute(IActionConstants.PREVIOUS_DISABLED) != null) {
               previousDisabled = (String)request.getAttribute(IActionConstants.PREVIOUS_DISABLED);
            }
            if (request.getAttribute(IActionConstants.NEXT_DISABLED) != null) {
               nextDisabled = (String)request.getAttribute(IActionConstants.NEXT_DISABLED);
            }
 
        %>
<script language="JavaScript1.2">
function customOnLoad() {
  //nothing to do here?
}     
function cancelToParentForm() {
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
   //alert("going to clear save clicked");
        window.opener.clearClicked();
        //alert("closing window");
        window.close();
   } 
}

</script>        
<center>
<table border="0" cellpadding="0" cellspacing="0">
	<tbody valign="middle">
		<tr>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
		  <td>
 			<html:button onclick="cancelToParentForm();"  property="cancel" >
  			   <bean:message key="label.button.exit"/>
  			</html:button>
	      </td>
   		  <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
   		  <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  		  <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  		  <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
   		  <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  		  <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	    </tr>
	 </tbody>
</table>
</center>
