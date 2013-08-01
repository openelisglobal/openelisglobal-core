<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<table width="95%">
<tr><td colspan="4">&nbsp;</td>
<tr>
    <td width="20%">&nbsp;</td>	
    <td width="110" noWrap>&nbsp;</td>
    <td colspan="2" align="left">
        <%--bugzilla 2376--%>
        <html:button property="save" styleId="submitButton" onclick="submitOnClick(this);return false;">
  			       <bean:message key="label.button.submit"/>
  		</html:button>
        <html:button property="changePassword" styleId="changePasswordButton" onclick="setAction(window.document.forms[0], 'ChangePassword', 'no', '');" >
  		    <bean:message key="label.button.changePassword"/>
  		</html:button>
    </td>        
</tr>         
</table>