<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.security.IAuthorizationActionConstants,
	org.apache.struts.action.*,
	org.apache.struts.Globals,
	java.util.Locale,
    us.mn.state.health.lims.common.util.Versioning,
	us.mn.state.health.lims.common.util.SystemConfiguration" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<%--bugzilla 2008--%>
<script language="JavaScript1.2">
    //Do not allow user to use the back button
    function disabledBackButton() {
        javascript:window.history.forward(1);
    }
    disabledBackButton();
</script>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />

<bean:define id="dictType" value='<%= SystemConfiguration.getInstance().getDictionaryType() %>' />

<bean:define id="analyteTypeRequired" value='<%= SystemConfiguration.getInstance().getAnalyteTypeRequired() %>' />
<bean:define id="analyteTypeNotRequired" value='<%= SystemConfiguration.getInstance().getAnalyteTypeNotRequired() %>' />

<bean:define id="analyteNames" name="<%=formName%>" property="selectedAnalyteNames" type="java.util.List" />
<bean:define id="analyteIds" name="<%=formName%>" property="selectedAnalyteIds" type="java.util.List" />
<bean:define id="analyteResultGroups" name="<%=formName%>" property="selectedAnalyteResultGroups" type="java.util.List" />
<bean:define id="analyteTypes" name="<%=formName%>" property="selectedAnalyteTypes" type="java.util.List" />
<bean:define id="analyteIsReportables" name="<%=formName%>" property="selectedAnalyteIsReportables" type="java.util.List" />
<bean:define id="testAnalyteIds" name="<%=formName%>" property="selectedTestAnalyteIds" type="java.util.List" />
<bean:define id="testAnalyteLastupdatedList" name="<%=formName%>" property="testAnalyteLastupdatedList" type="java.util.List" />

<bean:define id="testResultResultGroups" name="<%=formName%>" property="testResultResultGroups" type="java.util.List" />
<bean:define id="testResultResultGroupTypes" name="<%=formName%>" property="testResultResultGroupTypes" type="java.util.List" />
<bean:define id="dictionaryEntryIdList" name="<%=formName%>" property="dictionaryEntryIdList" type="java.util.List" />
<bean:define id="testResultValueList" name="<%=formName%>" property="testResultValueList" type="java.util.List" />
<bean:define id="flagsList" name="<%=formName%>" property="flagsList" type="java.util.List" />
<%--bugzilla 1845 added testResult sortOrder--%>
<bean:define id="sortList" name="<%=formName%>" property="sortList" type="java.util.List" />
<bean:define id="significantDigitsList" name="<%=formName%>" property="significantDigitsList" type="java.util.List" />
<bean:define id="quantLimitList" name="<%=formName%>" property="quantLimitList" type="java.util.List" />
<bean:define id="testResultIdList" name="<%=formName%>" property="testResultIdList" type="java.util.List" />
<bean:define id="testResultLastupdatedList" name="<%=formName%>" property="testResultLastupdatedList" type="java.util.List" />
<%--bugzilla 1994 default analyte type to R if test is reportable--%>
<bean:define id="selectedTest" name="<%=formName%>" property="test" type="us.mn.state.health.lims.test.valueholder.Test" />

<%-- bugzilla 1510 - incorporate more prototype functions:
     - use $() insead of document.getElementById()
     
--%>
<%!

String allowEdits = "true";
String testNotLocked = "false";
String editResultGroupRowsButtonMessage = "";
String errorTestRequired = "";
String errorInvalidAnalyteName = "";
String errorTest = "";
String errorAnalyteName = "";
Locale locale = null;
//bugzilla 1870
String yesOption = "";
String noOption = "";

String path = "";
String basePath = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

if (request.getAttribute(IAuthorizationActionConstants.UPDATE_TESTCOMPONENT_TESTRESULT) != null) {
 testNotLocked = (String)request.getAttribute(IAuthorizationActionConstants.UPDATE_TESTCOMPONENT_TESTRESULT);
}

//bugzilla 1494 externalize messages
locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);

errorTest =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "testanalytetestresult.browse.test");
errorTestRequired =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.required",
                    errorTest  );
errorAnalyteName =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "testanalytetestresult.analyteName");
errorInvalidAnalyteName =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.invalid",
                    errorAnalyteName  );
editResultGroupRowsButtonMessage =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
					"label.button.edit");

yesOption = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "page.default.yes.option");
noOption = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "page.default.no.option");
%>

<script language="JavaScript1.2">
var resultGroupNumber = 0;

function focus() {
 //alert("focus()");
}

function pageOnLoad() {
//load existing data for test
 <logic:iterate id="analName" indexId="ctr" name="analyteNames">
        	addRowToSectionA('<%=analName%>', '<%=analyteIds.get(ctr.intValue())%>' , '<%=analyteResultGroups.get(ctr.intValue())%>', '<%=analyteTypes.get(ctr.intValue())%>', '<%=analyteIsReportables.get(ctr.intValue())%>', '<%=testAnalyteIds.get(ctr.intValue())%>', '<%=testAnalyteLastupdatedList.get(ctr.intValue())%>');
 </logic:iterate>
 
 //create list of total lines per resultgroup
 var savedResultGroup = null;
 var count = 0;
 var resultGroupBucketCounts = new Array();
 var resultGroupBucketCountIndex = 0;
 <logic:iterate id="resultGroup" indexId="ctr" name="testResultResultGroups">
     if (savedResultGroup != null && '<%=resultGroup%>' != savedResultGroup) {
       resultGroupBucketCounts[resultGroupBucketCountIndex++] = count;
       count = 1;
       savedResultGroup = '<%=resultGroup%>';
     } else {
       savedResultGroup = '<%=resultGroup%>';
       count++;
     }
 </logic:iterate>
 resultGroupBucketCounts[resultGroupBucketCountIndex++] = count;
 
 var index = 0;
 var rgIndex = 0;
 <logic:iterate id="resultGroup" indexId="ctr" name="testResultResultGroups">
      if (rgIndex >= resultGroupBucketCounts[index]) {
           index++;
           rgIndex = 0;
      }
      addRowToSectionB(null, '<%=resultGroup%>', '<%=testResultResultGroupTypes.get(ctr.intValue())%>', '<%=testResultValueList.get(ctr.intValue())%>', '<%=dictionaryEntryIdList.get(ctr.intValue())%>', '<%=flagsList.get(ctr.intValue())%>', '<%=sortList.get(ctr.intValue())%>', '<%=significantDigitsList.get(ctr.intValue())%>', '<%=quantLimitList.get(ctr.intValue())%>', rgIndex++, resultGroupBucketCounts[index], '<%=testResultIdList.get(ctr.intValue())%>', '<%=testResultLastupdatedList.get(ctr.intValue())%>');
     
 </logic:iterate>
 
 //display informational message if there are results/reflexes for this test
 var testNotLocked = '<%= (String)request.getAttribute(IAuthorizationActionConstants.UPDATE_TESTCOMPONENT_TESTRESULT) %>';
 var message = '';

if (testNotLocked == "false") {
<%
    String message = "";
	org.apache.struts.util.MessageResources resources = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources();
    message = resources.getMessage(locale,"testanalytetestresult.error.testLocked.line1");
    out.println("message = '" + message + "';");
    message = resources.getMessage(locale,"testanalytetestresult.error.testLocked.line2");
    out.println("message += '" + message + "';");
    message = resources.getMessage(locale,"testanalytetestresult.error.testLocked.line3");
    out.println("message += '" + message + "';");
    message = resources.getMessage(locale,"testanalytetestresult.error.testLocked.line4");
    out.println("message += '" + message + "';");
    message = resources.getMessage(locale,"testanalytetestresult.error.testLocked.line5");
    out.println("message += '" + message + "';");
    message = resources.getMessage(locale,"testanalytetestresult.error.testLocked.line6");
    out.println("message += '" + message + "';");
  
    out.println("alert(message);");
%>

}
}


function validateForm(form) {
  return true;
 //return validateTestAnalyteTestResultForm(form);
}

//bugzilla 2236
function processFailure(xhr) {
  //ajax call failed
}

//bugzilla 2236
function processSuccess(xhr) {
  var message = xhr.responseXML.getElementsByTagName("message")[0];
  var extractedMessage = message.childNodes[0].nodeValue
    if (extractedMessage == 'invalid') {
      confirmSave();
    } else {
      setAction(window.document.forms[0], 'Update', 'yes', '?ID=');
    }
 
}

//bugzilla 2236
function validateData() {

   //check if no analytes
   var section = getSectionA();
   var tbody = section.getElementsByTagName("TBODY")[0];
   if (tbody.innerHTML == "") {
      document.forms[0].hiddenSelectedAnalyteIds.value = '0';
   } else {
      document.forms[0].hiddenSelectedAnalyteIds.value = '1';
   }
   
   //if no analytes or not at least one analyte with result group go to ajax validation to check if any samples already associated with this test
   if (document.forms[0].hiddenSelectedAnalyteIds.value == '0' || document.forms[0].selectedAnalyteResultGroups == null || document.forms[0].selectedAnalyteResultGroups.value == '') {
   	if ($F("selectedTestId") != ""){
	    new Ajax.Request (
                    'ajaxXML',  //url
                    {//options
                     method: 'get', //http method
                     parameters: 'provider=TestAnalyteTestResultValidationProvider&form=testAnalyteTestResultForm&field=selectedTestId&id=' + escape($F("selectedTestId")),      //request parameters
                     //indicator: 'throbbing'
                     onSuccess:  processSuccess,
                     onFailure:  processFailure
                    }
              ); 
	 } 
	}

}

function replaceInputIndex(aName, newIndex) {
    var indexOfOpenBrace = aName.indexOf("[");
    var indexOfCloseBrace = aName.indexOf("]");
    var aNewNameBefore = aName.substring(0, indexOfOpenBrace);
    var aNewNameAfter = aName.substring(indexOfCloseBrace + 1);
    var aNewName = aNewNameBefore + '[' + newIndex +  ']' + aNewNameAfter;
    return aNewName;
}

function getArrayName(aName) {
  var arrayName = aName;
  var i = arrayName.indexOf('[');
  if (i >= 0) {
     arrayName = arrayName.substring(0, i);
  }
  return arrayName;
}

function getSectionA() {
  var section = $('sectionA');
  return section;
}


function getSectionB() {
  var section = $('sectionB');
  return section;
}


function reSortRows(sect, index) {

//bug#1342
 var testNotLocked = '<%= (String)request.getAttribute(IAuthorizationActionConstants.UPDATE_TESTCOMPONENT_TESTRESULT) %>';

 var section = $(sect);
 tbody = section.getElementsByTagName("TBODY")[0];
 var trs = tbody.getElementsByTagName("tr");
  //var newRow = document.createElement("
  for (var i = index; i < trs.length; i++) {
        var row = trs[i];
        var inputs = row.getElementsByTagName("input");
        //var selects = row.getElementsByTagName("select");
        //inputs.concat(selects);
        
        //alert("This is number of inputs in row " + inputs.length);
        var newElement;
        var parent;
        var size;
        var text;
        var prop;
        var value;
        var onClick;
        
        //do the select drop downs separately
        var selects = row.getElementsByTagName("select");
        for (var x = 0; x < selects.length; x++) {
           var aName = selects[x].name;
           var options = selects[x].options;
           //alert("This is options " + options);
           var selectedoption = "";
           for (var y = 0; y < options.length; y++) {
                option = options[y];
                if (option.selected) {
                  selectedoption = y;
                  break;
                }
           }
           var selected = selects[x].options[selectedoption];
           //alert("This is aName " + aName + " selected " + selected.value);  
           var prop = replaceInputIndex(aName, i);
        
           //create select/dropdown for type
		   newElement = document.createElement('select');
		   newElement.setAttribute('name',prop);
		   newElement.setAttribute('indexed', 'true');
           //bug#1342
		   if (testNotLocked == "false" && aName.indexOf("selectedAnalyteTypes[") >0) {
	          newElement.setAttribute('disabled', 'true')
	       } 

           if (aName.indexOf("selectedAnalyteTypes[") >=0) {
             var analyteTypeDropDownOption = document.createElement('option');
	         analyteTypeDropDownOption.setAttribute('value','<%=analyteTypeNotRequired%>');
             analyteTypeDropDownOption.appendChild(document.createTextNode(" "));
	         if (selected.value == '<%=analyteTypeNotRequired%>') {
	               analyteTypeDropDownOption.setAttribute('selected', 'selected');
	         }
	         newElement.appendChild(analyteTypeDropDownOption);
             analyteTypeDropDownOption = document.createElement('option');
	         analyteTypeDropDownOption.setAttribute('value','<%=analyteTypeRequired%>');
             analyteTypeDropDownOption.appendChild(document.createTextNode('<%=analyteTypeRequired%>'));
	         if (selected.value == '<%=analyteTypeRequired%>') {
	              analyteTypeDropDownOption.setAttribute('selected', 'selected');
	         }
	         newElement.appendChild(analyteTypeDropDownOption);
           }
	       
	       if (aName.indexOf("selectedAnalyteIsReportables[") >=0) {
             var analyteIsReportableOption = document.createElement('option');
	         analyteIsReportableOption.setAttribute('value','<%=noOption%>');
             analyteIsReportableOption.appendChild(document.createTextNode('<%=noOption%>'));
	         if (selected.value == '<%=noOption%>') {
	               analyteIsReportableOption.setAttribute('selected', 'selected');
	         }
	         newElement.appendChild(analyteIsReportableOption);

             analyteIsReportableOption = document.createElement('option');
	         analyteIsReportableOption.setAttribute('value','<%=yesOption%>');
             analyteIsReportableOption.appendChild(document.createTextNode('<%=yesOption%>'));
	         if (selected.value == '<%=yesOption%>' || selected.value == 'null') {
	              analyteIsReportableOption.setAttribute('selected', 'selected');
	         }
	         newElement.appendChild(analyteIsReportableOption);
	       }
	       
	       parent = selects[x].parentNode;
           parent.replaceChild(newElement, selects[x]);
           
           
        }
        
        for (var x = 0; x < inputs.length; x++) {
        
           var aName = inputs[x].name;          
           //alert("This is an input element " + aName + " and type " + inputs[x].type);
       
           var aNewName = replaceInputIndex(aName, i);
           
           newElement =  document.createElement(aNewName);  
           
           var type = inputs[x].type;
           prop = inputs[x].name;
           
           //need to replace the index input field  - but not for rowFieldIndex
           if (prop == 'rowFieldIndex') {
              value = i;
              //there are some simple (not indexed) input fields
           } else if (prop == 'rgGrouping'){
              value = inputs[x].value;
           } else  {
              prop = replaceInputIndex(prop, i);
              value = inputs[x].value;
           }
           
           
 
           if (type == 'button') {
                       
              //get function name for onclick
              var indexOfOpenBrace = prop.indexOf('[');
              var functionName = prop.substring(0, indexOfOpenBrace);
              var onClick = functionName + '(\'' + sect + '\', ' + i + ');';
              //AIS - bugzilla 1840			
			  var n = prop.search(/editResultGroupRows/);        
              
              
              //bug#1342 disable all buttons in section A except for resort buttons if test is locked
              if (testNotLocked == "false" && value != 'v' && value != '^' && n == '-1') {
                newElement = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' DISABLED=\"true\" VALUE=\"' + value + '\" ONCLICK=\"' + onClick + '\" />');
              } else {
                newElement = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE=\"' + value + '\" ONCLICK=\"' + onClick + '\" />');
              }
              
           } else if (type == 'hidden' || type == 'text' || type == 'select') {
               size = inputs[x].size;
               
               if (inputs[x].readonly) {
                     newElement = document.createElement('<input type=\"' + type + '\" size=\"' + size + '\" name=\"' + prop + '\" value=\"' + value + '\" indexed=\"true\" readonly >');
               } else {
                     newElement = document.createElement('<input type=\"' + type + '\" size=\"' + size + '\" name=\"' + prop + '\" value=\"' + value + '\" indexed=\"true\">');
               }
               
               //for selectedAnalyteResultGroups we need to re-create the id also 'rgForAnalyte#' 
               if (getArrayName(prop) == 'selectedAnalyteResultGroups') {
                       newElement = document.createElement('<input id=\"' + inputs[x].id + '\" type=\"' + type + '\" size=\"' + size + '\" name=\"' + prop + '\" value=\"' + value + '\" indexed=\"true\" readonly >');
               }
           } 
       
           parent = inputs[x].parentNode;
           parent.replaceChild(newElement, inputs[x]);
           
            
        }
        
        
    }
 //alert("This is tbody after reSort " + tbody.innerHTML);
}


function getNextRowNumber(section) {
 //alert("I am in getNextRowNumber()");
 tbody = section.getElementsByTagName("TBODY")[0];
 var trs = tbody.getElementsByTagName("tr");
 //alert("returning " + trs.length);
 var len = 0;
 for (var i = 0; i < trs.length; i++) {
   var row = trs[i];
   //need to eliminate rows that just have the edit button (section B)
   var inputs = row.getElementsByTagName("input");
   if (inputs.length > 2) {
       len++;
   } 
 }
 return len;
}

function checkTestSelection() {
 //make sure test has been selected first
 var testId = $F("selectedTestId");

 if (testId == '') {
    alert('<%=errorTestRequired%>');
    return false;
 }
 
 return true;
}

function checkNoDuplicateAnalyte() {
 //check if there is a duplicate
 var analyteToAdd = $F("analyte");
 var numberOfRows = getNextRowNumber(getSectionA());
 //alert("numberOfRows = " + numberOfRows);
 var prop;
 if (analyteToAdd == '') {
    return false;
 }
 for (var i = 0; i < numberOfRows; i++) {
    prop = 'selectedAnalyteNames[' + i + ']';
    //alert("This is prop " + $F(prop));
    if ($F(prop) == analyteToAdd) {
        //alert("returning false");
         return false;
    }
 }
 //alert("returning true" );
 return true;
}


function popupAddDictionaryRG(prevWindow, aID, rgType) {
  //alert("This is popupAddDictionaryRG with rgType " + rgType); 
  var form = document.forms[0];
   
    //if  no errors otherwise on page -> go to add test popup
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';
	
	
	var hostStr = scheme + "://" + server;
	if ( port != 80 && port != 443 )
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	 var sessionid = '';

	 var sessionIndex = form.action.indexOf(';');
	 if(sessionIndex >= 0){
		 var queryIndex = form.action.indexOf('?');
		 var length = form.action.length;
		 if (queryIndex > sessionIndex) {
		 	length = queryIndex;
		 }
		 sessionid = form.action.substring(sessionIndex,length);
	 }
	
	var href = context + "/TestAnalyteTestResultAddDictionaryRGPopup.do" + sessionid;
	
    var parameters = "?aID=" +  aID + "&rgType=" + rgType;
    href +=  parameters;
    //alert("href "+ href);

    prevWindow.close();
	createPopup( href, 1100, 450);
}

function popupAddNonDictionaryRG(prevWindow, aID, rgType) {
//alert("This is popupAddNonDictionaryRG with rgType " + rgType);
  var form = document.forms[0];
   
    //if  no errors otherwise on page -> go to add test popup
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';
	
	
	var hostStr = scheme + "://" + server;
	if ( port != 80 && port != 443 )
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	 var sessionid = '';

	 var sessionIndex = form.action.indexOf(';');
	 if(sessionIndex >= 0){
		 var queryIndex = form.action.indexOf('?');
		 var length = form.action.length;
		 if (queryIndex > sessionIndex) {
		 	length = queryIndex;
		 }
		 sessionid = form.action.substring(sessionIndex,length);
	 }
	
	var href = context + "/TestAnalyteTestResultAddNonDictionaryRGPopup.do" + sessionid;
	
    var parameters = "?aID=" +  aID + "&rgType=" + rgType;
    href +=  parameters;
    //alert("href "+ href);

    prevWindow.close();
	createPopup( href, 1100, 450);
}

function popupEditDictionaryRG(rgType, rgNum) {
  //alert("This is popupEditDictionaryRG with rgType " + rgType); 
  var form = document.forms[0];
   
    //if  no errors otherwise on page -> go to add test popup
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';
	
	
	var hostStr = scheme + "://" + server;
	if ( port != 80 && port != 443 )
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	 var sessionid = '';

	 var sessionIndex = form.action.indexOf(';');
	 if(sessionIndex >= 0){
		 var queryIndex = form.action.indexOf('?');
		 var length = form.action.length;
		 if (queryIndex > sessionIndex) {
		 	length = queryIndex;
		 }
		 sessionid = form.action.substring(sessionIndex,length);
	 }
	
	var href = context + "/TestAnalyteTestResultEditDictionaryRGPopup.do" + sessionid;
	
	var updateTestComponentTestResult = '<%= (String)request.getAttribute(IAuthorizationActionConstants.UPDATE_TESTCOMPONENT_TESTRESULT) %>'
   
    var parameters = "?rgNum=" +  rgNum + "&rgType=" + rgType + "&updateTestComponentTestResult=" + updateTestComponentTestResult;
    href +=  parameters;
    //alert("href "+ href);

	createPopup( href, 1100, 450);
}

function popupEditNonDictionaryRG(rgType, rgNum) {
//alert("This is popupEditNonDictionaryRG with rgType " + rgType);
  var form = document.forms[0];
   
    //if  no errors otherwise on page -> go to add test popup
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';
	
	
	var hostStr = scheme + "://" + server;
	if ( port != 80 && port != 443 )
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	 var sessionid = '';

	 var sessionIndex = form.action.indexOf(';');
	 if(sessionIndex >= 0){
		 var queryIndex = form.action.indexOf('?');
		 var length = form.action.length;
		 if (queryIndex > sessionIndex) {
		 	length = queryIndex;
		 }
		 sessionid = form.action.substring(sessionIndex,length);
	 }
	
	var href = context + "/TestAnalyteTestResultEditNonDictionaryRGPopup.do" + sessionid;
	
	
    var updateTestComponentTestResult = '<%= (String)request.getAttribute(IAuthorizationActionConstants.UPDATE_TESTCOMPONENT_TESTRESULT) %>'
   
	
    var parameters = "?rgNum=" +  rgNum + "&rgType=" + rgType + "&updateTestComponentTestResult=" + updateTestComponentTestResult;
    href +=  parameters;
    //alert("href "+ href);

  	createPopup( href, 1100, 450);
}

function disassociateResultGroupFromAnalytes(rgNum) {
  var sect = 'sectionA';
  var section = $(sect);
  var tbody = section.getElementsByTagName("TBODY")[0];
  var trs = tbody.getElementsByTagName("tr");
    
  for (var i = 0; i < trs.length; i++) {
     var inputs = trs[i].getElementsByTagName("input");
     for (var x = 0; x < inputs.length; x++) {
       if (getArrayName(inputs[x].name) == 'selectedAnalyteResultGroups') {
         //if this is assigned to rgNum then blank it out because this result group no longer exists
        if (inputs[x].value == rgNum) {
           inputs[x].value = '';
        }
        break;
       }
       
     }
   }

}

function assignRGPopup(sect, index) {
  //alert("I am in assignRG");
  var form = document.forms[0];
  var section = $(sect);
  var prop = 'selectedAnalyteIds[' + index + ']';
  var analyteIdToAssignResult = $F(prop);
 
  
    //if  no errors otherwise on page -> go to add test popup
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';
	
	
	var hostStr = scheme + "://" + server;
	if ( port != 80 && port != 443 )
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	 var sessionid = '';

	 var sessionIndex = form.action.indexOf(';');
	 if(sessionIndex >= 0){
		 var queryIndex = form.action.indexOf('?');
		 var length = form.action.length;
		 if (queryIndex > sessionIndex) {
		 	length = queryIndex;
		 }
		 sessionid = form.action.substring(sessionIndex,length);
	 }
	

	var href = context + "/TestAnalyteTestResultAssignRGPopup.do" + sessionid;
	
    var parameters = "?aID=" + analyteIdToAssignResult;
    href +=  parameters;
    //alert("href "+ href);
	
	createPopup( href, 1100, 450 );

}

function addRGPopup(sect, index) {

  var form = document.forms[0];
  var section = $(sect);
  var prop = 'selectedAnalyteIds[' + index + ']';
  var analyteIdToAddResult = $F(prop);
 
  //if (checkNoDuplicateResult(section, analyteIdToAddResult)) {
       //alert("returned true");
  //}
   
    //if  no errors otherwise on page -> go to add test popup
	var context = '<%= request.getContextPath() %>';
	var server = '<%= request.getServerName() %>';
	var port = '<%= request.getServerPort() %>';
	var scheme = '<%= request.getScheme() %>';
	
	
	var hostStr = scheme + "://" + server;
	if ( port != 80 && port != 443 )
	{
		hostStr = hostStr + ":" + port;
	}
	hostStr = hostStr + context;

	// Get the sessionID
	 var sessionid = '';

	 var sessionIndex = form.action.indexOf(';');
	 if(sessionIndex >= 0){
		 var queryIndex = form.action.indexOf('?');
		 var length = form.action.length;
		 if (queryIndex > sessionIndex) {
		 	length = queryIndex;
		 }
		 sessionid = form.action.substring(sessionIndex,length);
	 }
	
	var href = context + "/TestAnalyteTestResultAddRGPopup.do" + sessionid;
	
    var parameters = "?aID=" + analyteIdToAddResult;
    href +=  parameters;
    //alert("href "+ href);
	
	createPopup( href, 550, 300 );
}
 
function buttonUp(sect, index) {

 var section = $(sect);
 tbody = section.getElementsByTagName("TBODY")[0];
 var trs = tbody.getElementsByTagName("tr");
 //alert("I am in buttonUp " + sect + " " + index + " length of trs " + trs.length);
 
 if (index > 0) {
   trs[index-1].swapNode(trs[index]);
   //alert("This is tbody after buttonUp " + tbody.innerHTML);
   reSortRows(sect, index -1);
 
 }
}

function buttonDown(sect, index) {

 var section = $(sect);
 tbody = section.getElementsByTagName("TBODY")[0];
 var trs = tbody.getElementsByTagName("tr");
  //alert("I am in buttonDown " + sect + " " + index + " length of trs " + trs.length);
 
 if (index < trs.length - 1) {
   trs[index].swapNode(trs[index + 1]);
   //alert("This is tbody after buttonDown " + tbody.innerHTML);
   reSortRows(sect, index);
 
 }
}

function delRow(sect, index) {
 //alert("I am in delRwo with " + sect + " " + index);
  var section = $(sect);
  tbody = section.getElementsByTagName("TBODY")[0];
  var trs = tbody.getElementsByTagName("tr");
  
  var row = trs[index];
  //alert("This is row to remove " + row);
  //tbody.removeChild(row);
  tbody.deleteRow(index);
  //alert("Just removed row");
  //alert("this is tbody " + tbody.innerHTML);
  reSortRows(sect, index);
  //alert("this is tbody " + tbody.innerHTML);
}


function editResultGroupRows(sect, index) {
  var section = $(sect);
  tbody = section.getElementsByTagName("TBODY")[0];
  var trs = tbody.getElementsByTagName("tr");
  var rgType, rgNum;
  var rgTypeFieldName = 'testResultResultGroupTypes[' + index + ']';
  var rgNumFieldName = 'testResultResultGroups[' + index + ']';
  
  
  for (var i = 0; i < trs.length; i++) {
     //find the correct row
     var row = trs[i];
     var inputs = row.getElementsByTagName("input");
     if (inputs != null && inputs.length > 2) {
         if (inputs['rowFieldIndex'] != null && inputs['rowFieldIndex'].value != null) {
               if (inputs['rowFieldIndex'].value == index) {
                rgType = inputs[rgTypeFieldName].value;
                rgNum = inputs[rgNumFieldName].value;
                break;
               }
         }
     }
  }

  
   if (rgType == '<%=dictType%>') {
        popupEditDictionaryRG(rgType, rgNum);
   } else {
        popupEditNonDictionaryRG(rgType, rgNum);
   }
}

 
function addRowToSectionA(analyteToAdd, selectedAnalyteIdToAdd, resultGroup, analyteType, analyteIsReportable, selectedTestAnalyteIdToAdd, testAnalyteLastupdatedToAdd) {
  var sect = 'sectionA';
  var section = $(sect);
  var content, prop, onClick;
  var inputAnalyteResultGroup, inputHiddenAnalyteId, inputHiddenTestAnalyteId, inputAnalyteName, analyteTypeDropDown, analyteIsReportableDropDown, analyteTypeDropDownOption, analyteIsReportableOption, inputHiddenTestAnalyteLastupdated;
  var upButton, downButton, delRowButton, assignRGButton, addRGPopupButton;
  var upButtonText,downButtonText, delRowButtonText, assignRGText, addRGPopupText;
  var td1, td2, td3, td4, td5, td6, td7, td8, td9;
  content = "";
  
  var testNotLocked = '<%= (String)request.getAttribute(IAuthorizationActionConstants.UPDATE_TESTCOMPONENT_TESTRESULT) %>';

     var i = getNextRowNumber(section);   
     tbody = section.getElementsByTagName("TBODY")[0];
     row = document.createElement('<tr>');
     td1 = document.createElement('<td width="5%" >');
     td2 = document.createElement('<td width="66%">');
     //td1.setAttribute("headers", "h1");
     td3 = document.createElement('<td width="5%" >');
     //td2.setAttribute("headers", "h2");    
     td4 = document.createElement('<td width="5%" >');
     td5 = document.createElement('<td width="5%" >');
     td6 = document.createElement('<td width="5%" >');
     td7 = document.createElement('<td width="3%" >');
     td8 = document.createElement('<td width="3%" >');
     td9 = document.createElement('<td width="3%" >');

     
     prop = 'selectedAnalyteResultGroups[' + i + ']';
     inputAnalyteResultGroup = document.createElement('<input id=\"rgForAnalyte' + selectedAnalyteIdToAdd + '\" type=\"text\" name=\"' + prop + '\" value=\"' + resultGroup + '\" indexed=\"true\" size=\"4\" readonly >');
     
     prop = 'selectedAnalyteIds[' + i + ']';
     inputHiddenAnalyteId = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + selectedAnalyteIdToAdd + '\" indexed=\"true\" >');
     
     prop = 'selectedTestAnalyteIds[' + i + ']';
     inputHiddenTestAnalyteId = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + selectedTestAnalyteIdToAdd + '\" indexed=\"true\" >');
     
     prop = 'testAnalyteLastupdatedList[' + i + ']';
     inputHiddenTestAnalyteLastupdated = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + testAnalyteLastupdatedToAdd + '\" indexed=\"true\" >');
     
     
     prop = 'selectedAnalyteNames[' + i + ']';
     inputAnalyteName = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + analyteToAdd + '\" indexed=\"true\" readonly size=\"120\" >');
     
     prop = 'selectedAnalyteTypes[' + i + ']';
     //create select/dropdown for type
     analyteTypeDropDown = document.createElement('select');
     analyteTypeDropDown.setAttribute('name',prop);
	 analyteTypeDropDown.setAttribute('indexed', 'true');
	 if (testNotLocked == "false") {
	   analyteTypeDropDown.setAttribute('disabled', 'true');
	 }
	 
	 prop = 'selectedAnalyteIsReportables[' + i + ']';
     //create select/dropdown for isReportable
     analyteIsReportableDropDown = document.createElement('select');
     analyteIsReportableDropDown.setAttribute('name',prop);
	 analyteIsReportableDropDown.setAttribute('indexed', 'true');
	

     //analyteTypeDropDown = document.createElement('<select name=\"' + prop + '\" indexed=\"true\" >');
     analyteTypeDropDownOption = document.createElement('option');
	 analyteTypeDropDownOption.setAttribute('value','<%=analyteTypeNotRequired%>');
	 analyteTypeDropDownOption.appendChild(document.createTextNode(" "));
	 if (analyteType == '<%=analyteTypeNotRequired%>') {
	 	 analyteTypeDropDownOption.setAttribute('selected', 'selected');
	 }
	 analyteTypeDropDown.appendChild(analyteTypeDropDownOption);
	 
     analyteTypeDropDownOption = document.createElement('option');
	 analyteTypeDropDownOption.setAttribute('value','<%=analyteTypeRequired%>');
	 analyteTypeDropDownOption.appendChild(document.createTextNode('<%=analyteTypeRequired%>'));
	 if (analyteType == '<%=analyteTypeRequired%>') {
	 	 analyteTypeDropDownOption.setAttribute('selected', 'selected');
	 }
	 analyteTypeDropDown.appendChild(analyteTypeDropDownOption);
	
	 //bugzilla 1870
     analyteIsReportableOption = document.createElement('option');
	 analyteIsReportableOption.setAttribute('value','<%=noOption%>');
	 analyteIsReportableOption.appendChild(document.createTextNode('<%=noOption%>'));
	 if (analyteIsReportable == '<%=noOption%>') {
	 	 analyteIsReportableOption.setAttribute('selected', 'selected');
	 }
	 analyteIsReportableDropDown.appendChild(analyteIsReportableOption);

     analyteIsReportableOption = document.createElement('option');
	 analyteIsReportableOption.setAttribute('value','<%=yesOption%>');
	 analyteIsReportableOption.appendChild(document.createTextNode('<%=yesOption%>'));
     if (analyteIsReportable == '<%=yesOption%>' || analyteIsReportable == 'null') {
	 	 analyteIsReportableOption.setAttribute('selected', 'selected');
	 }
	 analyteIsReportableDropDown.appendChild(analyteIsReportableOption);

	 //create assign result group button 
	 prop = 'assignRGPopup[' + i + ']';
	 onClick = 'assignRGPopup(\'' + sect + '\', ' + i + ');';
	 
	 if (testNotLocked == "false") {
       assignRGButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' disabled=\"true\" VALUE="Assign RG" ONCLICK=\"' + onClick + '\" />');
     } else {
       assignRGButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE="Assign RG" ONCLICK=\"' + onClick + '\" />');
     }
     
  	 //create add result group button 
	 prop = 'addRGPopup[' + i + ']';
	 onClick = 'addRGPopup(\'' + sect + '\', ' + i + ');';
	 
	 if (testNotLocked == "false") {
       addRGPopupButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' disabled=\"true\" VALUE="Add RG" ONCLICK=\"' + onClick + '\" />');
     } else {
       addRGPopupButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE="Add RG" ONCLICK=\"' + onClick + '\" />');
     }
	
	 //create up button (changing sortorder)
	 prop = 'buttonUp[' + i + ']';
	 onClick = 'buttonUp(\'' + sect + '\', ' + i + ');';
     upButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE="^" ONCLICK=\"' + onClick + '\" />');

	 
	 //create down button (changing sortorder)
	 prop = 'buttonDown[' + i + ']';
	 onClick = 'buttonDown(\'' + sect + '\', ' + i + ');';
     downButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE="v" ONCLICK=\"' + onClick + '\" />');

	 //create delete row button 
	 prop = 'delRow[' + i + ']';
	 //delRowButton = document.createElement('BUTTON');
	 onClick = 'delRow(\'' + sect + '\', ' + i + ');';
	 
	 if (testNotLocked == "false") {
       delRowButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' disabled=\"true\" VALUE="X" ONCLICK=\"' + onClick + '\" />');
     } else {
       delRowButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE="X" ONCLICK=\"' + onClick + '\" />');
     }
 

     td1.appendChild(inputAnalyteResultGroup);
     td2.appendChild(inputHiddenAnalyteId);
     td2.appendChild(inputHiddenTestAnalyteId);
     td2.appendChild(inputHiddenTestAnalyteLastupdated);
     td2.appendChild(inputAnalyteName);
     td3.appendChild(analyteIsReportableDropDown);
     td4.appendChild(analyteTypeDropDown);
     td5.appendChild(assignRGButton);
     td6.appendChild(addRGPopupButton);
     td7.appendChild(upButton);
     td8.appendChild(downButton);
     td9.appendChild(delRowButton);
  
     row.appendChild(td1);
     row.appendChild(td2);
     row.appendChild(td3);
     row.appendChild(td4);
     row.appendChild(td5);         
     row.appendChild(td6);         
     row.appendChild(td7);    
     row.appendChild(td8); 
     row.appendChild(td9);   
  
     tbody.appendChild(row);
     //alert("This is tbody " + tbody.innerHTML);
}

//coming from popup
function addRowToSectionB(aID, rg, rgType, testResultValue, deId, flag, sortOrder, sd, ql, index, numberOfRows, trId, trLastupdated) {

 //alert("Adding row to section B with rgType " + rgType);
 //alert("I am in addRGFromChild "  + " " + aID);
 //get result group number
 //assign to correct analyte (sectionA)
 //add rows to sectionB from popup
 
  var section = $('sectionB');
  
  var content, prop, onClick, rowSpan;
  var outputResultGroups, hiddenDEIds, hiddenTestResultResultGroupTypes, outputTestResultValues, outputFlags, outputSorts, outputSDs, outputQLs, hiddenTestResultIds, hiddenTestResultLastupdatedList;
  var editResultGroupRowsButton;
  var editResultGroupRowsButtonText = '<%=editResultGroupRowsButtonMessage%>';
  var td1, td2, td3, td4, td5, td6;
  var rowFieldIndex;
  content = "";
  var ALLOW_EDIT = '<%=allowEdits%>';  


     var i = getNextRowNumber(section);   

     tbody = section.getElementsByTagName("TBODY")[0];
     row = document.createElement("<tr>");
     
     rowSpan = numberOfRows;
     if (numberOfRows == 1) {
           rowSpan++;
     }
      
         
     if (index == 0) {
        td1 = document.createElement('<td rowspan=\"' + rowSpan + '\" valign=\"top\" width=\"5%\" >');
     }
   
    
     td2 = document.createElement('<td width=\"75%\"' + ' valign=\"top\" >');
     td3 = document.createElement('<td width=\"5%\"' + ' valign=\"top\" >');
     td4 = document.createElement('<td width=\"5%\"' + ' valign=\"top\" >');
     td5 = document.createElement('<td width=\"5%\"' + ' valign=\"top\" >');
     td6 = document.createElement('<td width=\"5%\"' + ' valign=\"top\" >');
      
           
     prop = 'testResultResultGroups[' + i + ']';
     if (index == 0) {  
        if (rg != null) {
          resultGroupNumber = rg;
        } else {
          resultGroupNumber++;
        }
        outputResultGroups = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + resultGroupNumber + '\" indexed=\"true\" size=\"4\" readonly >');
     } else {
        outputResultGroups = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + resultGroupNumber + '\" indexed=\"true\" size=\"4\" readonly >');
     }
     
     prop = 'testResultResultGroupTypes[' + i + ']';
     hiddenTestResultResultGroupTypes = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + rgType + '\" indexed=\"true\" size=\"4\" readonly >');
     
     prop = 'dictionaryEntryIdList[' + i + ']';
     hiddenDEIds = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + deId + '\" indexed=\"true\">');
     
     prop = 'testResultIdList[' + i + ']';
     hiddenTestResultIds = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + trId + '\" indexed=\"true\">');
     
     prop = 'testResultLastupdatedList[' + i + ']';
     hiddenTestResultLastupdatedList = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + trLastupdated + '\" indexed=\"true\">');
     
     
     prop = 'testResultValueList[' + i + ']';
     outputTestResultValues = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + testResultValue + '\" indexed=\"true\" readonly size=\"150\" >');
     
     prop = 'flagsList[' + i + ']';
     outputFlags = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + flag + '\" indexed=\"true\" readonly size=\"3\" >');
     
     prop = 'sortList[' + i + ']';
     outputSorts = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sortOrder + '\" indexed=\"true\" readonly size=\"3\" >');
 
     prop = 'significantDigitsList[' + i + ']';
     outputSDs = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sd + '\" indexed=\"true\" readonly size=\"1\" >');
     
     prop = 'quantLimitList[' + i + ']';
     outputQLs = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + ql + '\" indexed=\"true\" readonly size=\"1\" >');
     
     rowFieldIndex = document.createElement('<input type=\"hidden\" name=\"rowFieldIndex\" value=\"' + i + '\" indexed=\"true\">');
     
     var belongsToRGGroup = document.createElement('<input type=\"hidden\" name=\"rgGrouping\" value=\"' + resultGroupNumber + '\" >');

    
	 //create edit result group button 
	 prop = 'editResultGroupRows[' + i + ']';
	 //onClick = 'editResultGroupRows(sectionB, ' + i + ', \'' + rgType + '\', \'' + resultGroupNumber + '\');';
     onClick = 'editResultGroupRows(\'sectionB\', ' + i + ');';
     
     if ( ALLOW_EDIT == "false" ) {
        editResultGroupRowsButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE=' + '<%=editResultGroupRowsButtonMessage%>' + ' ONCLICK=\"' + onClick + '\"/>');
     } else {
        editResultGroupRowsButton = document.createElement('&nbsp;');
    }        
 
    if (index == 0) {
     td1.appendChild(outputResultGroups);
     td1.appendChild(document.createElement("<br>"));
     td1.appendChild(editResultGroupRowsButton);
     td1.appendChild(belongsToRGGroup);
    } else {
     td2.appendChild(outputResultGroups);
     td2.appendChild(belongsToRGGroup);
    }
    
     td2.appendChild(hiddenDEIds);
     td2.appendChild(hiddenTestResultIds);
     td2.appendChild(hiddenTestResultLastupdatedList);
     td2.appendChild(hiddenTestResultResultGroupTypes);
     td2.appendChild(outputTestResultValues);
     td2.appendChild(rowFieldIndex);
     td3.appendChild(outputSorts);
     td4.appendChild(outputFlags);
     td5.appendChild(outputSDs);
     td6.appendChild(outputQLs);
     

     if (index == 0) { 
       row.appendChild(td1);
     }
     row.appendChild(td2);
     row.appendChild(td3);
     row.appendChild(td4);
     row.appendChild(td5);   
     row.appendChild(td6)      
  
      
 
     tbody.appendChild(row); 
     
     //alert("Just appended a row " + tbody.innerHTML);   
     
     if (index == 0 && numberOfRows == 1) {
     //this is empty row for edit button
            row = document.createElement("<tr>");
            var cell = document.createElement("td");
            belongsToRGGroup = document.createElement('<input type=\"hidden\" name=\"rgGrouping\" value=\"' + resultGroupNumber + '\" >');
            cell.appendChild(belongsToRGGroup);
            row.appendChild(cell);
            //row.insertCell(1);
            row.insertCell(2);
            row.insertCell(3);
            row.insertCell(4);
            tbody.appendChild(row);
            //alert("Just appended another row " + tbody.innerHTML);   
       } 
       

    
    //now assign resultgroup to analyte   
    if (aID != null) {
     var rgForAnalyte = $("rgForAnalyte" + aID);
     rgForAnalyte.value = resultGroupNumber;
    }
 
 //alert("After addRowToSectionB " + tbody.innerHTML);
}

function addRowToSectionBForEdit(rgNum, rgType, row, index, numberOfRows) {

  var section = $('sectionB');
  
  var content, prop, onClick, rowSpan;
  var outputResultGroups, hiddenDEIds, hiddenTestResultResultGroupTypes, outputTestResultValues, outputFlags, outputSorts, outputSDs, outputQLs, hiddenTestResultIds, hiddenTestResultLastupdatedList;
  var editResultGroupRowsButton;
  var editResultGroupRowsButtonText = '<%=editResultGroupRowsButtonMessage%>';
  var td1, td2, td3, td4, td5, td6;

  content = "";
  var deId = '', testResultValue = '', flag = '', sortOrder = '', sd = '', ql = '', trId = '', trLastupdated = '';
  
  var inputs = row.getElementsByTagName("input");
  
  prop = 'dictionaryEntryIdList[' + index + ']';
  if (inputs[prop] != null) {
     deId = inputs[prop].value;
  }
  
  prop = 'testResultValueList[' + index + ']';
  if (inputs[prop] != null) {
     testResultValue = inputs[prop].value;
  }
  
  prop = 'testResultIdList[' + index + ']';
  if (inputs[prop] != null) {
     trId = inputs[prop].value;
  }
  
  prop = 'testResultLastupdatedList[' + index + ']';
  if (inputs[prop] != null) {
     trLastupdated = inputs[prop].value;
  }
  
  prop = 'dictionaryEntryList[' + index + ']';
  if (inputs[prop] != null) {
     testResultValue = inputs[prop].value;
  }
  prop = 'flagsList[' + index + ']';
  if (inputs[prop] != null) {
     flag = inputs[prop].value;
  }
  prop = 'sortList[' + index + ']';
  if (inputs[prop] != null) {
     sortOrder = inputs[prop].value;
  }
  prop = 'significantDigitsList[' + index + ']';
  if (inputs[prop] != null) {
     sd = inputs[prop].value;
  }
  prop = 'quantLimitList[' + index + ']';
  if (inputs[prop] != null) {
     ql = inputs[prop].value;
  }

  var i = getNextRowNumber(section);   

  tbody = section.getElementsByTagName("TBODY")[0];
  row = document.createElement("<tr>");
     
  rowSpan = numberOfRows;
  if (numberOfRows == 1) {
       rowSpan++;
   }
      
         
  if (index == 0) {
      td1 = document.createElement('<td rowspan=\"' + rowSpan + '\" valign=\"top\" width=\"5%\" >');
   }
   
    
   td2 = document.createElement('<td width=\"75%\"' + ' valign=\"top\" >');
   td3 = document.createElement('<td width=\"5%\"' + ' valign=\"top\" >');
   td4 = document.createElement('<td width=\"5%\"' + ' valign=\"top\" >');
   td5 = document.createElement('<td width=\"5%\"' + ' valign=\"top\" >');
   td6 = document.createElement('<td width=\"5%\"' + ' valign=\"top\" >');
      
           
     prop = 'testResultResultGroups[' + i + ']';
     if (index == 0) {   
        outputResultGroups = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + rgNum + '\" indexed=\"true\" size=\"4\" readonly >');
     } else {
        outputResultGroups = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + rgNum + '\" indexed=\"true\" size=\"4\" readonly >');
     }
     
     prop = 'testResultResultGroupTypes[' + i + ']';
     hiddenTestResultResultGroupTypes = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + rgType + '\" indexed=\"true\" size=\"4\" readonly >');
     
     prop = 'dictionaryEntryIdList[' + i + ']';
     hiddenDEIds = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + deId + '\" indexed=\"true\">');
     
     prop = 'testResultValueList[' + i + ']';
     outputTestResultValues = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + testResultValue + '\" indexed=\"true\" readonly size=\"150\" >');
     
     prop = 'flagsList[' + i + ']';
     outputFlags = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + flag + '\" indexed=\"true\" readonly size=\"3\" >');
     
     prop = 'sortList[' + i + ']';
     outputSorts = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sortOrder + '\" indexed=\"true\" readonly size=\"3\" >');
     
     prop = 'significantDigitsList[' + i + ']';
     outputSDs = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sd + '\" indexed=\"true\" readonly size=\"1\" >');
     
     prop = 'quantLimitList[' + i + ']';
     outputQLs = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + ql + '\" indexed=\"true\" readonly size=\"1\" >');
     
     rowFieldIndex = document.createElement('<input type=\"hidden\" name=\"rowFieldIndex\" value=\"' + i + '\" indexed=\"true\">');
     
     var belongsToRGGroup = document.createElement('<input type=\"hidden\" name=\"rgGrouping\" value=\"' + rgNum + '\" >');

     prop = 'testResultIdList[' + i + ']';
     hiddenTestResultIds = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + trId + '\" indexed=\"true\">');
     
     prop = 'testResultLastupdatedList[' + i + ']';
     hiddenTestResultLastupdatedList = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + trLastupdated + '\" indexed=\"true\">');

	 //create edit result group button 
	 prop = 'editResultGroupRows[' + i + ']';
     onClick = 'editResultGroupRows(\'sectionB\', ' + i + ');';
     editResultGroupRowsButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE=' + '<%=editResultGroupRowsButtonMessage%>' + ' ONCLICK=\"' + onClick + '\"/>');
  
 
 //alert("if index is 0 then adding edit button " + index);
    if (index == 0) {
     td1.appendChild(outputResultGroups);
     td1.appendChild(document.createElement("<br>"));
     td1.appendChild(editResultGroupRowsButton);
    } else {
     td2.appendChild(outputResultGroups);
    }
    
     td2.appendChild(hiddenDEIds);
     td2.appendChild(hiddenTestResultIds);
     td2.appendChild(hiddenTestResultLastupdatedList);
     td2.appendChild(hiddenTestResultResultGroupTypes);
     td2.appendChild(outputTestResultValues);
     td2.appendChild(rowFieldIndex);
     td2.appendChild(belongsToRGGroup);
     td3.appendChild(outputSorts);
     td4.appendChild(outputFlags);
     td5.appendChild(outputSDs);
     td6.appendChild(outputQLs);
     
     

     if (index == 0) { 
       row.appendChild(td1);
     }
     row.appendChild(td2);
     row.appendChild(td3);
     row.appendChild(td4);
     row.appendChild(td5); 
     row.appendChild(td6);         
  
      
 
       tbody.appendChild(row);    
     
     if (index == 0 && numberOfRows == 1) {
     //this is empty row for edit button
            row = document.createElement("<tr>");
            
            var cell = document.createElement("td");
            belongsToRGGroup = document.createElement('<input type=\"hidden\" name=\"rgGrouping\" value=\"' + rgNum + '\" >');
            cell.appendChild(belongsToRGGroup);
            row.appendChild(cell);
            
            //row.insertCell(1);
            row.insertCell(2);
            row.insertCell(3);
            row.insertCell(4);
            tbody.appendChild(row);
       } 
   //alert("This is tbody " + tbody.innerHTML);
   
}

function addRowToSectionBForEdit2(aID, rgType, testResultValue, deId, flag, sortOrder, sd, ql, index, numberOfRows, rgGroup, trId, trLastupdated) {

 //alert("Adding row to section B with rgType " + rgType);
 //alert("I am in addRGFromChild "  + " " + aID);
 //get result group number
 //assign to correct analyte (sectionA)
 //add rows to sectionB from popup
 
  var section = $('sectionB');
  
  var content, prop, onClick, rowSpan;
  var outputResultGroups, hiddenDEIds, hiddenTestResultResultGroupTypes, outputTestResultValues, outputFlags, outputSorts, outputSDs, outputQLs, hiddenTestResultIds, hiddenTestResultLastupdatedList;
  var editResultGroupRowsButton;
  var editResultGroupRowsButtonText = '<%=editResultGroupRowsButtonMessage%>';
  var td1, td2, td3, td4, td5, td6;
  var rowFieldIndex;
  content = "";
  


     var i = getNextRowNumber(section);   

     tbody = section.getElementsByTagName("TBODY")[0];
     row = document.createElement("<tr>");
     
     rowSpan = numberOfRows;
     if (numberOfRows == 1) {
           rowSpan++;
     }
      
         
     if (index == 0) {
        td1 = document.createElement('<td rowspan=\"' + rowSpan + '\" valign=\"top\" width=\"5%\" >');
     }
   
    
     td2 = document.createElement('<td width=\"75%\"' + ' valign=\"top\" >');
     td3 = document.createElement('<td width=\"5%\"' + ' valign=\"top\" >');
     td4 = document.createElement('<td width=\"5%\"' + ' valign=\"top\" >');
     td5 = document.createElement('<td width=\"5%\"' + ' valign=\"top\" >');
     td6 = document.createElement('<td width=\"5%\"' + ' valign=\"top\" >');
      
           
     prop = 'testResultResultGroups[' + i + ']';
     if (index == 0) {   
        outputResultGroups = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + rgGroup + '\" indexed=\"true\" size=\"4\" readonly >');
     } else {
        outputResultGroups = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + rgGroup + '\" indexed=\"true\" size=\"4\" readonly >');
     }
     
     prop = 'testResultResultGroupTypes[' + i + ']';
     hiddenTestResultResultGroupTypes = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + rgType + '\" indexed=\"true\" size=\"4\" readonly >');
     
     prop = 'dictionaryEntryIdList[' + i + ']';
     hiddenDEIds = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + deId + '\" indexed=\"true\">');
     
     prop = 'testResultIdList[' + i + ']';
     hiddenTestResultIds = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + trId + '\" indexed=\"true\">');
     
     prop = 'testResultLastupdatedList[' + i + ']';
     hiddenTestResultLastupdatedList = document.createElement('<input type=\"hidden\" name=\"' + prop + '\" value=\"' + trLastupdated + '\" indexed=\"true\">');
         
     prop = 'testResultValueList[' + i + ']';
     outputTestResultValues = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + testResultValue + '\" indexed=\"true\" readonly size=\"150\" >');
     
     prop = 'flagsList[' + i + ']';
     outputFlags = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + flag + '\" indexed=\"true\" readonly size=\"3\" >');
     
     prop = 'sortList[' + i + ']';
     outputSorts = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sortOrder + '\" indexed=\"true\" readonly size=\"3\" >');
     
     prop = 'significantDigitsList[' + i + ']';
     outputSDs = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + sd + '\" indexed=\"true\" readonly size=\"1\" >');
     
     prop = 'quantLimitList[' + i + ']';
     outputQLs = document.createElement('<input type=\"text\" name=\"' + prop + '\" value=\"' + ql + '\" indexed=\"true\" readonly size=\"1\" >');
     
     rowFieldIndex = document.createElement('<input type=\"hidden\" name=\"rowFieldIndex\" value=\"' + i + '\" indexed=\"true\">');
     
     var belongsToRGGroup = document.createElement('<input type=\"hidden\" name=\"rgGrouping\" value=\"' + rgGroup + '\" >');

    
	 //create edit result group button 
	 prop = 'editResultGroupRows[' + i + ']';
     onClick = 'editResultGroupRows(\'sectionB\', ' + i + ');';
     editResultGroupRowsButton = document.createElement('<INPUT TYPE="BUTTON" NAME=' + prop + ' VALUE=' + '<%=editResultGroupRowsButtonMessage%>' + ' ONCLICK=\"' + onClick + '\"/>');
  
 
    if (index == 0) {
     td1.appendChild(outputResultGroups);
     td1.appendChild(document.createElement("<br>"));
     td1.appendChild(editResultGroupRowsButton);
     td1.appendChild(belongsToRGGroup);
    } else {
     td2.appendChild(outputResultGroups);
     td2.appendChild(belongsToRGGroup);
    }
    
     td2.appendChild(hiddenDEIds);
     td2.appendChild(hiddenTestResultIds);
     td2.appendChild(hiddenTestResultLastupdatedList);
     td2.appendChild(hiddenTestResultResultGroupTypes);
     td2.appendChild(outputTestResultValues);
     td2.appendChild(rowFieldIndex);
     td3.appendChild(outputSorts);
     td4.appendChild(outputFlags);
     td5.appendChild(outputSDs);
     td6.appendChild(outputQLs);
     

     if (index == 0) { 
       row.appendChild(td1);
     }
     row.appendChild(td2);
     row.appendChild(td3);
     row.appendChild(td4);
     row.appendChild(td5);
     row.appendChild(td6);         
  
      
 
     tbody.appendChild(row); 
     
     //alert("Just appended a row " + tbody.innerHTML);   
     
     if (index == 0 && numberOfRows == 1) {
     //this is empty row for edit button
            row = document.createElement("<tr>");
            var cell = document.createElement("td");
            belongsToRGGroup = document.createElement('<input type=\"hidden\" name=\"rgGrouping\" value=\"' + rgGroup + '\" >');
            cell.appendChild(belongsToRGGroup);
            row.appendChild(cell);
            //row.insertCell(1);
            row.insertCell(2);
            row.insertCell(3);
            row.insertCell(4);
            tbody.appendChild(row);
            //alert("Just appended another row " + tbody.innerHTML);   
       } 
       

}


function replaceResultGroup(rgType, rgNum, trs) {

  if (trs == null || trs.length == 0) {
     disassociateResultGroupFromAnalytes(rgNum);
  }
  
  var section = $('sectionB');
  tbody = section.getElementsByTagName("TBODY")[0];
  thisTrs = tbody.getElementsByTagName("tr");
 
  var insertIndex = 0;
  var foundIndex = false;

  var ind = 0;
  while(true) {
      if (ind > thisTrs.length - 1) {
        break;
      }
      var inputs = thisTrs[ind].getElementsByTagName("input");
      if (inputs['rgGrouping'].value == rgNum) {
          if (!foundIndex) { 
             foundIndex = true;
             insertIndex = ind;
          }
          delRow('sectionB', ind);
      } else {
         ind++
      }
      

      
  }
  
 
   //starting at index insertIndex insert new rows
   // 1. read all rows after point of insertion into save array
   // 2. delete all rows after point of insertion (insertIndex)
   // 3. addRowToSectionBForEdit a) all from edit screen
   //                            b) all from save array (1.)
   // 4. reSortRows()
  


  var saveArray = new Array()
  //1.

  ind = 0;
  var ind2 = 0;
  var arrayOfNames = new Array();
  var arrayOfValues = new Array();
  var rowInfo = new Array();
  while(true) {
      if (ind > thisTrs.length - 1) {
         break;
      }
      if (ind >= insertIndex) {
         var r = thisTrs[ind++];
         var inputs = r.getElementsByTagName("input");
         var len = inputs.length;
         var rgGrp;
         arrayOfNames = new Array();
         arrayOfValues = new Array();
         for (var i = 0; i < len; i++) {
             arrayOfNames[i] = inputs[i].name;
             arrayOfValues[i] = inputs[i].value;
             if (inputs[i].name == 'rgGrouping') {
               rgGrp = inputs[i].value;
             }
         }
         rowInfo = new Array();
         rowInfo[0] = arrayOfNames;
         rowInfo[1] = arrayOfValues;
         rowInfo[2] = rgGrp;
         saveArray[ind2++] = rowInfo;
      } else {
         ind++;
      }
      
  }
  
  
  //2.
  ind = 0;
  while(true) {
      if (ind > thisTrs.length - 1) {
        break;
      }
      if (ind >= insertIndex) {
          delRow('sectionB', ind);
      } else {
         ind++
      }
      
  }
  
 
  
  //3a.
  for (var i = 0 ; i < trs.length; i++) {
     addRowToSectionBForEdit(rgNum, rgType, trs[i], i, trs.length);

  }
  
  
  
  //3b.
  var currentRGNumber;
  var oldRGNumber;
  var arrayOfRGChunks = new Array();
  var arrayOfRGChunksIndex = 0
  var chunks = new Array();
  var chunkIndex = 0;
 
 //sort saveArray information into chunks of resultGroups for reinserting
  for (var i = 0; i < saveArray.length; i++) {
    var rowI = new Array();
    rowI = saveArray[i];
    var aN = rowI[0];
    var aV = rowI[1];
    var currentRGNumber = rowI[2];
    
 
    if (currentRGNumber != oldRGNumber) {
      //this is first row of new rg number
        if (oldRGNumber != null && chunks != null) {
              arrayOfRGChunks[arrayOfRGChunksIndex++] = chunks;
        }
        oldRGNumber = currentRGNumber;
        chunks = new Array();
        chunkIndex = 0;
        chunks[chunkIndex++] = rowI;
 
      } else {
         chunks[chunkIndex++] = rowI;
      }
   
  }
  arrayOfRGChunks[arrayOfRGChunksIndex] = chunks;
  

  for (var i = 0; i < arrayOfRGChunks.length; i++) {
      var chunks = new Array();
      chunks = arrayOfRGChunks[i];
      
      for (var j = 0; j < chunks.length; j++) {
         var rI = new Array();
         rI = chunks[j]; 
         var aOfNames = new Array();
         var aOfValues = new Array();
         aOfNames = rI[0];
         aOfValues = rI[1];
         var rgGrp2 = rI[2]; 
         var index = j;
         var numberOfRows = chunks.length;
         var single = false;
         
         //peek ahead to see if there is an empty row
         if ( numberOfRows == 2 && (j+ 1 ) < chunks.length) {
                var rI2 = new Array();
                rI2 = chunks[j+1];
                var aOfNames2 = new Array();
                aOfNames2 = rI2[0];
                if (aOfNames2.length <= 1) {
                    //this is single row
                    single = true;
                }
         }
         
         
           var aID = null, rgType = null, testResultValue = null, deId = null, trId = null, trLastupdated = null, flag = null, sortOrder = null, sd = null, ql = null, index, numberOfRows;
           for (var x = 0; x < aOfNames.length; x++) {
           
                if (getArrayName(aOfNames[x]) == 'testResultResultGroupTypes') {
                        rgType = aOfValues[x];
                } 
                else if (getArrayName(aOfNames[x]) == 'testResultValueList') {
                        testResultValue = aOfValues[x];
                }
                else if (getArrayName(aOfNames[x]) == 'dictionaryEntryIdList') {
                        deId = aOfValues[x];
                }
                else if (getArrayName(aOfNames[x]) == 'flagsList') {
                        flag = aOfValues[x];
                }
                else if (getArrayName(aOfNames[x]) == 'sortList') {
                        sortOrder = aOfValues[x];
                }
                else if (getArrayName(aOfNames[x]) == 'significantDigitsList') {
                        sd = aOfValues[x];
                }
                else if (getArrayName(aOfNames[x]) == 'quantLimitList') {
                        ql = aOfValues[x];
                }
                else if (getArrayName(aOfNames[x]) == 'testResultIdList') {
                        trId = aOfValues[x];
                }
                else if (getArrayName(aOfNames[x]) == 'testResultLastupdatedList') {
                        trLastupdated = aOfValues[x];
                }

        
           }
           if (single && index == 0) {
             //adjust for Edit button with empty data row --this is the data row above the empty row ..we use rowSpan = 2 here to make space for Edit button
                 numberOfRows  = 1;
           }
           
           if (numberOfRows == 2 && index == 1 && aOfNames.length <= 1) {
             //don't load a null row (this is where there is only 1 data row and Edit button)
           } else {
              addRowToSectionBForEdit2(aID, rgType, testResultValue, deId, flag, sortOrder, sd, ql, index, numberOfRows, rgGrp2, trId, trLastupdated);
           }
          
         }
   
  }
  
  //4.
 reSortRows('sectionB', 0);
  
}

//THE FOLLOWING IS FOR VALIDATION OF AUTOCOMPLETE FIELD
function setAddAnalyteMessage(message, field) {
     //alert("I am in in setMessage - reg. validation - with message and field " + message + " " + field);
     var valField = $(field);
     //bugzilla 1465 problem with entering instead of selecting from dropdown in ajax autocomplete -> id is not retrieved
     var idField = $("targetSelectedAnalyteId");
     var analyteToAdd = $F("analyte");
     var selectedAnalyteIdToAdd = $("targetSelectedAnalyteId").value;
   
     //bugzilla 1994 default analyte type to R if test is reportable
     var isAnalyteRequired = '<%=analyteTypeNotRequired%>';
     if ('<%=selectedTest%>' != null && '<%=selectedTest.getIsReportable()%>' == 'Y') {
         isAnalyteRequired = '<%=analyteTypeRequired%>';
     } else {
         isAnalyteRequired = '<%=analyteTypeNotRequired%>';
     }

     if (message == "invalid") {
        alert('<%=errorInvalidAnalyteName%>');
     } else if (message.length > 5) {
     	 //bugzilla 2200
         if (checkNoDuplicateAnalyte()) {
          //bugzilla 1465 problem with entering instead of selecting from dropdown in ajax autocomplete -> id is not retrieved
          //valField.value = message.substring(5);
          idField.value = message.substring(5);
          selectedAnalyteIdToAdd = $F("targetSelectedAnalyteId");
          addRowToSectionA(analyteToAdd, selectedAnalyteIdToAdd, '', isAnalyteRequired, '<%=yesOption%>', '', '');
        }
     } else if (message == "valid") {           
         if (checkNoDuplicateAnalyte()) {
          addRowToSectionA(analyteToAdd, selectedAnalyteIdToAdd, '', isAnalyteRequired, '<%=yesOption%>', '', '');
         } 
     }

}

function processAddAnalyteSuccess(xhr) {
  var message = xhr.responseXML.getElementsByTagName("message")[0];
  var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
  //alert("I am in parseMessage and this is message, formfield " + message + " " + formfield);
  setAddAnalyteMessage(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
}

function processFailure(xhr) {
  //ajax call failed
}

function validateAnalyteAndAddRowToSectionA() {

     new Ajax.Request (
       'ajaxXML',  //url
       {//options
        method: 'get', //http method
        parameters: 'provider=AnalyteValidationProvider&field=analyte&id=' + escape($F("analyte")),      //request parameters
        //indicator: 'throbbing'
        onSuccess:  processAddAnalyteSuccess,
        onFailure:  processFailure
       }
     );
}



//bugzilla 2236
function confirmSave()
{
  var myWin = createSmallConfirmPopup( "", null , null );
  
   <% 
      
      out.println("var message = null;");
       
      String confirmsave_message =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "testanalytetestresult.message.popup.confirm.save");
         
     out.println("confirmsave_message = '" + confirmsave_message +"';");

     String button1 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.save");
     String button2 = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "label.button.cancel");
                       
     String title = 	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					    locale,
                       "title.popup.confirm.saveandforward");
                       
     String space = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
       		
 %> 

    var href = "<%=basePath%>css/openElisCore.css?ver=<%= Versioning.getBuildNumber() %>";
 
    var strHTML = ""; 
  
    strHTML = '<html><link rel="stylesheet" type="text/css" href="' + href + '" /><head><'; 
    strHTML += 'SCRIPT LANGUAGE="JavaScript">var tmr = 0;function fnHandleFocus(){if(window.opener.document.hasFocus()){window.focus();clearInterval(tmr);tmr = 0;}else{if(tmr == 0)tmr = setInterval("fnHandleFocus()", 500);}}</SCRIPT'; 
    strHTML += '><'; 
    strHTML += 'SCRIPT LANGUAGE="javascript" >'
    strHTML += ' var imp= null; function impor(){imp="norefresh";} '
    strHTML += ' function fcl(){  if(imp!="norefresh") {  opener.reFreshCurWindow(); }}';
     
    strHTML += '  function goToSave(){ ';
    strHTML += ' var reqParms = "?direction=previous&ID=";';
    strHTML += ' window.opener.setAction(window.opener.document.forms[0], "Update", "yes", reqParms);self.close();} ';
      
    strHTML += ' setTimeout("impor()",359999);</SCRIPT'; 
    strHTML += '><title>' + "<%=title%>" + '</title></head>'; 
    strHTML += '<body onBlur="fnHandleFocus();" onload="fnHandleFocus();" ><form name="confirmSaveIt" method="get" action=""><div id="popupBody"><table><tr><td class="popuplistdata">';
    strHTML += confirmsave_message;
  
    strHTML += '<br><center><input type="button"  name="save" value="' + "<%=button1%>" + '" onClick="goToSave();" />';
    strHTML += "<%=space%>";
    strHTML += '<input type="button"  name="cancel" value="' + "<%=button2%>" + '" onClick="window.opener.clearClicked();self.close();" /></center></div>';
    strHTML += '</td></tr></table></form></body></html>'; 

     myWin.document.write(strHTML); 

     myWin.window.document.close(); 

     setTimeout ('myWin.close()', 360000); 
}

</script>


<html:hidden property="hiddenSelectedAnalyteIds" name="<%=formName%>" />
<table width="100%">
<tr> 
    <td colspan="4"> 
      <h2><bean:message key="testanalytetestresult.edit.analyteSection.subtitle" /></h2>
    </td>
</tr>
<tr>
    <td colspan="2"><bean:message key="testanalytetestresult.messageSelectList" /></td>
    <td colspan="2"><%--bean:message key="testanalytetestresult.messageAddAnalyte"/--%>
      <%--app:button onclick="setAction(window.document.forms[0], 'Cancel', 'no', '');" property="cancel" >
	     <bean:message key="testanalytetestresult.label.button.addAnalyte"/>
       </app:button--%>
    </td>
</tr>	
<!-- bugzilla 2105 -->      
<%
    if ( testNotLocked.equals("true") )
        testNotLocked = "false";
    else
        testNotLocked = "true";
%>            
<tr> 
    <td colspan="4" height="19"> 
      <app:text styleId="analyte" size="150" name="<%=formName%>" property="analyte" onkeypress="return noenter()" disabled="<%=Boolean.valueOf(testNotLocked).booleanValue()%>"/> 
      <span id="indicator1" style="display:none;"><img src="<%=basePath%>images/indicator.gif"/></span>
      <input id="targetSelectedAnalyteId" name="targetSelectedAnalyteId" type="hidden" size="30" />
      &nbsp;
      &nbsp;
      <!-- BUG 2105 -->
      <html:button styleClass="button" property="add" onclick="validateAnalyteAndAddRowToSectionA();" disabled="<%=Boolean.valueOf(testNotLocked).booleanValue()%>">
         <bean:message key="label.button.add"/>
      </html:button>
    </td>
    <%--td width="15%" height="19">&nbsp;</td>
    <td width="10%" height="19">&nbsp;</td--%>
</tr>
<tr> 
    <td colspan="2"> 
       &nbsp;
    </td>
    <td colspan="2">&nbsp; </td>
   </tr>
  <tr> 
    <td colspan="4"> 
    </td>
  </tr>
</table>

<table width="100%" border=2">
<%--thead--%>
<tr>
<td id="h1" width="5%"><bean:message key="testanalytetestresult.selectedAnalyteRGs"/></th>
<td id="h2" width="63%"><bean:message key="testanalytetestresult.selectedAnalyteNames"/></th>
<td id="h3" width="5%"><bean:message key="testanalytetestresult.selectedAnalyteIsReportables"/></th>
<td id="h4" width="5%"><bean:message key="testanalytetestresult.selectedAnalyteTypes"/></th>
<td id="h5" width="22%">&nbsp;</th>
</tr>
<%--/thead--%>
</table>

<div class="scrollvertical">
<table id="sectionA" class="blank" width="100%" border="1">
<tbody>
</tbody>
</table>
</div>


<table width="100%">
<tr> 
    <td colspan="5"> 
      <h2><bean:message key="testanalytetestresult.edit.resultSection.subtitle" /></h2>
    </td>
</tr>
</table>
<table width="100%" border=2">
<%--thead--%>
<tr>
<td id="hb1" width="5%"><bean:message key="testanalytetestresult.selectedAnalyteRGs"/></th>
<td id="hb2" width="77%"><bean:message key="testanalytetestresult.edit.testResultSection.resultValue"/></th>
<td id="hb3" width="4%"><bean:message key="testanalytetestresult.edit.testResultSection.sortOrder"/></th>
<td id="hb4" width="5%"><bean:message key="testanalytetestresult.edit.testResultSection.flags"/></th>
<td id="hb5" width="4%"><bean:message key="testanalytetestresult.edit.testResultSection.significantDigits"/></th>
<td id="hb6" width="5%"><bean:message key="testanalytetestresult.edit.testResultSection.quantLimit"/></th>
</tr>
<%--/thead--%>
</table>

<div class="scrollvertical">
<table id="sectionB" class="blank" width="100%" border="1">
<tbody>
</tbody>
</table>
</div>

<%--html:javascript formName="testAnalyteTestResultForm"/--%>

<ajax:autocomplete
  source="analyte"
  target="targetSelectedAnalyteId"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="parentAnalyteName={analyte},provider=AnalyteAutocompleteProvider,fieldName=analyteName,idName=id"
  minimumCharacters="1" 
  indicator="indicator1"
 />
