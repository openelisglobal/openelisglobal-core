<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Hashtable,
	us.mn.state.health.lims.analyte.valueholder.Analyte,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />


<table width="100%" border=2">
	<tr>
	   <%--th>
	     <bean:message key="label.form.select"/>
	   </th--%>
	   <%--th>
	      <bean:message key="analyte.id"/>
	   </th--%>
	   <%--th>
	   	  <bean:message key="analyte.parent"/>
	   </th--%>
	   <%--th>ids for analyte and result (just for testing)</th--%>
	   <th>
	   	  <bean:message key="analyte.analyteName"/>
	   </th>
	   <th>
	   	  <bean:message key="analyte.isActive"/>
	   </th>
	   <th>
	   	  <bean:message key="analyte.externalId"/>
	   </th>
	   <th>
	   	  <bean:message key="testanalyte.resultGroup"/>
	   </th>
  	   <th>
	   	  <bean:message key="testresult.testResultType"/>
	   </th>
	   <th>
	   	  <bean:message key="testanalytetestresult.browse.testResultValue.title"/>
	   </th>
	   
	</tr>
	<logic:iterate id="testAnalTestRes" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.testanalyte.valueholder.TestAnalyteTestResult">
	<bean:define name="testAnalTestRes" id="testAnalTestResId" property="testAnalyteTestResultId"/>
	 <tr>	
	   <%--remove selection boxes - we will edit these at the TEST level not at the test_analyte-test_result level--%>
	   <%--td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="testAnalTestResId" />
	      </html:multibox>
     
   	   </td--%>
   	   <%--td class="textcontent">
   	     <bean:write name="testAnalTestResId" />
   	     &nbsp;
   	   </td--%>
	   <td class="textcontent">
     	     <bean:write name="testAnalTestRes" property="analyteName" />
	        &nbsp;
	   </td>
	   <td class="textcontent">
           <bean:write name="testAnalTestRes" property="isActive" />
	       &nbsp;
	   </td>
	   <td class="textcontent">
	      <bean:write name="testAnalTestRes" property="externalId"/>
	     &nbsp;
	    </td>
	   <td class="textcontent">
	   	  <bean:write name="testAnalTestRes" property="resultGroup"/>
	   	  &nbsp;
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="testAnalTestRes" property="resultType"/>
	   	  &nbsp;
	   </td>
	   <td class="textcontent">
	     <bean:write name="testAnalTestRes" property="resultValue"/>
	   	  &nbsp;
	    </td>
     </tr>
	</logic:iterate>
</table>
