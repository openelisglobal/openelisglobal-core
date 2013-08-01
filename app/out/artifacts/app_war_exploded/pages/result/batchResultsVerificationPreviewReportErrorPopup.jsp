<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.util.SystemConfiguration,
	org.apache.struts.taglib.TagUtils,org.apache.struts.action.*,org.apache.struts.Globals,java.util.Iterator,javax.servlet.jsp.JspException,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.StringUtil,
	us.mn.state.health.lims.common.util.resources.ResourceLocator,
    us.mn.state.health.lims.common.util.Versioning,
    us.mn.state.health.lims.common.util.validator.ActionError"
	%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<%!

String allowEdits = "true";
String disabled = "false";
ActionMessage error = null;
String messages = null;
String message = null; 
ActionMessages errors = null;
String path = "";
String basePath = "";
%>

<%

path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}


errors = TagUtils.getInstance().getActionMessages(pageContext, Globals.ERROR_KEY);

Iterator iterator = errors.get();
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(Globals.LOCALE_KEY);
//bugzilla 2375
messages = null;
while (iterator.hasNext()) {
 message = null;         
 error = (ActionMessage)iterator.next();   
 message =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    error.getKey(),
                    error.getValues());
        
 if (messages == null) {
     messages = message;
 } else {
	 messages = messages + message;
 }
}
%>


<script language="JavaScript1.2">

function customOnLoad() {
  //default the cursor to save button
  var cancel = document.getElementById('cancel');
  cancel.focus();
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
<html>
<link rel="stylesheet" type="text/css" href="<%=basePath%>css/openElisCore.css?ver=<%= Versioning.getBuildNumber() %>" />
<head>
</head>
<center>
<table align="center">
<tr>
<td><span class="mediumlabel"><%=messages%></span></td>
</tr>
</table>
<table border="0" cellpadding="0" cellspacing="0">
	<tbody valign="middle">
	    <tr height="22"><td>&nbsp;</td></tr>
		<tr>
		   <% 	
		    String allowEdits = "true";
		    if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
    		  	allowEdits = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.ALLOW_EDITS_KEY);
    		}
           %>
		<td width="45%">&nbsp;</td>
		<td>
  			<html:button styleId="cancel" onclick="cancelToParentForm();"  property="cancel" >
  			   <bean:message key="label.button.exit"/>
  			</html:button>
	    </td>
 	    </tr>
	 </tbody>
</table>
</center>
</html>
