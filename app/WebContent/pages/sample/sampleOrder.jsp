<%@page import="us.mn.state.health.lims.common.action.IActionConstants" %>
<%@page import="us.mn.state.health.lims.common.formfields.FormFields" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="us.mn.state.health.lims.common.formfields.FormFields.Field,
                 us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
                 us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
                 us.mn.state.health.lims.common.services.PhoneNumberService,
                 us.mn.state.health.lims.common.util.ConfigurationProperties,
                 us.mn.state.health.lims.common.util.ConfigurationProperties.Property,
                 us.mn.state.health.lims.common.util.StringUtil,
                 us.mn.state.health.lims.common.util.IdValuePair,
                 us.mn.state.health.lims.common.util.Versioning" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>'/>
<bean:define id="entryDate" name="<%=formName%>" property="currentDate"/>

<%!
    String path = "";
    String basePath = "";
    boolean useCollectionDate = true;
    boolean useInitialSampleCondition = false;
    boolean useCollector = false;
    boolean autofillCollectionDate = true;
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
    path = request.getContextPath();
    basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    useCollectionDate = FormFields.getInstance().useField( Field.CollectionDate );
    useInitialSampleCondition = FormFields.getInstance().useField( Field.InitialSampleCondition );
    useCollector = FormFields.getInstance().useField( Field.SampleEntrySampleCollector );
    autofillCollectionDate = ConfigurationProperties.getInstance().isPropertyValueEqual( Property.AUTOFILL_COLLECTION_DATE, "true" );
    useReferralSiteList = FormFields.getInstance().useField( FormFields.Field.RequesterSiteList );
    useReferralSiteCode = FormFields.getInstance().useField( FormFields.Field.SampleEntryReferralSiteCode );
    useProviderInfo = FormFields.getInstance().useField( FormFields.Field.ProviderInfo );
    patientRequired = FormFields.getInstance().useField( FormFields.Field.PatientRequired );
    trackPayment = ConfigurationProperties.getInstance().isPropertyValueEqual( Property.TRACK_PATIENT_PAYMENT, "true" );
    accessionNumberValidator = new AccessionNumberValidatorFactory().getValidator();
    requesterLastNameRequired = FormFields.getInstance().useField( Field.SampleEntryRequesterLastNameRequired );
    acceptExternalOrders = ConfigurationProperties.getInstance().isPropertyValueEqual( Property.ACCEPT_EXTERNAL_ORDERS, "true" );

%>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.jsp"></script>
<script type="text/javascript" src="scripts/jquery.asmselect.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="scripts/laborder.js?ver=<%= Versioning.getBuildNumber() %>"></script>



<link rel="stylesheet" type="text/css" href="css/jquery.asmselect.css?ver=<%= Versioning.getBuildNumber() %>"/>


<script type="text/javascript">
    var useReferralSiteList = <%= useReferralSiteList%>;
    var useReferralSiteCode = <%= useReferralSiteCode %>;

    function checkAccessionNumber(accessionNumber) {
        //check if empty
        if (!fieldIsEmptyById("labNo")) {
            validateAccessionNumberOnServer(false, accessionNumber.id, accessionNumber.value, processAccessionSuccess, null);
        }
        else {
            setSampleFieldInvalid(accessionNumber.name);
            setValidIndicaterOnField(false, accessionNumber.name);
        }

        if( window.setSave()){setSave();}
    }

    function processAccessionSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var message = xhr.responseXML.getElementsByTagName("message").item(0);
        var success = false;

        if (message.firstChild.nodeValue == "valid") {
            success = true;
        }
        var labElement = formField.firstChild.nodeValue;
        selectFieldErrorDisplay(success, $(labElement));
        setSampleFieldValidity(success, labElement);

        if (!success) {
            alert(message.firstChild.nodeValue);
        }

        if( window.setSave()){setSave();}
    }

    function getNextAccessionNumber() {
        generateNextScanNumber(processScanSuccess);
    }

    function processScanSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var returnedData = formField.firstChild.nodeValue;

        var message = xhr.responseXML.getElementsByTagName("message").item(0);

        var success = message.firstChild.nodeValue == "valid";

        if (success) {
            $("labNo").value = returnedData;

        } else {
            alert("<%= StringUtil.getMessageForKey("error.accession.no.next") %>");
            $("labNo").value = "";
        }

        var targetName = $("labNo").name;
        selectFieldErrorDisplay(success, $(targetName));
        setValidIndicaterOnField(success, targetName);

        if( window.setSave()){setSave();}
    }


    function siteListChanged(textValue) {
        var siteList = $("requesterId");

        //if the index is 0 it is a new entry, if it is not then the textValue may include the index value
        if (siteList.selectedIndex == 0 || siteList.options[siteList.selectedIndex].label != textValue) {
            $("newRequesterName").value = textValue;
        } else if (useReferralSiteCode) {
            getCodeForOrganization(siteList.options[siteList.selectedIndex].value, processCodeSuccess);
        }
    }

    function processCodeSuccess(xhr) {
        //alert(xhr.responseText);
        var code = xhr.responseXML.getElementsByTagName("code").item(0);
        var success = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue == "valid";

        if (success) {
            $jq("#requesterCodeId").val(code.getAttribute("value"));
        }
    }

    function labPeriodChanged(labOrderPeriodElement) {
        if (labOrderPeriodElement.length - 1 == labOrderPeriodElement.selectedIndex) {
            $("labOrderPeriodOtherId").show();
        } else {
            $("labOrderPeriodOtherId").hide();
            $("labOrderPeriodOtherId").value = "";
        }

    }

    function setOrderModified(){
        $jq("#orderModified").val("true");
        orderChanged = true;
        if( window.makeDirty ){ makeDirty(); }
        if( window.setSave){
            setSave()
        }else if( window.setSaveButton){
            setSaveButton();
        }
    }

</script>


<!-- This define may not be needed, look at usages (not in any other jsp or js page may be radio buttons for ci LNSP-->
<bean:define id="orderTypeList" name='<%=formName%>' property="sampleOrderItems.orderTypes"  type="java.util.Collection"/>
<bean:define id="sampleOrderItem" name='<%=formName%>' property="sampleOrderItems" type="us.mn.state.health.lims.sample.bean.SampleOrderItem" />
<html:hidden property="currentDate" name="<%=formName%>" styleId="currentDate"/>
<html:hidden property="sampleOrderItems.newRequesterName" name='<%=formName%>' styleId="newRequesterName"/>
<html:hidden property="sampleOrderItems.modified" name='<%=formName%>' styleId="orderModified"  />



<div id=orderDisplay <%= acceptExternalOrders && sampleOrderItem.getLabNo() == null ? "style='display:none'" : ""  %> >
<table style="width:100%">

<tr>
<td>
<table>
<logic:empty name="<%=formName%>" property="sampleOrderItems.labNo">
    <tr>
        <td style="width:35%">
            <%=StringUtil.getContextualMessageForKey( "quick.entry.accession.number" )%>
            :
            <span class="requiredlabel">*</span>
        </td>
        <td style="width:65%">
            <app:text name="<%=formName%>" property="sampleOrderItems.labNo"
                      maxlength='<%= Integer.toString(accessionNumberValidator.getMaxAccessionLength())%>'
                      onchange="checkAccessionNumber(this);"
                      styleClass="text"
                      styleId="labNo"/>

            <bean:message key="sample.entry.scanner.instructions"/>
            <input type="button" value='<%=StringUtil.getMessageForKey("sample.entry.scanner.generate")%>'
                   onclick="setOrderModified();getNextAccessionNumber(); " class="textButton">
        </td>
    </tr>
</logic:empty>
<logic:notEmpty name="<%=formName%>" property="sampleOrderItems.labNo" >
    <tr><td style="width:35%"></td><td style="width:65%"></td></tr>
</logic:notEmpty>
<% if( FormFields.getInstance().useField( Field.SampleEntryUseRequestDate ) ){ %>
<tr>
    <td><bean:message key="sample.entry.requestDate"/>:
        <span class="requiredlabel">*</span><span
                style="font-size: xx-small; "><bean:message key="sample.date.format"/></span></td>
    <td><html:text name='<%=formName %>'
                   property="sampleOrderItems.requestDate"
                   styleId="requestDate"
                   onchange="setOrderModified();checkValidEntryDate(this, 'past')"
                   onkeyup="addDateSlashes(this, event);"
                   maxlength="10"/>
</tr>
<% } %>
<tr>
    <td>
        <%= StringUtil.getContextualMessageForKey( "quick.entry.received.date" ) %>
        :
        <span class="requiredlabel">*</span>
        <span style="font-size: xx-small; "><bean:message key="sample.date.format"/>
        </span>
    </td>
    <td colspan="2">
        <app:text name="<%=formName%>"
                  property="sampleOrderItems.receivedDateForDisplay"
                  onchange="setOrderModified();checkValidEntryDate(this, 'past');"
                  onkeyup="addDateSlashes(this, event);"
                  maxlength="10"
                  styleClass="text"
                  styleId="receivedDateForDisplay"/>

        <% if( FormFields.getInstance().useField( Field.SampleEntryUseReceptionHour ) ){ %>
        <bean:message key="sample.receptionTime"/>:
        <html:text name="<%=formName %>"
                   onkeyup="filterTimeKeys(this, event);"
                   property="sampleOrderItems.receivedTime"
                   styleId="receivedTime"
                   maxlength="5"
                   onblur="setOrderModified(); checkValidTime(this, true);"/>

        <% } %>
    </td>
</tr>

<% if( FormFields.getInstance().useField( Field.SampleEntryNextVisitDate ) ){ %>
<tr>
    <td><bean:message key="sample.entry.nextVisit.date"/>&nbsp;<span style="font-size: xx-small; "><bean:message
            key="sample.date.format"/></span>:
    </td>
    <td>
        <html:text name='<%= formName %>'
                   property="sampleOrderItems.nextVisitDate"
                   onchange="setOrderModified();checkValidEntryDate(this, 'future', true)"
                   onkeyup="addDateSlashes(this, event);"
                   styleId="nextVisitDate"
                   maxlength="10"/>
    </td>
</tr>
<% } %>

<tr>
    <td>&nbsp;</td>
</tr>
<% if( FormFields.getInstance().useField( Field.SampleEntryRequestingSiteSampleId ) ){%>
<tr>
    <td>
        <%= StringUtil.getContextualMessageForKey( "sample.clientReference" ) %>:
    </td>
    <td>
        <app:text name="<%=formName%>"
                  property="sampleOrderItems.requesterSampleID"
                  size="50"
                  maxlength="50"
                  onchange="setOrderModified();"/>
    </td>
    <td style="width:10%">&nbsp;</td>
    <td style="width:45%">&nbsp;</td>
</tr>
<% } %>
<% if( FormFields.getInstance().useField( Field.SAMPLE_ENTRY_USE_REFFERING_PATIENT_NUMBER ) ){%>
<tr>
    <td>
        <%= StringUtil.getContextualMessageForKey( "sample.referring.patientNumber" ) %>:
    </td>
    <td>
        <app:text name="<%=formName%>"
                  property="sampleOrderItems.referringPatientNumber"
                  styleId="referringPatientNumber"
                  size="50"
                  maxlength="50"
                  onchange="setOrderModified();"/>
    </td>
    <td style="width:10%">&nbsp;</td>
    <td style="width:45%">&nbsp;</td>
</tr>
<% } %>
<% if( useReferralSiteList ){ %>
<tr>
    <td>
        <%= StringUtil.getContextualMessageForKey( "sample.entry.project.siteName" ) %>:
        <% if( FormFields.getInstance().useField( Field.SampleEntryReferralSiteNameRequired ) ){%>
        <span class="requiredlabel">*</span>
        <% } %>
    </td>
    <td colspan="3">
        <html:select styleId="requesterId"
                     name="<%=formName%>"
                     property="sampleOrderItems.referringSiteId"
                     onchange="setOrderModified();siteListChanged(this);setSave();"
                     onkeyup="capitalizeValue( this.value );"
                >
            <option value=""></option>
            <html:optionsCollection name="<%=formName%>" property="sampleOrderItems.referringSiteList" label="value"
                                    value="id"/>
        </html:select>
    </td>
</tr>
<% } %>
<% if( useReferralSiteCode ){ %>
<tr>
    <td>
        <%= StringUtil.getContextualMessageForKey( "sample.entry.referringSite.code" ) %>:
    </td>
    <td>
        <html:text styleId="requesterCodeId"
                   name="<%=formName%>"
                   property="sampleOrderItems.referringSiteCode"
                   onchange="setOrderModified();setSave();">
        </html:text>
    </td>
</tr>
<% } %>
<tr>
    <td>&nbsp;</td>
</tr>
<% if( useProviderInfo ){ %>
<tr>
    <td>
        <%= StringUtil.getContextualMessageForKey( "sample.entry.provider.name" ) %>:
        <% if( requesterLastNameRequired ){ %>
        <span class="requiredlabel">*</span>
        <% } %>
    </td>
    <td>
        <html:text name="<%=formName%>"
                   property="sampleOrderItems.providerLastName"
                   styleId="providerLastNameID"
                   onchange="setOrderModified();setSave()"
                   size="30"/>
        <bean:message key="humansampleone.provider.firstName.short"/>:
        <html:text name="<%=formName%>"
                   property="sampleOrderItems.providerFirstName"
                   styleId="providerFirstNameID"
                   onchange="setOrderModified();"
                   size="30"/>
    </td>
</tr>
<tr>
    <td>
        <%= StringUtil.getContextualMessageForKey( "humansampleone.provider.workPhone" ) + ": " + PhoneNumberService.getPhoneFormat()%>
    </td>
    <td>
        <app:text name="<%=formName%>"
                  property="sampleOrderItems.providerWorkPhone"
                  styleId="providerWorkPhoneID"
                  size="30"
                  maxlength="30"
                  styleClass="text"
                  onchange="setOrderModified();validatePhoneNumber(this)"/>
    </td>
</tr>
<% } %>
<% if( FormFields.getInstance().useField( Field.SampleEntryProviderFax ) ){ %>
<tr>
    <td>
        <%= StringUtil.getContextualMessageForKey( "sample.entry.project.faxNumber" )%>:
    </td>
    <td>
        <app:text name="<%=formName%>"
                  property="sampleOrderItems.providerFax"
                  styleId="providerFaxID"
                  size="20"
                  styleClass="text"
                  onchange="setOrderModified();makeDirty()"/>
    </td>
</tr>
<% } %>
<% if( FormFields.getInstance().useField( Field.SampleEntryProviderEmail ) ){ %>
<tr>
    <td>
        <%= StringUtil.getContextualMessageForKey( "sample.entry.project.email" )%>:
    </td>
    <td>
        <app:text name="<%=formName%>"
                  property="sampleOrderItems.providerEmail"
                  styleId="providerEmailID"
                  size="20"
                  styleClass="text"
                  onchange="setOrderModified();makeDirty()"/>
    </td>
</tr>
<% } %>
<% if( FormFields.getInstance().useField( Field.SampleEntryHealthFacilityAddress ) ){%>
<tr>
    <td><bean:message key="sample.entry.facility.address"/>:</td>
</tr>
<tr>
    <td>&nbsp;&nbsp;<bean:message key="sample.entry.facility.street"/>
    <td>
        <html:text name='<%=formName %>'
                   property="sampleOrderItems.facilityAddressStreet"
                   styleClass="text"
                   onchange="setOrderModified();makeDirty()"/>
    </td>
</tr>
<tr>
    <td>&nbsp;&nbsp;<bean:message key="sample.entry.facility.commune"/>:
    <td>
        <html:text name='<%=formName %>'
                   property="sampleOrderItems.facilityAddressCommune"
                   styleClass="text"
                   onchange="setOrderModified();makeDirty()"/>
    </td>
</tr>
<tr>
    <td><bean:message key="sample.entry.facility.phone"/>:
    <td>
        <html:text name='<%=formName %>'
                   property="sampleOrderItems.facilityPhone"
                   styleClass="text"
                   maxlength="17"
                   onchange="setOrderModified(); validatePhoneNumber( this );"/>
    </td>
</tr>
<tr>
    <td><bean:message key="sample.entry.facility.fax"/>:
    <td>
        <html:text name='<%=formName %>'
                   property="sampleOrderItems.facilityFax"
                   styleClass="text"
                   onchange="setOrderModified();makeDirty()"/>
    </td>
</tr>
<% } %>
<tr>
    <td>&nbsp;</td>
</tr>
<% if( trackPayment ){ %>
<tr>
    <td><bean:message key="sample.entry.patientPayment"/>:</td>
    <td>
        <html:select name="<%=formName %>" property="sampleOrderItems.paymentOptionSelection" onchange="setOrderModified();" >
            <option value=''></option>
            <logic:iterate id="optionValue" name='<%=formName%>' property="sampleOrderItems.paymentOptions"
                           type="IdValuePair">
                <option value='<%=optionValue.getId()%>' <%=optionValue.getId().equals(sampleOrderItem.getPaymentOptionSelection() ) ? "selected='selected'" : ""%>>
                    <bean:write name="optionValue" property="value"/>
                </option>
            </logic:iterate>
        </html:select>
    </td>
</tr>
<% } %>
<% if( FormFields.getInstance().useField( Field.SampleEntryLabOrderTypes ) ){%>
<tr>
    <td><bean:message key="sample.entry.sample.period"/>:</td>
    <td>
        <html:select name="<%=formName %>"
                     property="sampleOrderItems.followupPeriodOrderType"
                     onchange="setOrderModified(); labPeriodChanged( this )"
                     styleId="followupLabOrderPeriodId"
                     style="display:none">
            <option value=''></option>
            <logic:iterate id="optionValue" name='<%=formName%>' property="sampleOrderItems.followupPeriodOrderTypes"
                           type="IdValuePair">
                <option value='<%=optionValue.getId()%>' <%=optionValue.getValue().equals(sampleOrderItem.getFollowupPeriodOrderType() ) ? "selected='selected'" : ""%> >
                    <bean:write name="optionValue" property="value"/>
                </option>
            </logic:iterate>
        </html:select>
        <html:select name="<%=formName %>"
                     property="sampleOrderItems.initialPeriodOrderType"
                     onchange="setOrderModified(); labPeriodChanged( this )"
                     styleId="initialLabOrderPeriodId"
                     style="display:none">
            <option value=''></option>
            <logic:iterate id="optionValue" name='<%=formName%>' property="sampleOrderItems.initialPeriodOrderTypes"
                           type="IdValuePair">
                <option value='<%=optionValue.getId()%>' <%=optionValue.getValue().equals(sampleOrderItem.getInitialPeriodOrderType() ) ? "selected='selected'" : ""%> >
                    <bean:write name="optionValue" property="value"/>
                </option>
            </logic:iterate>
        </html:select>
        &nbsp;
        <html:text name='<%= formName %>'
                   property="sampleOrderItems.otherPeriodOrder"
                   styleId="labOrderPeriodOtherId"
                   style="display:none"/>
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

<script type="text/javascript">
    function displayOrderTypeDependencies() {
        var orderSelection, selectOptions;

        if (<%="HIV_firstVisit".equals(sampleOrderItem.getOrderType())%>){
            orderSelection = $jq("#initialLabOrderPeriodId");
        }else if(<%="HIV_followupVisit".equals(sampleOrderItem.getOrderType())%>){
            orderSelection = $jq("#followupLabOrderPeriodId");
        }

        if( orderSelection){
            if( $jq("#labOrderPeriodOtherId").val() ){
                $jq("#labOrderPeriodOtherId").show();
                selectOptions = orderSelection.find("option");
                selectOptions[selectOptions.length - 1].selected = true;
            }
            orderSelection.show();
        }
}
    $jq(document).ready(function () {
        var dropdown = $jq("select#requesterId");
        autoCompleteWidth = dropdown.width() + 66 + 'px';
        clearNonMatching = false;
        capitialize = true;
        // Actually executes autocomplete
        dropdown.combobox();
        // invalidLabID = '<bean:message key="error.site.invalid"/>'; // Alert if value is typed that's not on list. FIX - add bad message icon
        maxRepMsg = '<bean:message key="sample.entry.project.siteMaxMsg"/>';

        resultCallBack = function (textValue) {
            siteListChanged(textValue);
            setOrderModified();
            if( window.setSave()){setSave();}
        };

        displayOrderTypeDependencies();
    });

</script>

