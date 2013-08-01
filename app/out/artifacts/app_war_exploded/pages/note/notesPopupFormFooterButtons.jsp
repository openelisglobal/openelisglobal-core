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
		    //bugzilla 2204
		    String saveDisabled = "true"; 	
		    if (request.getAttribute(IActionConstants.SAVE_DISABLED) != null) {
    		  	saveDisabled = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.SAVE_DISABLED);
    		}
    		String addDisabled = "true";
    		if (request.getAttribute(IActionConstants.ADD_DISABLED) != null) {
    		  	addDisabled = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.ADD_DISABLED);
    		}
           %>
	     <td>
	        <!--bugzilla 2204-->
	      	<html:button property="save" onclick="saveItToParentForm(document.forms[0]);" disabled="<%=Boolean.valueOf(saveDisabled).booleanValue()%>">	
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
       <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
       <td>
        <!--bugzilla 2204-->
	     <html:button  styleClass="button" property="add" onclick="addRowToPopupPage();" disabled="<%=Boolean.valueOf(addDisabled).booleanValue()%>">
            <bean:message key="label.button.add"/>
         </html:button>
       </td>
	    </tr>
	 </tbody>
</table>
</center>