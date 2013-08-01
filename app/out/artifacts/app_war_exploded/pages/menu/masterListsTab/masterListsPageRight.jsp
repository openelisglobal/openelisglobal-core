<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

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
<logic:equal name="menuDef" value="ActionMenuDefinition">
  <tiles:insert attribute="rightAction" />
</logic:equal>
<logic:equal name="menuDef" value="AnalysisMenuDefinition">
  <tiles:insert attribute="rightAnalysis" />
</logic:equal>
<logic:equal name="menuDef" value="AnalyzerTestNameMenuDefinition">
  <tiles:insert attribute="rightAnalyzerTestName" />
</logic:equal>
<logic:equal name="menuDef" value="AnalyteMenuDefinition">
  <tiles:insert attribute="rightAnalyte" />
</logic:equal>
<logic:equal name="menuDef" value="TestAnalyteTestResultMenuDefinition">
  <tiles:insert attribute="rightTestAnalyteTestResult" />
</logic:equal>
<logic:equal name="menuDef" value="CodeElementTypeMenuDefinition">
  <tiles:insert attribute="rightCodeElementType" />
</logic:equal>
<%-- bugzilla 2126 --%>
<%--
<logic:equal name="menuDef" value="CountyMenuDefinition">
  <tiles:insert attribute="rightCounty" />
</logic:equal>
--%>
<logic:equal name="menuDef" value="DictionaryMenuDefinition">
  <tiles:insert attribute="rightDictionary" />
</logic:equal>
<logic:equal name="menuDef" value="DictionaryCategoryMenuDefinition">
  <tiles:insert attribute="rightDictionaryCategory" />
</logic:equal>
<logic:equal name="menuDef" value="GenderMenuDefinition">
  <tiles:insert attribute="rightGender" />
</logic:equal>
<logic:equal name="menuDef" value="LabelMenuDefinition">
  <tiles:insert attribute="rightLabel" />
</logic:equal>
<logic:equal name="menuDef" value="MessageOrganizationMenuDefinition">
  <tiles:insert attribute="rightMessageOrganization" />
</logic:equal>
<logic:equal name="menuDef" value="MethodMenuDefinition">
  <tiles:insert attribute="rightMethod" />
</logic:equal>
<logic:equal name="menuDef" value="NoteMenuDefinition">
  <tiles:insert attribute="rightNote" />
</logic:equal>
<logic:equal name="menuDef" value="OrganizationMenuDefinition">
  <tiles:insert attribute="rightOrganization" />
</logic:equal>
<logic:equal name="menuDef" value="PanelMenuDefinition">
  <tiles:insert attribute="rightPanel" />
</logic:equal>
<logic:equal name="menuDef" value="PanelItemMenuDefinition">
  <tiles:insert attribute="rightPanelItem" />
</logic:equal>
<logic:equal name="menuDef" value="PatientMenuDefinition">
  <tiles:insert attribute="rightPatient" />
</logic:equal>
<logic:equal name="menuDef" value="PatientTypeMenuDefinition">
  <tiles:insert attribute="rightPatientType" />
</logic:equal>
<logic:equal name="menuDef" value="PersonMenuDefinition">
  <tiles:insert attribute="rightPerson" />
</logic:equal>
<logic:equal name="menuDef" value="ProgramMenuDefinition">
  <tiles:insert attribute="rightProgram" />
</logic:equal>
<logic:equal name="menuDef" value="ProjectMenuDefinition">
  <tiles:insert attribute="rightProject" />
</logic:equal>
<logic:equal name="menuDef" value="ProviderMenuDefinition">
  <tiles:insert attribute="rightProvider" />
</logic:equal>
<logic:equal name="menuDef" value="QaEventMenuDefinition">
  <tiles:insert attribute="rightQaEvent" />
</logic:equal>
<logic:equal name="menuDef" value="ReceiverCodeElementMenuDefinition">
  <tiles:insert attribute="rightReceiverCodeElement" />
</logic:equal>
<logic:equal name="menuDef" value="ReferenceTablesMenuDefinition">
  <tiles:insert attribute="rightReferenceTables" />
</logic:equal>
<logic:equal name="menuDef" value="RegionMenuDefinition">
  <tiles:insert attribute="rightRegion" />
</logic:equal>
<logic:equal name="menuDef" value="ResultMenuDefinition">
  <tiles:insert attribute="rightResult" />
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
<logic:equal name="menuDef" value="SampleMenuDefinition">
  <tiles:insert attribute="rightSample" />
</logic:equal>
<logic:equal name="menuDef" value="SampleDomainMenuDefinition">
  <tiles:insert attribute="rightSampleDomain" />
</logic:equal>
<logic:equal name="menuDef" value="ScriptletMenuDefinition">
  <tiles:insert attribute="rightScriptlet" />
</logic:equal>
<logic:equal name="menuDef" value="SourceOfSampleMenuDefinition">
  <tiles:insert attribute="rightSourceOfSample" />
</logic:equal>
<logic:equal name="menuDef" value="StatusOfSampleMenuDefinition">
  <tiles:insert attribute="rightStatusOfSample" />
</logic:equal>
<logic:equal name="menuDef" value="TestMenuDefinition">
  <tiles:insert attribute="rightTest" />
</logic:equal>
<logic:equal name="menuDef" value="TestAnalyteMenuDefinition">
  <tiles:insert attribute="rightTestAnalyte" />
</logic:equal>
<logic:equal name="menuDef" value="TestReflexMenuDefinition">
  <tiles:insert attribute="rightTestReflex" />
</logic:equal>
<logic:equal name="menuDef" value="TestResultMenuDefinition">
  <tiles:insert attribute="rightTestResult" />
</logic:equal>
<logic:equal name="menuDef" value="TestSectionMenuDefinition">
  <tiles:insert attribute="rightTestSection" />
</logic:equal>
<logic:equal name="menuDef" value="TestTrailerMenuDefinition">
  <tiles:insert attribute="rightTestTrailer" />
</logic:equal>
<logic:equal name="menuDef" value="TypeOfSampleMenuDefinition">
  <tiles:insert attribute="rightTypeOfSample" />
</logic:equal>
<logic:equal name="menuDef" value="TypeOfSampleTestMenuDefinition">
  <tiles:insert attribute="rightTypeOfSampleTest" />
</logic:equal>
<logic:equal name="menuDef" value="TypeOfSamplePanelMenuDefinition">
  <tiles:insert attribute="rightTypeOfSamplePanel" />
</logic:equal>
<logic:equal name="menuDef" value="TypeOfTestResultMenuDefinition">
  <tiles:insert attribute="rightTypeOfTestResult" />
</logic:equal>
<logic:equal name="menuDef" value="UnitOfMeasureMenuDefinition">
  <tiles:insert attribute="rightUnitOfMeasure" />
</logic:equal>
<logic:equal name="menuDef" value="SystemUserMenuDefinition">
  <tiles:insert attribute="rightSystemUser" />
</logic:equal>
<logic:equal name="menuDef" value="LoginUserMenuDefinition">
  <tiles:insert attribute="rightLoginUser" />
</logic:equal>
<logic:equal name="menuDef" value="SystemModuleMenuDefinition">
  <tiles:insert attribute="rightSystemModule" />
</logic:equal>
<logic:equal name="menuDef" value="SystemUserModuleMenuDefinition">
  <tiles:insert attribute="rightSystemUserModule" />
</logic:equal>
<logic:equal name="menuDef" value="SystemUserSectionMenuDefinition">
  <tiles:insert attribute="rightSystemUserSection" />
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