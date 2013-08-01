<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<%@ page import="us.mn.state.health.lims.common.util.SystemConfiguration"%>

<%
	String bannerStyle = SystemConfiguration.getInstance().getBannerStyle();
	
	if( bannerStyle == SystemConfiguration.DEFAULT_BANNER_STYLE ){
%>


<!--bugzilla 2075 removed some junk -->
<table>
<tr height="10">
<td>
</td>
</tr>
</table>

  
<menu:useMenuDisplayer name="TabbedMenu"
  bundle="org.apache.struts.action.MESSAGE">
  <menu:displayMenu name="myHome"/>
  <menu:displayMenu name="requestFormEntry"/>
  <menu:displayMenu name="testManagement"/>
  <menu:displayMenu name="resultManagement"/>
  <menu:displayMenu name="sampleTracking"/>
  <menu:displayMenu name="reports"/>
  <menu:displayMenu name="qaEventManagement"/>
  <!-- bugzilla 2093 -->
  <!--menu:displayMenu name="labInteractions"-->
  <menu:displayMenu name="masterLists"/>
  <menu:displayMenu name="logout"/>
</menu:useMenuDisplayer>

<table>
<tr height="30">
<td>&nbsp;</td>
</tr>
</table>

<%
	} 
%>
