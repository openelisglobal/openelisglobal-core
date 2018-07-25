<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%!
String menuDef = "default";
%>
<%
menuDef = "default";
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
<logic:equal name="menuDef" value="AnalyzerTestNameMenuDefinition">
  <tiles:insert attribute="rightAnalyzerTestName" />
</logic:equal>
<logic:equal name="menuDef" value="DictionaryMenuDefinition">
  <tiles:insert attribute="rightDictionary" />
</logic:equal>
<logic:equal name="menuDef" value="OrganizationMenuDefinition">
  <tiles:insert attribute="rightOrganization" />
</logic:equal>
<logic:equal name="menuDef" value="PatientTypeMenuDefinition">
  <tiles:insert attribute="rightPatientType" />
</logic:equal>
<logic:equal name="menuDef" value="ResultLimitsMenuDefinition">
  <tiles:insert attribute="rightResultLimits" />
</logic:equal>
<logic:equal name="menuDef" value="RoleMenuDefinition">
  <tiles:insert attribute="rightRole" />
</logic:equal>
<logic:equal name="menuDef" value="SiteInformationMenuDefinition">
  <tiles:insert attribute="rightSiteInformation" />
</logic:equal>
<logic:equal name="menuDef" value="TestSectionMenuDefinition">
  <tiles:insert attribute="rightTestSection" />
</logic:equal>
<logic:equal name="menuDef" value="TypeOfSamplePanelMenuDefinition">
  <tiles:insert attribute="rightTypeOfSamplePanel" />
</logic:equal>
<logic:equal name="menuDef" value="TypeOfSampleTestMenuDefinition">
  <tiles:insert attribute="rightTypeOfSampleTest" />
</logic:equal>
<logic:equal name="menuDef" value="UserRoleMenuDefinition">
  <tiles:insert attribute="rightUserRole" />
</logic:equal>
<logic:equal name="menuDef" value="UnifiedSystemUserMenuDefinition">
  <tiles:insert attribute="rightSystemUserOnePage" />
</logic:equal>
<logic:equal name="menuDef" value="default">
<tiles:insert attribute="right"/>
</logic:equal>
</td>
</tr>
</table>
</center>