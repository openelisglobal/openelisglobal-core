<%@ page language="java"
         contentType="text/html; charset=utf-8"
         import="java.util.List,
                 us.mn.state.health.lims.testconfiguration.beans.TestCatalogBean"
        %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants" %>
<%@ page import="us.mn.state.health.lims.testconfiguration.beans.ResultLimitBean" %>
<%@ page import="us.mn.state.health.lims.common.util.Versioning" %>
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
        var element

        $jq(".testSection").each(function () {
            element = $jq(this);
            if (checked) {
                element.prop('checked', true);
                $jq("#" + element.val()).show();
            } else {
                element.prop('checked', false);
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
Test Catalog
</form>
</span>
<h1>Test Catalog</h1>
<input type="checkbox" onchange="guideSelection(this)">Show guide<br/><br/>

<div id="guide" style="display: none">
    <b>Name</b><br/>
    <span class="tab">The name of the test as it will appear within openELIS.  Both English and French are shown</span><br/>
    <b>Report Name</b><br/>
    <span class="tab">The name of the test as it will appear in reports.  Both English and French are show</span><br/>
    <b>Active/Not Active</b><br/>
  <span class="tab">If the test is active it can be ordered on the order form or as part of a test algorithm.
  If it is not active it can not be ordered or be part of a test algorith</span><br/>
    <b>Orderable/Not ordererable</b><br/>
  <span class="tab">If a test is active and orderable then it can be ordered on an order form.  If it is active but
  not orderable then it will only be done if it is reflexed from another test</span><br/>
    <b>Test Unit</b><br/>
    <span class="tab">Which section of the lab performs the test.  This is also known as a test section.</span><br/>
    <b>Sample Type</b><br/>
  <span class="tab">The type of sample on which the the test can be done.  If the intake technician is able to select
  the type of sample after they have ordered the test it will be marked as user to indicate that the user will
  select the type</span><br/>
    <b>Result type</b><br/>
  <span class="tab">The kind of result for this test
  <UL>
      <li>N - Numeric. Accepts only numeric results in a text box. Results can be evaluated as to being in a normal or a
          valid range
      </li>
      <li>A - Alphanumeric. Accepts either numeric or text in a text box. It will not be evaluated for being normal or
          valid
      </li>
      <li>R - Free text. Accepts up to 200 characters in a text area. It will not be evaluated for being normal or
          valid
      </li>
      <li>D - Select list. User will be able to select from a dropdown list. The normal value will be specified as the
          reference value
      </li>
      <li>M - Multi-select list. The user will be able to select one or more values from a dropdown list. No reference
          value will be specified
      </li>
      <li>C - Cascading multi-select list. Similar to multi-select but the user will be able to select multiple groups
          from the dropdown list.
      </li>
  </UL></span><br/>
    <b>uom</b><br/>
    <span class="tab">Unit of measure for the test.  This usually only applies to numeric or alphanumeric result types</span><br/>
    <b>Significan digits</b><br/>
  <span class="tab">The number of significant digits for numeric results.  Entered results will be rounded or padded to the correct number of digits.
    The normal range will also be displayed with the correct number of significant digits</span><br/>
    <b>Select values</b><br/>
    <span class="tab">Only specified for select, multi-select or cascading multi-select results.  These are the available selections shown to the user</span><br/>
    <b>Reference value</b><br/>
    <span class="tab">The value of a selection for a healthy person.  Only given for select list results</span><br/>
    <b>Result limits</b><br/>
    <span class="tab">The limits of normal and valid results for numeric tests.  The values can depend on both the age and sex of the patient.</span><br/>
    <b>Sex</b><br/>
    <span class="tab">If the sex of the patient maters for the given values it will be specified here</span><br/>
    <b>Age range</b><br/>
    <span class="tab">If the age range (in months) maters for the given values it will be specified here</span><br/>
    <b>Normal range</b><br/>
    <span class="tab">Any numeric result within this range is what is expected in a healthy person</span><br/>
    <b>Valid range</b><br/>
    <span class="tab">Any numeric result not in this range is an indication that the test may not have been done correctly</span><br/>
    <br/>
    <b>Note:</b><br/>
    <span class="tab">n/a means not available.  The value is not specified</span>
    <hr/>
</div>

<h4>Select test section to view catalog for that section</h4>
<input type="checkbox" onchange="sectionSelectionAll(this)">All<br/><br/>
<% for (String testSection : testSectionList) {%>
<input type="checkbox" class="testSection" value='<%=testSection.replace(" ", "_")%>'
       onchange="sectionSelection(this)"><%=testSection%><br/>
<% } %>

<div>
    <% for (TestCatalogBean bean : testList) { %>
    <hr/>
    <% if (!currentTestUnitName.equals(bean.getTestUnit())) { %>
</div>
<div id='<%=bean.getTestUnit().replace(" ", "_")%>' style="display: none">
    <h2><%=bean.getTestUnit()%>
    </h2>
    <hr/>
    <%
            currentTestUnitName = bean.getTestUnit();
        } %>
    <table width="80%">
        <tr>
            <td colspan="2"><span class="catalog-label">Name</span></td>
            <td colspan="2"><span class="catalog-label">Report Name</span></td>
        </tr>
        <tr>
            <td width="25%"><span class="catalog-label">En.</span> <%=bean.getEnglishName()%>
            </td>
            <td width="25%"><span class="catalog-label">Fr.</span> <%=bean.getFrenchName()%>
            </td>
            <td width="25%"><span class="catalog-label">En.</span> <%=bean.getEnglishReportName()%>
            </td>
            <td width="25%"><span class="catalog-label">Fr.</span> <%=bean.getFrenchReportName()%>
            </td>
        </tr>
        <tr>
            <td><%=bean.getActive()%>
            </td>
            <td><%=bean.getOrderable()%>
            </td>
        </tr>
        <tr>
            <td><span class="catalog-label">Test Unit</span> <%=bean.getTestUnit()%>
            </td>
            <td><span class="catalog-label">Sample Type</span> <%=bean.getSampleType()%>
            </td>
            <td><span class="catalog-label">Panel</span> <%=bean.getPanel()%>
            </td>
            <td><span class="catalog-label">Result Type</span> <%=bean.getResultType()%>
            </td>
        </tr>
        <tr>
            <td><span class="catalog-label">uom</span> <%=bean.getUom()%>
            </td>
            <td><span class="catalog-label">Significant digits</span> <%= bean.getSignificantDigits() %>
            </td>
        </tr>
        <% if (bean.isHasDictionaryValues()) {
            boolean top = true;
            for (String value : bean.getDictionaryValues()) {
        %>
        <tr>
            <td><% if (top) { %><span class="catalog-label">Select values</span><% } %></td>
            <td colspan="2"><%=value%>
            </td>
            <td colspan="2"><% if (top) {
                top = false;%><span class="catalog-label">Reference value  </span>
                <%=bean.getReferenceValue()%>
            </td>
            <% } %>
        </tr>
        <%
                }
            }
        %>
        <% if (bean.isHasLimitValues()) { %>
        <tr>
            <td colspan="5" align="center">Result Limits</td>
        </tr>
        <tr>
            <td><span class="catalog-label">Sex</span></td>
            <td><span class="catalog-label">Age Range (months)</span></td>
            <td><span class="catalog-label">Normal Range</span></td>
            <td><span class="catalog-label">Valid Range</span></td>
        </tr>
        <% for (ResultLimitBean limitBean : bean.getResultLimits()) {%>
        <tr>
            <td><%=limitBean.getGender()%>
            </td>
            <td><%=limitBean.getAgeRange()%>
            </td>
            <td><%=limitBean.getNormalRange()%>
            </td>
            <td><%=limitBean.getValidRange()%>
            </td>
        </tr>
        <% } %>
        <% } %>
    </table>
    <%} %>
</div>
