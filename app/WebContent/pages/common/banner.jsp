<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>

<%@ page import="us.mn.state.health.lims.common.util.SystemConfiguration,
				 us.mn.state.health.lims.common.util.ConfigurationProperties,
				 us.mn.state.health.lims.common.util.ConfigurationProperties.Property,
				 us.mn.state.health.lims.common.action.IActionConstants,
				 us.mn.state.health.lims.login.valueholder.UserSessionData,
				 us.mn.state.health.lims.menu.util.MenuUtil,
                 us.mn.state.health.lims.common.util.Versioning,
				 us.mn.state.health.lims.common.util.StringUtil"%>

<%!String path = "";
	String basePath = "";
	String bannerStyle = null;
	String menuItems[];
	boolean languageSwitch = false;
%>
<%
	path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path
	+ "/";
	bannerStyle = SystemConfiguration.getInstance().getBannerStyle();
	languageSwitch = "true".equals(ConfigurationProperties.getInstance().getPropertyValue(Property.languageSwitch));
%>


<script language="JavaScript1.2">
function /*void*/ setLanguage( language ){

	//this weirdness is because we want the language to which we are changing, not the one we are in
	if( language == 'en_US'){
	    update = confirm("Changing the language will affect all logged in users ");
	} else if( language == 'fr-FR' ){
		update = confirm( "Modification de la langue affectera tous les utilisateurs enregistrés");
	}
	
	if( update ){
		var form = window.document.forms[0];
		form.action = "LoginPage.do?lang=" + language;
		form.submit();
	}
}


//Note this is hardcoded for haiti clinical.  Message resources would be a good way to get both language and context
function displayHelp(){

    var url = '<%=basePath%>' + 'documentation/' + '<%= StringUtil.getContextualMessageForKey("documentation") %>';

	var	newwindow=window.open( url,'name','height=1000,width=850, menuBar=yes');

	if (window.focus) {newwindow.focus()}
}

</script>

<!-- New additions below by mark47 -->
<link rel="stylesheet" type="text/css" href="<%=basePath%>css/menu.css?ver=<%= Versioning.getBuildNumber() %>" />

<%
	if (bannerStyle == SystemConfiguration.DEFAULT_BANNER_STYLE) {
%>

<div id="header">
  <div id="oe-logo">
    <img src="images/openelis_logo.png" title="OpenELIS" alt="OpenELIS" />   
  </div>
</div>

<div align="center" style="margin: 20px auto; width: 800px">
<h2><bean:message key="ellis.openelis" /></h2></div>
</div>
  

<%
	} else {  //haiti, if another style is added use a case statement
%>

<!-- Begin new menu -->

<script type="text/javascript" src="<%=basePath%>scripts/menu/hoverIntent.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/menu/superfish.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/menu/supersubs.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/menu/supposition.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript">
	// initialize superfish menu plugin. supposition added to allow sub-menus on left when window size is too small.
	jQuery(function(){
		jQuery('ul.nav-menu').supersubs({
			minWidth: 9,
			maxWidth: 100,
			extraWidth: 1
		}).superfish({
			delay: 400,
			speed: 0
		}).supposition();
	});
</script>
<!--[if ie]>
<link rel="stylesheet" type="text/css" href="<%=basePath%>css/menu-ie7.css?ver=<%= Versioning.getBuildNumber() %>" />
<![endif]-->

<div id="header">
  	<div id="oe-logo" style="width: 89px" onclick="navigateToHomePage();"><img src="images/openelis_logo.png" title="OpenELIS" alt="OpenELIS" /></div>
	<div style="margin-left: 94px">
 		<div style="display: block">
			<%
				UserSessionData usd = null;
				if (request.getSession().getAttribute(IActionConstants.USER_SESSION_DATA) != null) {
					usd = (UserSessionData) request.getSession().getAttribute(IActionConstants.USER_SESSION_DATA);
			%>
			<div id="user-info"><div><%=usd.getElisUserName()%> - <html:link page="/LoginPage.do" styleId="log-out-link" titleKey="homePage.menu.logOut.toolTip"><bean:message key="homePage.menu.logOut.toolTip"/></html:link></div></div>
			<%
				}
			%>
  	  		<div id="oe-title" onclick="navigateToHomePage();"><%=StringUtil.getContextualMessageForKey("title.default")%></div>
  		</div>  
  		<div id="oe-version" style="display: block">
    		<div id="appVersion">
    		<bean:message key="ellis.version" />:&nbsp;
		    <%= ConfigurationProperties.getInstance().getPropertyValue(Property.releaseNumber)%> (<%= ConfigurationProperties.getInstance().getPropertyValue(Property.buildNumber) %>)&nbsp;&nbsp;&nbsp;
		    <bean:message key="ellis.configuration" />:&nbsp;
		    <%=ConfigurationProperties.getInstance().getPropertyValue(Property.configurationName)%></div>
    
		    <% if("true".equals(ConfigurationProperties.getInstance().getPropertyValueLowerCase(Property.TrainingInstallation))){ %>
		      <div id="training-alert"><span title="<bean:message key="training.note"/>"><bean:message key="training.note"/></span></div>
		    <% } %>
  		</div>
<%
	if (usd != null) {
%>

<%= MenuUtil.getMenuAsHTML(path) %>

<%
		}
%>

	</div>
</div> <!-- Closes id=header -->

<%
	}
%>

<% if( languageSwitch && "loginForm".equals((String)request.getAttribute(IActionConstants.FORM_NAME)) ){ %>
  <div id="language-chooser"><a href="#" onclick="setLanguage('fr-FR')">Français</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" onclick="setLanguage('en_US')">English</a></div>
<% } %>

