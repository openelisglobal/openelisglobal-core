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
	   <th>
	   	  <bean:message key="qaevent.name"/>
	   </th>
	   <th>
	   	  <bean:message key="qaevent.description"/>
	   </th>
	   <th>
	   	  <bean:message key="qaevent.type"/>
	   </th>
	   <th><%--bugzilla 2506--%>
	   	  <bean:message key="qaevent.category"/>
	   </th>
	   <th>
	   	  <bean:message key="qaevent.isHoldable"/>
	   </th>
	</tr>
	<logic:iterate id="qe" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.qaevent.valueholder.QaEvent">
	<bean:define id="qeID" name="qe" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="qeID" />
	      </html:multibox>
     
   	   </td>
	   <td class="textcontent">
	   	<logic:notEmpty name="qe" property="qaEventName">
	   	  <bean:write name="qe" property="qaEventName"/>
	   	</logic:notEmpty>
	   	&nbsp;
	   </td>
	   <td class="textcontent">
	      <logic:notEmpty name="qe" property="description">
	        <bean:write name="qe" property="description"/>
	      </logic:notEmpty>
	      &nbsp;
	   </td>
	    <td class="textcontent">
	      <logic:notEmpty name="qe" property="type">
	        <bean:write name="qe" property="type.dictEntry"/>
	      </logic:notEmpty>
	      &nbsp;
	   </td>
	    <td class="textcontent"><%--bugzilla 2506--%>
	      <logic:notEmpty name="qe" property="category">
	        <bean:write name="qe" property="category.dictEntry"/>
	      </logic:notEmpty>
	      &nbsp;
	   </td>
	   <td class="textcontent"> 
		   	<logic:notEmpty name="qe" property="isHoldable">
	           <bean:write name="qe" property="isHoldable"/>
	         </logic:notEmpty>
	         &nbsp;
       </td>  
     </r>
	</logic:iterate>
</table>
