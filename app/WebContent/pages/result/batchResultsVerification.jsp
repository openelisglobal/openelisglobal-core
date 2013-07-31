<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,java.util.List,java.util.Locale,
	us.mn.state.health.lims.common.util.SystemConfiguration,
	us.mn.state.health.lims.testresult.valueholder.TestResult,
    us.mn.state.health.lims.common.util.Versioning,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<!--bugzilla 2227-->
<bean:define id="accessionNumberParm" value='<%= IActionConstants.ACCESSION_NUMBER%>' />


<%--AIS - bugzilla 1872 --%>
<%--bugzilla 1908 - mods for tomcat logic tags--%>
<%--bugzilla 1900 - preview report--%>
<%!

String allowEdits = "true";
String analysisId = "";
Locale locale = null;
String popupMessage = "";
String path = "";
String basePath = "";
//bugzilla 2188
String noResultFound = "";
%>

<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

analysisId = (String)request.getAttribute("analysisId");

java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
popupMessage =  	us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "batchresultsverification.popup.display");

//bugzilla 2188
    noResultFound = us.mn.state.health.lims.common.util.resources.ResourceLocator
			        .getInstance().getMessageResources().getMessage(locale,
					"errors.no.result.found");                    

%>

<script language="JavaScript1.2">

function changeAllCheckBoxStates(checkState) {
      var selectedRows = window.document.forms[0].elements['selectedRows'];
      // Toggles through all of the checkboxes defined in the CheckBoxIDs array
      // and updates their value to the checkState input parameter
	if (selectedRows != null){	
        //If only one checkbox
        if (selectedRows[0] == null) {
             selectedRows.checked = checkState;
        } else {
      
      
         for (var i = 0; i < selectedRows.length; i++)
            selectedRows[i].checked = checkState;
        }
      } 
	if (checkState){
		alert( (selectedRows.length/4) + ' <%=popupMessage%>');
   }
}

//bugzilla 2227
function resultsEntryHistoryBySamplePopup (form, accessionNumber) {

  //if there is an error on the page we cannot go to add test
   //clear button clicked flag to allow add test again
   //if (isSaveEnabled() != true) {
       //clearAddTestClicked();
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
 
    var param = '?' + '<%=accessionNumberParm%>' + '=' + accessionNumber;
 	var href = context + "/ResultsEntryHistoryBySamplePopup.do" + param + sessionid;
    //alert("href "+ href);
	
	createPopup( href, 1250, 500 );
}  
</script>

<script src="<%=basePath%>scripts/tableSort.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<script>
//bugzilla 2445 (put this back)
function sortIt(lnk, col) {
  //sort for all four divs of datagrid

  var tableBodysToSort = saveOriginalContentForSorting.getElementsByTagName("TBODY");
  var tableBodyToSort = tableBodysToSort[0];
  sortTable(tableBodyToSort, col, true);
  
  tableBodysToSort = saveOriginalDivHeaderColumnForSorting.getElementsByTagName("TBODY");
  tableBodyToSort = tableBodysToSort[0];
  sortTable(tableBodyToSort, col, true);
  
  tableBodysToSort = saveOriginalDivHeaderRowColumnForSorting.getElementsByTagName("TBODY");
  tableBodyToSort = tableBodysToSort[0];
  sortTable(tableBodyToSort, col, true);
  
  tableBodysToSort = saveOriginalDivHeaderRowForSorting.getElementsByTagName("TBODY");
  tableBodyToSort = tableBodysToSort[0];
  sortTable(tableBodyToSort, col, true);

  reverseArrows(lnk);
  
  return false;
}
//-----------------------------------------------------------------------------
// sortTable(id, col, rev)
//  id  - ID of the TABLE, TBODY, THEAD or TFOOT element to be sorted.
//  col - Index of the column to sort, 0 = first column, 1 = second column,
//        etc.
//  rev - If true, the column is sorted in reverse (descending) order
//        initially.
//
// Note: the team name column (index 1) is used as a secondary sort column and
// always sorted in ascending order.
//-----------------------------------------------------------------------------

function sortTable(tblEl, col, rev) {

  // The first time this function is called for a given table, set up an
  // array of reverse sort flags.
  if (tblEl.reverseSort == null) {
    tblEl.reverseSort = new Array();
    // Also, assume the team name column is initially sorted.
    //tblEl.lastColumn = 1;
    //we don't have a team name column
    tblEl.lastColumn = 0;
  }
  
  // If this column has not been sorted before, set the initial sort direction.
  if (tblEl.reverseSort[col] == null)
    tblEl.reverseSort[col] = rev;

  // If this column was the last one sorted, reverse its sort direction.
  //if (col == tblEl.lastColumn)
    tblEl.reverseSort[col] = !tblEl.reverseSort[col];

  // Remember this column as the last one sorted.
  tblEl.lastColumn = col;

  // Set the table display style to "none" - necessary for Netscape 6 
  // browsers.
  var oldDsply = tblEl.style.display;
  tblEl.style.display = "none";

  // Sort the rows based on the content of the specified column using a
  // selection sort.

  var tmpEl;
  var i, j;
  var minVal, minIdx;
  var testVal;
  var cmp;
  
  for (i = 0; i < tblEl.rows.length - 1; i++) {

    // Assume the current row has the minimum value.
    minIdx = i;
    minVal = getTextValue(tblEl.rows[i].cells[col]);

    // Search the rows that follow the current one for a smaller value.
    for (j = i + 1; j < tblEl.rows.length; j++) {
      testVal = getTextValue(tblEl.rows[j].cells[col]);
      cmp = compareValues(minVal, testVal);
      // Negate the comparison result if the reverse sort flag is set.
      if (tblEl.reverseSort[col])
        cmp = -cmp;
      // Sort by the second column (team name) if those values are equal.
      if (cmp == 0 && col != 1)
        cmp = compareValues(getTextValue(tblEl.rows[minIdx].cells[1]),
                            getTextValue(tblEl.rows[j].cells[1]));
      // If this row has a smaller value than the current minimum, remember its
      // position and update the current minimum value.
      if (cmp > 0) {
        minIdx = j;
        minVal = testVal;
       }
    }

    // By now, we have the row with the smallest value. Remove it from the
    // table and insert it before the current row.
    if (minIdx > i) {
      tmpEl = tblEl.removeChild(tblEl.rows[minIdx]);
      tblEl.insertBefore(tmpEl, tblEl.rows[i]);
    }
  }

  // Make it look pretty.
  makePretty(tblEl, col);

  // Restore the table's display style.
  tblEl.style.display = oldDsply;
  
  return false;
}

//bugzilla 1900
function previewReport(accessionNumber) {
   var param = '?selectedAccessionNumber=' + accessionNumber;
   param += '&forward=All';
   param += '&ID=';
   //alert("param " + param);
   //bugzilla 2375 to avoid stack trace with tomcat: use popup and forward to empty.jsp on success
   //so that response.getWriter() is not called in same response where response.getOutputStream() has already
   //been called from ResultsReportProvider. Both methods cannot be called within one response.
   //setAction(window.document.forms[0], 'PreViewReport', 'yes', param);
    
   var form = document.forms[0];
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
 
   var href = context + "/PreViewReportBatchResultsVerification.do" + param + sessionid;
    //alert("href "+ href);
   createPopup( href, 1250, 1250 );
}


</script>

<%--bugzilla 2188--%>
<%--bugzilla 2552 fix 2188 for other app servers i.e. OC4J--%>
<script> var noSelections = false; </script>
<logic:empty name="<%=formName%>" property="selectedTestSectionId">
  <logic:empty name="<%=formName%>" property="selectedTestId">
    <logic:empty name="<%=formName%>" property="accessionNumber">
    <%-- if all 3 are blank then don't display the message --%>
      <script> noSelections = true; </script>
    </logic:empty>
  </logic:empty>
</logic:empty>
 
<logic:empty name="<%=formName%>" property="sample_TestAnalytes">
    	<script>if (!noSelections) {alert('<%=noResultFound%>');}</script>
</logic:empty> 

<html:hidden property="selectedTestResultIds" name="<%=formName%>" />
<logic:notEmpty name="<%=formName%>" property="sample_TestAnalytes">
 <bean:define id="sTestAnalytes" name="<%=formName%>" property="sample_TestAnalytes" type="java.util.List"/>
 <bean:size id="numberOfRows" name="sTestAnalytes" />
 <table width="100%">
   <logic:notEqual name="<%=numberOfRows.toString()%>" value="0" >
     <bean:define id="sTestAnalyte" name="<%=formName%>" property='<%= "sample_TestAnalytes[0]"%>' type="us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte" />
	 <bean:define id="tAnalytes" name="sTestAnalyte" property="testAnalytes" type="java.util.List" />
     <bean:size id="numberOfColumns" name="tAnalytes" />
     <logic:notEqual name="<%=numberOfColumns.toString()%>" value="0">
      <tr> 
       <td colspan="<%=numberOfColumns.intValue() + 1%>"> 
         <h2><bean:message key="batchresultsentry.browse.testResults.title"/></h2>
       </td>
     </tr>
   </logic:notEqual>
  </logic:notEqual>
</table>
</logic:notEmpty>

<div id="outerDiv">
  <div id="innerDiv">
     <table id="myTable" border="1" cellspacing="0" cellpadding="4">
      <logic:notEmpty name="<%=formName%>" property="sample_TestAnalytes">
      <bean:define id="smplTestAnalytes" name="<%=formName%>" property="sample_TestAnalytes" type="java.util.List"/>
      <bean:size id="numOfRows" name="smplTestAnalytes" />
      <logic:notEqual name="<%=numOfRows.toString()%>" value="0" >
       <bean:define id="smplTestAnalyte" name="<%=formName%>" property='<%= "sample_TestAnalytes[0]"%>' type="us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte" />
	   <bean:define id="tstAnalytes" name="smplTestAnalyte" property="testAnalytes" type="java.util.List" />
       <bean:size id="numOfColumns" name="tstAnalytes" />
       <logic:notEqual name="<%=numOfColumns.toString()%>" value="0">
         <thead>
           <tr>
             <th class="bre" nowrap="nowrap" align="middle"><a class="sortableheader" href="#" onclick="this.blur(); return sortIt(this, 0);" ><bean:message key="batchresultsentry.browse.receivedDate"/><span class="sortarrow">&nbsp;&nbsp;<img src="<%=basePath%>/images/arrow-none.gif"/></span></a></th>
             <th class="bre" nowrap="nowrap" align="middle"><a class="sortableheader" href="#" onclick="this.blur(); return sortIt(this, 1);" ><bean:message key="batchresultsentry.browse.accessionNumber"/><span class="sortarrow">&nbsp;&nbsp;<img src="<%=basePath%>/images/arrow-none.gif"/></span></a></th>
             <th class="bre">&nbsp;</th>
             <th class="bre" nowrap="nowrap" align="middle"><bean:message key="batchresultsverification.browse.verify"/></th>
             <logic:iterate id="testAnalyte" indexId="ctr" name="tstAnalytes" type="us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte">
                  <bean:define id="analyte" name="testAnalyte" property="analyte" />
                  <th class="bre" nowrap="nowrap" align="middle"><bean:write name="analyte" property="analyteName" /> </th>
             </logic:iterate>
           </tr>
          </thead>
          <tbody id="tblBody1">
           <logic:iterate id="sampleTestAnalyte" name="<%=formName%>" indexId="sample_ctr" property="sample_TestAnalytes" type="us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte">
              <%--bugzilla #1346 add ability to hover over accession number and
               view patient/person information (first and last name and external id)--%>
             <bean:define id="sample" name="sampleTestAnalyte" property="sample" type="us.mn.state.health.lims.sample.valueholder.Sample"/>
             <bean:define id="person" name="sampleTestAnalyte" property="person" type="us.mn.state.health.lims.person.valueholder.Person"/>
             <bean:define id="patient" name="sampleTestAnalyte" property="patient" type="us.mn.state.health.lims.patient.valueholder.Patient"/>
             <bean:define id="tst_ta" name="sampleTestAnalyte" property="testTestAnalyte" type="us.mn.state.health.lims.result.valueholder.Test_TestAnalyte"/>
				
              <tr>
               <%--bugzilla #1346 add ability to hover over accession number and
                  view patient/person information (first and last name and external id)--%>
                 <td class="bre" align="middle" bgcolor="#CCCC99">
                     <bean:write name="sample" property="receivedDateForDisplay" format="MM/dd/yyyy" />
                 </td>
                 <td class="bre" align="middle" bgcolor="#CCCC99">
                  <table style="BACKGROUND-COLOR: #cccc99;">
                   <tr style="BACKGROUND-COLOR: #cccc99;">
                    <td>
                     <a class="hoverinformation" href="" title='<%=person.getFirstName() + " " + person.getLastName() + "/ " + patient.getExternalId() %>' onclick="return false;">
                      <bean:write name="sample" property="accessionNumber"/>&nbsp;
                      </a> 
                    </td>
                    <%--bugzilla 1900 link to preview results report--%>
                    <td>
                     <a href="" onclick='previewReport("<%=sample.getAccessionNumber()%>");return false;' style="color:blue;BACKGROUND-COLOR: #cccc99;">
                      <bean:message key="batchresultsverification.label.hyperlink.preview.report" />
                     </a>
                    </td>
                   </tr>
                   <%--bugzilla 2227 link to history--%>
                   <logic:equal name="sampleTestAnalyte" property="sampleHasTestRevisions" value="true">     
                   <tr style="BACKGROUND-COLOR: #cccc99;">
                    <td>&nbsp;</td>
                    <td>
                     <a href="" onclick="resultsEntryHistoryBySamplePopup(document.forms[0], '<%= sample.getAccessionNumber()%>');return false;" style="color:blue">
                       <bean:message key="resultsentry.label.hyperlink.history" />
                     </a>
                    </td>
                   </tr>
                 </logic:equal>
                 </table>
                 </td>
                 <bean:define id="accn" name="sample" property="accessionNumber" />
                   <td class="bre" align="middle" bgcolor="#CCCC99"> 
                    <%--bugzilla 2146 --%>
                     <html:button onclick='<%= "editThis(" + accn + ");"%>' disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>" property="view">
  			            <bean:message key="label.button.edit"/>
  	                 </html:button>
                   </td>
                   <td class="bre" align="middle" bgcolor="#CCCC99">
	                  <html:multibox name='<%=formName%>' property="selectedRows">
	                    <bean:write name="sample_ctr" />
	                  </html:multibox>
   	               </td>  
                   <logic:iterate id="ta_Trs" indexId="analyte_ctr" name="tst_ta" property="testAnalyteTestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults">
     			
                      <%	//AIS - bugzilla 1838
							List testResults = ta_Trs.getTestResults();   
                           	TestResult testresult = null;
							if (testResults != null && testResults.size() > 0) {
							 testresult = (TestResult) testResults.get(0);
							}
	           		 %>
                      <td class="bre" align="middle" nowrap="nowrap">
                         &nbsp;
                        <%--bugzilla 1351 use app:write and hover over text--%>
                        <logic:notEmpty name="<%=formName%>" property='<%= "sample_TestAnalytes[" + sample_ctr + "].sampleTestResults[" +  analyte_ctr +"]" %>' >
                         <bean:define id="resultTitle" name="<%=formName%>" property='<%= "sample_TestAnalytes[" + sample_ctr + "].sampleTestResults[" +  analyte_ctr +"].value" %>' />
                          <%--AIS - bugzilla 1838/1891--%>
                          <a class="hoverinformation" href="" title='<%= resultTitle %>' onclick="return false;">
                            <% if (testresult != null && testresult.getTestResultType().equalsIgnoreCase(SystemConfiguration.getInstance().getDictionaryType())){ %>                            
                            <app:write name="<%=formName%>" property='<%= "sample_TestAnalytes[" + sample_ctr + "].sampleTestResults[" +  analyte_ctr +"].value" %>' maxLength="40"/>
                           <% }else{ %>                            
                           		<app:write name="<%=formName%>" property='<%= "sample_TestAnalytes[" + sample_ctr + "].testResultValues[" +  analyte_ctr +"]" %>' maxLength="40"/>
                          
                         <% } %>  
                          </a>  
                        </logic:notEmpty>
                      </td>
                 </logic:iterate>
                 </tr>
              </logic:iterate>
            </tbody>
    </logic:notEqual>
   </logic:notEqual>
   </logic:notEmpty>
 </table>
</div>
</div>






<app:javascript formName="batchResultsVerificationForm"/>

