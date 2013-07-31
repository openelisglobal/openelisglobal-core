<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.SystemConfiguration,
	us.mn.state.health.lims.common.util.StringUtil" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />

<script language="JavaScript1.2">
    function validateForm(form) {
        return validateLoginForm(form);
    }

function submitOnEnter(e){

 if (enterKeyPressed(e)) {
   var button = document.getElementById("submitButton");
   e.returnValue=false;
   e.cancel = true;
   button.click();
 }

}


function submitOnClick(button){
     setAction(window.document.forms[0], 'Validate', 'yes', '');
}

</script>

<table width="100%">
<tr>
    <td width="50%" valign="top">
        <table width="95%">
        <tr>
            <td width="20%">&nbsp;</td>
            <td colspan="2">
                <bean:message key="login.notice.message"/>
            </td>
            <td width="20%">&nbsp;</td>
        </tr>
        <tr>
            <td width="20%">&nbsp;<br/><br/></td>
            <td colspan="2">
                <%= StringUtil.getContextualMessageForKey("login.notice.notification") %>
            </td>
            <td width="20%">&nbsp;</td>
        </tr>
        </table>
        <br>
        <table width="95%">
        <tr>
            <td width="20%">&nbsp;</td>
            <td width="10%" noWrap><bean:message key="login.msg.userName"/>:</td>
            <td colspan="2" align="left">
                <%--bugzilla 2173, 2376--%>
                <html:text name="<%=formName%>" property="loginName" onkeypress="submitOnEnter(event)"/>
            </td>
        </tr>
        <tr>
            <td width="20%">&nbsp;</td>
            <td width="10%" noWrap><bean:message key="login.msg.password"/>:</td>
            <td colspan="2" align="left">
                <%--bugzilla 2173, 2376--%>
                <html:password name="<%=formName%>" property="password" onkeypress="submitOnEnter(event)"/>
            </td>
        </tr>
        </table>
    </td>
</tr>
</table>

<app:javascript formName="loginForm"/>


