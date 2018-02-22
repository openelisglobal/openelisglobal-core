<%@ page language="java"
         contentType="text/html; charset=utf-8"
         import="us.mn.state.health.lims.common.action.IActionConstants"
        %>
<%@ page language="java"
         contentType="text/html; charset=utf-8"
         import="java.util.List,
                 us.mn.state.health.lims.testconfiguration.beans.TestCatalogBean"
        %>
<%@ page import="us.mn.state.health.lims.common.util.IdValuePair" %>
<%@ page import="us.mn.state.health.lims.common.util.StringUtil" %>
<%@ page import="us.mn.state.health.lims.common.util.Versioning" %>
<%@ page import="us.mn.state.health.lims.common.provider.query.EntityNamesProvider" %>
<%@ page import="us.mn.state.health.lims.testconfiguration.beans.ResultLimitBean" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
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

<script type="text/javascript" src="scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>'/>
<bean:define id="testList" name='<%=formName%>' property="testList" type="java.util.List<IdValuePair>"/>

<bean:define id="sampleTypeList" name='<%=formName%>' property="sampleTypeList" type="java.util.List<IdValuePair>"/>
<bean:define id="panelList" name='<%=formName%>' property="panelList" type="java.util.List<IdValuePair>"/>
<bean:define id="uomList" name='<%=formName%>' property="uomList" type="java.util.List<IdValuePair>"/>
<bean:define id="resultTypeList" name='<%=formName%>' property="resultTypeList" type="java.util.List<IdValuePair>"/>
<bean:define id="testUnitList" name='<%=formName%>' property="labUnitList" type="java.util.List<IdValuePair>"/>
<bean:define id="ageRangeList" name='<%=formName%>' property="ageRangeList" type="java.util.List<IdValuePair>"/>
<bean:define id="dictionaryList" name='<%=formName%>' property="dictionaryList" type="java.util.List<IdValuePair>"/>
<bean:define id="groupedDictionaryList" name='<%=formName%>' property="groupedDictionaryList" type="java.util.List<java.util.List<IdValuePair>>"/>
<bean:define id="testCatBeanList" name='<%=formName%>' property="testCatBeanList" type="List<TestCatalogBean>"/>

<%!
    int testCount = 0;
    int columnCount = 0;
    int columns = 3;
%>

<%
    columnCount = 0;
    testCount = 0;
%>
<form>
<script type="text/javascript">
    if (!$jq) {
        var $jq = jQuery.noConflict();
    }
    
    function makeDirty(){
        function formWarning(){
            return "<bean:message key="banner.menu.dataLossWarning"/>";
        }
        window.onbeforeunload = formWarning;
    }

    function submitAction(target) {
        var form = window.document.forms[0];
        form.action = target;
        form.submit();
    }

    function setForEditing(testId, name) {
        $jq("#editDiv").show();
        $jq("#testName").text(name);
        $jq(".error").each(function (index, value) {
            value.value = "";
            $jq(value).removeClass("error");
            $jq(value).removeClass("confirmation");
        });
        
        $jq(".test").each(function () {
            var element = $jq(this);
            element.prop("disabled", "disabled");
            element.addClass("disabled-text-button");
        });
        
        $jq(".resultClass").each(function (i,elem) {
        	// console.log("sfe: " + testId + ":" + $jq(elem).attr('fTestId'));
        	if(testId !== $jq(elem).attr("fTestId")){    		
        		$jq(elem).remove();
        	}
        });
        
        getTestNames(testId, testNameSuccess);
        getTestEntities(testId, testEntitiesSuccess);
        
        // var resultTypeId = getResultTypeId(testId);
        
        $jq("#normalRangeDiv").show();
    }
    
   
    function testEntitiesSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var message = xhr.responseXML.getElementsByTagName("message").item(0);
        var response;
        var testSectionId = "";
        var uomId = "";

        if (message.firstChild.nodeValue == "valid") {
            response = JSON.parse(formField.firstChild.nodeValue);
            testSectionId = response["entities"]["testSectionId"];
            uomId = response["entities"]["uomId"];
            
            getEntityNames(testSectionId, "<%=EntityNamesProvider.TEST_SECTION%>", testSectionNameSuccess );
            getEntityNames(uomId, "<%=EntityNamesProvider.UNIT_OF_MEASURE%>", uomNameSuccess );
            $jq("#loinc").text(response["entities"]["loinc"]);
       
           // console.log("tes: " + testSectionId );
        }

        window.onbeforeunload = null;
    }
    
    function uomNameSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var message = xhr.responseXML.getElementsByTagName("message").item(0);
        var response;

        if (message.firstChild.nodeValue == "valid") {
            response = JSON.parse(formField.firstChild.nodeValue);
            $jq("#uomEnglish").text(response["name"]["english"]);
            $jq(".required").each(function () {
                $jq(this).val("");
            });
        }

        window.onbeforeunload = null;
    }
    
    function testSectionNameSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var message = xhr.responseXML.getElementsByTagName("message").item(0);
        var response;

        if (message.firstChild.nodeValue == "valid") {
            response = JSON.parse(formField.firstChild.nodeValue);
            $jq("#testSectionEnglish").text(response["name"]["english"]);
            $jq("#testSectionFrench").text(response["name"]["french"]);
            $jq(".required").each(function () {
                $jq(this).val("");
            });
        }

        window.onbeforeunload = null;
    }

    function testNameSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var message = xhr.responseXML.getElementsByTagName("message").item(0);
        var response;


        if (message.firstChild.nodeValue == "valid") {
            response = JSON.parse(formField.firstChild.nodeValue);
            $jq("#nameEnglish").text(response["name"]["english"]);
            $jq("#nameFrench").text(response["name"]["french"]);
            $jq("#reportNameEnglish").text(response["reportingName"]["english"]);
            $jq("#reportNameFrench").text(response["reportingName"]["french"]);
            //alert(response["reportingName"]["french"]);
            $jq(".required").each(function () {
                $jq(this).val("");
            });
        }

        window.onbeforeunload = null;
    }

    function confirmValues() {
        var hasError = false;
        $jq(".required").each(function () {
            var input = $jq(this);
            if (!input.val() || input.val().strip().length == 0) {
                input.addClass("error");
                hasError = true;
            }
        });

        if (hasError) {
            alert('<%=StringUtil.getMessageForKey("error.all.required")%>');
        } else {
            $jq(".required").each(function () {
                var element = $jq(this);
                element.prop("readonly", true);
                element.addClass("confirmation");
            });
            $jq(".requiredlabel").each(function () {
                $jq(this).hide();
            });
            $jq("#editButtons").hide();
            $jq("#confirmationButtons").show();
            $jq("#action").text('<%=StringUtil.getMessageForKey("label.confirmation")%>');
        }
    }

    function rejectConfirmation() {
        $jq(".required").each(function () {
            var element = $jq(this);
            element.removeProp("readonly");
            element.removeClass("confirmation");
        });
        $jq(".requiredlabel").each(function () {
            $jq(this).show();
        });

        $jq("#editButtons").show();
        $jq("#confirmationButtons").hide();
        $jq("#action").text('<%=StringUtil.getMessageForKey("label.button.edit")%>');
    }

    function cancel() {
        $jq("#editDiv").hide();
        $jq("#testId").val("");
        $jq(".test").each(function () {
            var element = $jq(this);
            element.removeProp("disabled");
            element.removeClass("disabled-text-button");
        });
        window.onbeforeunload = null;
    }

    function handleInput(element) {
        $jq(element).removeClass("error");
        makeDirty();
    }

    function savePage() {
        window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
        var form = window.document.forms[0];
        form.action = "TestModifyUpdate.do";
        form.submit();
    }
    
    function genderMatersForRange(checked, index) {
        if (checked) {
            $jq(".sexRange_" + index).show();
        } else {
            $jq(".sexRange_" + index).hide();
            $jq("#lowNormal_G_" + index).val("-Infinity");
            $jq("#highNormal_G_" + index).val("Infinity");
            $jq("#lowNormal_G_" + index).removeClass("error");
            $jq("#highNormal_G_" + index).removeClass("error");
        }
    }
</script>

<html:hidden property="testId" name="<%=formName%>" styleId="testId"/>

<input type="button" value='<%= StringUtil.getMessageForKey("banner.menu.administration") %>'
       onclick="submitAction('MasterListsPage.do');"
       class="textButton"/> &rarr;
<input type="button" value='<%= StringUtil.getMessageForKey("configuration.test.management") %>'
       onclick="submitAction('TestManagementConfigMenu.do');"
       class="textButton"/>&rarr;
<%=StringUtil.getMessageForKey( "label.testName" )%>
<br><br>

<div id="editDiv" style="display: none">
    <h1 id="action"><bean:message key="label.button.edit"/></h1>

    <h2><%=StringUtil.getMessageForKey( "sample.entry.test" )%>:<span id="testName"></span></h2>
    <br>
    <table>
        <tr>
            <td></td>
            <th colspan="2" style="text-align: center"><bean:message key="test.testName"/></th>
            <th colspan="2" style="text-align: center"><bean:message key="test.testName.reporting"/></th>
        </tr>
        <tr>
            <td></td>
            <td style="text-align: center"><bean:message key="label.english"/></td>
            <td style="text-align: center"><bean:message key="label.french"/></td>
            <td style="text-align: center"><bean:message key="label.english"/></td>
            <td style="text-align: center"><bean:message key="label.french"/></td>
        </tr>
        <tr>
            <td style="padding-right: 20px"><bean:message key="label.current"/>:</td>
            <td id="nameEnglish" style="padding-left: 10px"></td>
            <td id="nameFrench" style="padding-left: 10px"></td>
            <td id="reportNameEnglish" style="padding-left: 10px"></td>
            <td id="reportNameFrench" style="padding-left: 10px"></td>
        </tr>
        <tr>
            <td style="padding-right: 20px"><bean:message key="label.new"/>:</td>
            <td><span class="requiredlabel">*</span><html:text property="nameEnglish" name="<%=formName%>" size="40"
                                                               styleClass="required"
                                                               onchange="handleInput(this);"/>
            </td>
            <td><span class="requiredlabel">*</span><html:text property="nameFrench" name="<%=formName%>" size="40"
                                                               styleClass="required" 
                                                               onchange="handleInput(this);"/>
            </td>
            <td><span class="requiredlabel">*</span><html:text property="reportNameEnglish" name="<%=formName%>" size="40"
                                                               styleClass="required"
                                                               onchange="handleInput(this);"/>
            </td>
            <td><span class="requiredlabel">*</span><html:text property="reportNameFrench" name="<%=formName%>" size="40"
                                                               styleClass="required"
                                                               onchange="handleInput(this);"/>
            </td>
		</tr>
       	<tr>
			<td></td>
           	<td style="text-align: center"><bean:message key="test.testSectionName"/></td>
           	<td style="text-align: center"><bean:message key="uom.uomName"/></td>
           	<td style="text-align: center"><bean:message key="label.loinc"/></td>
        </tr>
        <tr>
			<td style="padding-right: 20px"><bean:message key="label.current"/>:</td>
			<td id="testSectionEnglish" style="padding-left: 10px"></td>
			<td id="uomEnglish" style="padding-left: 10px"></td>
			<td id="loinc" style="padding-left: 10px"></td>
		</tr>
		<tr>
			<td style="padding-right: 20px"><bean:message key="label.new"/>:</td>
				
        	<td width="25%" style="vertical-align: top; padding: 4px">
                    <span class="requiredlabel">*</span>
                <select id="testUnitSelection" name="testUnitSelection" class="required" onchange="checkReadyForNextStep()">
                    <option value=""></option>
                    <% for (IdValuePair pair : testUnitList) { %>
                      <option value='<%=pair.getId()%>'><%=pair.getValue()%>
                      </option>
                    <% } %>
                </select>
            </td>
            
            <td width="25%" style="vertical-align: top; padding: 4px">
                    <span class="requiredlabel">*</span>
                <select id="uomSelection" name="uomSelection" class="required" onchange="checkReadyForNextStep()">
                    <option value=""></option>
                    <% for (IdValuePair pair : uomList) { %>
                      <option value='<%=pair.getId()%>'><%=pair.getValue()%>
                      </option>
                    <% } %>
                </select>
            </td>
            
            <td><span class="requiredlabel">*</span><html:text property="loinc" name="<%=formName%>" size="40"
                                                               styleClass="required"
                                                               onchange="handleInput(this);"/>
            </td>
        </tr>

 	<% // tr section test result from testCatalog.jsp %>
 	
	<% for (TestCatalogBean bean : testCatBeanList) { %>
	   
	   <tbody fTestId='<%= bean.getId() %>' class='resultClass' >
	   
       <% if (bean.isHasDictionaryValues()) {
            boolean top = true;
            for (String value : bean.getDictionaryValues()) {
        %>
        <tr>
            <td><% if (top) { %><span class="catalog-label"><bean:message key="configuration.test.catalog.select.values" /></span>
                <% } %></td>
            <td colspan="2"><b><%=value%></b>
            </td>
            <td colspan="2"><% if (top) {
                top = false;%><span class="catalog-label"><bean:message key="configuration.test.catalog.reference.value" /></span>
                <b><%=bean.getReferenceValue()%></b>
            </td>
            <% 
            
            	} 
             }
         }
            %>
        </tr>
        
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
            <td><b><%=limitBean.getGender()%></b>
            </td>
            <td><b><%=limitBean.getAgeRange()%></b>
            </td>
            <td><b><%=limitBean.getNormalRange()%></b>
            </td>
            <td><b><%=limitBean.getValidRange()%></b>
            </td>
        </tr>
        
				
		 <% 
		 	 } 
		   } 
         }
		 %>
	     
</tbody>

        
    </table>
    
        <div id="normalRangeDiv" style="display:none;">
        <h3><bean:message key="configuration.test.catalog.normal.range" /></h3>
        <table style="display:inline-table">
            <tr>
                <th></th>
                <th colspan="8"><bean:message key="configuration.test.catalog.normal.range" /></th>
                <th colspan="2"><bean:message key="configuration.test.catalog.valid.range" /> </th>
                <th></th>
            </tr>
            <tr>
                <td><bean:message key="label.sex.dependent" /></td>
                <td><span class="sexRange" style="display: none"><bean:message key="label.sex" /> </span></td>
                <td colspan="4" align="center"><bean:message key="label.age.range" /> </td>
                <td colspan="2" align="center"><bean:message key="label.range" /></td>
                <td align="center"><bean:message key="label.reporting.range" /></td>
                <td colspan="2"></td>
            </tr>
            <tr class="row_0">
                <td><input type="hidden" class="rowKey" value="0"/><input id="genderCheck_0" type="checkbox"
                                                                          onchange="genderMatersForRange(this.checked, '0')">
                </td>
                <td>
                        <span class="sexRange_0" style="display: none">
                            <bean:message key="sex.male" />
                        </span>
                </td>
                <td><input class="yearMonthSelect_0" type="radio" name="time_0" value="<%=StringUtil.getMessageForKey("abbreviation.year.single")%>"
                           onchange="upperAgeRangeChanged('0')" checked><bean:message key="abbreviation.year.single" />
                    <input class="yearMonthSelect_0" type="radio" name="time_0" value="<%=StringUtil.getMessageForKey("abbreviation.month.single")%>"
                           onchange="upperAgeRangeChanged('0')"><bean:message key="abbreviation.month.single" />&nbsp;</td>
                <td id="lowerAge_0">0&nbsp;</td>
                <td><input type="text" id="upperAgeSetter_0" value="Infinity" size="10"
                           onchange="upperAgeRangeChanged('0')"><span id="upperAge_0"></span></td>
                <td>
                    <select id="ageRangeSelect_0" onchange="ageRangeSelected( this, '0');">
                        <option value="0"></option>
                        <% for (IdValuePair pair : ageRangeList) { %>
                        <option value='<%=pair.getId()%>'><%=pair.getValue()%>
                        </option>
                        <% } %>
                    </select>
                </td>
                <td><input type="text" value="-Infinity" size="10" id="lowNormal_0" class="lowNormal"
                           onchange="normalRangeCheck('0');"></td>
                <td><input type="text" value="Infinity" size="10" id="highNormal_0" class="highNormal"
                           onchange="normalRangeCheck('0');"></td>
                <td><input type="text" value="" size="12" id="reportingRange_0"></td>
                <td><input type="text" value="-Infinity" size="10" id="lowValid" onchange="validRangeCheck();"></td>
                <td><input type="text" value="Infinity" size="10" id="highValid" onchange="validRangeCheck();"></td>
            </tr>
            <tr class="sexRange_0 row_0" style="display: none">
                <td></td>
                <td><bean:message key="sex.female" /></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td><input type="text" value="-Infinity" size="10" id="lowNormal_G_0" class="lowNormal"
                           onchange="normalRangeCheck('0');"></td>
                <td><input type="text" value="Infinity" size="10" id="highNormal_G_0" class="highNormal"
                           onchange="normalRangeCheck('0');"></td>
                <td><input type="text" value="" size="12" id="reportingRange_G_0"></td>
                <td></td>
                <td></td>
            </tr>
            <tr id="endRow"></tr>
        </table>
        <label for="significantDigits"><bean:message key="label.significant.digits" /></label>
        <input type="number" min="0" max="10" id="significantDigits">
    </div>
    
    
    <div style="text-align: center" id="editButtons">
        <input type="button" value='<%=StringUtil.getMessageForKey("label.button.next")%>'
               onclick="confirmValues();"/>
        <input type="button" value='<%=StringUtil.getMessageForKey("label.button.previous")%>'
               onclick='cancel()'/>
    </div>
    <div style="text-align: center; display: none;" id="confirmationButtons">
        <input type="button" value='<%=StringUtil.getMessageForKey("label.button.accept")%>'
               onclick="savePage();"/>
        <input type="button" value='<%=StringUtil.getMessageForKey("label.button.reject")%>'
               onclick='rejectConfirmation();'/>
    </div>
    <br><br>
</div>

<table>
    <% while(testCount < testList.size()){%>
    <tr>
        <td><input type="button" value='<%= ((IdValuePair)testList.get(testCount)).getValue() %>'
                   onclick="setForEditing( '<%= ((IdValuePair)testList.get(testCount)).getId() + "', '" + ((IdValuePair)testList.get(testCount)).getValue() %>');"
                   class="textButton test"/>
            <%
                testCount++;
                columnCount = 1;
            %></td>
        <% while(testCount < testList.size() && ( columnCount < columns )){%>
        <td><input type="button" value='<%= ((IdValuePair)testList.get(testCount)).getValue() %>'
                   onclick="setForEditing( '<%= ((IdValuePair)testList.get(testCount)).getId() + "', '" + ((IdValuePair)testList.get(testCount)).getValue() %>' );"
                   class="textButton test"/>
            <%
                testCount++;
                columnCount++;
            %></td>
       <% } %>

    </tr>
    <% } %>
</table>

<br>
<input type="button" value='<%=StringUtil.getMessageForKey("label.button.finished") %>'
       onclick="submitAction('TestManagementConfigMenu.do');"/>
</form>