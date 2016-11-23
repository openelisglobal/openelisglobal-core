<%@ page language="java" contentType="text/html; charset=utf-8"
         import="us.mn.state.health.lims.common.action.IActionConstants,
            us.mn.state.health.lims.common.util.*,
            us.mn.state.health.lims.common.util.ConfigurationProperties.Property,us.mn.state.health.lims.login.dao.UserModuleDAO" %>

<%@ taglib uri="/tags/struts-bean"      prefix="bean" %>
<%@ taglib uri="/tags/struts-html"      prefix="html" %>
<%@ taglib uri="/tags/struts-logic"     prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"      prefix="app" %>
<%@ taglib uri="/tags/struts-tiles"     prefix="tiles" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>
<%@ taglib uri="/tags/globalOpenELIS"   prefix="global"%>

<bean:define id="formName"      value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="idSeparator"   value='<%=SystemConfiguration.getInstance().getDefaultIdSeparator()%>' />
<bean:define id="accessionFormat" value='<%=ConfigurationProperties.getInstance().getPropertyValue(Property.AccessionFormat)%>' />
<bean:define id="requestType" value='<%=(String)request.getSession().getAttribute("type")%>' />
<bean:define id="genericDomain" value='' />

<%@page import="us.mn.state.health.lims.login.daoimpl.UserModuleDAOImpl"%>
<%@page import="java.util.HashSet"%>
<%!
    String basePath = "";
    UserModuleDAO userModuleDAO = new UserModuleDAOImpl();
%>
<%
    String path = request.getContextPath();
    basePath = request.getScheme() + "://" + request.getServerName() + ":"  + request.getServerPort() + path + "/";
    HashSet accessMap = (HashSet)request.getSession().getAttribute(IActionConstants.PERMITTED_ACTIONS_MAP);
    boolean isAdmin = userModuleDAO.isUserAdmin(request);
    // no one should edit patient numbers at this time.  PAH 11/05/2010
    boolean canEditPatientSubjectNos =  isAdmin || accessMap.contains(IActionConstants.MODULE_ACCESS_PATIENT_SUBJECTNOS_EDIT);
    boolean canEditAccessionNo = isAdmin || accessMap.contains(IActionConstants.MODULE_ACCESS_SAMPLE_ACCESSIONNO_EDIT);
%>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/retroCIUtilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/entryByProjectUtils.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<script type="text/javascript" language="JavaScript1.2">

var dirty = false;
var type = '<%=requestType%>';
var requestType = '<%=requestType%>';
var pageType = "Sample";
birthDateUsageMessage = "<bean:message key='error.dob.complete.less.two.years'/>";
previousNotMatchedMessage = "<bean:message key='error.2ndEntry.previous.not.matched'/>";
noMatchFoundMessage = "<bean:message key='patient.message.patientNotFound'/>";
saveNotUnderInvestigationMessage = "<bean:message key='patient.project.conflicts.saveNotUnderInvestigation'/>";
testInvalid = "<bean:message key='error.2ndEntry.test.invalid'/>";
blankTextField = "<bean:message key='blank.text.field'/>";

var canEditPatientSubjectNos = <%= canEditPatientSubjectNos %>;
var canEditAccessionNo = <%= canEditAccessionNo %>;

function  /*void*/ setMyCancelAction(form, action, validate, parameters)
{
    //first turn off any further validation
    setAction(window.document.forms[0], 'Cancel', 'no', '');
}

function Studies() {
    this.validators = new Array();
    this.studyNames = ["InitialARV_Id", "FollowUp_ARV_Id", "RTN_Id", "EID_Id", "VL_Id",  "Indeterminate_Id", "Special_Request_Id"];

    this.validators["InitialARV_Id"] = new FieldValidator();
    this.validators["InitialARV_Id"].setRequiredFields( new Array("iarv.labNo", "iarv.receivedDateForDisplay", "iarv.interviewDate", "iarv.centerCode", "subjectOrSiteSubject", "iarv.gender", "iarv.dateOfBirth") );

    this.validators["FollowUpARV_Id"] = new FieldValidator();
    this.validators["FollowUpARV_Id"].setRequiredFields( new Array("farv.labNo", "farv.receivedDateForDisplay", "farv.interviewDate", "farv.centerCode", "subjectOrSiteSubject", "farv.gender", "farv.dateOfBirth") );

    this.validators["RTN_Id"] = new FieldValidator();
    this.validators["RTN_Id"].setRequiredFields( new Array("rtn.labNo", "rtn.receivedDateForDisplay", "rtn.interviewDate", "rtn.gender", "rtn.dateOfBirth") );

    // this.validators["EID_Id"] = new FieldValidator();
    this.validators["Indeterminate_Id"] = new FieldValidator();
    this.validators["Indeterminate_Id"].setRequiredFields( new Array("ind.labNo", "ind.receivedDateForDisplay", "ind.interviewDate", "subjectOrSiteSubject", "ind.centerName", "ind.dateOfBirth", "ind.gender") );

    this.validators["Special_Request_Id"] = new FieldValidator();
    this.validators["Special_Request_Id"].setRequiredFields( new Array("spe.labNo", "spe.receivedDateForDisplay", "spe.interviewDate", "subjectOrSiteSubject", "spe.gender") );


    this.getValidator = function /*FieldValidator*/ (divId) {
        return this.validators[divId];
    }

    this.projectChecker = new Array();

    this.initializeProjectChecker = function () {
        this.projectChecker["InitialARV_Id"] = iarv;
        this.projectChecker["FollowUpARV_Id"] = farv;
        this.projectChecker["RTN_Id"] = rtn;
        //this.projectChecker["EID_Id"] = eid;
        this.projectChecker["Indeterminate_Id"] = ind;
        this.projectChecker["Special_Request_Id"] = spe;
    }

    this.getProjectChecker = function (divId) {
        this.initializeProjectChecker(); // not clear why a navigating back to this page makes field checkers empty, so we'll always load.
        return this.projectChecker[divId];
    }
}


studies = new Studies();
projectChecker = null;

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

/*
 * Set default tests by study, but 
 */
function setDefaultTests( div )
{
    if ( requestType != 'initial' ) {
        return;
    }
    var tests = new Array();
    if (div=="InitialARV_Id") {
        tests = new Array("iarv.serologyHIVTest", "iarv.glycemiaTest", "iarv.creatinineTest",
                "iarv.transaminaseTest", "iarv.edtaTubeTaken", "iarv.dryTubeTaken",
                "iarv.nfsTest", "iarv.cd4cd8Test") ;
    }
    if (div=="FollowUpARV_Id") {
        tests = new Array("farv.glycemiaTest", "farv.creatinineTest",
               "farv.transaminaseTest", "farv.edtaTubeTaken", "farv.dryTubeTaken",
               "farv.nfsTest", "farv.cd4cd8Test") ;
    }
    //if (div=="EID_Id") {
    //  tests = new Array ("eid.dnaPCR", "eid.dbsTaken");
    //}
    if (div=="RTN_Id" ) {
        tests = new Array ("rtn.serologyHIVTest", "rtn.dryTubeTaken");
    }
    if (div=="Indeterminate_Id" ){
            tests = new Array ("ind.serologyHIVTest", "ind.dryTubeTaken");
    }

    for( var i = 0; i < tests.length; i++ ){
        var testId = tests[i];
        $(testId).value = true;
        $(testId).checked = true;
    }
}

function initializeStudySelection() {
    selectStudy($('projectFormName').value);
}

function selectStudy( divId ) {
    var i = getSelectIndexFor("studyFormsId", divId);
    document.forms[0].studyForms.selectedIndex = i;
    switchStudyForm( divId );
}

function switchStudyForm( divId ){
    hideAllDivs();
    if (divId != "" && divId != "0") {
        $("projectFormName").value = divId;
        switch (divId) {
        case "EID_Id":
            //location.replace("SampleEntryByProject.do?type=initial");
            savePage__("SampleEntryByProject.do?type=" + type);
            return;
        case "VL_Id":
            //location.replace("SampleEntryByProject.do?type=initial");
            savePage__("SampleEntryByProject.do?type=" + type);
            return;
        }
        toggleDisabledDiv(document.getElementById(divId), true);
        //document.forms[0].project.value = divId;
        document.getElementById(divId).style.display = "block";
        fieldValidator = studies.getValidator(divId); // reset the page fieldValidator for all fields to use.
        projectChecker = studies.getProjectChecker(divId);
        projectChecker.setSubjectOrSiteSubjectEntered();                
        adjustFieldsForRequestType();
        setDefaultTests(divId);
        setSaveButton();
    }
}
function adjustFieldsForRequestType()  {
    switch (requestType) {
    case "initial":
        break;
    case "verify":
        break;
    }
}

function hideAllDivs(){
    toggleDisabledDiv(document.getElementById("InitialARV_Id"), false);
    toggleDisabledDiv(document.getElementById("FollowUpARV_Id"), false);
    toggleDisabledDiv(document.getElementById("RTN_Id"), false);
    //toggleDisabledDiv(document.getElementById("EID_Id"), false);
    toggleDisabledDiv(document.getElementById("Indeterminate_Id"), false);
    toggleDisabledDiv(document.getElementById("Special_Request_Id"), false);

    document.getElementById('InitialARV_Id').style.display = "none";
    document.getElementById('FollowUpARV_Id').style.display = "none";
    document.getElementById('RTN_Id').style.display = "none";
    //document.getElementById('EID_Id').style.display = "none";
    document.getElementById('Indeterminate_Id').style.display = "none";
    document.getElementById('Special_Request_Id').style.display = "none";
}

function /*boolean*/ allSamplesHaveTests(){
    // based on studyType, check that at least one test is chosen
    // TODO PAHill this check is done on the server, but could be done here also.
}

function  /*void*/ savePage__(action) {
    window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
    var form = window.document.forms[0];
    if (action == null) {
        action = "SampleEntryByProjectSave.do?type=" + type
    }
    form.action = action;
    form.submit();
}

function /*void*/ setSaveButton() {
    var validToSave = fieldValidator.isAllValid();

    $("saveButtonId").disabled = !validToSave;
    
}

</script>

<html:hidden name="<%=formName%>" property="currentDate" styleId="currentDate"/>
<html:hidden name="<%=formName%>" property="domain" value="<%=genericDomain%>" styleId="domain"/>
<html:hidden name="<%=formName%>" property="project" styleId="project"/>
<html:hidden name="<%=formName%>" property="patientLastUpdated" styleId="patientLastUpdated" />
<html:hidden name="<%=formName%>" property="personLastUpdated" styleId="personLastUpdated"/>
<html:hidden name="<%=formName%>" property="patientProcessingStatus" styleId="processingStatus" value="add" />
<html:hidden name="<%=formName%>" property="patientPK" styleId="patientPK" />
<html:hidden name="<%=formName%>" property="samplePK" styleId="samplePK" />
<html:hidden name="<%=formName%>" property="observations.projectFormName" styleId="projectFormName"/>
<html:hidden name="<%=formName%>" property=""  styleId="subjectOrSiteSubject" value="" />

<b><bean:message key="sample.entry.project.form"/></b>
<select name="studyForms" onchange="switchStudyForm(this.value);" id="studyFormsId">
    <option value="0" selected> </option>
    <option value="InitialARV_Id" ><bean:message key="sample.entry.project.initialARV.title"/></option>
    <option value="FollowUpARV_Id" ><bean:message key="sample.entry.project.followupARV.title"/></option>
    <option value="RTN_Id" ><bean:message key="sample.entry.project.RTN.title"/></option>
    <option value="EID_Id" ><bean:message key="sample.entry.project.EID.title"/></option>
    <option value="Indeterminate_Id" ><bean:message key="sample.entry.project.indeterminate.title"/></option>
    <option value="Special_Request_Id"><bean:message key="sample.entry.project.specialRequest.title"/></option>
    <option value="VL_Id" ><bean:message key="sample.entry.project.VL.title"/></option>
</select>
<br/>
<hr>

<div id="studies">
<div id="InitialARV_Id" style="display:none;">
    <h2><bean:message key="sample.entry.project.initialARV.title"/></h2>
    <table width="100%">
        <tr >
            <td class="required" width="2%">*</td>
            <td width="28%">
                <bean:message key="sample.entry.project.ARV.centerName" />
            </td>
            <td width="70%">
                <html:select name="<%=formName%>"
                             property="ProjectData.ARVcenterName"
                             styleId="iarv.centerName"
                             onchange="iarv.checkCenterName(true)">
                    <app:optionsCollection name="<%=formName%>"
                        property="organizationTypeLists.ARV_ORGS_BY_NAME.list"
                        label="organizationName"
                        value="id" />
                </html:select>
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="patient.project.centerCode" />
            </td>
            <td>
                <html:select name="<%=formName%>"
                             property="ProjectData.ARVcenterCode"
                             styleId="iarv.centerCode"
                             onchange="iarv.checkCenterCode(true)">
                    <app:optionsCollection name="<%=formName%>"
                        property="organizationTypeLists.ARV_ORGS.list" label="doubleName"
                        value="id" />
                </html:select>
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message key="sample.entry.project.doctor"/>
            </td>
            <td>
            <app:text name="<%=formName%>" property="observations.nameOfDoctor"
                        styleClass="text"
                        styleId="iarv.nameOfDoctor" size="50"
                        onchange="compareAllObservationHistoryFields(true)"/>
            </td>
            <div id="iarv.nameOfDoctorMessage" class="blank"></div>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="sample.entry.project.receivedDate"/>&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="receivedDateForDisplay"
                    onkeyup="addDateSlashes(this, event);"
                    onchange="iarv.checkReceivedDate(false);"
                    styleClass="text"
                    styleId="iarv.receivedDateForDisplay" maxlength="10"/>
                    <div id="iarv.receivedDateForDisplayMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                 <bean:message key="sample.entry.project.receivedTime" />&nbsp;<bean:message key="sample.military.time.format"/>
            </td>
            <td>
            <app:text name="<%=formName%>"
                property="receivedTimeForDisplay"   
                onkeyup="filterTimeKeys(this, event);"                 
                onblur="iarv.checkReceivedTime(true);"
                styleClass="text"
                styleId="iarv.receivedTimeForDisplay" maxlength="5"/>
                <div id="iarv.receivedTimeForDisplayMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="sample.entry.project.dateTaken"/>&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="interviewDate"
                    onkeyup="addDateSlashes(this, event);"
                    onchange="iarv.checkInterviewDate(false)"
                    styleClass="text"
                    styleId="iarv.interviewDate" maxlength="10"/>
                    <div id="iarv.interviewDateMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message key="sample.entry.project.timeTaken"/>&nbsp;<bean:message key="sample.military.time.format"/>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="interviewTime"
                    onkeyup="filterTimeKeys(this, event);"              
                    onblur="iarv.checkInterviewTime(true);"
                    styleClass="text"
                    styleId="iarv.interviewTime" maxlength="5"/>
                    <div id="iarv.interviewTimeMessage" class="blank" />
            </td>
        </tr>       
        <tr>
            <td class="required">+</td>
            <td><bean:message key="sample.entry.project.subjectNumber"/></td>
            <td>
                <app:text name="<%=formName%>"
                        property="subjectNumber"
                        styleId="iarv.subjectNumber"
                        styleClass="text"
                        maxlength="7"
                        onchange="iarv.checkSubjectNumber(true)"/>
                <div id="iarv.subjectNumberMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td class="required">+</td>
            <td><bean:message key="patient.site.subject.number"/></td>
            <td>
                <app:text name="<%=formName%>"
                        property="siteSubjectNumber"
                        styleId="iarv.siteSubjectNumber"
                        styleClass="text"
                        onchange="iarv.checkSiteSubjectNumber(true);"/>
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <%=StringUtil.getContextualMessageForKey("quick.entry.accession.number")%>
            </td>
            <td>
                <div class="blank"><bean:message key="sample.entry.project.LART"/></div>
                <INPUT type="text" name="iarv.labNoForDisplay" id="iarv.labNoForDisplay" size="5" class="text"
                    onchange="handleLabNoChange( this, '<bean:message key="sample.entry.project.LART"/>', 'false' );makeDirty();"
                    maxlength="5" />
                <app:text name="<%=formName%>" property="labNo"
                        styleClass="text"
                        style="display:none;"
                        styleId="iarv.labNo" />
                <div id="iarv.labNoMessage" class="blank"  ></div>
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message  key="patient.gender" />
            </td>
            <td>
                <html:select name="<%=formName%>"
                         property="gender"
                         styleId="iarv.gender"
                         onchange="iarv.checkGender(true)">
                <app:optionsCollection name="<%=formName%>" property="genders"
                    label="localizedName" value="genderType" />
                </html:select>
                <div id="iarv.genderMessage" class="blank" />
            </td>
        </tr>

        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="patient.birthDate" />&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td>
                <app:text name="<%=formName%>"
                      property="birthDateForDisplay"
                      styleClass="text"
                      size="20"
                      maxlength="10"
                      onkeyup="addDateSlashes(this, event);"
                      onchange="iarv.checkDateOfBirth(false)"
                      styleId="iarv.dateOfBirth" maxlength="10"/>
                <div id="iarv.dateOfBirthMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td ></td>
            <td>
                <bean:message  key="patient.age" />
            </td>
            <td>
                <label for="iarv.age" ><bean:message  key="label.year" /></label>
                <INPUT type="text" name="ageYear" id="iarv.age" size="3"
                    onchange="iarv.checkAge( this, true, 'year' );"
                    maxlength="2" />
                <div id="ageMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="3" class="sectionTitle">
                <bean:message  key="sample.entry.project.title.specimen" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.ARV.dryTubeTaken" /></td>
            <td>

                <html:checkbox name="<%=formName%>"
                       property="ProjectData.dryTubeTaken"
                       styleId="iarv.dryTubeTaken"
                       onchange="iarv.checkSampleItem(this);"/>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.ARV.edtaTubeTaken" /></td>
            <td>
                <html:checkbox name="<%=formName%>"
                       property="ProjectData.edtaTubeTaken"
                       styleId="iarv.edtaTubeTaken"
                       onchange="iarv.checkSampleItem(this);"/>
            </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="3" class="sectionTitle">
                <bean:message  key="sample.entry.project.title.dryTube" />
            </td>
        </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.serologyHIVTest" /></td>
                <td>

                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.serologyHIVTest"
                           styleId="iarv.serologyHIVTest"
                           onchange="iarv.checkSampleItem($('iarv.dryTubeTaken'), this)"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.glycemiaTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.glycemiaTest"
                           styleId="iarv.glycemiaTest"
                           onchange="iarv.checkSampleItem($('iarv.dryTubeTaken'), this)"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.creatinineTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                                property="ProjectData.creatinineTest"
                                styleId="iarv.creatinineTest"
                                onchange="iarv.checkSampleItem($('iarv.dryTubeTaken'), this);" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.transaminaseTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                                property="ProjectData.transaminaseTest"
                                styleId="iarv.transaminaseTest"
                                onchange="iarv.checkSampleItem($('iarv.dryTubeTaken'), this)" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="3" class="sectionTitle">
                    <bean:message  key="sample.entry.project.title.edtaTube" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.nfsTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                                property="ProjectData.nfsTest"
                                styleId="iarv.nfsTest"
                                onchange="iarv.checkSampleItem($('iarv.edtaTubeTaken'), this)" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.cd4cd8Test" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                                property="ProjectData.cd4cd8Test"
                                styleId="iarv.cd4cd8Test"
                                onchange="iarv.checkSampleItem($('iarv.edtaTubeTaken'), this)" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="3" class="sectionTitle">
                    <bean:message  key="sample.entry.project.title.otherTests" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.viralLoadTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.viralLoadTest"
                           styleId="iarv.viralLoadTest"
                           onchange="iarv.checkSampleItem($('iarv.edtaTubeTaken'), this);" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.genotypingTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.genotypingTest"
                           styleId="iarv.genotypingTest"
                           onchange="iarv.checkSampleItem($('iarv.edtaTubeTaken'), this)" />
                </td>
            </tr>
            <tr><td colspan="6"><hr/></td></tr>
            <tr id="iarv.underInvestigationRow">
                <td class="required"></td>
                <td>
                    <bean:message key="patient.project.underInvestigation" />
                </td>
                <td>
                    <html:select name="<%=formName%>"
                    property="observations.underInvestigation" onchange="makeDirty();compareAllObservationHistoryFields(true)"
                    styleId="iarv.underInvestigation">
                    <app:optionsCollection name="<%=formName%>"
                        property="dictionaryLists.YES_NO.list" label="localizedName"
                        value="id" />
                    </html:select>
                </td>
            </tr>
            <tr id="iarv.underInvestigationCommentRow">
                <td class="required"></td>
                <td>
                    <bean:message key="patient.project.underInvestigationComment" />
                </td>
                <td colspan="3">
                    <app:text name="<%=formName%>" property="ProjectData.underInvestigationNote" maxlength="1000" size="80"
                        onchange="makeDirty();" styleId="iarv.underInvestigationComment" />
                </td>
            </tr>
    </table>

</div>
<div id="FollowUpARV_Id" style="display:none;">
    <h2><bean:message key="sample.entry.project.followupARV.title"/></h2>
    <table width="100%">
        <tr>
            <td class="required" width="2%">*</td>
            <td width="28%">
                <bean:message key="sample.entry.project.ARV.centerName" />
            </td>
            <td width="70%">
                <html:select name="<%=formName%>"
                             property="ProjectData.ARVcenterName"
                             styleId="farv.centerName"
                             onchange="farv.checkCenterName(true)">
                    <app:optionsCollection name="<%=formName%>"
                        property="organizationTypeLists.ARV_ORGS_BY_NAME.list"
                        label="organizationName"
                        value="id" />
                </html:select>
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="patient.project.centerCode" />
            </td>
            <td>
                <html:select name="<%=formName%>"
                             property="ProjectData.ARVcenterCode"
                             styleId="farv.centerCode"
                             onchange="farv.checkCenterCode(true)">
                    <app:optionsCollection name="<%=formName%>"
                        property="organizationTypeLists.ARV_ORGS.list" label="doubleName"
                        value="id" />
                </html:select>
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message key="sample.entry.project.doctor"/>
            </td>
            <td>
            <app:text name="<%=formName%>" property="observations.nameOfDoctor"
                        styleClass="text"
                        styleId="farv.nameOfDoctor" size="50"
                        onchange="compareAllObservationHistoryFields(true)" />
            </td>
        </tr>

        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="sample.entry.project.receivedDate"/>&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="receivedDateForDisplay"
                    styleClass="text"
                    styleId="farv.receivedDateForDisplay" maxlength="10"
                    onkeyup="addDateSlashes(this, event);"
                    onchange="farv.checkReceivedDate(false);" />
                    <div id="farv.receivedDateForDisplayMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message key="sample.entry.project.receivedTime"/>&nbsp;<bean:message key="sample.military.time.format"/>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="receivedTimeForDisplay"
                    styleClass="text"
                    onkeyup="filterTimeKeys(this, event);"
                    styleId="farv.receivedTimeForDisplay" maxlength="5"                    
                    onblur="farv.checkReceivedTime(true);" />
                    <div id="farv.receivedTimeForDisplayMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="sample.entry.project.dateTaken"/>&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="interviewDate"
                    onkeyup="addDateSlashes(this, event);"
                    onchange="farv.checkInterviewDate(false)"
                    styleClass="text"
                    styleId="farv.interviewDate" maxlength="10"/>
                    <div id="farv.interviewDateMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message key="sample.entry.project.timeTaken"/>&nbsp;<bean:message key="sample.military.time.format"/>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="interviewTime"
                    onkeyup="filterTimeKeys(this, event);"                 
                    onblur="farv.checkInterviewTime(true);"
                    styleClass="text"
                    styleId="farv.interviewTime" maxlength="5"/>
                    <div id="farv.interviewTimeMessage" class="blank" />
            </td>
        </tr>       
       
        <tr>
            <td class="required">+</td>
            <td><bean:message key="sample.entry.project.subjectNumber"/></td>
            <td>
                <app:text name="<%=formName%>"
                        property="subjectNumber"
                        styleId="farv.subjectNumber"
                        styleClass="text"
                        maxlength="7"
                        onchange="farv.checkSubjectNumber(true);" />
                <div id="farv.subjectNumberMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td class="required">+</td>
            <td><bean:message key="patient.site.subject.number"/></td>
            <td>
                <app:text name="<%=formName%>"
                        property="siteSubjectNumber"
                        styleId="farv.siteSubjectNumber"
                        styleClass="text"
                        onchange="farv.checkSiteSubjectNumber(true);" />
                <div id="farv.siteSubjectNumberMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <%=StringUtil.getContextualMessageForKey("quick.entry.accession.number")%>

            </td>
            <td>
                <div class="blank"><bean:message key="sample.entry.project.LART"/></div>
                <INPUT type=text name="farv.labNoForDisplay" id="farv.labNoForDisplay" size="5" class="text"
                    onchange="handleLabNoChange( this, '<bean:message key="sample.entry.project.LART"/>', false );makeDirty();"
                    maxlength="5" />
                <app:text name="<%=formName%>" property="labNo"
                        styleClass="text" style="display:none;"
                        styleId="farv.labNo" />
                <div id="farv.labNoMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message  key="patient.gender" />
            </td>
            <td>
                <html:select name="<%=formName%>"
                         property="gender"
                         styleId="farv.gender"
                         onchange="farv.checkGender(false)" >
                    <app:optionsCollection name="<%=formName%>" property="genders"
                        label="localizedName" value="genderType" />
                </html:select>
                <div id="farv.genderIDMessage" class="blank" />
            </td>
        </tr>

        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="patient.birthDate" />&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td>
                <app:text name="<%=formName%>"
                      property="birthDateForDisplay"
                      styleClass="text"
                      size="20"
                      maxlength="10"
                      styleId="farv.dateOfBirth"
                      onkeyup="addDateSlashes(this, event);"
                      onchange="farv.checkDateOfBirth(false)" />
                <div id="farv.dateOfBirthMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td ></td>
            <td>
                <bean:message  key="patient.age" />
            </td>
            <td>
                <label for="farv.age" ><bean:message  key="label.year" /></label>
                <INPUT type="text" name="ageYear" id="farv.age" size="3"
                    onchange="farv.checkAge( this, true, 'year' );"
                    maxlength="2" />
                <div id="ageMessage" class="blank" ></div>
            </td>
        </tr>
        <tr >
            <td></td>
            <td>
                <bean:message key="patient.project.hivStatus" />
            </td>
            <td>
                <html:select name="<%=formName%>"
                         property="observations.hivStatus"
                         onchange="farv.checkHivStatus(true);"
                         styleId="farv.hivStatus"  >
                    <app:optionsCollection name="<%=formName%>" property="ProjectData.hivStatusList"
                        label="localizedName" value="id" />
                </html:select>
                <div id="farv.hivStatusMessage" class="blank"></div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="3" class="sectionTitle">
                <bean:message  key="sample.entry.project.title.specimen" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.ARV.dryTubeTaken" /></td>
            <td>

                <html:checkbox name="<%=formName%>"
                                property="ProjectData.dryTubeTaken"
                                styleId="farv.dryTubeTaken"
                                onchange="farv.checkSampleItem(this)" />
                <div id="farv.dryTubeTakenMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.ARV.edtaTubeTaken" /></td>
            <td>
                <html:checkbox name="<%=formName%>"
                            property="ProjectData.edtaTubeTaken"
                            styleId="farv.edtaTubeTaken"
                            onchange="farv.checkSampleItem(this);"/>
                <div id="farv.edtaTubeTakenMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="3" class="sectionTitle">
                <bean:message  key="sample.entry.project.title.dryTube" />
            </td>
        </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.serologyHIVTest" /></td>
                <td>

                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.serologyHIVTest"
                           styleId="farv.serologyHIVTest"
                           onchange="farv.checkSampleItem($('farv.dryTubeTaken'), $('farv.serologyHIVTest'))" />
                    <div id="farv.serologyHIVTestMessage" class="blank" ></div>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.glycemiaTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.glycemiaTest"
                           styleId="farv.glycemiaTest"
                           onchange="farv.checkSampleItem($('farv.dryTubeTaken'), $('farv.glycemiaTest'))" />
                    <div id="farv.glycemiaTestMessage" class="blank" ></div>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.creatinineTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                            property="ProjectData.creatinineTest"
                            styleId="farv.creatinineTest"
                            onchange="farv.checkSampleItem($('farv.dryTubeTaken'), $('farv.creatinineTest'))" />
                    <div id="farv.creatinineTest" class="blank" ></div>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.transaminaseTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                            property="ProjectData.transaminaseTest"
                            styleId="farv.transaminaseTest"
                            onchange="farv.checkSampleItem($('farv.dryTubeTaken'), $('farv.transaminaseTest'))" />
                    <div id="farv.transaminaseTestMessage" class="blank" ></div>
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="3" class="sectionTitle">
                    <bean:message  key="sample.entry.project.title.edtaTube" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.nfsTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.nfsTest"
                           styleId="farv.nfsTest"
                           onchange="farv.checkSampleItem($('farv.edtaTubeTaken'), $('farv.nfsTest'))" />
                    <div id="farv.nfsTestMessage" class="blank" ></div>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.cd4cd8Test" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                                property="ProjectData.cd4cd8Test"
                                styleId="farv.cd4cd8Test"
                                onchange="farv.checkSampleItem($('farv.edtaTubeTaken'), $('farv.cd4cd8Test'))" />
                    <div id="farv.cd4cd8TestMessage" class="blank" ></div>
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="3" class="sectionTitle">
                    <bean:message  key="sample.entry.project.title.otherTests" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.viralLoadTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.viralLoadTest"
                           styleId="farv.viralLoadTest"
                           onchange="farv.checkSampleItem($('farv.edtaTubeTaken'), $('farv.viralLoadTest'))" />
                    <div id="farv.viralLoadTestMessage" class="blank" ></div>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.genotypingTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.genotypingTest"
                           styleId="farv.genotypingTest"
                           onchange="farv.checkSampleItem($('farv.edtaTubeTaken'), $('farv.genotypingTest'))" />
                    <div id="farv.genotypingTestMessage" class="blank" ></div>
                </td>
            </tr>
            
            <tr><td colspan="6"><hr/></td></tr>
            <tr id="farv.underInvestigationRow">
                <td class="required"></td>
                <td>
                    <bean:message key="patient.project.underInvestigation" />
                </td>
                <td>
                    <html:select name="<%=formName%>"
                    property="observations.underInvestigation" onchange="makeDirty();compareAllObservationHistoryFields(true)"
                    styleId="farv.underInvestigation">
                    <app:optionsCollection name="<%=formName%>"
                        property="dictionaryLists.YES_NO.list" label="localizedName"
                        value="id" />
                    </html:select>
                </td>
            </tr>
            <tr id="farv.underInvestigationCommentRow">
                <td class="required"></td>
                <td>
                    <bean:message key="patient.project.underInvestigationComment" />
                </td>
                <td colspan="3">
                    <app:text name="<%=formName%>" property="ProjectData.underInvestigationNote" maxlength="1000" size="80"
                        onchange="makeDirty();" styleId="farv.underInvestigationComment" />
                </td>
            </tr>
    </table>
</div>

<div id="RTN_Id" style="display:none;">
    <h2><bean:message key="sample.entry.project.RTN.title"/></h2>
    <table width="100%">
        <tr>
            <td class="required" width="2%">*</td>
            <td width="28%">
                <bean:message key="sample.entry.project.receivedDate"/>&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td style="width: 70%;">
            <app:text name="<%=formName%>"
                    property="receivedDateForDisplay"
                    onkeyup="addDateSlashes(this, event);"
                    onchange="rtn.checkReceivedDate(false)"
                    styleClass="text"
                    styleId="rtn.receivedDateForDisplay" maxlength="10"/>
                    <div id="rtn.receivedDateForDisplayMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message key="sample.entry.project.receivedTime"/>&nbsp;<bean:message key="sample.military.time.format"/>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="receivedTimeForDisplay"
                    styleClass="text"
                    onkeyup="filterTimeKeys(this, event);"
                    styleId="rtn.receivedTimeForDisplay" maxlength="5"                    
                    onblur="rtn.checkReceivedTime(true);" />
                    <div id="rtn.receivedTimeForDisplayMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="sample.entry.project.dateTaken"/>&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="interviewDate"
                    onkeyup="addDateSlashes(this, event);"
                    onchange="rtn.checkInterviewDate(false)"
                    styleClass="text"
                    styleId="rtn.interviewDate" maxlength="10"/>
                    <div id="rtn.interviewDateMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message key="sample.entry.project.timeTaken"/>&nbsp;<bean:message key="sample.military.time.format"/>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="interviewTime"
                    onkeyup="filterTimeKeys(this, event);"
                    styleClass="text"
                    styleId="rtn.interviewTime" maxlength="5"                    
                    onblur="rtn.checkInterviewTime(true);" />
                    <div id="rtn.interviewTimeMessage" class="blank" />
            </td>
        </tr>       
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="patient.birthDate" />&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td>
                <app:text name="<%=formName%>"
                      property="birthDateForDisplay"
                      styleClass="text"
                      size="20"
                      maxlength="10"
                      onkeyup="addDateSlashes(this, event);"
                      onchange="rtn.checkDateOfBirth(true)"
                      styleId="rtn.dateOfBirth" maxlength="10"/>
                <div id="rtn.dateOfBirthMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message  key="patient.age" />
            </td>
            <td>
                <label for="rtn.age" ><bean:message  key="label.year" /></label>
                <INPUT type='text' name='age' id="rtn.age" size="3"
                    onchange="rtn.checkAge( this, true, 'year' );clearField('rtn.month');"
                    maxlength="2" />
                <label for="rtn.month" ><bean:message  key="label.month" /></label>
                <INPUT type='text' name='month' id="rtn.month" size="3"
                    onchange="rtn.checkAge( this, true, 'month' ); clearField('rtn.age');"
                    maxlength="2" />
                <div id="ageMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message  key="patient.gender" />
            </td>
            <td>
                <html:select name="<%=formName%>"
                         property="gender"
                         styleId="rtn.gender"
                         onchange="rtn.checkGender(true)" >
                <app:optionsCollection name="<%=formName%>" property="genders"
                    label="localizedName" value="genderType" />
                </html:select>
                <div id="rtn.genderMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <%=StringUtil.getContextualMessageForKey("quick.entry.accession.number")%>
            </td>
            <td>
                <div class="blank"><bean:message key="sample.entry.project.LRTN"/></div>
                <INPUT type="text" name="rtn.labNoForDisplay" id="rtn.labNoForDisplay" size="5" class="text"
                    onchange="handleLabNoChange( this, 'LRTN', false );makeDirty();"
                    maxlength="5" />
                <app:text name="<%=formName%>" property="labNo"
                        styleClass="text" style="display:none;"
                        styleId="rtn.labNo" />
                <div id="rtn.labNoForDisplayMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="3" class="sectionTitle">
                <bean:message  key="sample.entry.project.title.specimen" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.ARV.dryTubeTaken" /></td>
            <td>
                <html:checkbox name="<%=formName%>"
                       property="ProjectData.dryTubeTaken"
                       styleId="rtn.dryTubeTaken"
                       onchange="rtn.checkSampleItem($('rtn.dryTubeTaken'))" />
                <div id="rtn.dryTubeTakenMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="3" class="sectionTitle">
                <bean:message  key="sample.entry.project.title.dryTube" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message  key="sample.entry.project.serologyHIVTest" /></td>
            <td>
                <html:checkbox name="<%=formName%>"
                       property="ProjectData.serologyHIVTest"
                       styleId="rtn.serologyHIVTest"
                       onchange="rtn.checkSampleItem($('rtn.dryTubeTaken'), $('rtn.serologyHIVTest'))" />
            </td>
        </tr>
        <tr><td colspan="6"><hr/></td></tr>
        <tr id="rtn.underInvestigationRow">
            <td class="required"></td>
            <td>
                <bean:message key="patient.project.underInvestigation" />
            </td>
            <td>
                <html:select name="<%=formName%>"
                property="observations.underInvestigation" onchange="makeDirty();compareAllObservationHistoryFields(true)"
                styleId="rtn.underInvestigation">
                <app:optionsCollection name="<%=formName%>"
                    property="dictionaryLists.YES_NO.list" label="localizedName"
                    value="id" />
                </html:select>
            </td>
        </tr>       
        <tr id="rtn.underInvestigationCommentRow">
            <td class="required"></td>
            <td>
                <bean:message key="patient.project.underInvestigationComment" />
            </td>
            <td colspan="3">
                <app:text name="<%=formName%>" property="ProjectData.underInvestigationNote" maxlength="1000" size="80"
                    onchange="makeDirty();" styleId="rtn.underInvestigationComment" />
            </td>
        </tr>
    </table>
</div>

<div id="EID_Id" style="display:none;">
    <h2><bean:message key="sample.entry.project.EID.title"/></h2>
</div>

<div id="VL_Id" style="display:none;">
    <h2><bean:message key="sample.entry.project.VL.title"/></h2>
</div>

<div id="Indeterminate_Id" style="display:none;">
    <h2><bean:message key="sample.entry.project.indeterminate.title"/></h2>
    <table width="100%">
        <tr>
            <td class="required" width="2%">*</td>
            <td width="28%">
                <bean:message key="sample.entry.project.receivedDate"/>&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td width="70%">
            <app:text name="<%=formName%>"
                    property="receivedDateForDisplay"
                    onkeyup="addDateSlashes(this, event);"
                    onchange="ind.checkReceivedDate(false);"
                    styleClass="text"
                    styleId="ind.receivedDateForDisplay" maxlength="10"/>
                    <div id="ind.receivedDateForDisplayMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message key="sample.entry.project.receivedTime"/>&nbsp;<bean:message key="sample.military.time.format"/>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="receivedTimeForDisplay"
                    onkeyup="filterTimeKeys(this, event);"
                    styleClass="text"
                    styleId="ind.receivedTimeForDisplay" maxlength="5"                    
                    onblur="ind.checkReceivedTime(true);" />
                    <div id="ind.receivedTimeForDisplayMessage" class="blank" />
            </td>
        </tr>       
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="sample.entry.project.dateTaken"/>&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="interviewDate"
                    onkeyup="addDateSlashes(this, event);"
                    onchange="ind.checkInterviewDate(false)"
                    styleClass="text"
                    styleId="ind.interviewDate" maxlength="10"/>
                    <div id="ind.interviewDateMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message key="sample.entry.project.timeTaken"/>&nbsp;<bean:message key="sample.military.time.format"/>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="interviewTime"
                    onkeyup="filterTimeKeys(this, event);"
                    styleClass="text"
                    styleId="ind.interviewTime" maxlength="5"                    
                    onblur="ind.checkInterviewTime(true);" />
                    <div id="ind.interviewTimeMessage" class="blank" />
            </td>
        </tr>       
        <tr>
            <td class="required">*</td>
            <td><bean:message key="sample.entry.project.siteName"/></td>
            <td style="width: 40%;">
                <html:select name="<%=formName%>"  property="ProjectData.INDsiteName" styleClass="text" styleId="ind.centerCode"
                        onchange="ind.checkCenterCode(true)" >
                    <app:optionsCollection name="<%=formName%>" property="ProjectData.EIDSites" label="doubleName" value="id" />
                </html:select>
                <div id="ind.centerCodeMessage" class="blank"/>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.address"/></td>
            <td>
                <app:text name="<%=formName%>"
                        property="ProjectData.address"
                        styleClass="text"
                        styleId ="ind.address"
                        onchange="ind.checkPatientField('address', true, 'street')" />
                        <div id="ind.addressMessage" class="blank"></div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.phoneNumber"/></td>
            <td>
                <app:text name="<%=formName%>"
                        property="ProjectData.phoneNumber"
                        styleClass="text"
                        styleId="ind.phoneNumber"
                        onchange="ind.checkPatientField('phoneNumber')" />
                        <div id="ind.phoneNumberMessage" class="blank"></div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.faxNumber"/></td>
            <td>
                <app:text name="<%=formName%>"
                        property="ProjectData.faxNumber"
                        styleClass="text"
                        styleId="ind.faxNumber"
                        onchange="ind.checkPatientField('faxNumber')"/>
                        <div id="ind.faxNumberMessage" class="blank"></div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.email"/></td>
            <td>
                <app:text name="<%=formName%>"
                        property="ProjectData.email"
                        styleClass="text"
                        styleId="ind.email"
                        onchange="ind.checkPatientField('email');" />
                        <div id="ind.emailMessage" class="blank"></div>
            </td>
        </tr>
        <tr>
            <td class="required">+</td>
            <td><bean:message key="sample.entry.project.subjectNumber"/></td>
            <td>
                <app:text name="<%=formName%>"
                        property="subjectNumber"
                        styleClass="text"
                        styleId="ind.subjectNumber"
                        maxlength="7"
                        onchange="ind.checkSubjectNumber(true)" />
                <div id="ind.subjectIDMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td class="required">+</td>
            <td><bean:message key="patient.site.subject.number"/></td>
            <td>
                <app:text name="<%=formName%>"
                        property="siteSubjectNumber"
                        styleId="ind.siteSubjectNumber"
                        styleClass="text"
                        onchange="ind.checkSiteSubjectNumber(true)" />
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <%=StringUtil.getContextualMessageForKey("quick.entry.accession.number")%>
            </td>
            <td>
                <div class="blank"><bean:message key="sample.entry.project.LIND"/></div>
                <INPUT type="text" name="ind.labNoForDisplay" id="ind.labNoForDisplay" size="5" class="text"
                    onchange="handleLabNoChange( this, '<bean:message key="sample.entry.project.LIND"/>', false );makeDirty();"
                    maxlength="5" />
                <app:text name="<%=formName%>" property="labNo" style="display:none;"
                        styleClass="text"
                        styleId="ind.labNo" />
                <div id="ind.labNoMessage"  class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message  key="patient.gender" />
            </td>
            <td>
                <html:select name="<%=formName%>"
                         property="gender"
                         styleId="ind.gender"
                         onchange="ind.checkGender(false);" >
                <app:optionsCollection name="<%=formName%>" property="genders"
                    label="localizedName" value="genderType" />
                </html:select>
                <div id="ind.genderMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="patient.birthDate" />&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td>
                <app:text name="<%=formName%>"
                      property="birthDateForDisplay"
                      styleClass="text"
                      size="20"
                      maxlength="10"
                      styleId="ind.dateOfBirth"
                      onkeyup="addDateSlashes(this, event);"
                      onchange="ind.checkDateOfBirth(false)"/>
                <div id="ind.dateOfBirthMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message  key="patient.age" />
            </td>
            <td>
                <label for="ind.age" ><bean:message  key="label.year" /></label>
                <INPUT type="text" name="age" id="ind.age" size="3"
                    maxlength="2"
                    onchange="ind.checkAge( this, 'ind.dateOfBirth', 'ind.interviewDate', 'year' ); makeDirty();"/>
                <div id="ageMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="2" ><h3><bean:message key="sample.entry.project.firstTest"/></h3></td>
        </tr>
        <tr>
        <td></td>
        <td ><bean:message key="sample.entry.project.date"/></td>
        <td>
            <app:text name="<%=formName%>" property="observations.indFirstTestDate"
                      styleClass="text"
                      styleId="ind.indFirstTestDate"
                      maxlength="10"
                      onkeyup="addDateSlashes(this, event);"
                      onchange="compareAllObservationHistoryFields(true, 'ind.');checkValidDate(this);"/>
                      <div id="ind.indFirstTestDateMessage" class="blank" />
        </td>
        </tr>
        <tr>
        <td></td>
        <td><bean:message key="sample.entry.project.testName"/></td>
            <td>
                <app:text name="<%=formName%>" property="observations.indFirstTestName"
                          styleClass="text"
                          styleId="ind.indFirstTestName"
                          onchange="compareAllObservationHistoryFields(true)" />
            </td>
        </tr>
        <tr>
        <td></td>
        <td><bean:message key="sample.entry.project.result"/></td>
        <td>
            <app:text name="<%=formName%>" property="observations.indFirstTestResult"
                styleClass="text"
                styleId="ind.indFirstTestResult"
                onchange="compareAllObservationHistoryFields(true)" />
        </td>
        </tr>


        <tr>
            <td></td>
            <td colspan="2" ><h3><bean:message key="sample.entry.project.secondTest"/></h3></td>
        </tr>
        <tr>
        <td></td>
        <td ><bean:message key="sample.entry.project.date"/></td>
        <td>
            <app:text name="<%=formName%>" property="observations.indSecondTestDate"
                      styleClass="text"
                      styleId="ind.indSecondTestDate"
                      maxlength="10"
                      onkeyup="addDateSlashes(this, event);"
                      onchange="compareAllObservationHistoryFields(true);checkValidDate(this);"/>
                      <div id="ind.indSecondTestDateMessage" class="blank" />
        </td>
        </tr>
        <tr>
        <td></td>
        <td><bean:message key="sample.entry.project.testName"/></td>
            <td>
                <app:text name="<%=formName%>" property="observations.indSecondTestName"
                          styleClass="text"
                          styleId="ind.indSecondTestName"
                          onchange="compareAllObservationHistoryFields(true)" />
            </td>
        </tr>
        <tr>
        <td></td>
        <td><bean:message key="sample.entry.project.result"/></td>
        <td>
            <app:text name="<%=formName%>" property="observations.indSecondTestResult"
                styleClass="text"
                styleId="ind.indSecondTestResult"
                onchange="compareAllObservationHistoryFields(true)" />
        </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.finalResultOfSite"/></td>
            <td>
                <app:text name="<%=formName%>"
                    property="observations.indSiteFinalResult"
                    styleClass="text"
                    styleId="ind.indSiteFinalResult"
                    onchange="compareAllObservationHistoryFields(true)"/>
            </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="2" class="sectionTitle">
                <bean:message  key="sample.entry.project.title.specimen" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.ARV.dryTubeTaken" /></td>
            <td>

                <html:checkbox name="<%=formName%>"
                       property="ProjectData.dryTubeTaken"
                       styleId="ind.dryTubeTaken"
                       onchange="ind.checkSampleItem($('ind.dryTubeTaken'));"/>
            </td>
        </tr>

        <tr>
            <td></td>
            <td colspan="3" class="sectionTitle">
                <bean:message  key="sample.entry.project.title.dryTube" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message  key="sample.entry.project.serologyHIVTest" /></td>
            <td>
                <html:checkbox name="<%=formName%>"
                       property="ProjectData.serologyHIVTest"
                       styleId="ind.serologyHIVTest"
                       onchange="ind.checkSampleItem($('ind.dryTubeTaken'), $('ind.serologyHIVTest'));" />
            </td>
        </tr>
        <tr><td colspan="6"><hr/></td></tr>
        <tr id="ind.underInvestigationRow">
            <td class="required"></td>
            <td>
                <bean:message key="patient.project.underInvestigation" />
            </td>
            <td>
                <html:select name="<%=formName%>"
                property="observations.underInvestigation" onchange="makeDirty();compareAllObservationHistoryFields(true)"
                styleId="ind.underInvestigation">
                <app:optionsCollection name="<%=formName%>"
                    property="dictionaryLists.YES_NO.list" label="localizedName"
                    value="id" />
                </html:select>
            </td>
        </tr>       
        <tr id="ind.underInvestigationCommentRow">
            <td class="required"></td>
            <td>
                <bean:message key="patient.project.underInvestigationComment" />
            </td>
            <td colspan="3">
                <app:text name="<%=formName%>" property="ProjectData.underInvestigationNote" maxlength="1000" size="80"
                    onchange="makeDirty();" styleId="ind.underInvestigationComment" />
            </td>
        </tr>
    </table>
</div>

<div id="Special_Request_Id" style="display:none;">
    <h2><bean:message key="sample.entry.project.specialRequest.title"/></h2>
    <table width="100%">
        <tr>
            <td class="required" width="2%">*</td>
            <td width="28%">
                <bean:message key="sample.entry.project.receivedDate"/>&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td width="70%">
            <app:text name="<%=formName%>" property="receivedDateForDisplay"
                styleClass="text"
                styleId="spe.receivedDateForDisplay" maxlength="10"
                onkeyup="addDateSlashes(this, event);"
                onchange="spe.checkReceivedDate(false);"/>
                <div id="spe.receivedDateForDisplayMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message key="sample.entry.project.receivedTime"/>&nbsp;<bean:message key="sample.military.time.format"/>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="receivedTimeForDisplay"
                    onkeyup="filterTimeKeys(this, event);"
                    styleClass="text"
                    styleId="spe.receivedTimeForDisplay" maxlength="5"                    
                    onblur="spe.checkReceivedTime(true);" />
                    <div id="spe.receivedTimeForDisplayMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="sample.entry.project.dateTaken"/>&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="interviewDate"
                    styleClass="text"
                    onkeyup="addDateSlashes(this, event);"
                    onchange="spe.checkInterviewDate(false);"
                    styleId="spe.interviewDate"  maxlength="10"/>
            <div id="spe.interviewDateMessage" class="blank" />
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message key="sample.entry.project.timeTaken"/>&nbsp;<bean:message key="sample.military.time.format"/>
            </td>
            <td>
            <app:text name="<%=formName%>"
                    property="interviewTime"
                    onkeyup="filterTimeKeys(this, event);"
                    styleClass="text"
                    styleId="spe.interviewTime" maxlength="5"                    
                    onblur="spe.checkInterviewTime(true);" />
                    <div id="spe.interviewTimeMessage" class="blank" />
            </td>
        </tr>       
        <tr>
            <td class="required">+</td>
            <td><bean:message key="sample.entry.project.subjectNumber"/></td>
            <td>
                <app:text name="<%=formName%>"
                        property="subjectNumber"
                        styleClass="text"
                        styleId="spe.subjectNumber"
                        maxlength="7"
                        onchange="spe.checkSubjectNumber(true);"  />
                <div id="spe.subjectNumberMessage" class="blank" />
            </td>
        </tr>
        <tr>
            <td class="required">+</td>
            <td><bean:message key="patient.site.subject.number"/></td>
            <td>
                <app:text name="<%=formName%>"
                        property="siteSubjectNumber"
                        styleId="spe.siteSubjectNumber"
                        styleClass="text"
                        onchange="spe.checkSiteSubjectNumber(true)" />
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message key="patient.birthDate" />&nbsp;<%=DateUtil.getDateUserPrompt()%>
            </td>
            <td>
                <app:text name="<%=formName%>"
                      property="birthDateForDisplay"
                      styleClass="text"
                      size="20"
                      maxlength="10"
                      onkeyup="addDateSlashes(this, event);"
                      onchange="spe.checkDateOfBirth(false)"
                      styleId="spe.dateOfBirth" />
                <div id="spe.dateOfBirthMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <bean:message  key="patient.age" />
            </td>
            <td>
                <label for="spe.age" ><bean:message  key="label.year" /></label>
                <INPUT type="text" name="age" id="spe.age" size="3"
                    onchange="spe.checkAge( this, true, 'year'); updatePatientEditStatus(); makeDirty();"
                    maxlength="3" />
                <div id="ageMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <bean:message  key="patient.gender" />
            </td>
            <td>
                <html:select name="<%=formName%>"
                         property="gender"
                         styleId="spe.gender"
                         onchange="spe.checkGender(false);" >
                    <app:optionsCollection name="<%=formName%>" property="genders"
                        label="localizedName" value="genderType" />
                </html:select>
                <div id="spe.genderMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td class="required">*</td>
            <td>
                <%=StringUtil.getContextualMessageForKey("quick.entry.accession.number")%>
            </td>
            <td>
                <div class="blank"><bean:message key="sample.entry.project.LSPE"/></div>
                <INPUT type="text" name="spe.labNoForDisplay" id="spe.labNoForDisplay" size="5" class="text"
                    onchange="handleLabNoChange( this, '<bean:message key="sample.entry.project.LSPE"/>', 'false' );makeDirty();"
                    maxlength="5" />
                <app:text name="<%=formName%>" property="labNo"
                        styleClass="text" style="display:none;"
                        styleId="spe.labNo" />
                <div id="spe.labNoMessage" class="blank" ></div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.specialRequest.reason"/></td>
            <td>
                <html:select name="<%=formName%>"
                             property="observations.reasonForRequest"
                             styleId="spe.reasonForRequest"
                             onchange="compareAllObservationHistoryFields(true)">
                    <app:optionsCollection name="<%=formName%>"
                        property="ProjectData.requestReasons"
                        label="localizedName"
                        value="id" />
                </html:select>
            </td>
        </tr>
        <tr>
            <td ></td>
            <td colspan="2" class="sectionTitle">
                <bean:message  key="sample.entry.project.title.specimen" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.ARV.dryTubeTaken" /></td>
            <td>
                <html:checkbox name="<%=formName%>"
                        property="ProjectData.dryTubeTaken"
                        styleId="spe.dryTubeTaken"
                        onchange="spe.checkSampleItem($('spe.dryTubeTaken'));"/>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.ARV.edtaTubeTaken" /></td>
            <td>
                <html:checkbox name="<%=formName%>"
                        property="ProjectData.edtaTubeTaken"
                        styleId="spe.edtaTubeTaken"
                        onchange="spe.checkSampleItem($('spe.edtaTubeTaken'));"/>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><bean:message key="sample.entry.project.title.dryBloodSpot" /></td>
            <td>
                <html:checkbox name="<%=formName%>"
                        property="ProjectData.dbsTaken"
                        styleId="spe.dbsTaken"
                        onchange="spe.checkSampleItem($('spe.dbsTaken'))" />
            </td>
        </tr>
        <tr>
            <td></td>
            <td colspan="2" class="sectionTitle">
                <bean:message  key="sample.entry.project.title.dryTube" />
            </td>
        </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.murexTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                            property="ProjectData.murexTest"
                            styleId="spe.murexTest"
                            onchange="spe.checkSampleItem($('spe.dryTubeTaken'), $('spe.murexTest'))"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.integralTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                            property="ProjectData.integralTest"
                            styleId="spe.integralTest"
                            onchange="spe.checkSampleItem($('spe.dryTubeTaken'), $('spe.integralTest'))"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.vironostikaTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                            property="ProjectData.vironostikaTest"
                            styleId="spe.vironostikaTest"
                            onchange="spe.checkSampleItem($('spe.dryTubeTaken'), $('spe.vironostikaTest'))"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.innoliaTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                            property="ProjectData.innoliaTest"
                            styleId="spe.innoliaTest"
                            onchange="spe.checkSampleItem($('spe.dryTubeTaken'), $('spe.innoliaTest'))"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.glycemiaTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                                property="ProjectData.glycemiaTest"
                                styleId="spe.glycemiaTest"
                                onchange="spe.checkSampleItem($('spe.dryTubeTaken'), $('spe.glycemiaTest'))"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.creatinineTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                                property="ProjectData.creatinineTest"
                                styleId="spe.creatinineTest"
                                onchange="spe.checkSampleItem($('spe.dryTubeTaken'), $('spe.creatinineTest'))"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.transaminaseTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.transaminaseTest"
                           styleId="spe.transaminaseTest"
                           onchange="spe.checkSampleItem($('spe.dryTubeTaken'), $('spe.transaminaseTest'))" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.transaminaseALTLTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.transaminaseALTLTest"
                           styleId="spe.transaminaseALTLTest"
                           onchange="spe.checkSampleItem($('spe.dryTubeTaken'), $('spe.transaminaseALTLTest'))" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.transaminaseASTLTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.transaminaseASTLTest"
                           styleId="spe.transaminaseASTLTest"
                           onchange="spe.checkSampleItem($('spe.dryTubeTaken'), $('spe.transaminaseASTLTest'))" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="2" class="sectionTitle">
                    <bean:message  key="sample.entry.project.title.edtaTube"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.nfsTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.nfsTest"
                           styleId="spe.nfsTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.nfsTest'))" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.gbTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.gbTest"
                           styleId="spe.gbTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.gbTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.lymphTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.lymphTest"
                           styleId="spe.lymphTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.lymphTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.monoTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.monoTest"
                           styleId="spe.monoTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.monoTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.eoTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.eoTest"
                           styleId="spe.eoTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.eoTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.basoTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.basoTest"
                           styleId="spe.basoTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.basoTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.grTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.grTest"
                           styleId="spe.grTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.grTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.hbTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.hbTest"
                           styleId="spe.hbTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.hbTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.hctTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.hctTest"
                           styleId="spe.hctTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.hctTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.vgmTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.vgmTest"
                           styleId="spe.vgmTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.vgmTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.tcmhTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.tcmhTest"
                           styleId="spe.tcmhTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.tcmhTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ccmhTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.ccmhTest"
                           styleId="spe.ccmhTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.ccmhTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.plqTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.plqTest"
                           styleId="spe.plqTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.plqTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.cd4cd8Test" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.cd4cd8Test"
                           styleId="spe.cd4cd8Test"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.cd4cd8Test'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.cd3CountTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.cd3CountTest"
                           styleId="spe.cd3CountTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.cd3CountTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.cd4CountTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                           property="ProjectData.cd4CountTest"
                           styleId="spe.cd4CountTest"
                           onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.cd4CountTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="2" class="sectionTitle">
                    <bean:message  key="sample.entry.project.title.otherTests" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.dnaPCR" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                       property="ProjectData.dnaPCR"
                       styleId="spe.dnaPCR"
                       onchange="spe.checkSampleItem($('spe.dbsTaken'), $('spe.dnaPCR'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.viralLoadTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                       property="ProjectData.viralLoadTest"
                       styleId="spe.viralLoadTest"
                       onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.viralLoadTest'));" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><bean:message key="sample.entry.project.ARV.genotypingTest" /></td>
                <td>
                    <html:checkbox name="<%=formName%>"
                       property="ProjectData.genotypingTest"
                       styleId="spe.genotypingTest"
                       onchange="spe.checkSampleItem($('spe.edtaTubeTaken'), $('spe.genotypingTest'));" />
                </td>
            </tr>
            <tr><td colspan="6"><hr/></td></tr>
            <tr id="spe.underInvestigationRow">
                <td class="required"></td>
                <td>
                    <bean:message key="patient.project.underInvestigation" />
                </td>
                <td>
                    <html:select name="<%=formName%>"
                    property="observations.underInvestigation" onchange="makeDirty();compareAllObservationHistoryFields(true)"
                    styleId="spe.underInvestigation">
                    <app:optionsCollection name="<%=formName%>"
                        property="dictionaryLists.YES_NO.list" label="localizedName"
                        value="id" />
                    </html:select>
                </td>
            </tr>
            <tr id="spe.underInvestigationCommentRow">
                <td class="required"></td>
                <td>
                    <bean:message key="patient.project.underInvestigationComment" />
                </td>
                <td colspan="3">
                    <app:text name="<%=formName%>" property="ProjectData.underInvestigationNote" maxlength="1000" size="80"
                        onchange="makeDirty();" styleId="spe.underInvestigationComment" />
                </td>
            </tr>
    </table>
</div>
</div>
<script type="text/javascript" language="JavaScript1.2">
    // On load using the built in feature of OpenElis pages onLoad
/**
 * A list of answers that equate to yes in certain lists when comparing (cross check or 2nd entry for a match).
 */
yesesInDiseases = [
     <%= us.mn.state.health.lims.dictionary.ObservationHistoryList.YES_NO.getList().get(0).getId() %>,
     <%= us.mn.state.health.lims.dictionary.ObservationHistoryList.YES_NO_UNKNOWN.getList().get(0).getId() %>
     ];


function ArvInitialProjectChecker() {
    this.idPre = "iarv.";

    this.checkAllSampleItemFields = function () {
        this.checkSampleItem($("iarv.dryTubeTaken"));
        this.checkSampleItem($("iarv.edtaTubeTaken"));
        this.checkSampleItem($('iarv.dryTubeTaken'), $('iarv.serologyHIVTest'));
        this.checkSampleItem($('iarv.dryTubeTaken'), $('iarv.glycemiaTest'));
        this.checkSampleItem($('iarv.dryTubeTaken'), $('iarv.creatinineTest'));
        this.checkSampleItem($('iarv.dryTubeTaken'), $('iarv.transaminaseTest'));
        this.checkSampleItem($('iarv.edtaTubeTaken'), $('iarv.nfsTest'));
        this.checkSampleItem($('iarv.edtaTubeTaken'), $('iarv.cd4cd8Test'));
        this.checkSampleItem($('iarv.edtaTubeTaken'), $('iarv.viralLoadTest'));
        this.checkSampleItem($('iarv.edtaTubeTaken'), $('iarv.genotypingTest'));
    }
}
ArvInitialProjectChecker.prototype = new BaseProjectChecker();
iarv = new ArvInitialProjectChecker();

function ArvFollowupProjectChecker() {

    this.idPre = "farv.";

    this.checkAllSampleItemFields = function() {
        farv.checkSampleItem($('farv.dryTubeTaken'));
        farv.checkSampleItem($('farv.edtaTubeTaken'));
        farv.checkSampleItem($('farv.dryTubeTaken'), $('farv.serologyHIVTest'));
        farv.checkSampleItem($('farv.dryTubeTaken'), $('farv.glycemiaTest'));
        farv.checkSampleItem($('farv.dryTubeTaken'), $('farv.creatinineTest'));
        farv.checkSampleItem($('farv.dryTubeTaken'), $('farv.transaminaseTest'));
        farv.checkSampleItem($('farv.edtaTubeTaken'), $('farv.nfsTest'));
        farv.checkSampleItem($('farv.edtaTubeTaken'), $('farv.cd4cd8Test'));
        farv.checkSampleItem($('farv.edtaTubeTaken'), $('farv.viralLoadTest'));
        farv.checkSampleItem($('farv.edtaTubeTaken'), $('farv.genotypingTest'));
    }
}

ArvFollowupProjectChecker.prototype = new BaseProjectChecker();
/// the object which knows about Followup ARV questions and which fields to show etc.
farv = new ArvFollowupProjectChecker();

function RtnProjectChecker() {
    this.idPre = "rtn.";

    this.checkAllSampleFields = function (blanksAllowed) {
        // this.checkCenterName(blanksAllowed);
        // this.checkCenterCode(blanksAllowed);
        this.checkInterviewDate(blanksAllowed);
        this.checkReceivedDate(blanksAllowed);
        //var receivedTimeField = $(this.idPre + "receivedTimeForDisplay");
        //compareSampleField( receivedTimeField.id, false, blanksAllowed);
        //var interviewTimeField = $(this.idPre + "interviewTime");
        //compareSampleField( interviewTimeField.id, false, blanksAllowed, "collectionTimeForDisplay");
        this.checkInterviewTime(true);
        this.checkReceivedTime(true);
    }

    this.checkAllSampleItemFields = function () {
        this.checkSampleItem($("rtn.dryTubeTaken"));
        this.checkSampleItem($('rtn.dryTubeTaken'), $('rtn.serologyHIVTest'));
        // TODO PAHill list ALL sampleItem and Test fields
    }
}

RtnProjectChecker.prototype = new BaseProjectChecker();
rtn = new RtnProjectChecker();

function IndProjectChecker() {
    this.idPre = "ind.";

    this.checkAllSampleFields = function (blanksAllowed) {
        // this.checkCenterName(blanksAllowed);
        this.checkCenterCode(blanksAllowed);
        this.checkInterviewDate(blanksAllowed);
        this.checkReceivedDate(blanksAllowed);
        this.checkInterviewTime(true);
        this.checkReceivedTime(true);
    }

    this.checkAllSampleItemFields = function () {
        ind.checkSampleItem($('ind.dryTubeTaken'));
        ind.checkSampleItem($('ind.dryTubeTaken'), $('ind.serologyHIVTest'));
    }

    this.checkAllSubjectFields = function (blanksAllowed, validateSubjectNumber) {
        this.checkAllSubjectFieldsBasic(blanksAllowed, validateSubjectNumber);
        this.checkPatientField('address', blanksAllowed, 'street');
        this.checkPatientField('phoneNumber', blanksAllowed);
        this.checkPatientField('faxNumber', blanksAllowed);
        this.checkPatientField('email', blanksAllowed);
    }
}
IndProjectChecker.prototype = new BaseProjectChecker();
ind = new IndProjectChecker();

function SpeProjectChecker() {
    this.idPre = "spe."

    this.checkAllSampleFields = function (blanksAllowed) {
        // this.checkCenterName(blanksAllowed);
        // this.checkCenterCode(blanksAllowed);
        this.checkInterviewDate(blanksAllowed);
        this.checkReceivedDate(blanksAllowed);
        this.checkInterviewTime(true);
        this.checkReceivedTime(true);
    }

    this.checkAllSampleItemFields = function () {
    }
}
SpeProjectChecker.prototype = new BaseProjectChecker();
spe = new SpeProjectChecker();

function pageOnLoad(){
    initializeStudySelection();
    studies.initializeProjectChecker();
    projectChecker == null || projectChecker.refresh(); 
}
</script>
