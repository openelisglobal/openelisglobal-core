<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>

<%@ page import="us.mn.state.health.lims.common.util.SystemConfiguration"%>

<%!
	
	String bannerStyle = null;
%>
<%

	bannerStyle = SystemConfiguration.getInstance().getBannerStyle();


	if (bannerStyle != SystemConfiguration.DEFAULT_BANNER_STYLE) {
%>


<div  id="IEWarning" style="display:none;background-color:#b0c4de"  ><b>
<bean:message  key="banner.menu.ie.warning"/><br/>
<bean:message key="banner.menu.ie.instructions"/></b>
</div>

<table cellpadding="30" align="center">
<tr>
	<td align="center" width="139" valign="top">
		<img src="images/mainSamples.jpg" /><br/>
		<h1 class="txtHeader"><bean:message key="banner.menu.sample"/></h1>
	</td>
	<td align="center" width="139" valign="top">
		<img src="images/mainPatient.jpg" /><br/>
		<h1 class="txtHeader"><bean:message key="banner.menu.patient"/></h1>
	</td>
	<td align="center" width="139" valign="top">
		<img src="images/mainResults.jpg" /><br/>
		<h1 class="txtHeader"><bean:message key="banner.menu.results"/></h1>
	</td>
	<td align="center" width="139" valign="top">
		<img src="images/mainInventory.jpg" /><br/>
		<h1 class="txtHeader"><bean:message key="banner.menu.inventory"/></h1>
	</td>
	<td align="center" width="139" valign="top">
		<img src="images/mainReports.jpg" /><br/>
		<h1 class="txtHeader"><bean:message key="banner.menu.reports"/></h1>
	</td>
</tr>
</table>

<script language="JavaScript1.2">

function initWarning(){
	var ua = navigator.userAgent;

	//all we care about is if it is IE in non-capatibility mode
	var regEx = new RegExp("MSIE 8");
	var messageNeeded = regEx.test(ua);

	if( messageNeeded ){
		$("IEWarning").show();
	}else{
		$("IEWarning").hide();
	}
}

initWarning();
</script>


<%
}

%>
