<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Hashtable,
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
	      <bean:message key="codeelementtype.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="codeelementtype.text"/>
	   </th>
	   <th>
	   	  <bean:message key="codeelementtype.referenceTable"/>
	   </th>
  
	</tr>
	<logic:iterate id="cet" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.codeelementtype.valueholder.CodeElementType">
	<bean:define id="cetID" name="cet" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="cetID" />
	      </html:multibox>
     
   	   </td>
	   <td class="textcontent">
	   	  <bean:write name="cet" property="text"/>
	   </td>
	   <td class="textcontent">
          <%--bugzilla 2571 go through ReferenceTablesDAO to get reference tables info--%>
	      <logic:notEmpty name="cet" property="referenceTables">
	        <bean:write name="cet" property="referenceTables.name"/>
	      </logic:notEmpty>
	      &nbsp;
	   </td>
       </tr>
	</logic:iterate>
</table>
