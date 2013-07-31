<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>


<center>
<table cellpadding="0" cellspacing="1" width="100%">
<%--id is important for activating the menu tabs: see tabs.jsp from struts-menu for how masterListsSubMenu is used--%>
<%-- similar code will need to be added in the left panel and in tabs.jsp for any menu tab that has the submenu on the left hand side--%>
<ul id="resultManagementSubMenu" class="leftnavigation">

<li> 
  <html:link action="/ResultsEntry">
    <bean:message key="results.entry.title" />
  </html:link>
</li>
<li>
  <html:link action="/BatchResultsEntry">
    <bean:message key="batchresults.entry.title" />
  </html:link>
</li>
<li><%--bugzilla 1348--%>
  <html:link action="/BatchResultsVerification">
    <bean:message key="batchresults.verification.title" />
  </html:link>
</li>
</ul>
</table>
</center>