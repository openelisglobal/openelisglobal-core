<%@ page language="java" contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.util.SystemConfiguration,
	us.mn.state.health.lims.common.util.ConfigurationProperties,
	us.mn.state.health.lims.common.formfields.AdminFormFields,us.mn.state.health.lims.common.formfields.AdminFormFields.Field,
	us.mn.state.health.lims.siteinformation.valueholder.SiteInformation,
	us.mn.state.health.lims.siteinformation.daoimpl.SiteInformationDAOImpl"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>



<%!String permissionBase = SystemConfiguration.getInstance()
			.getPermissionAgent();
	AdminFormFields adminFields = AdminFormFields.getInstance();
	SiteInformation dataSubmission = (new SiteInformationDAOImpl()).getSiteInformationByName("Data Submission");%>
<table cellpadding="0" cellspacing="1" width="100%" align="left">
	<%--id is important for activating the menu tabs: see tabs.jsp from struts-menu for how masterListsSubMenu is used--%>
	<%-- similar code will need to be added in the left panel and in tabs.jsp for any menu tab that has the submenu on the left hand side--%>

	<ul id="masterListsSubMenu" class="leftnavigation">
		<%
			if ("true"
					.equals(ConfigurationProperties
							.getInstance()
							.getPropertyValueLowerCase(
									ConfigurationProperties.Property.TrainingInstallation))) {
		%>
		<li><html:link action="/DatabaseCleaning">
				<bean:message key="database.clean" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.AnalyzerTestNameMenu)) {
		%>
		<li><html:link action="/AnalyzerTestNameMenu">
				<bean:message key="analyzerTestName.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.CodeElementXref)) {
		%>
		<li><html:link action="/CodeElementXref">
				<bean:message key="codeelementxref.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.CodeElementTypeMenu)) {
		%>
		<li><html:link action="/CodeElementTypeMenu">
				<bean:message key="codeelementtype.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.CountyMenu)) {
		%>
		<li><html:link action="/CountyMenu">
				<bean:message key="county.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.DictionaryMenu)) {
		%>
		<li><html:link action="/DictionaryMenu">
				<bean:message key="dictionary.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.DictionaryCategoryMenu)) {
		%>
		<li><html:link action="/DictionaryCategoryMenu">
				<bean:message key="dictionarycategory.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.LabelMenu)) {
		%>
		<li><html:link action="/LabelMenu">
				<bean:message key="label.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.MethodMenu)) {
		%>
		<li><html:link action="/MethodMenu">
				<bean:message key="method.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.OrganizationMenu)) {
		%>
		<li><html:link action="/OrganizationMenu">
				<bean:message key="organization.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.PanelMenu)) {
		%>
		<li><html:link action="/PanelMenu">
				<bean:message key="panel.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.PanelItemMenu)) {
		%>
		<li><html:link action="/PanelItemMenu">
				<bean:message key="panelitem.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.PatientTypeMenu)) {
		%>
		<li><html:link action="/PatientTypeMenu">
				<bean:message key="patienttype.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.ProgramMenu)) {
		%>
		<li><html:link action="/ProgramMenu">
				<bean:message key="program.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.ProjectMenu)) {
		%>
		<li><html:link action="/ProjectMenu">
				<bean:message key="project.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.ProviderMenu)) {
		%>
		<li><html:link action="/ProviderMenu">
				<bean:message key="provider.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.QaEventMenu)) {
		%>
		<li><html:link action="/QaEventMenu">
				<bean:message key="qaevent.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.ReceiverCodeElementMenu)) {
		%>
		<li><html:link action="/ReceiverCodeElementMenu">
				<bean:message key="receivercodeelement.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.RegionMenu)) {
		%>
		<li><html:link action="/RegionMenu">
				<bean:message key="region.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.ResultLimitsMenu)) {
		%>
		<li><html:link action="/ResultLimitsMenu">
				<bean:message key="resultlimits.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.RoleMenu)) {
		%>
		<li><html:link action="/RoleMenu">
				<bean:message key="role.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.SiteInformationMenu)) {
		%>
		<li><html:link action="/SiteInformationMenu">
				<bean:message key="siteInformation.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.SampleEntryMenu)) {
		%>
		<li><html:link action="/SampleEntryConfigMenu">
				<bean:message key="sample.entry.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<li><html:link action="/TestManagementConfigMenu">
				<bean:message key="configuration.test.management" />
			</html:link></li>

		<li><html:link action="/BatchTestReassignment">
				<bean:message key="configuration.batch.test.reassignment" />
			</html:link></li>

		<li><html:link action="/MenuStatementConfigMenu">
				<bean:message key="MenuStatementConfig.browse.title" />
			</html:link></li>

		<%
			if (adminFields.useField(Field.PATIENT_ENTRY_CONFIGURATION)) {
		%>
		<li><html:link action="/PatientConfigurationMenu">
				<bean:message key="patientEntryConfiguration.browse.title" />
			</html:link></li>
		<%
			}
		%>

		<%
			if (adminFields.useField(Field.ResultInformationMenu)) {
		%>
		<li><html:link action="/ResultConfigurationMenu">
				<bean:message key="resultConfiguration.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.PRINTED_REPORTS_CONFIGURATION)) {
		%>
		<li><html:link action="/PrintedReportsConfigurationMenu">
				<bean:message key="printedReportsConfiguration.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.WORKPLAN_CONFIGURATION)) {
		%>
		<li><html:link action="/WorkplanConfigurationMenu">
				<bean:message key="workplanConfiguration.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.NON_CONFORMITY_CONFIGURATION)) {
		%>
		<li><html:link action="/NonConformityConfigurationMenu">
				<bean:message key="nonConformityConfiguration.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.SampleDomainMenu)) {
		%>
		<li><html:link action="/SampleDomainMenu">
				<bean:message key="sampledomain.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.ScriptletMenu)) {
		%>
		<li><html:link action="/ScriptletMenu">
				<bean:message key="scriptlet.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.SourceOfSampleMenu)) {
		%>
		<li><html:link action="/SourceOfSampleMenu">
				<bean:message key="sourceofsample.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.StatusOfSampleMenu)) {
		%>
		<li><html:link action="/StatusOfSampleMenu">
				<bean:message key="statusofsample.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.TestMenu)) {
		%>
		<li><html:link action="/TestMenu">
				<bean:message key="test.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.TestAnalyteMenu)) {
		%>
		<li><html:link action="/TestAnalyteMenu">
				<bean:message key="testanalyte.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.TestReflexMenu)) {
		%>
		<li><html:link action="/TestReflexMenu">
				<bean:message key="testreflex.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.TestResultMenu)) {
		%>
		<li><html:link action="/TestResultMenu">
				<bean:message key="testresult.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.TestSectionMenu)) {
		%>
		<li><html:link action="/TestSectionMenu">
				<bean:message key="testsection.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.TestTrailerMenu)) {
		%>
		<li><html:link action="/TestTrailerMenu">
				<bean:message key="testtrailer.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.TypeOfSampleMenu)) {
		%>
		<li><html:link action="/TypeOfSampleMenu">
				<bean:message key="typeofsample.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.TypeOfSamplePanelMenu)) {
		%>
		<li><html:link action="/TypeOfSamplePanelMenu">
				<bean:message key="typeofsample.panel" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.TypeOfSampleTestMenu)) {
		%>
		<li><html:link action="/TypeOfSampleTestMenu">
				<bean:message key="typeofsample.test" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.TypeOfTestResultMenu)) {
		%>
		<li><html:link action="/TypeOfTestResultMenu">
				<bean:message key="typeoftestresult.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.UnitOfMeasureMenu)) {
		%>
		<li><html:link action="/UnitOfMeasureMenu">
				<bean:message key="unitofmeasure.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.TestAnalyteTestResult)) {
		%>
		<li><html:link action="/TestAnalyteTestResult">
				<bean:message key="testanalytetestresult.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.TestUsageAggregatation)) {
		%>
		<li><html:link action="/TestUsageConfiguration">
				<bean:message key="testusageconfiguration.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (adminFields.useField(Field.RESULT_REPORTING_CONFIGURATION)) {
		%>
		<li><html:link action="/ResultReportingConfiguration">
				<bean:message key="resultreporting.browse.title" />
			</html:link></li>
		<%
			}
		%>
		
		<li><html:link action="/BarcodeConfiguration">
				<bean:message key="barcodeconfiguration.browse.title" />
		</html:link></li>
		
		<%
			if ("true".equalsIgnoreCase((new SiteInformationDAOImpl()).getSiteInformationByName("Data Submission").getValue())) {
		%>
		<li><html:link action="/DataSubmission">
				<bean:message key="datasubmission.browse.title" />
		</html:link></li>
		<%
			}
		%>
		
		<li><html:link action="/ListPlugins">
				<bean:message key="plugin.menu.list.plugins" />
			</html:link></li>
		<hr>
		<%
			if (adminFields.useField(Field.SystemUserModuleMenu)) {
		%>
		<li><html:link action="/SystemUserModuleMenu">
				<bean:message key="systemusermodule.browse.title" />
			</html:link></li>
		<%
			}
		%>
		<%
			if (permissionBase.equals("USER")) {
		%>
		<li><html:link action="/SystemUserSectionMenu">
				<bean:message key="systemusersection.browse.title" />
			</html:link></li>
		<%
			} else if (permissionBase.equals("ROLE")) {
		%>
		<li><html:link action="/UnifiedSystemUserMenu">
				<bean:message key="unifiedSystemUser.browser.title" />
			</html:link></li>
		<%
			}
		%>
	</ul>
</table>
