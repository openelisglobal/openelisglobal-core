<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ page import="us.mn.state.health.lims.common.util.Versioning" %>

Build number: <%= Versioning.getBuildNumber() %><br/>
Database version: <%= Versioning.getDatabaseVersion() %>
<table width="100%">
<tr width="100%">
<td width="15%" valign="top"><tiles:insert attribute="left"/></td>
<td width="85%"><tiles:insert attribute="right"/></td>
</tr>
</table>

