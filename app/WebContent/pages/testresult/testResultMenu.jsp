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
	      <bean:message key="testresult.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="testresult.testName"/>
	   </th>
	   <th>
	   	  <bean:message key="testresult.resultGroup"/>
	   </th>
	   <th>
	   	  <bean:message key="testresult.testResultType"/>
	   </th>
	</tr>
	<logic:iterate id="tr" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.testresult.valueholder.TestResult">
	<bean:define id="trID" name="tr" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name='<%=formName%>' property="selectedIDs">
	         <bean:write name="trID" />
	      </html:multibox>
   	   </td>
   	   <%--remove the following 09/12/2006 bugzilla 1399--%>
	   <%--
	   <td class="textcontent">
	      <bean:write name="tr" property="id"/>
	   </td>
	   --%>
	   <td class="textcontent">
  	   	 <logic:notEmpty name="tr" property="test">
	   	  <bean:write name="tr" property="test.description"/>
	   	 </logic:notEmpty>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="tr" property="resultGroup"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="tr" property="testResultType"/>
	   </td>
     </tr>
	</logic:iterate>
</table>
