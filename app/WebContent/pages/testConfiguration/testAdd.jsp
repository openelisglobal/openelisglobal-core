<%@ page language="java"
         contentType="text/html; charset=utf-8"
         import="us.mn.state.health.lims.common.util.StringUtil"
        %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants" %>
<%@ page import="us.mn.state.health.lims.common.util.IdValuePair" %>
<%@ page import="us.mn.state.health.lims.common.util.Versioning" %>
<%@ page import="us.mn.state.health.lims.common.util.SystemConfiguration" %>
<%@ page import="us.mn.state.health.lims.common.services.TypeOfTestResultService" %>

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

<%!
    String basePath = "";
    String locale = "en_US";
%>
<%
    String path = request.getContextPath();
    basePath = request.getScheme() + "://" + request.getServerName() + ":"
            + request.getServerPort() + path + "/";
    locale = SystemConfiguration.getInstance().getDefaultLocale().toString();
%>
<!--Do not add jquery.ui.js, it will break the sorting -->
<script type="text/javascript" src="scripts/jquery.asmselect.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/multiselectUtils.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="scripts/jquery-ui.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<link rel="stylesheet" type="text/css" href="css/jquery.asmselect.css?ver=<%= Versioning.getBuildNumber() %>" />
<link rel="stylesheet" media="screen" type="text/css"
      href="<%=basePath%>css/jquery_ui/jquery.ui.theme.css?ver=<%= Versioning.getBuildNumber() %>"/>
<link rel="stylesheet" type="text/css" href="css/openElisCore.css?ver=<%= Versioning.getBuildNumber() %>" />


<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>'/>
<bean:define id="sampleTypeList" name='<%=formName%>'  property="sampleTypeList" type="java.util.List<IdValuePair>" />
<bean:define id="panelList" name='<%=formName%>'  property="panelList" type="java.util.List<IdValuePair>" />
<bean:define id="uomList" name='<%=formName%>'  property="uomList" type="java.util.List<IdValuePair>" />
<bean:define id="resultTypeList" name='<%=formName%>'  property="resultTypeList" type="java.util.List<IdValuePair>" />
<bean:define id="testUnitList" name='<%=formName%>'  property="labUnitList" type="java.util.List<IdValuePair>" />


    <script type="text/javascript">
        var step = "step1";

        if (!$jq) {
            var $jq = jQuery.noConflict();
        }

        $jq(document).ready( function() {
            $jq("select[multiple]").asmSelect({
                removeLabel: "X"
            });

            $jq("select[multiple]").change(function (e, data) {
                handleMultiSelectChange(e, data);
            });
        });


        function handleMultiSelectChange(e, data){
            if( step == 'step2'){
                createOrderBoxForSampleType(e, data);
            }
            checkReadyForNextStep(e,data);
        }

        function guideSelection(checkbox) {
            if (checkbox.checked) {
                $jq("#guide").show();
            } else {
                $jq("#guide").hide();
            }
        }

        function copyFromTestName(){
            $jq("#testReportNameEnglish").val($jq("#testNameEnglish").val());
            $jq("#testReportNameFrench").val($jq("#testNameFrench").val());
        }

        function createOrderBoxForSampleType(e, data){
            var sampleTypeName = $jq("#sampleTypeSelection option[value=" + data.value + "]").text();
            var divId = data.value;
            if( data.type == 'add'){
                createNewSortingDiv(sampleTypeName, divId);
                getTestsForSampleType(data.value, testForSampleTypeSuccess);
            }else{
                $jq("#" + divId).remove();
            }
        }

        function testForSampleTypeSuccess(xhr){
            //alert(xhr.responseText);
            var response = xhr.responseXML.getElementsByTagName("formfield").item(0);
            var tests = response.getElementsByTagName("test");
            var sampleTypeId = getValueFromXmlElement(response, "sampleTypeId");
            var test, name, id, li, span;
            var ul = $jq(document.createElement("ul"));
            var length = tests.length;
            ul.addClass("sortable");

            for( var i = 0; i < length; ++i){
                test = tests[i];
                name = getValueFromXmlElement( test, "name" );
                id = getValueFromXmlElement( test, "id" );
                li = createLI( id, name, false);
                ul.append(li);
            }

            <% if( locale.equals("en_US")){ %>
            li = createLI( 0, $jq("#testNameEnglish").val(), true);
            <% } else { %>
            li = createLI( 0, $jq("#testNameFrench").val(), true);
            <% } %>
            ul.append(li);
            $jq("#sort" + sampleTypeId).append(ul);

            $jq(".sortable").sortable();
            $jq(".sortable").disableSelection();
        }

        function createLI(id, name, highlight){
            var li = $jq(document.createElement("li"));
            var span = $jq(document.createElement("span"));

            li.val(id);
            li.addClass("ui-state-default_oe");
            span.addClass("ui-icon ui-icon-arrowthick-2-n-s");
            li.append(span);
            li.append(name);
            if (highlight) {
                li.addClass("altered");
            }
            return li
        }
        function getValueFromXmlElement( parent, tag ){
            var element = parent.getElementsByTagName( tag );
            return element ? element[0].childNodes[0].nodeValue : "";
        }
        function createNewSortingDiv( sampleTypeName, divId){
            var mainDiv = $jq(document.createElement("div"));
            var nameSpan = createNameSpan(sampleTypeName);
            var sortSpan = createSortSpan(divId);

            mainDiv.attr("id", divId);
            mainDiv.addClass( "sortingMainDiv");
            mainDiv.css("padding", "20px");
            mainDiv.append(nameSpan);
            mainDiv.append(sortSpan);
            $jq("#endOrderMarker").before(mainDiv);

        }

        function createNameSpan( sampleTypeName){
            var nameSpan = $jq(document.createElement("span"));
            nameSpan.append(sampleTypeName);
            return nameSpan;
        }

        function createSortSpan(divId){
            var sortSpan = $jq(document.createElement("span"));
            sortSpan.attr("id", "sort" + divId);

            return sortSpan;
        }
        function checkReadyForNextStep(){
            var ready = true;
            if( step == "step1"){
               $jq("#step1Div .required").each(function(){
                   if(!$jq(this).val() || $jq(this).val() == 0 || $jq(this).val().length == 0 ){ ready = false; } });
            }else if( step == "step2"){
                $jq("#step2Div .required").each(function(){
                    if(!$jq(this).val() || $jq(this).val() == 0 || $jq(this).val().length == 0 ){ ready = false; } });
            }
            $jq( "#nextButton" ).prop( "disabled", !ready );
        }

        function nextStep(){
            var resultTypeId;

            if( step == 'step1'){
                step = 'step2';
                setStep1ReadOnlyFields();
                $jq("#step1Div").hide();
                $jq(".step2").show();
                $jq( "#nextButton" ).prop( "disabled", true );
            }else if( step == 'step2'){
              resultTypeId = $jq("#resultTypeSelection").val();
              if( resultTypeId == '<%= TypeOfTestResultService.ResultType.ALPHA.getId()%>' ||
                      resultTypeId == '<%= TypeOfTestResultService.ResultType.REMARK.getId()%>' ){
                  $jq("#sampleTypeSelectionDiv").hide();
                  //The reason for the li is that the sample sortable UL is hardcoded as sortable, even if it has no contents
                  if( $jq(".sortable li").length > 0) {
                      $jq(".sortable").sortable("disable");
                  }
                  $jq("#sortTitleDiv").text("Sample type and test sort order");
                  $jq(".confirmHide").hide();
                  $jq(".confirmShow").show();
                  createJSON();
              }
            }
        }
        function navigateBack(){
            if( step == 'step1'){
                submitAction('TestManagementConfigMenu.do');
            }else if( step == 'step2'){
                step = 'step1';
                $jq('.step2').hide();
                $jq("#step1Div").show();
                $jq( "#nextButton" ).prop( "disabled", false );
                $jq( ".sortingMainDiv").remove();
                $jq('.asmListItemRemove').each(function(i) {
                    $jq(this).click();
                });
            }
        }

        function navigateBackFromConfirm(){
            if( step == 'step2'){
                $jq("#sampleTypeSelectionDiv").show();
                //The reason for the li is that the sample sortable UL is hardcoded as sortable, even if it has no contents
                if( $jq(".sortable li").length > 0) {
                    $jq(".sortable").sortable("enable");
                }
                $jq("#sortTitleDiv").text('<%=StringUtil.getMessageForKey("label.test.display.order")%>');
                $jq(".confirmHide").show();
                $jq(".confirmShow").hide();
            }
        }

        function setStep1ReadOnlyFields(){
            var panelNames = "";

            $jq("#testNameEnglishRO").text($jq("#testNameEnglish").val());
            $jq("#testNameFrenchRO").text($jq("#testNameFrench").val());
            $jq("#testReportNameEnglishRO").text($jq("#testReportNameEnglish").val());
            $jq("#testReportNameFrenchRO").text($jq("#testReportNameFrench").val());
            $jq("#testSectionRO").text($jq("#testUnitSelection  option:selected").text());
            $jq("#panelSelection option:selected").each(function() {
                panelNames += ($jq(this).text()) + "\<br\>";
            });
            //we use append for optional
            $jq("#panelRO").append(panelNames);
            $jq("#uomRO").append($jq("#uomSelection  option:selected").text());
            $jq("#resultTypeRO").text($jq("#resultTypeSelection  option:selected").text());
            $jq("#activeRO").text($jq("#active").attr("checked") ? "Y" : "N");
            $jq("#orderableRO").text($jq("#orderable").attr("checked") ? "Y" : "N");
        }

        function createJSON(){
            var jsonObj = {};
            jsonObj.testNameEnglish = $jq("#testNameEnglish").val();
            jsonObj.testNameFrench = $jq("#testNameFrench").val();
            jsonObj.testReportNameEnglish = $jq("#testReportNameEnglish").val();
            jsonObj.testReportNameFrench = $jq("#testReportNameFrench").val();
            jsonObj.testSection = $jq("#testUnitSelection").val();
            jsonObj.panels = [];
            addJsonPanels(jsonObj);
            $jq("#panelSelection").val();
            jsonObj.uom = $jq("#uomSelection").val();
            jsonObj.resultType = $jq("#resultTypeSelection").val();
            jsonObj.orderable = $jq("#orderable").attr("checked") ? 'Y' : 'N';
            jsonObj.active = $jq("#active").attr("checked") ? 'Y' : 'N';
            jsonObj.sampleTypes = [];
            addJsonSortingOrder( jsonObj);
            //alert(JSON.stringify(jsonObj));
            $jq("#jsonWad").val(JSON.stringify(jsonObj));
        }

        function addJsonPanels(jsonObj) {
            var panelSelections = $jq("#panelSelection").val();
            var jsonPanel, index = 0;
            jsonObj.panels = [];
            if (panelSelections) {
                panelSelections.each(function (value, index) {
                    jsonPanel = {};
                    jsonPanel.id = value;
                    jsonObj.panels[index++] = jsonPanel;
                });
            }
        }

        function addJsonSortingOrder( jsonObj){
            var sampleTypes  = $jq("#sampleTypeSelection").val();
            var i, jsonSampleType, index, test;


            for( i = 0; i < sampleTypes.length; i++){
                jsonSampleType = {};
                jsonSampleType.typeId = sampleTypes[i];
                jsonSampleType.tests = [];
                index = 0;
                $jq("#" + sampleTypes[i] + " li").each(function(){
                    test = {};
                    test.id = $jq(this).val();
                    jsonSampleType.tests[index++] = test;
                });
                jsonObj.sampleTypes[i] = jsonSampleType;
            }
        }
        function submitAction(target) {
            var form = window.document.forms[0];
            form.action = target;
            form.submit();
        }


    </script>
    <br>
    <form>
        <html:hidden styleId="jsonWad" name='<%=formName%>' property="jsonWad" />
        <input type="button" value="<%= StringUtil.getMessageForKey("banner.menu.administration") %>"
           onclick="submitAction('MasterListsPage.do');"
           class="textButton"/> &rarr;
    <input type="button" value="<%= StringUtil.getMessageForKey("configuration.test.management") %>"
           onclick="submitAction('TestManagementConfigMenu.do');"
           class="textButton"/>&rarr;
    <bean:message key="configuration.test.add" />

    <h3><bean:message key="configuration.test.add" /></h3>

    <input type="checkbox" onchange="guideSelection(this)">Show guide<br/><br/>

    <div id="guide" style="display: none">
        <span class="requiredlabel">*</span> Indicates a required field <br/><br/>
        <b>Name</b><span class="requiredlabel">*</span><br/>
        <span class="tab">The name of the test as it will appear within openELIS.  Both English and French are required</span><br/>
        <b>Report Name</b><span class="requiredlabel">*</span><br/>
        <span class="tab">The name of the test as it will appear in reports.  Both English and French are required.
            If the Name and reporting name are the same click on the button to copy the name</span><br/>
        <b>Test Section</b><span class="requiredlabel">*</span><br/>
        <span class="tab">The test section in which the test will be done</span><br/>
        <b>Panel</b><br/>
        <span class="tab">If this test is part of a test panel then the panel can be added here.
            It is possible, but not usual, for a test to be in more then one panel</span><br/>
        <b>uom</b><br/>
        <span class="tab">Unit of measure for the test.  This usually only applies to numeric or alphanumeric result types</span><br/>
        <b>Result type</b><span class="requiredlabel">*</span><br/>
        <span class="tab">The kind of result for this test</span>
        <UL>
            <li>Numeric. Accepts only numeric results in a text box. Results can be evaluated as to being in a normal or
                a
                valid range
            </li>
            <li>Alphanumeric. Accepts either numeric or text in a text box. It will not be evaluated for being normal or
                valid
            </li>
            <li>Free text. Accepts up to 200 characters in a text area. It will not be evaluated for being normal or
                valid
            </li>
            <li>Select list. User will be able to select from a dropdown list. The normal value will be specified as the
                reference value
            </li>
            <li>Multi-select list. The user will be able to select one or more values from a dropdown list. No reference
                value will be specified
            </li>
            <li>Cascading multi-select list. Similar to multi-select but the user will be able to select multiple groups
                from the dropdown list.
            </li>
        </UL>
        <br/>
        <hr/>
    </div>


    <div id="step1Div" >
    <table width="80%">
        <tr>
            <td width="25%">
            <table>
                <tr>
                    <td colspan="2"><bean:message key="test.testName"/><span class="requiredlabel">*</span></td>
                </tr>
                <tr>
                    <td width="25%" align="right"><bean:message key="label.english" /></td>
                    <td width="75%"><input type="text" id="testNameEnglish" class="required" onchange="checkReadyForNextStep()"/></td>
                </tr>
                <tr>
                    <td width="25%" align="right"><bean:message key="label.french" /></td>
                    <td width="75%"><input type="text" id="testNameFrench" class="required" onchange="checkReadyForNextStep()"/></td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                    <td colspan="2"><bean:message key="test.testName.reporting"/><span class="requiredlabel">*</span> </td>
                </tr>
                <tr><td></td>
                    <td ><input type="button" onclick="copyFromTestName(); checkReadyForNextStep()" value='<%= StringUtil.getMessageForKey("test.add.copy.name")%>'> </td>
                </tr>
                <tr>
                    <td width="25%" align="right"><bean:message key="label.english" /></td>
                    <td width="75%"><input type="text" id="testReportNameEnglish" class="required" onchange="checkReadyForNextStep()"/></td>
                </tr>
                <tr>
                    <td width="25%" align="right"><bean:message key="label.french" /></td>
                    <td width="75%"><input type="text" id="testReportNameFrench" class="required" onchange="checkReadyForNextStep()"/></td>
                </tr>
            </table>
            </td>
            <td width="25%" style="vertical-align: top; padding: 4px">
                <bean:message key="test.testSectionName" /><span class="requiredlabel">*</span><br/>
                <select id="testUnitSelection" class="required" onchange="checkReadyForNextStep()" >
                    <option value="0"></option>
                    <% for(IdValuePair pair : testUnitList ){ %>
                    <option value='<%=pair.getId()%>' ><%=pair.getValue()%></option>
                    <% } %>
                </select>
            </td>
            <td width="25%" style="vertical-align: top; padding: 4px">
                <bean:message key="typeofsample.panel.panel" /><br/>
                <select id="panelSelection" multiple="multiple" title="Multiple">
                    <% for(IdValuePair pair : panelList ){ %>
                    <option value='<%=pair.getId()%>' ><%=pair.getValue()%></option>
                    <% } %>
                </select><br/><br/><br/>
                <bean:message key="label.unitofmeasure" /><br/>
                <select id="uomSelection" >
                    <option value='0' ></option>
                    <% for(IdValuePair pair : uomList ){ %>
                    <option value='<%=pair.getId()%>' ><%=pair.getValue()%></option>
                    <% } %>
                </select>
            </td>
            <td width="25%" style="vertical-align: top; padding: 4px">
                <bean:message key="result.resultType" /><span class="requiredlabel">*</span><br/>
                <select id="resultTypeSelection" class="required" onchange="checkReadyForNextStep()">
                    <option value="0"></option>
                    <% for(IdValuePair pair : resultTypeList ){ %>
                    <option value='<%=pair.getId()%>' ><%=pair.getValue()%></option>
                    <% } %>
                </select><br/><br/><br/><br/><br/>
                <label for="orderable" ><bean:message key="test.isActive" /></label>
                <input type="checkbox" id="active" checked="checked" /><br/>
                <label for="orderable" ><bean:message key="label.orderable" /></label>
                <input type="checkbox" id="orderable" checked="checked" />

            </td>
        </tr>
    </table>
   </div>

        <div id="step1ReadOnly" class="step2" style="float:left;  width:20%; display: none;" >
            <bean:message key="test.testName"/><br/>
            <span class="tab" ><bean:message key="label.english" />: <span id="testNameEnglishRO"></span></span><br/>
            <span class="tab" ><bean:message key="label.french" />: <span id="testNameFrenchRO"></span></span><br/>
            <br/>
            <bean:message key="test.testName.reporting"/><br/>
            <span class="tab" ><bean:message key="label.english" />: <span id="testReportNameEnglishRO"></span></span><br/>
            <span class="tab" ><bean:message key="label.french" />: <span id="testReportNameFrenchRO"></span></span><br/>
            <br/>
            <bean:message key="test.testSectionName" />
            <div id="testSectionRO" class="tab"></div><br/>
            <bean:message key="typeofsample.panel.panel" />
            <div class="tab" id="panelRO" ><bean:message key="label.none"/></div><br/>
            <bean:message key="label.unitofmeasure" />
            <div class="tab" id="uomRO" ><bean:message key="label.none"/></div><br/>
            <bean:message key="result.resultType" />
            <div class="tab" id="resultTypeRO" ></div><br/>
            <bean:message key="test.isActive" />
            <div class="tab" id="activeRO" ></div><br/>
            <bean:message key="label.orderable" />
            <div class="tab" id="orderableRO" ></div><br/>
        </div>
        <div id="step2Div" class="step2" style="float:right;  width:80%; display: none" >
            <div id="sampleTypeSelectionDiv" style="float:left; width:20%;" >
                <bean:message key="label.sampleType" />
                <select id="sampleTypeSelection" class="required" multiple="multiple" title="Multiple">
                    <% for(IdValuePair pair : sampleTypeList ){ %>
                    <option value='<%=pair.getId()%>' ><%=pair.getValue()%></option>
                    <% } %>
                </select><br/>
            </div>
            <div id="testDisplayOrderDiv" style="float:left; width:40%;" >
                <div id="sortTitleDiv" align="center" ><bean:message key="label.test.display.order" /></div>
                <div id="endOrderMarker" ></div>
            </div>
        </div>


    <div class="selectShow confirmHide" style="margin-left:auto; margin-right:auto;width: 40%; ">
        <input type="button"
               value="<%= StringUtil.getMessageForKey("label.button.next") %>"
               disabled="disabled"
               onclick="nextStep();"
               id="nextButton"/>
        <input type="button" value="<%=StringUtil.getMessageForKey("label.button.back")%>" onclick="navigateBack()" />
    </div>
        <div class="selectShow confirmShow" style="margin-left:auto; margin-right:auto;width: 40%; display: none" >
            <input type="button"
                   value="<%= StringUtil.getMessageForKey("label.button.accept") %>"
                   onclick="submitAction('TestAddUpdate.do');"/>
            <input type="button" value="<%=StringUtil.getMessageForKey("label.button.back")%>" onclick="navigateBackFromConfirm()" />
        </div>
    </form>