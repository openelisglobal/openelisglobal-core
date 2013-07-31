<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>


<center>
<table cellpadding="0" cellspacing="1" width="100%">
<%--id is important for activating the menu tabs: see tabs.jsp from struts-menu for how masterListsSubMenu is used--%>
<%-- similar code will need to be added in the left panel and in tabs.jsp for any menu tab that has the submenu on the left hand side--%>
<ul id="requestFormEntrySubMenu" class="leftnavigation">

<li> 
  <html:link action="/SampleLabelPrint">
    <bean:message key="sample.label.print.title" />
  </html:link>
</li>
<li> 
  <html:link action="/QuickEntry">
    <bean:message key="quick.entry.title" />
  </html:link>
</li>
<br/>
<br/>
<li> 
  <html:link action="/HumanSampleOne">
    <bean:message key="human.sample.one.title" />
  </html:link>
</li>
<li> 
    <html:link action="/HumanSampleTwo">
    <bean:message key="human.sample.two.title" />
  </html:link>
</li>
<br/>
<br/>
<%-- bugzilla 2529 --%>
<li> 
    <html:link action="/NewbornSampleOne">
    <bean:message key="newborn.sample.one.title" />
  </html:link>
</li>
<%-- bugzilla 2530 --%>
<li> 
    <html:link action="/NewbornSampleTwo">
    <bean:message key="newborn.sample.two.title" />
  </html:link>
</li>
<%-- bugzilla 2531 --%>
<li> 
    <html:link action="/NewbornSampleFull">
    <bean:message key="newborn.sample.full.title" />
  </html:link>
</li>
<%-- bugzilla 2093 removed links --%>
<%--
<li> 
    <html:link action="/AnimalSampleOne">
    <bean:message key="animal.sample.one.title" />
  </html:link>
</li>
<li> 
    <html:link action="/AnimalSampleTwo">
    <bean:message key="animal.sample.two.title" />
  </html:link>
</li>
<br/>
<br/>
<li> 
    <html:link action="/BTSampleOne">
    <bean:message key="bt.sample.one.title" />
  </html:link>
</li>
<li> 
    <html:link action="/BTSampleTwo">
    <bean:message key="bt.sample.two.title" />
  </html:link>
</li>
<br/>
<br/>
<li> 
  <html:link action="/RabiesSampleOne">
    <bean:message key="rabies.sample.one.title" />
  </html:link>
</li>
<li> 
  <html:link action="/RabiesSampleTwo">
    <bean:message key="rabies.sample.two.title" />
  </html:link>
</li>
--%>
</ul>
</table>
</center>