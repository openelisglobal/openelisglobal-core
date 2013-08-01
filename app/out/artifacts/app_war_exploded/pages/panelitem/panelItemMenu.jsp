<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Hashtable,
	us.mn.state.health.lims.panelitem.valueholder.PanelItem,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />


<table width="100%" border=2">
	<tr>
	   <th>
	     <bean:message key="label.form.select"/>
	   </th>
	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <th>
	      <bean:message key="panelitem.id"/>
	   </th>
	   --%>
	   <%--bugzilla 1399 replacing method with parent panel since we are sorting on that column--%>
	   <th>
	      <bean:message key="panelitem.panelParent"/>
	   </th>
	   <th>
	      <bean:message key="panelitem.testName"/>
	   </th>
	   <%--th>
	   	  <bean:message key="panelitem.parent"/>
	   </th>
	   <th>
	   	  <bean:message key="panelitem.panelitemName"/>
	   </th--%>
	   <th>
	   	  <bean:message key="panelitem.sortOrder"/>
	   </th>
	   
	</tr>
	<logic:iterate id="pi" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.panelitem.valueholder.PanelItem">
	<bean:define id="piID" name="pi" property="id"/>
	<logic:notEmpty name="pi" property="panel">
	 <bean:define id="parentPanelID" name="pi" property="panel.id"/>
	</logic:notEmpty>
	
	  <tr>	
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="piID" />
	      </html:multibox>
     
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="pi" property="id"/>
	   </td>
	   --%>
	   <%--bugzilla 1399 replacing method with parent panel since we are sorting on that column--%>
	   <td class="textcontent">
	      <bean:write name="pi" property="panel.panelName"/>
	   </td>
	   <td class="textcontent">
	      <bean:write name="pi" property="testName"/>
	   </td>
	   <%--td class="textcontent">
	    <logic:notEmpty name="pi" property="panel">
	        <bean:write name="pi" property="panelitem.selectedPanelName"/>
	    </logic:notEmpty>
	      &nbsp;
	    </td>
	   <td class="textcontent">
	   	  <bean:write name="pi" property="panelItemName"/>
	   </td--%>
	   <td class="textcontent">
	     <bean:write name="pi" property="sortOrder"/>
	   	  &nbsp;
	    </td>
     </tr>
	</logic:iterate>
</table>
