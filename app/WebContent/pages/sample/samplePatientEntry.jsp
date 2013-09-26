<%@ page import="us.mn.state.health.lims.common.formfields.FormFields.Field"%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
                 us.mn.state.health.lims.common.util.SystemConfiguration,
                 us.mn.state.health.lims.common.util.ConfigurationProperties,
                 us.mn.state.health.lims.common.util.ConfigurationProperties.Property,
                 us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
                 us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
                 us.mn.state.health.lims.common.formfields.FormFields,
                 us.mn.state.health.lims.common.util.Versioning,
                 us.mn.state.health.lims.common.util.StringUtil,
                 us.mn.state.health.lims.common.util.IdValuePair" %>
<%@ page import="us.mn.state.health.lims.common.services.PhoneNumberService" %>


<%@ taglib uri="/tags/struts-bean"      prefix="bean" %>
<%@ taglib uri="/tags/struts-html"      prefix="html" %>
<%@ taglib uri="/tags/struts-logic"     prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"      prefix="app" %>
<%@ taglib uri="/tags/struts-tiles"     prefix="tiles" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName"      value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="idSeparator"   value='<%=SystemConfiguration.getInstance().getDefaultIdSeparator()%>' />
<bean:define id="accessionFormat" value='<%= ConfigurationProperties.getInstance().getPropertyValue(Property.AccessionFormat)%>' />
<bean:define id="genericDomain" value='' />
<bean:define id="entryDate" name="<%=formName%>" property="currentDate" />


<%!
    String basePath = "";
    boolean useSTNumber = true;
    boolean useMothersName = true;
    boolean useReferralSiteList = false;
    boolean useReferralSiteCode = false;
    boolean useProviderInfo = false;
    boolean patientRequired = false;
    boolean trackPayment = false;
    boolean requesterLastNameRequired = false;
    boolean acceptExternalOrders = false;
    IAccessionNumberValidator accessionNumberValidator;

%>
<%
    String path = request.getContextPath();
    basePath = request.getScheme() + "://" + request.getServerName() + ":"  + request.getServerPort() + path + "/";
    useSTNumber =  FormFields.getInstance().useField(FormFields.Field.StNumber);
    useMothersName = FormFields.getInstance().useField(FormFields.Field.MothersName);
    useReferralSiteList = FormFields.getInstance().useField(FormFields.Field.RequesterSiteList);
    useReferralSiteCode = FormFields.getInstance().useField(FormFields.Field.SampleEntryReferralSiteCode);
    useProviderInfo = FormFields.getInstance().useField(FormFields.Field.ProviderInfo);
    patientRequired = FormFields.getInstance().useField(FormFields.Field.PatientRequired);
    trackPayment = ConfigurationProperties.getInstance().isPropertyValueEqual(Property.TRACK_PATIENT_PAYMENT, "true");
    accessionNumberValidator = new AccessionNumberValidatorFactory().getValidator();
    requesterLastNameRequired = FormFields.getInstance().useField(Field.SampleEntryRequesterLastNameRequired);
    acceptExternalOrders = ConfigurationProperties.getInstance().isPropertyValueEqual(Property.ACCEPT_EXTERNAL_ORDERS, "true");
%>


<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>

<link rel="stylesheet" href="css/jquery_ui/jquery.ui.all.css?ver=<%= Versioning.getBuildNumber() %>">
<link rel="stylesheet" href="css/customAutocomplete.css?ver=<%= Versioning.getBuildNumber() %>">

<script src="scripts/ui/jquery.ui.core.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.widget.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.button.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.menu.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.position.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.autocomplete.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/customAutocomplete.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="scripts/laborder.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<script type="text/javascript" >

var useSTNumber = <%= useSTNumber %>;
var useMothersName = <%= useMothersName %>;
var useReferralSiteList = <%= useReferralSiteList%>;
var useReferralSiteCode = <%= useReferralSiteCode %>;
var requesterLastNameRequired = <%= requesterLastNameRequired %>
var dirty = false;
var invalidSampleElements = new Array();
var requiredFields = new Array("labNo", "receivedDateForDisplay" );
var acceptExternalOrders = <%= acceptExternalOrders %>;

if( requesterLastNameRequired ){
    requiredFields.push("providerLastNameID");
}
<% if( FormFields.getInstance().useField(Field.SampleEntryUseRequestDate)){ %>
    requiredFields.push("requestDate");
<% } %>
<%  if (requesterLastNameRequired) { %>
    requiredFields.push("providerLastNameID");
<% } %>

 
function isFieldValid(fieldname)
{
    return invalidSampleElements.indexOf(fieldname) == -1;
}

function setSampleFieldInvalid(field)
{
    if( invalidSampleElements.indexOf(field) == -1 )
    {
        invalidSampleElements.push(field);
    }
}

function setSampleFieldValid(field)
{
    var removeIndex = invalidSampleElements.indexOf( field );
    if( removeIndex != -1 )
    {
        for( var i = removeIndex + 1; i < invalidSampleElements.length; i++ )
        {
            invalidSampleElements[i - 1] = invalidSampleElements[i];
        }

        invalidSampleElements.length--;
    }
}

function isSaveEnabled()
{
    return invalidSampleElements.length == 0;
}

function submitTheForm(form)
{
    setAction(form, 'Update', 'yes', '?ID=');
}

function  /*void*/ processValidateEntryDateSuccess(xhr){

    //alert(xhr.responseText);
    
    var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;

    var isValid = message == "<%=IActionConstants.VALID%>";

    //utilites.js
    selectFieldErrorDisplay( isValid, $(formField));
    setSampleFieldValidity( isValid, formField );
    setSave();

    if( message == '<%=IActionConstants.INVALID_TO_LARGE%>' ){
        alert( '<bean:message key="error.date.inFuture"/>' );
    }else if( message == '<%=IActionConstants.INVALID_TO_SMALL%>' ){
        alert( '<bean:message key="error.date.inPast"/>' );
    }
}


function checkValidEntryDate(date, dateRange, blankAllowed)
{   
    if((!date.value || date.value == "") && !blankAllowed){
        setSave();
        return;
    } else if ((!date.value || date.value == "") && blankAllowed) {
        setSampleFieldValid(date.id);
        setValidIndicaterOnField(true, date.id);
        return;
    }


    if( !dateRange || dateRange == ""){
        dateRange = 'past';
    }
    
    //ajax call from utilites.js
    isValidDate( date.value, processValidateEntryDateSuccess, date.id, dateRange );
}


function processAccessionSuccess(xhr)
{
    //alert(xhr.responseText);
    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
    var message = xhr.responseXML.getElementsByTagName("message").item(0);
    var success = false;

    if (message.firstChild.nodeValue == "valid"){
        success = true;
    }
    var labElement = formField.firstChild.nodeValue;
    selectFieldErrorDisplay( success, $(labElement));
    setSampleFieldValidity( success, labElement);

    if( !success ){
        alert( message.firstChild.nodeValue );
    }

    setSave();
}

function processAccessionFailure(xhr)
{
    //unhandled error: someday we should be nicer to the user
}


function checkAccessionNumber( accessionNumber )
{
    //check if empty
    if ( !fieldIsEmptyById( "labNo" ) )
    {
        validateAccessionNumberOnServer(false, accessionNumber.id, accessionNumber.value, processAccessionSuccess, processAccessionFailure );
    }
    else
    {
        setSampleFieldInvalid(accessionNumber.name );
        setValidIndicaterOnField(false, accessionNumber.name);
    }

    setSave();
}


function setSampleFieldValidity( valid, fieldName ){

    if( valid )
    {
        setSampleFieldValid(fieldName);
    }
    else
    {
        setSampleFieldInvalid(fieldName);
    }
}


function checkValidTime(time, blankAllowed)
{
    var lowRangeRegEx = new RegExp("^[0-1]{0,1}\\d:[0-5]\\d$");
    var highRangeRegEx = new RegExp("^2[0-3]:[0-5]\\d$");

    if (time.value.blank() && blankAllowed == true) {
        clearFieldErrorDisplay(time);
        setSampleFieldValid(time.name);
        setSave();
        return;        
    }

    if( lowRangeRegEx.test(time.value) ||
        highRangeRegEx.test(time.value) )
    {
        if( time.value.length == 4 )
        {
            time.value = "0" + time.value;
        }
        clearFieldErrorDisplay(time);
        setSampleFieldValid(time.name);
    }
    else
    {
        setFieldErrorDisplay(time);
        setSampleFieldInvalid(time.name);
    }

    setSave();
}

function setMyCancelAction(form, action, validate, parameters)
{
    //first turn off any further validation
    setAction(window.document.forms[0], 'Cancel', 'no', '');
}


function patientInfoValid()
{
    var hasError = false;
    var returnMessage = "";

    if( fieldIsEmptyById("patientID") )
    {
        hasError = true;
        returnMessage += ": patient ID";
    }

    if( fieldIsEmptyById("dossierID") )
    {
        hasError = true;
        returnMessage += ": dossier ID";
    }

    if( fieldIsEmptyById("firstNameID") )
    {
        hasError = true;
        returnMessage += ": first Name";
    }
    if( fieldIsEmptyById("lastNameID") )
    {
        hasError = true;
        returnMessage += ": last Name";
    }


    if( hasError )
    {
        returnMessage = "Please enter the following patient values  " + returnMessage;
    }else
    {
        returnMessage = "valid";
    }

    return returnMessage;
}



function saveItToParentForm(form) {
 submitTheForm(form);
}

function getNextAccessionNumber() {
    generateNextScanNumber();
}

function generateNextScanNumber(){

    new Ajax.Request (
                          'ajaxQueryXML',  //url
                           {//options
                             method: 'get', //http method
                             parameters: "provider=SampleEntryGenerateScanProvider",
                             //indicator: 'throbbing'
                             onSuccess:  processScanSuccess,
                             onFailure:  processScanFailure
                           }
                          );
}

function processScanSuccess(xhr){
    //alert(xhr.responseText);
    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
    var returnedData = formField.firstChild.nodeValue;

    var message = xhr.responseXML.getElementsByTagName("message").item(0);

    var success = message.firstChild.nodeValue == "valid";

    if( success ){
        $("labNo").value = returnedData;

    }else{
        alert( "<%= StringUtil.getMessageForKey("error.accession.no.next") %>");
        $("labNo").value = "";
    }

    var targetName = $("labNo").name;
    selectFieldErrorDisplay(success, $(targetName));
    setValidIndicaterOnField( success, targetName );

    setSave();
}

function processScanFailure(xhr){
    //some user friendly response needs to be given to the user
}

function addPatientInfo(  ){
    $("patientInfo").show();
}

function showHideSection(button, targetId){
    if( button.value == "+" ){
        showSection(button, targetId);
    }else{
        hideSection(button, targetId);
    }
}

function showSection( button, targetId){
    $jq("#" + targetId ).show();
    button.value = "-";
}

function hideSection( button, targetId){
    $jq("#" + targetId ).hide();
    button.value = "+";
}

function /*bool*/ requiredSampleEntryFieldsValid(){

    if( acceptExternalOrders){ 
        if (missingRequiredValues())
            return false;
    }
        
    for( var i = 0; i < requiredFields.length; ++i ){
        if( $(requiredFields[i]).value.blank() ){
            //special casing
            if( requiredFields[i] == "requesterId" && 
               !( ($("requesterId").selectedIndex == 0)  &&  $("newRequesterName").value.blank())){
                continue;
            }
        return false;
        }
    }
    
    return sampleAddValid( true );
}

function /*bool*/ sampleEntryTopValid(){
    return invalidSampleElements.length == 0 && requiredSampleEntryFieldsValid();
}

function /*void*/ loadSamples(){
    alert( "Implementation error:  loadSamples not found in addSample tile");
}

function show(id){
    document.getElementById(id).style.visibility="visible";
}

function hide(id){
    document.getElementById(id).style.visibility="hidden";
}

function orderTypeSelected( radioElement){
    labOrderType = radioElement.value; //labOrderType is in sampleAdd.jsp
    if( removeAllRows){
        removeAllRows();
    }
    //this is bogus, we should go back to the server to load the dropdown
    if( radioElement.value == 2){
        $("followupLabOrderPeriodId").show();
        $("initialLabOrderPeriodId").hide();
    }else{
        $("initialLabOrderPeriodId").show();
        $("followupLabOrderPeriodId").hide();
    }
    
    $("sampleEntryPage").show();
}
function labPeriodChanged( labOrderPeriodElement){
    if( labOrderPeriodElement.length - 1 ==  labOrderPeriodElement.selectedIndex  ){
        $("labOrderPeriodOtherId").show();
    }else{
        $("labOrderPeriodOtherId").hide();
        $("labOrderPeriodOtherId").value = "";
    }
    
}

function siteListChanged(textValue){
    var siteList = $("requesterId");
    
    //if the index is 0 it is a new entry, if it is not then the textValue may include the index value
    if( siteList.selectedIndex == 0 || siteList.options[siteList.selectedIndex].label != textValue){
          $("newRequesterName").value = textValue;
    }else if(useReferralSiteCode){
        getCodeForOrganization( siteList.options[siteList.selectedIndex].value, processCodeSuccess);
    }
}

function processCodeSuccess(xhr){
    //alert(xhr.responseText);
    var code = xhr.responseXML.getElementsByTagName("code").item(0);
    var success = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue == "valid";

    if( success ){
        $jq("#requesterCodeId").val(code.getAttribute("value"));
    }
}

function capitalizeValue( text){
    $("requesterId").value = text.toUpperCase();
}

function checkOrderReferral( value ){
    
    getLabOrder(value, processLabOrderSuccess);
    showSection( $("orderSectionId"), 'orderDisplay');
}

function clearOrderData() {
    
    removeAllRows();    
    clearTable(addTestTable);
    clearTable(addPanelTable);
    clearSearchResultTable();
    addPatient();
    clearPatientInfo();
    clearRequester();
    removeCrossPanelsTestsTable();

}

function processLabOrderSuccess(xhr){
    //alert(xhr.responseText);
    
    clearOrderData();
    
    var message = xhr.responseXML.getElementsByTagName("message").item(0);
    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
    var order = formField.getElementsByTagName("order").item(0);
    
    SampleTypes = [];
    CrossPanels = [];
    CrossTests = [];
    sampleTypeMap = {};

    if( message.firstChild.nodeValue == "valid" ) {
        $jq(".patientSearch").hide();
        var patienttag = order.getElementsByTagName('patient');
        if (patienttag) {
            parsePatient(patienttag);
        }
        
        var requester = order.getElementsByTagName('requester');
        if (requester) {
            parseRequester(requester);
        }
        
        var useralert = order.getElementsByTagName("user_alert");
        var alertMessage = "";
        if (useralert) {
            if (useralert.length > 0) {
                alertMessage = useralert[0].firstChild.nodeValue;
                alert(alertMessage);
            }
        }
        var sampletypes = order.getElementsByTagName("sampleType");

        // initialize objects and globals
        sampleTypeOrder = -1;
        crossSampleTypeMap = {};
        crossSampleTypeOrderMap = {};
        
        parseSampletypes(sampletypes, SampleTypes);
        var crosspanels = order.getElementsByTagName("crosspanel"); 
        parseCrossPanels(crosspanels, crossSampleTypeMap, crossSampleTypeOrderMap);  
        var crosstests = order.getElementsByTagName("crosstest");
        parseCrossTests(crosstests, crossSampleTypeMap, crossSampleTypeOrderMap);  
        
        showSection( $("samplesSectionId"), 'samplesDisplay');  
        $("samplesAdded").show();

        notifyChangeListeners();
        testAndSetSave();
        populateCrossPanelsAndTests(CrossPanels, CrossTests, '<%=entryDate%>');
        displaySampleTypes('<%=entryDate%>');
        
        if (SampleTypes.length > 0)
             sampleClicked(1);
        
        } else {
            $jq(".patientSearch").show();
            alert(message.firstChild.nodeValue);
        }
        
}

function parsePatient(patienttag) {
    var guidtag = patienttag.item(0).getElementsByTagName("guid");
    var guid;
    if (guidtag) {
        if (guidtag[0].firstChild) {
            guid = guidtag[0].firstChild.nodeValue;
            patientSearch("", "", "", "", "", "", guid, "true", processSearchSuccess, processSearchFailure );
        }       
    }
    
    
}

function clearRequester() {

    $("providerFirstNameID").value = '';
    $("providerLastNameID").value = '';
    $("labNo").value = '';
    $("receivedDateForDisplay").value = '<%=entryDate%>';
    $("recievedTime").value = '';
    $("referringPatientNumber").value = '';

}

function parseRequester(requester) {
    var firstName = requester.item(0).getElementsByTagName("firstName");
    var first = "";
    if (firstName.length > 0) {
            first = firstName[0].firstChild.nodeValue;
            $("providerFirstNameID").value = first;
    };
    var lastName = requester.item(0).getElementsByTagName("lastName");
    var last = "";
    if (lastName.length > 0) {
            last = lastName[0].firstChild.nodeValue;
            $("providerLastNameID").value = last;    
    }
    
    var phoneNum = requester.item(0).getElementsByTagName("providerWorkPhoneID");
    var phone = "";
    if (phoneNum.length > 0) {
        if (phoneNum[0].firstChild) {
            phone = phoneNum[0].firstChild.nodeValue;
            $("providerWorkPhoneID").value = phone;
        }
    }
    
    
    
}
function parseSampletypes(sampletypes, SampleTypes) {
        
        var index = 0;
        for( var i = 0; i < sampletypes.length; i++ ) {

            var sampleTypeName = sampletypes.item(i).getElementsByTagName("name")[0].firstChild.nodeValue;
            var sampleTypeId   = sampletypes.item(i).getElementsByTagName("id")[0].firstChild.nodeValue;
            var panels         = sampletypes.item(i).getElementsByTagName("panels")[0];
            var tests          = sampletypes.item(i).getElementsByTagName("tests")[0];
            var sampleTypeInList = getSampleTypeMapEntry(sampleTypeId);
            if (!sampleTypeInList) {
                index++;
                SampleTypes[index-1] = new SampleType(sampleTypeId, sampleTypeName);
                sampleTypeMap[sampleTypeId] = SampleTypes[index-1];
                SampleTypes[index-1].rowid = index;
                sampleTypeInList = SampleTypes[index-1];
                                
                //var addTable = $("samplesAddedTable");
                //var sampleDescription = sampleTypeName;
                //var sampleTypeValue = sampleTypeId;
                //var currentTime = getCurrentTime();
                
                //addTypeToTable(addTable, sampleDescription, sampleTypeValue, currentTime,  '<%=entryDate%>' );
            
            }
            var panelnodes = getNodeNamesByTagName(panels, "panel");
            var testnodes  = getNodeNamesByTagName(tests, "test");
            
            addPanelsToSampleType(sampleTypeInList, panelnodes);
            addTestsToSampleType(sampleTypeInList, testnodes);
           
        }

}

function addPanelsToSampleType(sampleType, panelNodes) {
    for (var i=0; i<panelNodes.length; i++) {
       sampleType.panels[sampleType.panels.length] = panelNodes[i];
    }
}

function addTestsToSampleType(sampleType, testNodes) {
    for (var i=0; i<testNodes.length; i++) {
       sampleType.tests[sampleType.tests.length] = new Test(testNodes[i].id, testNodes[i].name);
    }
}


function parseCrossPanels(crosspanels, crossSampleTypeMap, crossSampleTypeOrderMap) {
        for(i = 0; i < crosspanels.length; i++ ) {
            var crossPanelName = crosspanels.item(i).getElementsByTagName("name")[0].firstChild.nodeValue;
            var crossPanelId   = crosspanels.item(i).getElementsByTagName("id")[0].firstChild.nodeValue;
            var crossSampleTypes         = crosspanels.item(i).getElementsByTagName("crosssampletypes")[0];
            
            CrossPanels[i] = new CrossPanel(crossPanelId, crossPanelName);
            CrossPanels[i].sampleTypes = getNodeNamesByTagName(crossSampleTypes, "crosssampletype");
            CrossPanels[i].typeMap = new Array(CrossPanels[i].sampleTypes.length);
            
            for (j = 0; j < CrossPanels[i].sampleTypes.length; j = j + 1) {
                CrossPanels[i].typeMap[CrossPanels[i].sampleTypes[j].name] = "t";
                var sampleType = getCrossSampleTypeMapEntry(CrossPanels[i].sampleTypes[j].id);
                
                if (sampleType === undefined) {
                    crossSampleTypeMap[CrossPanels[i].sampleTypes[j].id] = CrossPanels[i].sampleTypes[j];
                    sampleTypeOrder = sampleTypeOrder + 1;
                    crossSampleTypeOrderMap[sampleTypeOrder] = CrossPanels[i].sampleTypes[j].id;
                }
            }
        }
} 

function parseCrossTests(crosstests, crossSampleTypeMap, crossSampleTypeOrderMap) {
    for (x = 0; x < crosstests.length; x = x + 1) {
        var crossTestName = crosstests.item(x).getElementsByTagName("name")[0].firstChild.nodeValue;
        var crossTestId   = crosstests.item(x).getElementsByTagName("id")[0].firstChild.nodeValue;
        var crossSampleTypes  = crosstests.item(x).getElementsByTagName("crosssampletypes")[0];
        
        CrossTests[x] = new CrossTest(crossTestName);
        CrossTests[x].sampleTypes = getNodeNamesByTagName(crossSampleTypes, "crosssampletype");
        CrossTests[x].typeMap = new Array(CrossTests[x].sampleTypes.length);    
        var sTypes = new Array();                                                                                                                                                                                                                       
        for (y = 0; y < CrossTests[x].sampleTypes.length; y = y + 1) {
        
            //alert(crossTestName + " " + CrossTests[x].sampleTypes[y].id + " testid=" + CrossTests[x].sampleTypes[y].testId);
            sTypes[y] = CrossTests[x].sampleTypes[y];
            CrossTests[x].typeMap[CrossTests[x].sampleTypes[y].name] = "t";
            var sType = getCrossSampleTypeMapEntry(CrossTests[x].sampleTypes[y].id);
            
            if (sType === undefined) {
                crossSampleTypeMap[CrossTests[x].sampleTypes[y].id] = CrossTests[x].sampleTypes[y];
                sampleTypeOrder++;
                crossSampleTypeOrderMap[sampleTypeOrder] = CrossTests[x].sampleTypes[y].id;               
            }
        }
        crossTestSampleTypeTestIdMap[crossTestName] = sTypes;
    }

}

function validatePhoneNumber( phoneElement){
    validatePhoneNumberOnServer( phoneElement, processPhoneSuccess);
}

function  processPhoneSuccess(xhr){
    //alert(xhr.responseText);

    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
    var message = xhr.responseXML.getElementsByTagName("message").item(0);
    var success = false;

    if (message.firstChild.nodeValue == "valid"){
        success = true;
    }
    var labElement = formField.firstChild.nodeValue;
    selectFieldErrorDisplay( success, $(labElement));
    setSampleFieldValidity( success, labElement);

    if( !success ){
        alert( message.firstChild.nodeValue );
    }

    setSave();
}
</script>

<bean:define id="orderTypeList"  name='<%=formName%>' property="orderTypes" type="java.util.Collection"/>
<html:hidden property="currentDate" name="<%=formName%>" styleId="currentDate"/>
<html:hidden property="domain" name="<%=formName%>" value="<%=genericDomain%>" styleId="domain"/>
<html:hidden property="removedSampleItem" value="" styleId="removedSampleItem"/>
<html:hidden property="newRequesterName" name='<%=formName %>' styleId="newRequesterName" />


<% if( FormFields.getInstance().useField(Field.SampleEntryLabOrderTypes)) {%>
    <logic:iterate indexId="index" id="orderTypes"  type="IdValuePair" name='<%=formName%>' property="orderTypes">
        <input id='<%="orderType_" + index %>' 
               type="radio" 
               name="orderType" 
               onclick='orderTypeSelected(this);'
               value='<%=orderTypes.getId() %>' />
        <label for='<%="orderType_" + index %>' ><%=orderTypes.getValue() %></label>
    </logic:iterate>
    <hr/>
<% } %>

<% if( acceptExternalOrders){ %>
<%= StringUtil.getContextualMessageForKey("referring.order.number") %>: <html:text name='<%=formName %>' 
                                   styleId="externalOrderNumber"
                                   property="externalOrderNumber"
                                   onchange="checkOrderReferral(this.value);makeDirty();"/> 
                                   
                                   <html:button property="searchExternalButton" onclick="checkOrderReferral($(externalOrderNumber).value);makeDirty();" >
                                   <bean:message key="label.button.search" />
                                   </html:button> <%= StringUtil.getContextualMessageForKey("referring.order.not.found") %>
<hr style="width: 100%; height: 5px" />                                   

<% } %>
            
<div id=sampleEntryPage <%= (orderTypeList == null || orderTypeList.size() == 0)? "" : "style='display:none'"  %>>
<html:button property="showHide" value='<%= acceptExternalOrders ? "+" : "-" %>' onclick="showHideSection(this, 'orderDisplay');" styleId="orderSectionId" />

<%= StringUtil.getContextualMessageForKey("sample.entry.order.label") %>
<span class="requiredlabel">*</span>


<div id=orderDisplay <%= acceptExternalOrders ? "style='display:none'" : ""  %>>
<table  style="width:90%" >

    <tr>
        <td>
            <table >
                    <tr>
                    <td width="35%">
                        <%=StringUtil.getContextualMessageForKey("quick.entry.accession.number")%>
                        :
                        <span class="requiredlabel">*</span>
                    </td>
                    <td width="65%">
                        <app:text name="<%=formName%>" property="labNo"
                            maxlength='<%= Integer.toString(accessionNumberValidator.getMaxAccessionLength())%>'
                            onchange="checkAccessionNumber(this);makeDirty();"
                            styleClass="text"
                            styleId="labNo" />
                
                        <bean:message key="sample.entry.scanner.instructions"/>
                        <html:button property="generate"
                                     styleClass="textButton"
                                     onclick="getNextAccessionNumber(); makeDirty();" >
                        <bean:message key="sample.entry.scanner.generate"/>
                        </html:button>
                    </td>
                    </tr>
                    <% if( FormFields.getInstance().useField(Field.SampleEntryUseRequestDate)){ %>
                    <tr>
                        <td><bean:message key="sample.entry.requestDate" />:
                        <span class="requiredlabel">*</span><font size="1"><bean:message key="sample.date.format" /></font></td>
                        <td><html:text name='<%=formName %>' 
                                       property="requestDate" 
                                       styleId="requestDate"
                                       onchange="makeDirty();checkValidEntryDate(this, 'past')" 
                                       onkeyup="addDateSlashes(this, event);" 
                                       maxlength="10"/>
                    </tr>
                    <% } %>
                    <tr>
                    <td >
                        <%= StringUtil.getContextualMessageForKey("quick.entry.received.date") %>
                        :
                        <span class="requiredlabel">*</span>
                        <font size="1"><bean:message key="sample.date.format" />
                        </font>
                    </td>
                    <td colspan="2">
                        <app:text name="<%=formName%>" 
                            property="receivedDateForDisplay"
                            onchange="checkValidEntryDate(this, 'past');makeDirty();"
                            onkeyup="addDateSlashes(this, event);" 
                            maxlength="10"
                            styleClass="text"
                            styleId="receivedDateForDisplay" />
                    
                    <% if( FormFields.getInstance().useField(Field.SampleEntryUseReceptionHour)){ %>
                        <bean:message key="sample.receptionTime" />:
                            <html:text name="<%=formName %>" 
                            onkeyup="filterTimeKeys(this, event);" 
                            property="recievedTime" 
                            styleId="recievedTime"
                            maxlength="5"
                            onblur="makeDirty(); checkValidTime(this, true);"/>
                    
                    <% } %>
                        </td>
                </tr>     
                
                <% if( FormFields.getInstance().useField(Field.SampleEntryNextVisitDate)){ %>
                <tr>
                    <td><bean:message key="sample.entry.nextVisit.date" />&nbsp;<font size="1"><bean:message key="sample.date.format" /></font>:</td>
                    <td>
                        <html:text name='<%= formName %>'
                                   property="nextVisitDate" 
                                   onchange="makeDirty();checkValidEntryDate(this, 'future', true)"
                                   onkeyup="addDateSlashes(this, event);" 
                                   styleId="nextVisitDate"
                                   maxlength="10"/>
                    </td>
                </tr>
                <% } %>
                
                <tr>
                <td>&nbsp;</td>
                </tr>
                <% if( FormFields.getInstance().useField(Field.SampleEntryRequestingSiteSampleId)) {%>
                <tr>
                    <td >
                        <%= StringUtil.getContextualMessageForKey("sample.clientReference") %>:
                    </td>
                    <td >
                        <app:text name="<%=formName%>"
                                  property="requesterSampleID"
                                  size="50"
                                  maxlength="50"
                                  onchange="makeDirty();"/>
                    </td>
                    <td width="10%" >&nbsp;</td>
                    <td width="45%" >&nbsp;</td>
                </tr>
                <% } %>
                <% if( FormFields.getInstance().useField(Field.SAMPLE_ENTRY_USE_REFFERING_PATIENT_NUMBER)) {%>
                <tr>
                    <td >
                        <%= StringUtil.getContextualMessageForKey("sample.referring.patientNumber") %>:
                    </td>
                    <td >
                        <app:text name="<%=formName%>"
                                  property="referringPatientNumber"
                                  styleId="referringPatientNumber"
                                  size="50"
                                  maxlength="50"
                                  onchange="makeDirty();"/>
                    </td>
                    <td width="10%" >&nbsp;</td>
                    <td width="45%" >&nbsp;</td>
                </tr>
                <% } %>
                <% if( useReferralSiteList){ %>
                <tr>
                    <td >
                        <%= StringUtil.getContextualMessageForKey("sample.entry.project.siteName") %>:
                        <% if( FormFields.getInstance().useField(Field.SampleEntryReferralSiteNameRequired)) {%>
                        <span class="requiredlabel">*</span>
                        <% } %>
                    </td>
                    <td colspan="3">
                        <html:select styleId="requesterId"
                                     name="<%=formName%>"
                                     property="referringSiteId"
                                     onchange="makeDirty();siteListChanged(this);setSave();" 
                                     onkeyup="capitalizeValue( this.value );"
                                     >
                            <option value=""></option>
                            <html:optionsCollection name="<%=formName%>" property="referringSiteList" label="value" value="id" />
                        </html:select>
                    </td>
                </tr>
                <% } %>
                <% if( useReferralSiteCode ){ %>
                <tr>
                    <td >
                        <%= StringUtil.getContextualMessageForKey("sample.entry.referringSite.code") %>:
                    </td>
                    <td>    
                        <html:text styleId="requesterCodeId"
                                     name="<%=formName%>"
                                     property="referringSiteCode"
                                     onchange="makeDirty();setSave();">
                        </html:text>
                    </td>
                </tr>
                <% } %>
                <tr>
                <td>&nbsp;</td>
                </tr>
                <%  if (useProviderInfo) { %>
                <tr>
                    <td >
                        <%= StringUtil.getContextualMessageForKey("sample.entry.provider.name") %>:
                        <% if(requesterLastNameRequired ){ %>
                        <span class="requiredlabel">*</span>
                        <% } %>
                    </td>
                    <td >
                        <html:text name="<%=formName%>"
                                  property="providerLastName"
                                  styleId="providerLastNameID"
                                  onchange="makeDirty();setSave()"
                                  size="30" />
                        <bean:message key="humansampleone.provider.firstName.short"/>:
                        <html:text name="<%=formName%>"
                                  property="providerFirstName"
                                  styleId="providerFirstNameID"
                                  onchange="makeDirty();"
                                  size="30" />
                    </td>
                </tr>
                <tr>
                    <td>
                        <%= StringUtil.getContextualMessageForKey("humansampleone.provider.workPhone") + ": " + PhoneNumberService.getPhoneFormat()%>
                    </td>
                    <td>
                        <app:text name="<%=formName%>"
                                  property="providerWorkPhone"
                                  styleId="providerWorkPhoneID"
                                  size="30"
                                  maxlength="30"
                                  styleClass="text"
                                  onchange="makeDirty(); validatePhoneNumber(this)" />
                    </td>
                </tr>
                <% } %>
                <% if( FormFields.getInstance().useField(Field.SampleEntryProviderFax)){ %>
                    <tr>
                    <td>
                        <%= StringUtil.getContextualMessageForKey("sample.entry.project.faxNumber")%>:
                    </td>
                    <td>
                        <app:text name="<%=formName%>"
                                  property="providerFax"
                                  styleId="providerFaxID"
                                  size="20"
                                  styleClass="text"
                                  onchange="makeDirty()" />
                    </td>
                </tr>
                <% } %>
                <% if( FormFields.getInstance().useField(Field.SampleEntryProviderEmail)){ %>
                    <tr>
                    <td>
                        <%= StringUtil.getContextualMessageForKey("sample.entry.project.email")%>:
                    </td>
                    <td>
                        <app:text name="<%=formName%>"
                                  property="providerEmail"
                                  styleId="providerEmailID"
                                  size="20"
                                  styleClass="text"
                                  onchange="makeDirty()" />
                    </td>
                </tr>
                <% } %>
                <% if( FormFields.getInstance().useField(Field.SampleEntryHealthFacilityAddress)) {%>
                <tr>
                    <td><bean:message key="sample.entry.facility.address"/>:</td>
                </tr>
                <tr>
                    <td>&nbsp;&nbsp;<bean:message key="sample.entry.facility.street"/>
                    <td>    
                    <html:text name='<%=formName %>'
                               property="facilityAddressStreet" 
                               styleClass="text"
                               onchange="makeDirty()"/>
                    </td>            
                </tr>
                <tr>
                    <td>&nbsp;&nbsp;<bean:message key="sample.entry.facility.commune"/>:<td>    
                    <html:text name='<%=formName %>'
                               property="facilityAddressCommune" 
                               styleClass="text"
                               onchange="makeDirty()"/>
                    </td>  
                </tr>
                <tr>
                    <td><bean:message key="sample.entry.facility.phone"/>:<td>  
                    <html:text name='<%=formName %>'
                               property="facilityPhone" 
                               styleClass="text"
                               maxlength="17"
                               onchange="makeDirty(); validatePhoneNumber( this );"/>
                    </td>  
                </tr>
                <tr>
                    <td><bean:message key="sample.entry.facility.fax"/>:<td>    
                    <html:text name='<%=formName %>'
                               property="facilityFax" 
                               styleClass="text"
                               onchange="makeDirty()"/>
                    </td>  
                </tr>
                <% } %>
                <tr><td>&nbsp;</td></tr>
                <% if( trackPayment){ %>
                <tr>
                    <td><bean:message key="sample.entry.patientPayment"/>: </td>
                    <td>
                        
                    <html:select name="<%=formName %>" property="paymentOptionSelection" >
                                <option value='' ></option>
                    <logic:iterate id="optionValue" name='<%=formName%>' property="paymentOptions" type="IdValuePair" >
                                <option value='<%=optionValue.getId()%>' >
                                    <bean:write name="optionValue" property="value"/>
                                </option>
                    </logic:iterate>
                    </html:select>
                    </td>
                </tr>
                <% } %>
                <% if( FormFields.getInstance().useField(Field.SampleEntryLabOrderTypes)) {%>
                <tr >
                    <td><bean:message key="sample.entry.sample.period"/>:</td>
                    <td>
                        <html:select name="<%=formName %>" 
                                     property="followupPeriodOrderType" 
                                     onchange="makeDirty(); labPeriodChanged( this, '8' )" 
                                     styleId="followupLabOrderPeriodId" 
                                     style="display:none">
                        <option value='' ></option>
                        <logic:iterate id="optionValue" name='<%=formName%>' property="followupPeriodOrderTypes" type="IdValuePair" >
                        <option value='<%=optionValue.getId()%>' >
                            <bean:write name="optionValue" property="value"/>
                        </option>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
                        </logic:iterate>
                        </html:select>
                        <html:select name="<%=formName %>" 
                                     property="initialPeriodOrderType" 
                                     onchange="makeDirty(); labPeriodChanged( this, '2' )" 
                                     styleId="initialLabOrderPeriodId" 
                                     style="display:none">
                        <option value='' ></option>
                        <logic:iterate id="optionValue" name='<%=formName%>' property="initialPeriodOrderTypes" type="IdValuePair" >
                        <option value='<%=optionValue.getId()%>' >
                            <bean:write name="optionValue" property="value"/>
                        </option>
                        </logic:iterate>
                        </html:select>
                        &nbsp;
                        <html:text name='<%= formName %>' 
                                   property="otherPeriodOrder" 
                                   styleId="labOrderPeriodOtherId" 
                                   style="display:none" />
                    </td>                           
                </tr>
                <% } %>
                <tr>
                    <td>
                        &nbsp;
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</div>      
<hr style="width: 100%; height: 5px" />
<html:button property="showHide" value="+" onclick="showHideSection(this, 'samplesDisplay');" styleId="samplesSectionId" />
<%= StringUtil.getContextualMessageForKey("sample.entry.sampleList.label") %>
<span class="requiredlabel">*</span>

<div id="samplesDisplay" class="colorFill" style="display:none;" >
    <tiles:insert attribute="addSample"/>
</div>

<br />
<hr style="width: 100%; height: 5px" />
<html:hidden name="<%=formName%>" property="patientPK" styleId="patientPK"/>

<table style="width:100%">
    <tr>
        <td width="15%" align="left">
            <html:button property="showPatient" onclick="showHideSection(this, 'patientInfo');" >+</html:button>
            <bean:message key="sample.entry.patient" />:
            <% if ( patientRequired ) { %><span class="requiredlabel">*</span><% } %>
        </td>
        <td width="15%" id="firstName"><b>&nbsp;</b></td>
        <td width="15%">
            <% if(useMothersName){ %><bean:message key="patient.mother.name"/>:<% } %>
        </td>
        <td width="15%" id="mother"><b>&nbsp;</b></td>
        <td width="10%">
            <% if( useSTNumber){ %><bean:message key="patient.ST.number"/>:<% } %>
        </td>
        <td width="15%" id="st"><b>&nbsp;</b></td>
        <td width="5%">&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td id="lastName"><b>&nbsp;</b></td>
        <td>
            <bean:message key="patient.birthDate"/>:
        </td>
        <td id="dob"><b>&nbsp;</b></td>
        <td>
            <%=StringUtil.getContextualMessageForKey("patient.NationalID") %>:
        </td>
        <td id="national"><b>&nbsp;</b></td>
        <td>
            <bean:message key="patient.gender"/>:
        </td>
        <td id="gender"><b>&nbsp;</b></td>
    </tr>
</table>

<div id="patientInfo"  style="display:none;" >
    <tiles:insert attribute="patientInfo" />
    <tiles:insert attribute="patientClinicalInfo" />
</div>
</div>
<script type="text/javascript" >

//all methods here either overwrite methods in tiles or all called after they are loaded

function /*void*/ makeDirty(){
    dirty=true;
    if( typeof(showSuccessMessage) != 'undefinded' ){
        showSuccessMessage(false); //refers to last save
    }
    // Adds warning when leaving page if content has been entered into makeDirty form fields
    function formWarning(){ 
    return "<bean:message key="banner.menu.dataLossWarning"/>";
    }
    window.onbeforeunload = formWarning;
}

function  /*void*/ savePage()
{
    loadSamples(); //in addSample tile

  window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
    var form = window.document.forms[0];
    form.action = "SamplePatientEntrySave.do";
    form.submit();
}


function /*void*/ setSave()
{
    var validToSave =  patientFormValid() && sampleEntryTopValid();
    $("saveButtonId").disabled = !validToSave;
}

//called from patientSearch.jsp
function /*void*/ selectedPatientChangedForSample(firstName, lastName, gender, DOB, stNumber, subjectNumb, nationalID, mother, pk ){
    patientInfoChangedForSample( firstName, lastName, gender, DOB, stNumber, subjectNumb, nationalID, mother, pk );
    $("patientPK").value = pk;

    setSave();
}

//called from patientManagment.jsp
function /*void*/ patientInfoChangedForSample( firstName, lastName, gender, DOB, stNumber, subjectNum, nationalID, mother, pk ){
    setPatientSummary( "firstName", firstName );
    setPatientSummary( "lastName", lastName );
    setPatientSummary( "gender", gender );
    setPatientSummary( "dob", DOB );
    if( useSTNumber){setPatientSummary( "st", stNumber );}
    setPatientSummary( "national", nationalID );
    if( useMothersName){setPatientSummary( "mother", mother );}
    $("patientPK").value = pk;

    makeDirty();
    setSave();
}

function /*voiid*/ setPatientSummary( name, value ){
    $(name).firstChild.firstChild.nodeValue = value;
}

//overwrites function from patient search
function /*void*/ doSelectPatient(){
/*  $("firstName").firstChild.firstChild.nodeValue = currentPatient["first"];
    $("mother").firstChild.firstChild.nodeValue = currentPatient["mother"];
    $("st").firstChild.firstChild.nodeValue = currentPatient["st"];
    $("lastName").firstChild.firstChild.nodeValue = currentPatient["last"];
    $("dob").firstChild.firstChild.nodeValue = currentPatient["DOB"];
    $("national").firstChild.firstChild.nodeValue = currentPatient["national"];
    $("gender").firstChild.firstChild.nodeValue = currentPatient["gender"];
    $("patientPK").value = currentPatient["pk"];

    setSave();

*/
}
 
var patientRegistered = false;
var sampleRegistered = false;

/* is registered in patientManagement.jsp */
function /*void*/ registerPatientChangedForSampleEntry(){
    if( !patientRegistered ){
        addPatientInfoChangedListener( patientInfoChangedForSample );
        patientRegistered = true;
    }
}

/* is registered in sampleAdd.jsp */
function /*void*/ registerSampleChangedForSampleEntry(){
    if( !sampleRegistered ){
        addSampleChangedListener( makeDirty );
        sampleRegistered = true;
    }
}

registerPatientChangedForSampleEntry();
registerSampleChangedForSampleEntry();

// Moving autocomplete to end - needs to be at bottom for IE to trigger properly
$jq(document).ready(function(){
        var dropdown = $jq( "select#requesterId" );
        autoCompleteWidth = dropdown.width() + 66 + 'px';
        clearNonMatching = false;
        capitialize = true;
        // Actually executes autocomplete
        dropdown.combobox();
       // invalidLabID = '<bean:message key="error.site.invalid"/>'; // Alert if value is typed that's not on list. FIX - add badmessage icon
        maxRepMsg = '<bean:message key="sample.entry.project.siteMaxMsg"/>'; 
        
        resultCallBack = function( textValue) {
                siteListChanged(textValue);
                makeDirty();
                setSave();
                };
});

</script>
