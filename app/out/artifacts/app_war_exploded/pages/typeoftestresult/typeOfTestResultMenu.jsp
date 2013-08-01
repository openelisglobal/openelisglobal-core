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
	      <bean:message key="typeoftestresult.id"/>
	   </th>
	   --%>
	   <th><%--bugzilla 1412--%>
	   	  <bean:message key="typeoftestresult.testResultType"/>
	   </th>
	   <th>
	   	  <bean:message key="typeoftestresult.description"/>
	   </th>
	   <th>
	   	  <bean:message key="typeoftestresult.hl7Value"/>
	   </th>
  
	</tr>
	<logic:iterate id="totr" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.typeoftestresult.valueholder.TypeOfTestResult">
	<bean:define id="totrID" name="totr" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="totrID" />
	      </html:multibox>
     
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="totr" property="id"/>
	   </td>
	   --%>
	   <td class="textcontent">
	   	  <bean:write name="totr" property="testResultType"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="totr" property="description"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="totr" property="hl7Value"/>
	   </td>
       </tr>
	</logic:iterate>
</table>
