<!DOCTYPE html>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page 
language="java"
contentType="text/html; charset=UTF-8"
import="us.mn.state.health.lims.common.action.IActionConstants"
import="us.mn.state.health.lims.common.util.Versioning"
%>

<%!
String path = "";
String basePath = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html:html>


<%--bugzilla 2447--%>
<head>
<link rel="stylesheet" type="text/css" href="<%=basePath%>css/openElisCore.css?ver=<%= Versioning.getBuildNumber() %>" />
<script type="text/javascript" src="scripts/jquery-1.5.1.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script language="JavaScript1.2" src="<%=basePath%>scripts/utilities.jsp"></script>
<script type="text/javascript" src="<%=basePath%>scripts/prototype-1.5.1.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/scriptaculous.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/overlibmws.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ajaxtags-1.2.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/Tooltip-0.6.0.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/lightbox.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script language="JavaScript1.2">
<%if (request.getAttribute("formName") != null){%>
	var myFormName = '<%= (String)request.getAttribute("formName") %>';
<%}%>
function popupOnFocus() {
	if (window.opener.closed) {
		window.close();
	}
}
	
function popupOnLoad() {
       check_width();
       handleFocus();
	   customOnLoad();
}
	
function popupOnBlur() {
	   //handleFocus();
	   
}
	
function setAction(form, action, validate, parameters) {
   
    //alert("Iam in setAction " + form.name + " " + form.action);
  
    var sessionid = getSessionFromURL(form.action);
	var context = '<%= request.getContextPath() %>';
	var formName = form.name; 
	//alert("form name " + formName);
	var parsedFormName = formName.substring(1, formName.length - 4);
	parsedFormName = formName.substring(0,1).toUpperCase() + parsedFormName;
    //alert("parsedFormName " + parsedFormName);
    
    var idParameter = <%= (String)request.getParameter("ID") %>;
    var startingRecNoParameter = <%= (String)request.getParameter("startingRecNo")%>;
    //alert("This is idParameter " + idParameter);   
    if (!idParameter) {
       idParameter = '0';
    }
    
    if (!startingRecNoParameter) {
       startingRecNoParameter = '1';
    }
       
    if (parameters != '') {
	   parameters = parameters + idParameter;
	} else {
	   parameters = parameters + "?ID=" + idParameter;
	}
    parameters = parameters + "&startingRecNo=" + startingRecNoParameter;
	
	
	form.action = context + '/' + action + parsedFormName + ".do"  + sessionid + parameters ;
	form.validateDocument = new Object();
	form.validateDocument.value = validate;
	//alert("Going to validatedAnDsubmitForm this is action " + form.action);
	validateAndSubmitForm(form);
	
}
	

var tmr = 0;
function handleFocus()
{
   //bugzilla 1510 cross-browser use hasFocus not hasFocus()
   //bugzilla 1903 fix problem - go back to hasFocus() for now 
  if(window.opener.document.hasFocus()) {
    window.focus();
    clearInterval(tmr);
    tmr = 0;
  } else {
    if(tmr == 0)tmr = setInterval("handleFocus()", 500);
  }
}
var imp= null; 
function impor()
{
  imp='norefresh';
} 

setTimeout("impor()",359999);
</SCRIPT>


<title><bean:write name="<%=IActionConstants.PAGE_TITLE_KEY%>" scope='request' /></title>
</head>
  <body onBlur="popupOnBlur();" onLoad="popupOnLoad();" onFocus="popupOnFocus();">
  <% if (request.getAttribute(IActionConstants.ACTION_KEY) != null) { %>
       <form name='<%=(String)request.getAttribute(IActionConstants.FORM_NAME) %>' action='<%=(String)request.getAttribute(IActionConstants.ACTION_KEY) %>' onSubmit="return submitForm(this);" method="POST">
  <% } %>
	<%--tiles:insert attribute="error"/--%>
    <table width="100%" cellpadding="0" cellspacing="0">
		<tr>
			<td class="popuplistheader"><bean:write name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>" scope='request' /></td>
		</tr>
        <%--bugzilla 2227: for history by sample popup--%>
		<tr>
		    <td><tiles:insert attribute="preSelectionHeader"/></td>
		</tr>
		<tr>
			<% request.setAttribute( IActionConstants.CLOSE, "true"); %>
			<td class="popuplistdata"><tiles:insert attribute="body"/></td>
		</tr>
		<tr>
		    <td><tiles:insert attribute="footer"/></td>
		</tr>
	</table>

<% if (request.getAttribute(IActionConstants.ACTION_KEY) != null) { %>
</form>
<% } %>
</body>
</html:html>
