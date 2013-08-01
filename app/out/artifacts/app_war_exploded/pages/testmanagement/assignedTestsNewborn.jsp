<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date, java.util.Locale,
	us.mn.state.health.lims.common.action.IActionConstants, 
	us.mn.state.health.lims.common.util.SystemConfiguration" %>  
	
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>


<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<bean:define id="newbornDomain" value='<%= SystemConfiguration.getInstance().getHumanDomain() %>' />


<html:hidden property="selectedTestIds" name="<%=formName%>" />
<html:hidden property="selectedTestNames" name="<%=formName%>" />
<html:hidden property="editOption" name="<%=formName%>" />
<html:hidden property="testsAddOption" name="<%=formName%>" />
<html:hidden property="testsCancelOption" name="<%=formName%>" />
<html:hidden property="amendMode" name="<%=formName%>" />
<html:hidden property="qaEventsAllowEdit" name="<%=formName%>" />

<%!

Locale locale = null;
String errorNoTestsSelected = "";
%>
<%
    String saveDisabled = (String)request.getAttribute(IActionConstants.SAVE_DISABLED);	
    String addDisabled = (String)request.getAttribute(IActionConstants.ADD_DISABLED);
    String cancelDisabled = (String)request.getAttribute(IActionConstants.CANCEL_DISABLED);	
    
    locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
    errorNoTestsSelected =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "testmanagement.error.no.tests.selected");
  
    String accnNumb = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"quick.entry.accession.number");
    String quickEntry = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"quick.entry.title");				
			
    String	errorMessageAccessionNumber = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"errors.invalid", accnNumb);
    String	errorMessageLabelPrinted = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale,"error.testmanagement.entrystatus", quickEntry);                                       
%>


<script language="JavaScript1.2">
//bugzilla 2041 (moved the button logic to a function in order to prepend a check for an invalid accession number
function setButtons(accessionNumberInvalid) {
    var thisForm = document.forms[0];

    if (thisForm.oldAssignedTests.options.length < 5)
    {
    	thisForm.oldAssignedTests.size = thisForm.oldAssignedTests.options.length;
    	thisForm.oldAssignedTests.visibility = 'visible';
    }
    else
    {
    	thisForm.oldAssignedTests.size = 0;
    	thisForm.oldAssignedTests.visibility = 'none';
    }
    
    if (accessionNumberInvalid) {
        thisForm.editSample.disabled = true;
        thisForm.qaEvents.disabled = true;
        thisForm.addTest.disabled = true;
        thisForm.save.disabled = true;
        thisForm.deactivate.disabled = true;
        return;   
    } else {
        //initialize them to enabled
        thisForm.editSample.disabled = false;
        thisForm.qaEvents.disabled = false;
        thisForm.addTest.disabled = false;
        thisForm.save.disabled = false;
    }
    if (thisForm.editOption.value.toLowerCase() != 'true'){
    	thisForm.editSample.disabled = true;
    }   
    
    //bugzilla 2028
    if (thisForm.qaEventsAllowEdit.value.toLowerCase() != 'true'){
    	thisForm.qaEvents.disabled = true;
    }
    
    if (
      (thisForm.accessionNumber.value == '')
        || 
      (thisForm.testsAddOption.value.toLowerCase() != 'true') 
        ){
    	thisForm.addTest.disabled = true;
    	thisForm.save.disabled = true;
    } 
    if (
      (thisForm.accessionNumber.value == '')
        || 
      (thisForm.testsCancelOption.value.toLowerCase() != 'true') 
        ){
    	thisForm.deactivate.disabled = true;
     } 
	<%--bugzilla 2177 --%>
    <%
	    if ( Boolean.valueOf(addDisabled).booleanValue() ) { %>
	        thisForm.addTest.disabled = true; <%
        } 
        if ( Boolean.valueOf(saveDisabled).booleanValue() ) { %>
            thisForm.save.disabled = true;
    <%  }  
        if (Boolean.valueOf(cancelDisabled).booleanValue() ) { %>
            thisForm.deactivate.disabled = true; <%
        }
    %>       
}

function pageOnLoad() {
 //bugzilla 2041 (moved the button logic to a function in order to prepend a check for an invalid accession number
 setButtons(false);
}

function saveItToParentForm(form) {
	setAction(form, 'Update', 'yes', '?ID=');	
	document.forms[0].selectedTestIds.value = '';        
    document.forms[0].selectedTestNames.value = '';  
}

// This is for the ADD TEST functionality
function addTestPopup(form) 
{

	var myfield = document.getElementById("accessionNumber");
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';	
	var hostStr = scheme + "://" + server;
	if (port != 80 && port != 443)
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	var sessionid = '';
	var sessionIndex = form.action.indexOf(';');
	if (sessionIndex >= 0)
	{
		var queryIndex = form.action.indexOf('?');
		var length = form.action.length;
		if (queryIndex > sessionIndex) 
		{
			length = queryIndex;
		}
		sessionid = form.action.substring(sessionIndex,length);
	}
    //standardize request parameter naming
	var href = context+"/TestManagementAddTestPopup.do?accessionNumber="+myfield.value+"&"+sessionid;
	//alert("href "+ href);
	<%--AIS - bugzilla 1775--%>	
	createPopup(href, 880, 500);
}


function editSamplePopup(form) 
{
	var myfield = document.getElementById("accessionNumber");
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';	
	var hostStr = scheme + "://" + server;
	if (port != 80 && port != 443)
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	var sessionid = '';
	var sessionIndex = form.action.indexOf(';');
	if (sessionIndex >= 0)
	{
		var queryIndex = form.action.indexOf('?');
		var length = form.action.length;
		if (queryIndex > sessionIndex) 
		{
			length = queryIndex;
		}
		sessionid = form.action.substring(sessionIndex,length);
	}
    //standardize request parameter naming
	var href = context+"/TestManagementEditSamplePopupNewborn.do?ID=0&accessionNumber="+myfield.value+"&"+sessionid;
	//alert("href "+ href);	
    //bugzilla 1904: increased width for new longer label
	createPopup(href, 1200, 890);
}


//This is for the ADD TEST functionality
function setAddTestResults(addTestForm)
{
	var popupPickListOptions = addTestForm.PickList.options;
	var thisForm = document.forms[0];
    //initialize
    document.forms[0].selectedTestIds.value = '';
    document.forms[0].selectedTestNames.value = '';
    
    thisForm.assignedTests.options.length = 0;
    for (var i = 0; i < popupPickListOptions.length; i++) 
    { 
         document.forms[0].selectedTestIds.value += '<%=idSeparator%>';
         document.forms[0].selectedTestIds.value += popupPickListOptions[i].value;        
		
         document.forms[0].selectedTestNames.value += '<%=idSeparator%>';
         document.forms[0].selectedTestNames.value += popupPickListOptions[i].text;         
         
         thisForm.assignedTests.options[i] = new Option(popupPickListOptions[i].text, popupPickListOptions[i].value);
         //bugzilla 1856
         thisForm.assignedTests.options[i].sortFieldA = popupPickListOptions[i].sortFieldA;
         thisForm.assignedTests.options[i].sortFieldB = popupPickListOptions[i].sortFieldB;
         
    }
    if (thisForm.assignedTests.options.length < 5)
    {
    	thisForm.assignedTests.size = thisForm.assignedTests.options.length;
    	thisForm.assignedTests.visibility = 'visible';
    }
    
    sortOrder = 'sortFieldB';
    sort('assignedTests', null, false);
}

function cancelTests (thisForm) {
    var optLength = thisForm.oldAssignedTests.options.length;
    var options = thisForm.oldAssignedTests.options;
    var selectedTests = "";
    
    if (optLength > 0) {
    
      for (var i = 0; i < optLength; i++) {
        if (options[i].selected) {
          selectedTests += '<%=idSeparator%>';
          selectedTests += options[i].value;      
        }
        
      }
    }

   if (selectedTests != null && selectedTests != '') {
     var param = "?selectedTests=" + selectedTests + '&ID=';
     setAction(window.document.forms[0], 'CancelTests', 'yes', param);
   } else {
     alert("<%=errorNoTestsSelected%>");
   }
}

function getSelectedTestIds()
{
  return document.forms[0].selectedTestIds;
}

function getSelectedTestNames()
{
  return document.forms[0].selectedTestNames;
}

function validateAccessionNumber2() {
     new Ajax.Request (
       'ajaxXML',  //url
       {//options
        method: 'get', //http method
        parameters: 'provider=AccessionNumberValidationProvider&form=' + document.forms[0].name + '&field=accessionNumber&id=' + escape($F("accessionNumber")),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processAccessionNumberValidationSuccess2,
        onFailure:  processFailure
       }
     );
}

function processAccessionNumberValidationSuccess2(xhr) {
	var message = xhr.responseXML.getElementsByTagName("message")[0];
  	var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
  	setAccessionNumberValidationMessage2(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
}

function processFailure(xhr) {
	//ajax call failed
}

function setAccessionNumberValidationMessage2(message, field) {
    idField = $(field);

    if (message == "invalid") { 
        setButtons(true); 
       	alert('<%=errorMessageAccessionNumber%>');
       	document.forms[0].accessionNumber.focus();
    } else if (message == "invalidStatus") {
        setButtons(true);
     	alert('<%=errorMessageLabelPrinted%>');
     	document.forms[0].accessionNumber.focus();
    } else {
        setButtons(false); 
        validateDomain2(field);
    }
}

function validateDomain2(field) {
    new Ajax.Request (
        'ajaxXML',  //url
        {//options
        method: 'get', //http method
        parameters: 'provider=SampleDomainValidationProvider&form=' + document.forms[0].name + '&field=accessionNumber&id=' + escape(document.forms[0].accessionNumber.value) + '&expectedDomain=' + escape(expectedDomain),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processDomainValidationSuccess2,
        onFailure:  processFailure
        }
    ); 
}

function processDomainValidationSuccess2(xhr) {
    var message = xhr.responseXML.getElementsByTagName("message")[0];
    var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
    setDomainValidationMessage2(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
}

function setDomainValidationMessage2(message, field) {   
    if ( message == 'invalid') {        
        setButtons(true);
        alert('Invalid Domain');
        document.forms[0].accessionNumber.focus();
    } else {
        setButtons(false);
        editSamplePopup(window.document.forms[0]);
    }       
}

</script>


<table>
	<tr>
		<td valign="top">
			<bean:message key="testmanagement.assigned.tests" />
			:
		</td>

		<td valign="top">
            <%--bugzilla 1844 use testDisplayValue instead of testName--%>
			<html:select name="<%=formName%>" property="oldAssignedTests" multiple="true" size="4" style="width: 200px">
				<html:optionsCollection name="<%=formName%>" property="tests" label="testDisplayValue" value="id" />
			</html:select>
		</td>

		<td>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</td>
		<td>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</td>

		<td valign="top">
			<bean:message key="testmanagement.additional.tests" />
			:
		</td>
		<td valign="top">
			<select name="assignedTests" id="assignedTests" size="4" multiple="multiple" style="width: 200px" readonly="true">
			</select>
		</td>
        <%--bugzilla 1942 add sample status name to top of screen--%>
    	<td>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</td>
		<td valign="top" align="right">
		    <bean:message key="testmanagement.sample.status" />:
		    &nbsp;<app:write name="<%=formName%>" property="sampleStatus" />
		</td>
	</tr>

	<tr>

		<td>
			&nbsp;
		</td>

		<td>
            <html:button onclick="addTestPopup(window.document.forms[0]);" property="addTest" disabled="<%=Boolean.valueOf(addDisabled).booleanValue()%>">
              <%--bugzilla 2227 amend tests--%>
              <logic:equal name="<%=formName%>" property="amendMode" value="true">
              	 <bean:message key="label.button.editTests.amend" />
              </logic:equal>
              <logic:equal name="<%=formName%>" property="amendMode" value="false">
				<bean:message key="label.button.editTests" />
    		  </logic:equal>
			</html:button>
            &nbsp;
            <%--bugzilla 2300--%>
			<html:button onclick="cancelTests(window.document.forms[0]);" property="deactivate" styleId="deactivate" disabled="<%=Boolean.valueOf(cancelDisabled).booleanValue()%>">
				<bean:message key="label.button.cancelTests" />
			</html:button>
		</td>
 		<td>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</td>
		<td>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</td>


		<td>
			&nbsp;
		</td>

		<td>
			<html:button property="save" onclick="saveItToParentForm(window.document.forms[0]);" disabled="<%=Boolean.valueOf(addDisabled).booleanValue()%>">
			 	<bean:message key="label.button.save" />
			</html:button>
		</td>
	</tr>
</table>


