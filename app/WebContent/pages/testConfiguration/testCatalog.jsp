<%@ page language="java"
         contentType="text/html; charset=utf-8"
         import="java.util.List,
                 us.mn.state.health.lims.testconfiguration.beans.TestCatalogBean"
        %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants" %>
<%@ page import="us.mn.state.health.lims.testconfiguration.beans.ResultLimitBean" %>
<%@ page import="us.mn.state.health.lims.common.util.StringUtil" %>

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
<bean:define id="testList" name='<%=formName%>' property="testList" type="List<TestCatalogBean>"/>
<bean:define id="testSectionList" name='<%=formName%>' property="testSectionList" type="List<String>"/>


<%!
    String currentTestUnitName;
%>

<%
    currentTestUnitName = "";
%>

<script type="text/javascript">
    if (!$jq) {
        var $jq = jQuery.noConflict();
    }

    function sectionSelection(checkbox) {
        var element = $jq(checkbox).val();
        if (checkbox.checked) {
            $jq("#" + element).show();
        } else {
            $jq("#" + element).hide();
        }
    }

    function sectionSelectionAll(checkbox) {
        var checked = checkbox.checked;
        var element;

        $jq(".testSection").each(function () {
            element = $jq(this);
            element.prop('checked', checked);
            if (checked) {
                $jq("#" + element.val()).show();
            } else {
                $jq("#" + element.val()).hide();
            }
        })
    }

    function guideSelection(checkbox) {
        if (checkbox.checked) {
            $jq("#guide").show();
        } else {
            $jq("#guide").hide();
        }
    }

    function submitAction(target) {
        var form = window.document.forms[0];
        form.action = target;
        form.submit();
    }
</script>
<form>
    <input type="button" value="<%= StringUtil.getMessageForKey("banner.menu.administration") %>"
           onclick="submitAction('MasterListsPage.do');"
           class="textButton"/> &rarr;
    <input type="button" value="<%= StringUtil.getMessageForKey("configuration.test.management") %>"
           onclick="submitAction('TestManagementConfigMenu.do');"
           class="textButton"/>&rarr;
    <bean:message key="configuration.test.catalog" />
</form>
<h1><bean:message key="configuration.test.catalog" /></h1>
<input type="checkbox" onchange="guideSelection(this)"><bean:message key="configuration.test.catalog.guide.show" /><br/><br/>

<div id="guide" style="display: none"><bean:message key="configuration.test.catalog.guide" /><hr/>
</div>

<h4><bean:message key="configuration.test.catalog.sections" /></h4>
<input type="checkbox" onchange="sectionSelectionAll(this)"><bean:message key="label.all" /><br/><br/>
<% for (String testSection : testSectionList) {%>
<input type="checkbox" class="testSection" value='<%=testSection.replace(" ", "_").replace("/", "_")%>'
       onchange="sectionSelection(this)"><%=testSection%><br/>
<% } %>
<br/>
<%-- This div has to do with the divs in the loop.  The closing div is before the opening div because each change of test unit
needs to be in a div.  This div matches the first time through and there is a closing div at the end of the html
which closes it the last time through--%>
<div>
<% for (TestCatalogBean bean : testList) { %>
<hr/>
    <% if (!currentTestUnitName.equals(bean.getTestUnit())) { %>
</div>
<div id='<%=bean.getTestUnit().replace(" ", "_").replace("/", "_")%>' style="display: none">


    <h2><%=bean.getTestUnit()%>
    </h2>
    <hr/>
    <%
            currentTestUnitName = bean.getTestUnit();
        } %>
    <table width="80%">
        <tr>
            <td colspan="2"><span class="catalog-label"><bean:message key="configuration.test.catalog.name" /></span></td>
            <td colspan="2"><span class="catalog-label"><bean:message key="configuration.test.catalog.report.name" /></span></td>
        </tr>
        <tr>
            <td width="25%"><span class="catalog-label">En.</span> <b><%=bean.getEnglishName()%></>
            </td>
            <td width="25%"><span class="catalog-label">Fr.</span> <b><%=bean.getFrenchName()%></>
            </td>
            <td width="25%"><span class="catalog-label">En.</span> <b><%=bean.getEnglishReportName()%></>
            </td>
            <td width="25%"><span class="catalog-label">Fr.</span> <b><%=bean.getFrenchReportName()%></>
            </td>
        </tr>
        <tr>
            <td><b><%=bean.getActive()%></>
            </td>
            <td><b><%=bean.getOrderable()%></>
            </td>
        </tr>
        <tr>
            <td><span class="catalog-label"><bean:message key="label.test.unit" /></span> <b><%=bean.getTestUnit()%></>
            </td>
            <td><span class="catalog-label"><bean:message key="label.sample.types" /></span> <b><%=bean.getSampleType()%></>
            </td>
            <td><span class="catalog-label"><bean:message key="label.panel" /></span> <b><%=bean.getPanel()%></>
            </td>
            <td><span class="catalog-label"><bean:message key="label.result.type" /></span> <b><%=bean.getResultType()%></>
            </td>
        </tr>
        <tr>
            <td><span class="catalog-label"><bean:message key="label.uom" /></span> <b><%=bean.getUom()%></>
            </td>
            <td><span class="catalog-label"><bean:message key="label.significant.digits" /></span> <b><%= bean.getSignificantDigits() %></>
            </td>
        </tr>
        <% if (bean.isHasDictionaryValues()) {
            boolean top = true;
            for (String value : bean.getDictionaryValues()) {
        %>
        <tr>
            <td><% if (top) { %><span class="catalog-label"><bean:message key="configuration.test.catalog.select.values" /></span><% } %></td>
            <td colspan="2"><b><%=value%></>
            </td>
            <td colspan="2"><% if (top) {
                top = false;%><span class="catalog-label"><bean:message key="configuration.test.catalog.reference.value" /></span>
                <b><%=bean.getReferenceValue()%></>
            </td>
            <% } %>
        </tr>
        <%
                }
            }
        %>
        <% if (bean.isHasLimitValues()) { %>
        <tr>
            <td colspan="5" align="center"><span class="catalog-label"><bean:message key="configuration.test.catalog.result.limits" /></span></td>
        </tr>
        <tr>
            <td><span class="catalog-label"><bean:message key="label.sex" /></span></td>
            <td><span class="catalog-label"><bean:message key="configuration.test.catalog.age.range.months" /></span></td>
            <td><span class="catalog-label"><bean:message key="configuration.test.catalog.normal.range" /></span></td>
            <td><span class="catalog-label"><bean:message key="configuration.test.catalog.valid.range" /></span></td>
        </tr>
        <% for (ResultLimitBean limitBean : bean.getResultLimits()) {%>
        <tr>
            <td><b><%=limitBean.getGender()%></>
            </td>
            <td><b><%=limitBean.getAgeRange()%></>
            </td>
            <td><b><%=limitBean.getNormalRange()%></>
            </td>
            <td><b><%=limitBean.getValidRange()%></>
            </td>
        </tr>
        <% } %>
        <% } %>
    </table>
<%} %>
    </div>

