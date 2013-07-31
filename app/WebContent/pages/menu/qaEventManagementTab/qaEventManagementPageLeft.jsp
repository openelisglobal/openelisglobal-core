<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>


<center>
<table cellpadding="0" cellspacing="1" width="100%">
<%--id is important for activating the menu tabs: see tabs.jsp from struts-menu for how masterListsSubMenu is used--%>
<%-- similar code will need to be added in the left panel and in tabs.jsp for any menu tab that has the submenu on the left hand side--%>
<ul id="qaEventManagementSubMenu" class="leftnavigation">

<li> 
  <html:link action="/QaEventsEntry">
    <bean:message key="qaevents.entry.title" />
  </html:link>
</li>
<li>
  <html:link action="/BatchQaEventsEntry">
    <bean:message key="batchqaevents.entry.title" />
  </html:link>
</li>
<li><%--bugzilla 2504--%>
  <html:link action="/PreViewQaEventsEntryLineListing">
    <bean:message key="qaevents.entry.linelisting.title" />
  </html:link>
</li>
</ul>
</table>
</center>