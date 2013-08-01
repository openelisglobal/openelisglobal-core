<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Hashtable,
	us.mn.state.health.lims.common.action.IActionConstants" %>
<%@page import="org.apache.commons.validator.GenericValidator"%>

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
	      <bean:message key="testreflex.id"/>
	   </th>
	   --%>
	   <th>
	   	  <bean:message key="testreflex.test"/>
	   </th>
	   <th>
	   	  <bean:message key="testreflex.testAnalyte"/>
	   </th>
	   <th>
	   	  <bean:message key="testreflex.testResult"/>
	   </th>
       <%--bugzilla 1890 added 2 columns--%>
	   <th>
	   	  <bean:message key="testreflex.addedTest"/>
	   </th>
	   <th>
	   	  <bean:message key="testreflex.addedAction"/>
	   </th>
	   <th>
	   	  <bean:message key="testreflex.flags"/>
	   </th>
   </tr>
	<logic:iterate id="tr" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.testreflex.valueholder.TestReflex">
	<bean:define id="trID" name="tr" property="id"/>
	  <tr >
	   <td class="textcontent" <%= tr.getSiblingReflexId() != null ? "style='background-color:#C8DADA;'" : " " %>>
	   	<logic:equal name="tr" property="passiveSibling"  value="false">
	      <html:multibox name='<%=formName%>' property="selectedIDs">
	         <bean:write name="trID" />
	      </html:multibox>
	    </logic:equal>
   	   </td>
 	   <td class="textcontent" <%= tr.getSiblingReflexId() != null ? "style='background-color:#C8DADA;'" : " " %>>
 	   	 <logic:notEmpty name="tr" property="test">
 	   	   <!--bugzilla 1844-->
 	   	   <bean:write name="tr" property="test.testName"/>
 	   	 </logic:notEmpty>
 	   	 &nbsp;
	   </td>
	   <td class="textcontent" <%= tr.getSiblingReflexId() != null ? "style='background-color:#C8DADA;'" : " " %>>
 	   	 <logic:notEmpty name="tr" property="testAnalyte">
 	   	   <bean:write name="tr" property="testAnalyte.analyte.analyteName"/>
 	   	 </logic:notEmpty>
 	   	 &nbsp;
	   </td>
	    <td class="textcontent" <%= tr.getSiblingReflexId() != null ? "style='background-color:#C8DADA;'" : " " %>>
  	   	 <logic:notEmpty name="tr" property="testResult">
 	   	  <bean:write name="tr" property="testResult.value"/>
	   	 </logic:notEmpty>
	   	 &nbsp;
	   </td>
	    <td class="textcontent" <%= tr.getSiblingReflexId() != null ? "style='background-color:#C8DADA;'" : " " %>>
  	   	 <logic:notEmpty name="tr" property="addedTest">
 	   	  <bean:write name="tr" property="addedTest.testName"/>
	   	 </logic:notEmpty>
	   	 &nbsp;
	   </td>
	   <td class="textcontent" <%= tr.getSiblingReflexId() != null ? "style='background-color:#C8DADA;'" : " " %>>
  	   	 <logic:notEmpty name="tr" property="actionScriptlet">
 	   	  <bean:write name="tr" property="actionScriptlet.scriptletName"/>
	   	 </logic:notEmpty>
	   	 &nbsp;
	   </td>
	    <td class="textcontent" <%= tr.getSiblingReflexId() != null ? "style='background-color:#C8DADA;'" : " " %>>
  	   	 <logic:notEmpty name="tr" property="flags">
 	   	  <bean:write name="tr" property="flags"/>
	   	 </logic:notEmpty>
	   	 &nbsp;
	   </td>
	  </tr>
	</logic:iterate>
</table>
