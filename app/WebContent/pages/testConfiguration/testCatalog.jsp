<%@ page language="java"
         contentType="text/html; charset=utf-8"
         import="java.util.List,
         us.mn.state.health.lims.testconfiguration.beans.TestCatalogBean"
        %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>

<%--
  ~ The contents of this file are subject to the Mozilla Public License
  ~ Version 1.1 (the "License"); you may not use this file except in
  ~ compliance with the License. You may obtain a copy of the License at
  ~ http://www.mozilla.org/MPL/
  ~
  ~ Software distributed under the License is distributed on an "AS IS"
  ~ basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
  ~ License for the specific language governing rights and limitations under
  ~ the License.
  ~
  ~ The Original Code is OpenELIS code.
  ~
  ~ Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
  --%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>'/>
<bean:define id="testList" name='<%=formName%>' property="testList" type="List<TestCatalogBean>" />

<%!
  String currentTestUnitName;
%>

<%
  currentTestUnitName = "";
%>

Test Catalog<br/>

<% for ( TestCatalogBean bean : testList){ %>
<hr/>
<% if(!currentTestUnitName.equals(bean.getTestUnit())){  %>
<h2><%=bean.getTestUnit()%></h2>
<hr/>
<%
  currentTestUnitName = bean.getTestUnit();
  } %>
<table width="80%">
  <tr>
    <td width="10%" ><span class="catalog-label" >Name</span></td>
    <td width="20%"><span class="catalog-label" >En.</span> <%=bean.getEnglishName()%></td>
    <td width="20%"><span class="catalog-label" >Fr.</span> <%=bean.getFrenchName()%></td>
    <td width="10%" ><span class="catalog-label" >Report Name</span></td>
    <td width="20%"><span class="catalog-label" >En.</span> <%=bean.getEnglishReportName()%></td>
    <td width="20%"><span class="catalog-label" >Fr.</span> <%=bean.getFrenchReportName()%></td>
  </tr>
  <tr>
    <td></td>
    <td><%=bean.getActive()%></td>
    <td><%=bean.getOrderable()%></td>
  </tr>
  <tr>
    <td></td>
    <td><span class="catalog-label" >Test Unit</span> <%=bean.getTestUnit()%></td>
    <td><span class="catalog-label" >Sample Type</span> <%=bean.getSampleType()%></td>
    <td></td>
    <td><span class="catalog-label" >Panel</span> <%=bean.getPanel()%></td>
    <td><span class="catalog-label" >Result Type</span> <%=bean.getResultType()%></td>
  </tr>
</table>
<%} %>
