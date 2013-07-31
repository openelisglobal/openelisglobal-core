<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<%--AIS - bugzilla 1853 more changes--%>
<%--bugzilla 2028/2037--%>
<%!

String allowEdits = "true";

//AIS - bugzilla 1496/1851
String errorMessageAccessionNumber = "";
String accnNumb = "";
String errorMessagePatientAndOrOthers = "";
String errorMessageEmpty = "";
String patientInfo = "";
String otherSearchCriteria = "";
String project = "";
String errorMessageProject = "";


%>


<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}


//AIS - bugzilla 1496
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);

accnNumb =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "sampletracking.accessionNumber");
                    
errorMessageAccessionNumber =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.invalid",
                    accnNumb);  
                                     
patientInfo =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "sampletracking.patientInfo");
                    
otherSearchCriteria =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
					"sampletracking.subtitle.other");
					
errorMessageEmpty =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.manyfield",
                    accnNumb,patientInfo,otherSearchCriteria);
                                     
//bugzilla 2189                         
errorMessagePatientAndOrOthers =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
					"errors.no.result.found",
					patientInfo,otherSearchCriteria); 	
					
project =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "sampletracking.provider.project");
					
errorMessageProject =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.more.selection",
                    project);  				

                    %>

<script language="JavaScript1.2">

function pageOnLoad() {
    var accn = $("accessionNumber");
    accn.focus();
    //AIS - bugzilla 1851
    var proj1 = $("selectedProjIdOne");
    var proj2 = $("selectedProjIdTwo");    
    var proj = $("selectedProjId");    
    var selectOptions = proj.options;
    for (var i =0; i < selectOptions.length ; i++) {    
	    if ( (proj.options[i].value == proj1.value) || (proj.options[i].value == proj2.value) ){
	    	proj[i].selected = 'true';	    		    
	    }    
    }    
}

function validateForm(form) {
  
    return true;
}


function setForward() {
      
        setAction(window.document.forms[0], 'Tracking', 'yes', '');
   
}

function submitTheFormWithAccession(val) {
   document.forms[0].accessionNumber.value= val;
   //$F("accessionNumber")= val; /Ais/ Causes JS error -(Cannot assign to a function result) Research later.
   setAction(window.document.forms[0], 'Tracking', 'yes', '');
}

//AIS - bugzilla 1493 & 1496
function setMessage(message, field) {
	//AIS - bugzilla 1851
	var listOfMessage = message;
    var messageArr = new Array();

    if (listOfMessage.indexOf(';') == 0){				
    	listOfMessage = listOfMessage.substring(1);
    }
    messageArr = listOfMessage.split(';');
    
        
    idField = document.getElementById(field);
    
    if (messageArr[0] == "invalid") {       
       alert('<%=errorMessageAccessionNumber%>');
    } else if (messageArr[0] == "invalidOthers") {           
            alert('<%=errorMessagePatientAndOrOthers%>');
    } else if (messageArr[0] == "moreThanOneAccessionNumber") {    			 				
                moreSamplePopup(window.document.forms[0]);
	}else{               		
	 	//valid (patient and others) and also the no. of records returned for accnum is just one
	 	//2028: removed param accessionId causing problems and not needed
	    setAction(window.document.forms[0], 'Tracking', 'yes', '');
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
}

function validateAccessionNumber() {
   var field = $("accessionNumber");
   var field2 = $("clientReference");
   var field3 = $("lastname");
   var field4 = $("firstname"); 
   var field5 = $("selectedOrgId");
   var field6 = $("selectedReceivedDate"); 
   var field7 = $("selectedSampleType");
   var field8 = $("selectedSampleSource");
   var field9 = $("externalId");  
   var field10 = $("selectedCollectionDate");    
   var field11 = $("selectedProjId");  
   var field12 = $("selectedSortBy");
   //bugzilla 2455
   var field13 = $("selectedSpecimenOrIsolate");
   
   if ((field.value.length == 0)&&(field2.value.length == 0)
    &&(field3.value.length == 0)&&(field4.value.length == 0)
    &&(field5.value.length == 0)&&(field6.value.length == 0)
    &&(field7.value.length == 0)&&(field8.value.length == 0)
    &&(field9.value.length == 0)&&(field10.value.length == 0) 
    &&(field11.value.length == 0) 
    //bugzilla 2455
    &&(field13.value.length == 0) 
    )
	{
   	    //AIS - bugzilla 1496
   	    alert('<%=errorMessageEmpty%>');   	     
    }else{ 
		//AIS - bugzilla 1851 
	    var proj = $("selectedProjId");    
	    var selectOptions = proj.options;
	    var flag =0;
	    for (var i =0; i < selectOptions.length ; i++) {    
		    if (proj[i].selected){
		    	flag = flag+1;	    	  		    
		    }    
	    }      
	    if (flag > 1 && field.value.length != 10) {
	      alert('<%=errorMessageProject%>');	     
	    }else{	     	       	  	
	   		if (field.value.length == 10 ){ 		   
		   		new Ajax.Request (
	                  'ajaxXML',  //url
	                  {//options
	                    method: 'get', //http method
	                    parameters: 'provider=AccessionNumberValidationProvider&form=' + document.forms[0].name + '&field=accessionNumber&id=' + escape($F("accessionNumber")),      //request parameters
	                    //indicator: 'throbbing'
	                    onSuccess:  processSuccess,
	                    onFailure:  processFailure
	                  }
	                );	   		
	   		
	   		}else {		   
			   new Ajax.Request (
                  'ajaxXML',  //url
                  {//options
                    method: 'get', //http method
                    parameters: 'provider=SampleTrackingValidationProvider&field=clientReference&cr=' + escape(field2.value)
                                                                                              +"&ln="+escape(field3.value)+"&fn="+escape(field4.value)
		                                                                                      +"&sub="+escape(field5.value)+"&rd="+escape(field6.value)
		                                                                                      +"&st="+escape(field7.value)+"&ss="+escape(field8.value)
                                                                                    	      +"&ei="+escape(field9.value)+"&cd="+escape(field10.value)
																							   +"&an="+escape(field.value)+"&pi="+escape(field11.value)
																							  +"&sb="+escape(field12.value)+"&si="+escape(field13.value),  //bugzilla 2455, request parameters
                    //indicator: 'throbbing'
                    onSuccess:  processSuccess,
                    onFailure:  processFailure
                  }
                );   
			   
	        }       	   	    
					
		}
	}	   	
}

function moreSamplePopup(form) 
{
   var field = $("accessionNumber");
   var field2 = $("clientReference");
   var field3 = $("lastname");
   var field4 = $("firstname");
   var field5 = $("selectedOrgId");   
   var field6 = $("selectedReceivedDate"); 
   var field7 = $("selectedSampleType");
   var field8 = $("selectedSampleSource"); 
   var field9 = $("externalId"); 
   var field10 = $("selectedCollectionDate");  
   var field11 = $("selectedProjId"); 
   var field12 = $("selectedSortBy"); 
   //bugzilla 2455
   var field13 = $("selectedSpecimenOrIsolate"); 
   
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
   
	var href = context+"/SampleTrackingPopup.do?cr="+field2.value+"&ln="+field3.value+"&fn="+field4.value
	+"&sub="+field5.value+"&rd="+field6.value+"&st="+field7.value+"&ss="+field8.value+"&ei="+field9.value
	+"&cd="+field10.value+"&an="+field.value+"&pi="+field11.value+"&sb="+field12.value+"&si="+field13.value
	+"&"+sessionid;
	//alert("href "+ href);	
	//createPopup(href, null, null);
	createPopup(href, 1200, 700);
}

//AIS - bugzilla 1439  - Start
function cancelSampleTracking() {
    setAction(window.document.forms[0], 'Cancel', 'no', '?close=true&ID=');
}
function clearForm (form) {
  //if there is an error on the page we cannot go to add test
   //clear button clicked flag to allow add test again
   //if (isSaveEnabled() != true) {
       //clearAddTestClicked();   //}
   
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
	
	var hyperref = context + "/SampleTracking.do" + sessionid;
	//alert("href "+ hyperref);
	document.location.href= hyperref;	
}
//AIS - bugzilla 1439 - End
</script>

        
<center>
<table border="0" cellpadding="0" cellspacing="0">
	<tbody valign="middle">
		<tr>
		   <% 	
		    String allowEdits = "true";
		    if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
    		  	allowEdits = (String)request.getAttribute(us.mn.state.health.lims.common.action.IActionConstants.ALLOW_EDITS_KEY);
    		}
    		String searchDisabled = (String)request.getAttribute(IActionConstants.VIEW_DISABLED);
           %>
	      	<td>

  	    </td>
        
		<td>&nbsp;</td>
		<td>
	   <html:button  onclick="validateAccessionNumber();" property="save" disabled="<%=Boolean.valueOf(searchDisabled).booleanValue()%>">
	           <bean:message key="label.button.search"/>
	   </html:button>
	    </td>
	    
	     <!-- AIS - bugzilla 1439 - Start -->     
	     <td>          
			<%
			if (request.getParameter("ID") == null) {
			%> 
				<html:reset>      
				    <bean:message key="sampletracking.label.button.clearsearch"/>
				</html:reset>
		
			<%}else {%>        
		    
				<html:button property="cancel" onclick="clearForm(window.document.forms[0])">
					<bean:message key="sampletracking.label.button.clearsearch"/>
				</html:button>
				
			<% } %>  	 
		</td>  
	    
	    <td>
	     <html:button onclick="cancelSampleTracking();"  property="cancel" >
	    	   <%--AIS - bugzilla 1860--%>
  			   <bean:message key="label.button.exit"/>
  			</html:button>	    
	    </td>
	     <!-- AIS - bugzilla 1439 - End -->
	     
   		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
   		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
   		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td> 		

	    </tr>
	 </tbody>
</table>
</center>