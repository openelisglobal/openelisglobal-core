<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%--bugzilla 2554--%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
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
		        String saveDisabled = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.SAVE_DISABLED);
           %>
          <%--bugzilla 1802 added save button for screen redesign--%>
          <%--bugzilla 2554--%>
          <logic:notEmpty name="<%=formName%>" property="testTestAnalytes">
          <td>
            	<html:button onclick="if(checkClicked()) 
							 {
							 	return false;
							 }
							 else {
							   saveThis(document.forms[0]);
							 }" property="save" disabled="<%=Boolean.valueOf(saveDisabled).booleanValue()%>">
  			       <bean:message key="label.button.save"/>
  		    	</html:button>
          </td>
          </logic:notEmpty>
		<td>&nbsp;</td>
		<td><%--bugzilla 1348 need to differentiate from where we were coming : menu or results verification --%>
  			<%--html:button onclick="setAction(window.document.forms[0], 'Cancel', 'no', '?close=true&ID=');"  property="cancel" >
  			   <bean:message key="label.button.cancel"/>
  			</html:button--%>
 			<html:button onclick="cancelResultsEntry();"  property="cancel" >
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