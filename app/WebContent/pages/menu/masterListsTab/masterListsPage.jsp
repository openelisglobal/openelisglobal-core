<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ page import="us.mn.state.health.lims.common.util.ConfigurationProperties" %>
<%@ page import="us.mn.state.health.lims.common.util.Versioning" %>
<%@ page import="us.mn.state.health.lims.common.util.ConfigurationProperties.Property" %>


Build number: <%= Versioning.getBuildNumber() %>&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="ellis.configuration" />:&nbsp;
<%=ConfigurationProperties.getInstance().getPropertyValue( Property.configurationName )%><br/>
<table width="100%">
<tr>
<td width="15%" valign="top"><tiles:insert attribute="left"/></td>
<td width="85%"><tiles:insert attribute="right"/></td>
</tr>
</table>

