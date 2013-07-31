<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Hashtable,
	us.mn.state.health.lims.systemuser.valueholder.SystemUser,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<%--bugzilla 1922 modifications to make readonly and more useful--%>


<table width="100%" border=2">
	<tr>
	   <th>
	     <bean:message key="label.form.select"/>
	   </th>
	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <th>
	      <bean:message key="note.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="note.referencetable"/>
	   </th>
	   <th>
	   	  <bean:message key="note.referencetableid"/>
	   </th>
	   <th>
	   	  <bean:message key="note.sysuser"/>
	   </th>
	   <th>
	   	  <bean:message key="note.notetype"/>
	   </th>
       <th>
	   	  <bean:message key="note.subject"/>
	   </th>
	   
	</tr>
	<logic:iterate id="nt" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.note.valueholder.Note">
	<bean:define id="ntID" name="nt" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="ntID" />
	      </html:multibox>
     
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="nt" property="id"/>
	   </td>
	   --%>
	   <td class="textcontent">
          <%--bugzilla 2571 go through ReferenceTablesDAO to get reference tables info--%>
     	  <bean:write name="nt" property="referenceTables.name"/>
	   </td>
	   <td class="textcontent">
          <%--bugzilla 2571 go through ReferenceTablesDAO to get reference tables info--%>
	   	  <bean:write name="nt" property="referenceTables.id"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="nt" property="systemUser.nameForDisplay"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="nt" property="noteType"/>
	   </td>
       <td class="textcontent">
	   	  <bean:write name="nt" property="subject"/>
	   </td>
	     </tr>
	</logic:iterate>
</table>
