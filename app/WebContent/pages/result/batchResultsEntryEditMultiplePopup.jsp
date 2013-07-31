<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,java.util.List,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.SystemConfiguration,
	us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults,
	us.mn.state.health.lims.testresult.valueholder.TestResult,
	us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<bean:define id="titerType" value='<%= SystemConfiguration.getInstance().getTiterType() %>' />
<bean:define id="numericType" value='<%= SystemConfiguration.getInstance().getNumericType() %>' />

<%--AIS - bugzilla 1863/1891 Many changes --%>
<%--bugzilla 1933 change for dictType, titerType, numericType error--%>
<%!

String allowEdits = "true";
String errorNoEmpty = "";
//bugzilla 2322
String resultDetail = "";
String resultConfirm = "";
%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
errorNoEmpty = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "batchresultsentry.editMultiplePopup.errorMessage");
                    
//bugzilla 2322
resultDetail =      us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultentry.detail");
                    
resultConfirm =     us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultentry.confirm");                    
%>

<!--bugzilla 2322-->
<!--bugzilla 2628 prevent js problem with single quote in dict entry-->
<% int index = 0; %>
<script language="JavaScript">
    var dictArray = new Array();   
</script>  
<logic:notEmpty name="<%=formName%>" property="sample_TestAnalytes">
    <logic:iterate id="ta_Trs" name="<%=formName%>" indexId="analyte_ctr" property="testAnalyte_TestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults" >

        <!--build dictionary array list-->
        <script>
        <%
            List testResults = ta_Trs.getTestResults();
            for ( int i=0; i<testResults.size(); i++ ) {
                TestResult tr = (TestResult)testResults.get(i);           
            %>            
                dictArray[<%=index++%>] = "<%=tr.getId()%>" + "|TRID|" + "<%= tr.getValue() %>";
            <%                
            }
        %> 
        </script>          
         
    </logic:iterate>
</logic:notEmpty> 

<script language="JavaScript1.2">
var BLANK = "b|l|a|n|k";
var tfFlag = true;

var skipcycle = false;
function myOnBlur() {
 
    if (!skipcycle){
       window.focus(); 
    }
    mytimer = setTimeout('myOnBlur()', 500);
}

function customOnLoad() {
     
}

function validateForm(form) {
    return validateBatchResultsEntryEditMultiplePopupForm(form);
}

function cancelToParentForm() {
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
       //alert("Going to clear add test clicked");
        //window.opener.clearAddTestClicked();
        window.close();
   } 
}

function saveItToParentForm(form) {
 tfFlag= true;
   if (window.opener && !window.opener.closed && window.opener.document.forms[0]) {
    var selectedTestResultIds = '';
    var selectedTestResultValues = '';    
     <logic:notEmpty name="<%=formName%>" property="testAnalyte_TestResults">
      <logic:iterate id="ta_trs" indexId="ctr" name="<%=formName%>" property="testAnalyte_TestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults">
        <logic:notEqual name="ctr" value="0">
                selectedTestResultIds += '<%=idSeparator%>';
                selectedTestResultValues += '<%=idSeparator%>';
        </logic:notEqual>
		if (window.document.forms[0].elements['testAnalyte_TestResults[<%=ctr%>].resultValue'] == null ){
        	selectedTestResultValues += "null";        
        }else if (window.document.forms[0].elements['testAnalyte_TestResults[<%=ctr%>].resultValue'].value != ""){
        	validateResultValue(
            window.document.forms[0].elements['testAnalyte_TestResults[<%=ctr%>].resultValue'], 
            window.document.forms[0].elements['testAnalyte_TestResults[<%=ctr%>].selectedTestResultId'].value );
        	selectedTestResultValues += window.document.forms[0].elements['testAnalyte_TestResults[<%=ctr%>].resultValue'].value;
        }else{
 			selectedTestResultValues += BLANK;
        	alert('<%=errorNoEmpty%>');
           	return;
        }
        if (window.document.forms[0].elements['testAnalyte_TestResults[<%=ctr%>].selectedTestResultId'].value == null || window.document.forms[0].elements['testAnalyte_TestResults[<%=ctr%>].selectedTestResultId'].value == '') {
           selectedTestResultIds += BLANK;
		   alert('<%=errorNoEmpty%>');
           return;
        } else {
           selectedTestResultIds += window.document.forms[0].elements['testAnalyte_TestResults[<%=ctr%>].selectedTestResultId'].value;
        }


      </logic:iterate>
     </logic:notEmpty>
   
       window.opener.document.forms[0].elements['selectedTestResultIds'].value = selectedTestResultIds;
       window.opener.document.forms[0].elements['selectedTestResultValues'].value = selectedTestResultValues;
       checkStep(form);
   }
}

function checkStep(form){
	if (tfFlag == true){
		window.opener.setResults(form);      
		window.close();
	}
}

function validateValue(field, testResultId){
	//alert("field.value = " + field.value +  "tresultId =" + testResultId );  
	if (field.value != ''){
		validateResultValue(field, testResultId );
	} 
}

function processFailure(xhr) {
  //ajax call failed
}

function processSuccess(xhr) {
  var message = xhr.responseXML.getElementsByTagName("message")[0];
  var formfield = xhr.responseXML.getElementsByTagName("formfield")[0];
  //alert("I am in parseMessage and this is message, formfield " + message + " " + formfield);
  setMessage(message.childNodes[0].nodeValue, formfield.childNodes[0].nodeValue);
  //setSave();
}

//bugzilla 2361 modified handling of numeric and titer type validation
function validateResultValue(fieldI, testResultId){
	//alert ("In validateResultValue "+ fieldI.value + ""+ testResultId );	
	var value = fieldI.value;
    new Ajax.Request (
                  'ajaxXML',  //url
                   {//options
                     method: 'get', //http method
                     parameters: 'provider=ResultsValueValidationProvider&field=fieldI&val=' + value + '&trId=' + testResultId + '&clientType=js',      //request parameters
                     //indicator: 'throbbing'
                     onSuccess:  processSuccess,
                     onFailure:  processFailure
                   }
                  );
}

//bugzilla 2361
function setMessage(message, field) {
    var listOfMessage = message;
    var messageArr = new Array();

    if (listOfMessage.indexOf('<%=idSeparator%>') == 0){				
    	listOfMessage = listOfMessage.substring(1);
    }
    messageArr = listOfMessage.split('<%=idSeparator%>');
    if (messageArr[0] == "invalid"){      
      alert(messageArr[1]);     
	  tfFlag = false;     
    }            
} 
var req;

//bugzilla 2322
function viewDictionary(form) {
    var selectedId = (form.options[form.selectedIndex].value);
    var defaultBoxLength = "30";
    var select;
    for ( var i=0; i<dictArray.length; i++ ) {
        var trid  = dictArray[i].split("|TRID|");        
        if ( (trid[0] == selectedId) && (trid[1].length >= defaultBoxLength) ) {
            select = confirm("<%=resultDetail%>:\n\n " + trid[1] + "\n\n<%=resultConfirm%>"); 
            if ( !select )  {
                form.selectedIndex = 0;
            }
            break;                                     
        }
    }
}

</script>

<table align="center" width="353" border="0" cellspacing="0" cellpadding="0">
     <logic:empty name="<%=formName%>" property="testAnalyte_TestResults">
        <tr>
          <td height="27" bgcolor="#CCCC99" colspan="2" scope="col"><strong><bean:message key="resultsentry.selectedTest.title"/>:</strong></td>
        </tr>
     </logic:empty>
     <logic:notEmpty name="<%=formName%>" property="testAnalyte_TestResults">
      <logic:iterate id="ta_trs" indexId="ctr" name="<%=formName%>" property="testAnalyte_TestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults">
         <bean:define id="ta" name="ta_trs" property="testAnalyte" />
         <bean:define id="taName" name="ta" property="analyte.analyteName" />
          <logic:equal name="ctr" value="0">
            <tr><%--bugzilla 1844 use testDisplayValue instead of name--%>
               <td height="27" bgcolor="#CCCC99" colspan="2" scope="col"><strong><bean:message key="resultsentry.selectedTest.title"/>: <bean:write name="ta" property="test.testDisplayValue"/></strong></td>
            </tr>
          </logic:equal>
               <tr>
                 <td width="113" bgcolor="#CCCC99" scope="row"><bean:write name="taName"/>:</td>
                 <%		
							List testResults = ta_trs.getTestResults();                            
							TestResult testresult = null;
							if (testResults != null && testResults.size() > 0) {
							 testresult = (TestResult) testResults.get(0);
							}
							
	               %> 
                 
                 <td width="228" bgcolor="#CCCC99">                 
                 <% if (testresult != null && testresult.getTestResultType().equalsIgnoreCase(numericType)){ %>                 
   	       		 	<app:text name="<%=formName%>" property= '<%= "testAnalyte_TestResults[" + ctr + "].resultValue"%>'   	       		 	
   	       			size="5" styleClass="text" onblur='<%="validateValue(this,"+ testresult.getId()+")"%>' 
   	       			/>   	             
   	             
   	       		  <%--bugzilla 2322--%>	
   	              <html:select name="<%=formName%>" 
   	              property='<%= "testAnalyte_TestResults[" + ctr + "].selectedTestResultId"%>' 
   	             style="display:none" value='<%= testresult.getId() %>' onchange='viewDictionary(this);'>
 	             	  <app:optionsCollection 
	                	name="ta_trs" 
	            		property="testResults" 
		            	label="value" 
		            	maxLength="30"
	             		value="id" 
	               />
							        
   	             </html:select>
   	             
   	              <%}else if (testresult.getTestResultType().equalsIgnoreCase(titerType)){ %>    
   	             
   	             <table width="220" style="border-collapse:collapse;">
	                     <tr>	                     
		                     <td width="10" bgcolor="#CCCC99">1:</td>
		                     <td width="210" bgcolor="#CCCC99">
			                    <app:text name="<%=formName%>" property= '<%= "testAnalyte_TestResults[" + ctr + "].resultValue"%>'   	       		 	
	   	       						size="5" styleClass="text" onblur='<%="validateValue(this,"+ testresult.getId()+")"%>' 
	   	       					/> 
   	       					</td>
	                     </tr>
                     </table>         
   	             
                  <%--bugzilla 2322--%>   
   	              <html:select name="<%=formName%>" 
   	              property='<%= "testAnalyte_TestResults[" + ctr + "].selectedTestResultId"%>' 
   	             style="display:none" value='<%= testresult.getId() %>' onchange='viewDictionary(this);'>
 	             	  <app:optionsCollection 
	                	name="ta_trs" 
	            		property="testResults" 
		            	label="value" 
		            	maxLength="30"
	             		value="id" 
	               />
							        
   	             </html:select>
   	             
   	              
   	             <% }else{ %>         
                 
   	             <%--bugzilla 2322--%>
                 <html:select name="<%=formName%>" property='<%= "testAnalyte_TestResults[" + ctr + "].selectedTestResultId"%>' onfocus="skipcycle=true" onblur="skipcycle=false" onchange="viewDictionary(this);">
 	             	  <app:optionsCollection 
	                	name="ta_trs" 
	            		property="testResults" 
		            	label="value" 
		            	maxLength="30"
	             		value="id" 
	               />
							        
   	             </html:select>
   	             <% } %>     
                </td>
              </tr>
      </logic:iterate>
     </logic:notEmpty>
     </table>

<%--html:javascript formName="batchResultsEntryEditMultiplePopupForm" staticJavascript="true"/--%>

