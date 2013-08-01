<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.SystemConfiguration,
	us.mn.state.health.lims.result.valueholder.ResultsEntryRoutingSwitchSessionHandler"
%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

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

<%--bugzilla 2001 to fix buggy code changed the result blank logic to use isFormFieldDirty()--%>
<%!

String allowEdits = "true";
//bugzilla 1348 - modified in 1774
String resultsEntryFromBatchVerification = "false";
//bugzilla 2501
String resultsEntryFromQaEventsEntry = "false";
//bugzilla 2504
String resultsEntryFromQaEventsEntryLineListing = "false";
String testIdFromVerification = "";
String errorMessageAccessionNumber = "";
String accnNumb = "";
String errorMessageStatusOfSample = "";


%>

<%

String viewDisabled = (String)request.getAttribute(IActionConstants.VIEW_DISABLED);

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

//bugzilla 2053
if (ResultsEntryRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION, session)) {
  resultsEntryFromBatchVerification = "true";
} else {
  resultsEntryFromBatchVerification = "false";
}

if (ResultsEntryRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.RESULTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY, session)) {
  resultsEntryFromQaEventsEntry = "true";
} else {
  resultsEntryFromQaEventsEntry = "false";
}

//bugzilla 2504
if (ResultsEntryRoutingSwitchSessionHandler.isSwitchOn(IActionConstants.RESULTS_ENTRY_ROUTING_FROM_QAEVENTS_ENTRY_LINELISTING, session)) {
  resultsEntryFromQaEventsEntryLineListing = "true";
} else {
  resultsEntryFromQaEventsEntryLineListing = "false";
}


if (session.getAttribute(IActionConstants.RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_TEST_ID) != null) {
 testIdFromVerification = (String)session.getAttribute(IActionConstants.RESULTS_ENTRY_ROUTING_FROM_BATCH_RESULTS_VERIFICATION_PARAM_TEST_ID);
}

//bugzilla 1494
java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
accnNumb =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.accessionNumber");
errorMessageAccessionNumber =
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "errors.invalid",
                    accnNumb);
errorMessageStatusOfSample = us.mn.state.health.lims.common.util.resources.ResourceLocator
					.getInstance().getMessageResources().getMessage(locale,
					"error.invalid.sample.status");

%>
<script language="JavaScript1.2">

//bugzilla 1774
var gotoTestManagement = false;

function pageOnLoad() {
    var accn = $("accessionNumber");
    accn.focus();
    
 
  //check if we need to popup the notes (attribute IActionConstants.POPUP_NOTES will only be populated if set in the action)
  //this is needed after update of results has been chosen on save confirm popup
 
   if ('<%= request.getAttribute(IActionConstants.POPUP_NOTES) %>' != null) {
        var notesRefId = '<%= (String)request.getAttribute(IActionConstants.NOTES_REFID) %>';
        var notesRefTable = '<%= (String)request.getAttribute(IActionConstants.NOTES_REFTABLE) %>';
        var pop = '<%= (String)request.getAttribute(IActionConstants.POPUP_NOTES) %>'
        if (pop == 'true') {
           popupNotes(document.forms[0], notesRefTable, notesRefId);
        } 
   }
  <% request.setAttribute(IActionConstants.POPUP_NOTES, "false"); %>

      
//bugzilla 1906- add prototype based Tooltip (for formatting and to fix timeout problem)
var customTooltip = {

    //bugzila 2244 - we need to know the width of the longest note for positioning of tooltip
	textWidth: "0",
	
    _follow: function (activator, event)
    {
      if (activator.timer) {
	      try {
	         clearTimeout(activator.timer);
         }
         catch (e) { }
      }

		var winWidth, winHeight, d=document;
		if (typeof window.innerWidth!='undefined') {
			winWidth = window.innerWidth;
			winHeight = window.innerHeight;
		} else {
			if (d.documentElement && typeof d.documentElement.clientWidth!='undefined' && d.documentElement.clientWidth!=0) {
				winWidth = d.documentElement.clientWidth
				winHeight = d.documentElement.clientHeight
			} else {
				if (d.body && typeof d.body.clientWidth!='undefined') {
					winWidth = d.body.clientWidth
					winHeight = d.body.clientHeight
				}
			}
		}

		var tooltipWidth, tooltipHeight;
		if (activator.Tooltip.currentStyle) {
			tooltipWidth = activator.Tooltip.currentStyle.width;
			tooltipHeight = activator.Tooltip.currentStyle.height;
		} else if (window.getComputedStyle) {
			tooltipWidth = window.getComputedStyle(activator.Tooltip, null).width;
			tooltipHeight = window.getComputedStyle(activator.Tooltip, null).height;
		}

      activator.Tooltip.style.position = "absolute";

		if (event.pageY) {
			var top = event.pageY;
			var left = event.pageX;
		} else if (event.clientY) {
			// put an If here instead, ?: doesn't seem to work
			if (document.body.scrollTop > document.documentElement.scrollTop) {
				var top = event.clientY + document.body.scrollTop;
			} else {
				var top = event.clientY + document.documentElement.scrollTop;
			}

			if (document.body.scrollLeft > document.documentElement.scrollLeft) {
				var left = event.clientX + document.body.scrollLeft;
			} else {
				var left = event.clientX + document.documentElement.scrollLeft;
			}
           }

		// Make sure the Tooltip doesn't go off the page. The 1.2 comes from Trial and error. 
		// We don't track the height, its possible (and much more common) that the height of an item will be more than the browser pane
		if ((left + parseInt(tooltipWidth)) > winWidth) {
			left = winWidth - parseInt(tooltipWidth) * 1.2;
		}
	
		//bugzilla 2244 - notepad needs special tooltip positioning logic
		if (event.srcElement) {
			var node = event.srcElement;
		} else if (event.fromElement) {
			var node = event.fromElement;
		} else if (event.target) {
			var node = event.target;
		}
		
		if (node.className != null && node.className.indexOf("notepad") >= 0) {
		 try {
		   //100 is to adjust for difference between window width and scroll box width (trial and error)
           left = winWidth - 100 - (activator.Tooltip.textWidth * 7);
         } catch (e) {}
		
		}
		//end bugzilla 2244
		
        //diane customizing _follow to not change top or left since we
        //are showing tooltips within a scrollable div (scrollvertical)
   		activator.Tooltip.style.left = left + "px";
		//activator.Tooltip.style.top = top + "px";


    }
};

Object.extend(Tooltip, customTooltip);
}

function validateForm(form) {
    //return validateResultsEntryForm(form);
    return true;
}
//AIS - bugzilla 1797 - modified
function setMessage(message, field) {
  idField = $(field);
  if (idField != null ){
  	  //alert("idField name " + idField.name);
      if (idField.name == "accessionNumber") {
         if (message == "invalid") {
           //only submit if accession number isn't left blank
           //bugzilla 1494
           alert('<%=errorMessageAccessionNumber%>');
           //disable save if accession number is incorrect  
          } else if (message == "invalidStatus") {
        	alert('<%=errorMessageStatusOfSample%>');   
         } else {
           //bugzilla 1774 if accn# is valid there are two possible routes
           if (gotoTestManagement) {
             setAction(window.document.forms[0], 'TestManagementFrom', 'yes', '?accessionNumber=' + idField.value + '&ID=');
           } else {
             setAction(window.document.forms[0], 'PreView', 'yes', '?ID=');
           }
         }
       }
    }else{ 
	    var listOfMessage = message;
	    var messageArr = new Array();
  
        //bugzilla 2361
	    if (listOfMessage.indexOf('<%=idSeparator%>') == 0){				
	    	listOfMessage = listOfMessage.substring(1);
        }
	    messageArr = listOfMessage.split('<%=idSeparator%>');	    
	    //AIS - bugzilla 1891
        if (messageArr[0] == "invalid"){ 
            alert(messageArr[1]);	          
        }
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

//BGM - bugzilla 1495 added form=resultsEntryForm to be passed in to validate status code
//bugzilla 2050 fix to results entry accession number validation
function validateAccessionNumber() {
   var field = $("accessionNumber");
   new Ajax.Request (
                  'ajaxXML',  //url
                   {//options
                     method: 'get', //http method
                     parameters: 'provider=AccessionNumberValidationProvider&form=' + document.forms[0].name + '&field=accessionNumber&id=' + escape(field.value),      //request parameters
                     //indicator: 'throbbing'
                     onSuccess:  processSuccess,
                     onFailure:  processFailure
                   }
                  );
}

//bugzilla 1348, 2501/2053
function cancelResultsEntry() {
  var reFrombv;
  var reFromQaEventsEntry;
//bugzilla 2504
  var reFromQaEventsEntryLineListing;
  var accessionNumber = document.getElementById("accessionNumber").value;
  reFrombv = '<%=resultsEntryFromBatchVerification%>';
  reFromQaEventsEntry = '<%=resultsEntryFromQaEventsEntry%>';
//bugzilla 2504
  reFromQaEventsEntryLineListing = '<%=resultsEntryFromQaEventsEntryLineListing%>';
  if (reFrombv == 'false' && reFromQaEventsEntry == 'false' && reFromQaEventsEntryLineListing == 'false') {
    setAction(window.document.forms[0], 'Cancel', 'no', '?close=true&ID=');
  }else if (reFromQaEventsEntry == 'true') {
    setAction(window.document.forms[0], 'CancelToQaEventsEntry', 'no', '?accessionNumber=' + accessionNumber + '&ID=');
  }else if (reFrombv == 'true') {
    setAction(window.document.forms[0], 'CancelToBatchResultsVerification', 'no', '?testId=' + '<%=testIdFromVerification%>' + '&accessionNumber=' + accessionNumber + '&ID=');
//bugzilla 2504
  }else if (reFromQaEventsEntryLineListing == 'true') {
    setAction(window.document.forms[0], 'CancelToQaEventsEntryLineListing', 'no', '?accessionNumber=' + accessionNumber + '&ID=');
  }
  

}

</script>

<%
	if(null != request.getAttribute(IActionConstants.FORM_NAME))
	{
%>
<h1>
	<logic:notEmpty
		name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>">
		<bean:write name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>" />
	</logic:notEmpty> 
	<logic:empty
		name="<%=IActionConstants.PAGE_SUBTITLE_KEY%>">
		<% if (request.getParameter("ID").equals("0")) { %>
		  <bean:message key="default.add.title" />
		<% } else { %>
		  <bean:message key="default.edit.title" />
		<%}%>
   	</logic:empty> 
   	<app:text name="<%=formName%>" styleId="accessionNumber" property="accessionNumber" allowEdits="true" onkeypress="return noenter()"/>
            <%--bugzilla 1774--%>
   	<html:button  onclick="gotoTestManagement=false; validateAccessionNumber();"
               			 property="view" disabled="<%=Boolean.valueOf(viewDisabled).booleanValue()%>">
		<bean:message key="label.button.view"/>
	</html:button>
   	<html:button  onclick="gotoTestManagement = true; validateAccessionNumber();"
               			 property="sampleAndTestMgmt" disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>">
   		<bean:message key="resultsentry.button.sample.and.test.management"/>
   	</html:button>
</h1>
<%
	}
%>
<html:javascript formName="resultsEntryForm"/>

