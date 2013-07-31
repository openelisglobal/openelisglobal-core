<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<table width="100%">
<tr width="100%">
<td width="15%"><tiles:insert attribute="left"/></td>
<td width="85%"><tiles:insert attribute="right"/></td>
</tr>
<tr><td colspan="2">
<tiles:insert attribute="homePageContent"/>
</td>
</tr>
</table>
