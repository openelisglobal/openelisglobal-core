<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>


<center>
	<table cellpadding="0" cellspacing="1" width="100%">
		<%--id is important for activating the menu tabs: see tabs.jsp from struts-menu for how masterListsSubMenu is used--%>
		<%-- similar code will need to be added in the left panel and in tabs.jsp for any menu tab that has the submenu on the left hand side--%>
		<ul id="requestFormEntrySubMenu" class="leftnavigation">

			<li>
				<html:link action="/SampleDemographicsAndTestManagement">
					<bean:message key="testmanagement.sample.demographics.title" />
				</html:link>
			</li>
			<!-- bugzilla 2564 -->
			<li>
				<html:link action="/SampleDemographicsAndTestManagementNewborn">
					<bean:message key="testmanagement.newborn.sample.demographics.title" />
				</html:link>
			</li>
			<!--bugzilla 2070 remove this temporarily as this has not been implemented yet-->
			<!--li>
				<html:link action="/BatchTest">
					<bean:message key="testmanagement.batch.test.title" />
				</html:link>
			</li-->
			<br />
			<br />
		</ul>
	</table>
</center>
