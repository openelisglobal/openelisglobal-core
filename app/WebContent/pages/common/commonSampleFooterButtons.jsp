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
<table border="0" cellpadding="0" cellspacing="0">
	<tbody valign="middle">
		<tr>
<% 	
    String saveDisabled = (String)request.getAttribute(IActionConstants.SAVE_DISABLED);    
%>
	   	<td><%--bugzilla 1968 validate before lightbox--%>      
   			<html:button  onclick="if (validateForm(window.document.forms[0])) {checkValidCityZipCodeCombination();}"                                 
						 property="save" styleId="save" disabled="<%=Boolean.valueOf(saveDisabled).booleanValue()%>">
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
	    <%--dont need reset option yet--%>
	   	<%--td>
	    		
  			<app:button  onclick="setAction(window.document.forms[0], 'Reset', 'no', '');" property="cancel" >
		  			   <bean:message key="label.button.reset"/>
  			</app:button>
	    </td--%>
	    <td>&nbsp;</td>
	    <%--bugzilla 1714 clerical will not be printing - remove two print buttons--%>
	    <%--td>
  			<app:button  onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');" property="cancel" >
						
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
   		<td>
  			<app:button  onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');" property="cancel" >
					
  			   <bean:message key="label.button.printRequestForm"/>
  			</app:button>
	    </td>
	    <td>&nbsp;</td>
	    </tr>
	 </tbody>
</table>