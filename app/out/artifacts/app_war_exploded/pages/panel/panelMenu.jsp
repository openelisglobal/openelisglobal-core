<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Hashtable,
	us.mn.state.health.lims.panel.valueholder.Panel,
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
	      <bean:message key="panel.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="panel.panelName"/>
	   </th>
	   <th>
	   	  <bean:message key="panel.description"/>
	   </th>
	   
	</tr>
	<logic:iterate id="pan" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.panel.valueholder.Panel">
	<bean:define id="panID" name="pan" property="id"/>
	  <tr>	
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="panID" />
	      </html:multibox>
     
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="pan" property="id"/>
	   </td>
	   --%>
	 <td class="textcontent">
	   	  <bean:write name="pan" property="panelName"/>
	   </td>
	   <td class="textcontent">
	     <bean:write name="pan" property="description"/>
	   	  &nbsp;
	    </td>
     </tr>
	</logic:iterate>
</table>
