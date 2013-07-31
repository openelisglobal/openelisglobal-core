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
	   <th>
	     <bean:message key="label.form.select"/>
	   </th>
	   <th>
	      <bean:message key="analyte.id"/>
	   </th>
	   <%--th>
	   	  <bean:message key="analyte.parent"/>
	   </th--%>
	   <th>
	   	  <bean:message key="analyte.analyteName"/>
	   </th>
	   <th>
	      <bean:message key="analyte.isActive"/>
	   </th>
	   <th>
	   	  <bean:message key="analyte.externalId"/>
	   </th>
       <!--bugzilla 2432-->
	   <th>
	      <bean:message key="analyte.localAbbreviation"/>
	   </th>	   
	</tr>
	<logic:iterate id="anal" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.analyte.valueholder.Analyte">
	<bean:define id="analID" name="anal" property="id"/>
	<logic:notEmpty name="anal" property="analyte">
	 <bean:define id="parentAnalyteID" name="anal" property="analyte.id"/>
	</logic:notEmpty>
	
	  <tr>	
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs">
	         <bean:write name="analID" />
	      </html:multibox>
     
   	   </td>
	   <td class="textcontent">
	      <bean:write name="anal" property="id"/>
	   </td>
	   <%--td class="textcontent">
	    <logic:notEmpty name="anal" property="analyte">
	        <bean:write name="anal" property="analyte.analyteName"/>
	    </logic:notEmpty>
	      &nbsp;
	    </td--%>
	   <td class="textcontent">
	   	  <bean:write name="anal" property="analyteName"/>
	   	   &nbsp;
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="anal" property="isActive"/>
	   </td>
	   <td class="textcontent">
	     <bean:write name="anal" property="externalId"/>
	   	  &nbsp;
	    </td>
       <!--bugzilla 2432-->
	   <td class="textcontent">
   	      <app:write name="anal" property="localAbbreviation" maxLength="10" />
	      &nbsp;
       </td>
     </tr>
	</logic:iterate>
</table>
