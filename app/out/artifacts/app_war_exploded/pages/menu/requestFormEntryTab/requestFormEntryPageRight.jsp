<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%!
String menuDef = "default";

%>
<%
if (request.getAttribute("menuDefinition") != null) {
 menuDef = (String)request.getAttribute("menuDefinition");
}
//System.out.println("menuDef " + menuDef);
%>
<bean:define id="menuDef" value="<%=menuDef%>" />
<center>
<table cellpadding="0" cellspacing="0" width="100%" height="100%" border="0">
<tr>
<td>
<logic:equal name="menuDef" value="HumanSampleOneDefinition">
  <tiles:insert attribute="rightHumanSampleOne" />
</logic:equal>
<logic:equal name="menuDef" value="default">
<tiles:insert attribute="right"/>
</logic:equal>
</td>
</tr>
</table>
</center>