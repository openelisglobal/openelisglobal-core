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
  			<html:button onclick="if(checkClicked()) 
							 {
							 	return false;
							 }
							 else {
                                //bugzilla 2236
                                validateData();
							  
							 }" property="save" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">
  			   <bean:message key="label.button.save"/>
  			</html:button>
  	    </td>
        
		<td>&nbsp;</td>
		<td>
  			<html:button onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');"  property="cancel" >
  			   <%--AIS - bugzilla 1860--%>
  			   <bean:message key="label.button.exit"/>
  			</html:button>
	    </td>
   		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
   		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
   		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  		<td>
  			<html:button onclick="setAction(window.document.forms[0], 'Update', 'yes', '?direction=previous&ID=');"  property="previous" disabled="<%=Boolean.valueOf(previousDisabled).booleanValue()%>">
  			   <bean:message key="label.button.previous"/>
  			</html:button>
	    </td>
     	<td>&nbsp;</td>
   		<td>
  			<html:button onclick="setAction(window.document.forms[0], 'Update', 'yes', '?direction=next&ID=');"  property="next" disabled="<%=Boolean.valueOf(nextDisabled).booleanValue()%>">
  			   <bean:message key="label.button.next"/>
  			</html:button>
	    </td>
	    </tr>
	 </tbody>
</table>
</center>