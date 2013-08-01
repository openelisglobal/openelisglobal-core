<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants,org.apache.struts.Globals"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<%!
String path = "";
String basePath = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
String saveDisabled = (String)request.getAttribute(IActionConstants.SAVE_DISABLED);
%>

<center>
<table border="0" cellpadding="0" cellspacing="0">
	<tbody valign="middle">
		<tr>
	   	<td>
  			<html:button  onclick="checkValidSampleStatusInRange();"
	                 			 property="save" disabled="<%=Boolean.valueOf(saveDisabled).booleanValue()%>">
  			   <bean:message key="label.button.save"/>
  			</html:button>        			
	    </td>
        
		<td>&nbsp;</td>
		<td>
  			<html:button  onclick="setMyCancelAction(window.document.forms[0], 'Cancel', 'no', '');" property="cancel" >
  			   <bean:message key="label.button.exit"/>
  			</html:button>
	    </td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	    <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	    
	    		<td>
	    		
  			<%--app:button  onclick="if(checkAddTestClicked()) 
							                          {
							         	                 return false;
						        	                  }
						                              else
							                          {
							                           addTestPopup(window.document.forms[0]);
							                           }"
							                          property="addTest" --%>
			<%--bugzilla 1751 move this button to before assigned tests window--%>
  			<%--app:button  onclick="addTestPopup(window.document.forms[0]);"
							                          property="addTest">
		  			   <bean:message key="label.button.addTest"/>
  			</app:button--%>
	    </td>
	    <td>&nbsp;</td>
	    <%--bugzilla 1714 clerical will not be printing - remove two print buttons--%>
        <%--td>
  			<app:button onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');" property="cancel" >
						
  			   <bean:message key="label.button.printSampleLabels"/>
   			</app:button>
	    </td>
	    <td>&nbsp;</td>
        <td>
  			<app:button onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');" property="cancel" >
						
  			   <bean:message key="label.button.printTestLabels"/>
   			</app:button>
	    </td>
	    <td>&nbsp;</td--%>
	    <%--bugzilla 2067 disable button--%>
   		<td>
  			<app:button onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');"	property="cancel" allowEdits="false">
					
  			   <bean:message key="label.button.printRequestForm"/>
  			</app:button>
	    </td>
	    <td>&nbsp;</td>
	    </tr>
	 </tbody>
</table>