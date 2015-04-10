<%@ page language="java" contentType="text/html; charset=utf-8"
	import="java.util.Date,
	        java.util.List,
	        java.util.Locale,
	        org.apache.struts.Globals,
	        us.mn.state.health.lims.common.action.IActionConstants,
	        us.mn.state.health.lims.common.util.DateUtil,
	        us.mn.state.health.lims.common.util.StringUtil,
	        us.mn.state.health.lims.note.valueholder.Note,
	        us.mn.state.health.lims.testresult.valueholder.TestResult,
	        us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults,
            us.mn.state.health.lims.common.util.Versioning,
	        us.mn.state.health.lims.common.util.SystemConfiguration"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<!--bugzilla 2227-->
<bean:define id="accessionNumberParm" value='<%= IActionConstants.ACCESSION_NUMBER%>' />
<%--bugzilla 2446--%>
<bean:define id="internalNote" value='<%= SystemConfiguration.getInstance().getNoteTypeInternal() %>' />
<bean:define id="externalNote" value='<%= SystemConfiguration.getInstance().getNoteTypeExternal() %>' />

<%--AIS - bugzilla 1872 -Created--%>
<%--bugzilla 1992: cleanup use of Sample_TestAnalyte and Test_TestAnalyte--%>
<%--bugzilla 2164: reformat to make headers static and resize scrollable area/also remove sorting--%>
<%--bugzilla 1900 preview report--%>
<%--bugzilla 2313: restored the sorting function previously removed--%>

<%!
String popupMessage = "";
String analysisId = "";
Locale locale = null;
String path = "";
String basePath = "";
String allowEdits = "true"; 
//bugzilla 2446
String internalNotesHeading = "";
String externalNotesHeading = "";
//bugzilla 2188
String noResultFound = "";
//bugzilla 2603
String tooltipheader = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

//this is the test selected for editing
			analysisId = (String) request
					.getAttribute(IActionConstants.ANALYSIS_ID);

			locale = (java.util.Locale) request.getSession().getAttribute(
					org.apache.struts.Globals.LOCALE_KEY);

			popupMessage = us.mn.state.health.lims.common.util.resources.ResourceLocator
					.getInstance().getMessageResources().getMessage(locale,
							"batchresultsverification.popup.display");
							
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
    allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}	
					
//bugzilla 2446
locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
internalNotesHeading = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "note.type.internal.heading");
externalNotesHeading = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "note.type.external.heading"); 
                    
//bugzilla 2188
noResultFound = us.mn.state.health.lims.common.util.resources.ResourceLocator
			        .getInstance().getMessageResources().getMessage(locale,
					"errors.no.result.found"); 

//bugzilla 2603
tooltipheader = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "resultsentry.tooltip.header");                   
%>

<script language="JavaScript1.2">


//This is for tooltips (icon displaying parent analysis/result information)

if (!document.layers&&!document.all)
event="test"
function showtip(current,e,text){

if (document.all){
thetitle=text.split('<br>')
if (thetitle.length>1){
thetitles=''
for (i=0;i<thetitle.length;i++)
thetitles+=thetitle[i]
current.title=thetitles
}
else
current.title=text
}

else if (document.layers){
document.tooltip.document.write('<layer bgColor="white" style="border:1px solid black;font-size:12px;">'+text+'</layer>')
document.tooltip.document.close()
document.tooltip.left=e.pageX+5
document.tooltip.top=e.pageY+5
document.tooltip.visibility="show"
}
}
function hidetip(){
if (document.layers)
document.tooltip.visibility="hidden"
}

function changeAllCheckBoxStates(checkState) {   
   var selectedRows = window.document.forms[0].elements['selectedRows'];
  // Toggles through all of the checkboxes defined in the CheckBoxIDs array
  // and updates their value to the checkState input parameter
  if (selectedRows != null)
  {
    //If only one checkbox
    if (selectedRows[0] == null) {
         selectedRows.checked = checkState;
    }else{  
	     for (var i = 0; i < selectedRows.length; i++)
	        selectedRows[i].checked = checkState;
    }
  } 
  if ((checkState) && (selectedRows[0] != null)){
  	alert( (selectedRows.length) + ' <%=popupMessage%>');
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

<script src="<%=basePath%>scripts/tableSort.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<logic:notEmpty name="<%=formName%>" property="sample_TestAnalytes">
	<bean:define id="sTestAnalytes" name="<%=formName%>" property="sample_TestAnalytes" type="java.util.List" />
	<bean:size id="numberOfRows" name="sTestAnalytes" />
	<table width="100%">
        <%--bugzilla 1908 - mods for tomcat logic tags--%>
		<logic:notEqual name="<%=numberOfRows.toString()%>" value="0">

			<tr>
				<td>
					<h2>
						<bean:message key="batchresultsentry.browse.testResults.title" />
					</h2>
				</td>
			</tr>

		</logic:notEqual>
	</table>
</logic:notEmpty>

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

<logic:notEmpty name="<%=formName%>" property="sample_TestAnalytes">
<%--bugzilla 2227 changed the way the title is displayed to accomodate history link--%>
<!-- Loop start 1 -->
<logic:iterate id="s_ta" indexId="sta_ctr" name="<%=formName%>" property="sample_TestAnalytes" type="us.mn.state.health.lims.result.valueholder.Sample_TestAnalyte">
<bean:define id="sample" name="s_ta" property="sample" type="us.mn.state.health.lims.sample.valueholder.Sample" />
<bean:define id="person" name="s_ta" property="person" type="us.mn.state.health.lims.person.valueholder.Person" />
<bean:define id="patient" name="s_ta" property="patient" type="us.mn.state.health.lims.patient.valueholder.Patient" />
<bean:define id="accn" name="sample" property="accessionNumber" />

<logic:equal name="sta_ctr" value="0">
	<table width="100%" border="1">
		<tr>
			<td width="100%" valign="top">
					<div class="scrollvertical" style="height:400px;width:100%">
					<table id="myTable" width="98.5%" border="1">
					<thead>
						<tr>
							<th width="8%" class="bre" nowrap="nowrap" align="middle"><a class="sortableheader" href="" onclick="this.blur(); reverseArrows(this); return sortTable('tblBody1', 0 , true);"><bean:message key="batchresultsentry.browse.receivedDate" /><span class="sortarrow">&nbsp;&nbsp;<img src="<%=basePath%>/images/arrow-none.gif"/></span></a></th>
							<th width="12%" class="bre" nowrap="nowrap" align="middle"><a class="sortableheader" href="" onclick="this.blur(); reverseArrows(this); return sortTable('tblBody1', 1 , true);"><bean:message key="batchresultsentry.browse.accessionNumber" /><span class="sortarrow">&nbsp;&nbsp;<img src="<%=basePath%>/images/arrow-none.gif"/></span></a></th>
							<th width="5%" class="bre" nowrap="nowrap" align="middle">&nbsp;</th>
							<th width="75%" class="bre" nowrap="nowrap" align="middle">
									<table width="100%">
										<tr>
											<th width="5%">
												<strong><bean:message key="batchresultsverification.browse.verify" /><strong>
											</th>
											<th width="22%">
												<strong><bean:message key="resultsentry.tests.title" /></strong>
											</th>
											<th width="4%">
												<strong><bean:message key="resultsentry.tests.testisreportable.title" /></strong>
											</th>
											<th width="9%">
												<strong><bean:message key="resultsentry.tests.parentlink.title" /></strong>
											</th>
											<th width="20%">
												<strong><bean:message key="resultsentry.tests.component.title" /></strong>
											</th>
											<th width="24%">
												<strong><bean:message key="resultsEntry.tests.result.title" /></strong>
											</th>
											<th width="6%">
												<strong><bean:message key="resultsentry.tests.resultisreportable.title" /></strong>
											</th>
											<th width="10%">
												<strong><bean:message key="resultsentry.tests.notes.title" /></strong>
											</th>
										</tr>
									</table>
								</th>
						</tr>
						</thead>
						<tbody id="tblBody1">
<%--bugzilla 2556 moved View history link to sample level--%>
                        </logic:equal>
								<tr>
									<td width="8%" valign="top">
										<bean:write name="sample" property="receivedDateForDisplay" format="MM/dd/yyyy" />
									</td>
									<td width="12%" valign="top">
									   <a class="hoverinformation" title='<%=person.getFirstName() + " " + person.getLastName() + "/ " + patient.getExternalId() %>' onclick="return false;">
									    <bean:write name="sample" property="accessionNumber" /> 
									   </a>
							 		  <%--bugzilla 1900 link to preview results report--%>
								       <br>
									   <a href="" onclick='previewReport("<%=sample.getAccessionNumber()%>");return false;' style="color:blue;BACKGROUND-COLOR: #f7f7e7;">
                                         <bean:message key="batchresultsverification.label.hyperlink.preview.report" />
                                       </a><%--bugzilla 2556 View History link moved to here--%>
                					   <logic:equal name="s_ta" property="sampleHasTestRevisions" value="true">   
                					     <br>
                					     <a href="" onclick="resultsEntryHistoryBySamplePopup(document.forms[0], '<%= sample.getAccessionNumber()%>');return false;" style="color:blue;BACKGROUND-COLOR: #f7f7e7;">
                                           <bean:message key="resultsentry.label.hyperlink.history" />
                                       </a>
                					   </logic:equal>
 									</td>
									<td width="5%" valign="top">
									    <%--bugzilla 2146 --%>
										<html:button onclick='<%= "editThis(" + accn + ");"%>' disabled="<%=Boolean.valueOf(allowEdits).booleanValue()%>" property="view">
											<bean:message key="label.button.edit" />
										</html:button>
									</td>
									<td valign="top" width="75%">
										<logic:notEmpty name="s_ta" property="testTestAnalyte">
											<table width="100%" border="1" style="border-collapse: collapse">
											 <bean:define id="tst_ta" name="s_ta" property="testTestAnalyte" type="us.mn.state.health.lims.result.valueholder.Test_TestAnalyte"/>
											 <bean:define id="tas" name="tst_ta" property="testAnalytes" />
											 <bean:define id="test" name="tst_ta" property="test" />
											 <bean:define id="testId" name="test" property="id" />
											 <bean:define id="analysis" name="tst_ta" property="analysis" type="us.mn.state.health.lims.analysis.valueholder.Analysis" />
											 <bean:define id="analysisId" name="analysis" property="id" />
											 <bean:size id="ct" name="tas" />
											 <bean:define id="disabled" value="true" />
											 <logic:notEmpty name="tas">
  					   						   <tr height="100%">
												<td width="5%" rowspan="<%=ct%>" valign="top">
												   <html:multibox name='<%=formName%>' property="selectedRows">
                                               	      <bean:write name="sta_ctr" />
                                                   </html:multibox>
                                                </td>
												<td width="22%" rowspan="<%=ct%>" scope="row" valign="top">
													<strong><bean:write name="test" property="testDisplayValue" /></strong>
												</td>
												<td width="4%" rowspan="<%=ct%>" scope="row" valign="top">
											  	 <center>
											      <bean:write name="test" property="isReportable" />
											     </center>
												</td>
												<%--parent link column--%>
												<td width="9%" rowspan="<%=ct%>" scope="row" valign="top">
												 <logic:notEmpty name="analysis" property="parentAnalysis">
								                  <bean:define id="parentAnalysis" name="analysis" property="parentAnalysis" />
												  <bean:define id="parentResult" name="analysis" property="parentResult" />
												  <bean:define id="parentAnalysisTest" name="parentAnalysis" property="test" />
												  <bean:define id="parentAnalysisTestName" name="parentAnalysisTest" property="testDisplayValue" />
												  <bean:define id="parentResultValue" name="parentResult" property="value" />
												  <bean:define id="parentTestAnalyteName" name="parentResult" property="analyte.analyteName" />
												  <!--bug 2603-->
												  <div id='<%= "questionLink" + sta_ctr %>' style="background-color: #f7f7e7;">
                       	                            <html:button style="color:#000000; background-color: #00FFFF; font-size:80%;" property='<%= "questionLink" + sta_ctr %>' onclick="return false;">
  			                                             <bean:message key="resultsentry.button.parent.from.reflex"/>
  	     	                                        </html:button>
  	                                             </div>
  	                                             <div id='<%= "questionLink_tooltip" + sta_ctr %>' class='<%= "tooltip for_questionlink" + sta_ctr %>'>
							                      <span class="tooltip">
							                         <h4><%=tooltipheader%></h4>
							                         <% out.println(parentAnalysisTestName + ":" + parentTestAnalyteName + ":" + parentResultValue); %>
							                       </span>
	                                               </div>
											     </logic:notEmpty>
												 <logic:empty name="analysis" property="parentAnalysis">
										          	&nbsp;
										         </logic:empty>
												</td>
												<logic:iterate id="ta" indexId="taCtr" name="tst_ta" property="testAnalyteTestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults">
     													<td width="20%" valign="top">
	     													<bean:write name="ta" property="testAnalyte.analyte.analyteName" />:
														</td>
														<bean:define id="taId" name="ta" property="testAnalyte.analyte.id" />
														<td width="24%" valign="top">
															<logic:notEmpty name="ta" property="resultValue">
																<bean:write name="ta" property="resultValue" />
															</logic:notEmpty>
														</td>
														<td width="6%" valign="top">
															<center>
																<bean:write name="ta" property="resultIsReportable" />
															</center>
														</td>
														<td width="10%" valign="top">
															<%StringBuffer sbuf = new StringBuffer();
															String notes = "";
															if (ta != null) {
																List resultNotes = ((TestAnalyte_TestResults) ta)
																		.getResultNotes();
																for (int i = 0; i < resultNotes.size(); i++) {
																	if (i > 0) {
																		sbuf.append("\n");
																	}
																	Note note = (Note) resultNotes.get(i);
																	sbuf.append(note.getText());
																}
																notes = sbuf.toString();
															}
												
															%>
															<%--bugzilla 2244 notepad tooltip--%>
                                                            <div id='<%= "notepad" + sta_ctr + "" + taCtr %>'>
                                                             <a href="" onclick="return false;">
                                                               <div id='<%= "resultNote" + sta_ctr + "" + taCtr %>'
   	                                                            <% if (notes.equals("")){ %>
   	                    	                                         class="notepad" 
   	                                                            <% }else{ %>
   	                   		                                         class="notepaddata"
   	                                                            <% } %>
   	                                                            style="background-color: #f7f7e7;">&nbsp;</div>  
   	                   
                                                             </a>
                                                            </div>
											                <div id='<%= "notepad_tooltip" + sta_ctr + "" + taCtr %>' class='<%= "tooltip for_notepad" + sta_ctr + "" + taCtr %>'>
	                                                            <span class="tooltip">
	                                                             <%  
	                                                               int tempWidth = 0;
	   	                                                           if (ta != null) {
	   	                                                             List resultNotes = ((TestAnalyte_TestResults)ta).getResultNotes();
	   	                                                             //bugzilla 2446 sort notes, add heading, date and time
                                            	                     String noteType = "";
	   	                                                             for (int i = 0; i < resultNotes.size(); i++) {
	   	                                                               Note note = (Note)resultNotes.get(i);
   	                                                                   if (!note.getNoteType().equals(noteType)) {
                                               	                          noteType = note.getNoteType();
                                            	                          //don't break if first line
											   	                          if (i != 0) {
											   	                            out.println("<br/>");
											   	                          }
											   	                          if (noteType.equals(internalNote) ) {
											   	                            out.println("<b>" + internalNotesHeading +":</b>");
											   	                          } else if (noteType.equals(externalNote) ){
											   	                            out.println("<b>" + externalNotesHeading +":</b>");
											  	                          }
											   	                        }
											   	                        String dateAndTime = DateUtil.convertTimestampToStringDateAndTime(note.getLastupdated());
											   	                        //bugzilla 2480
                                              		  	                String text = dateAndTime + " - " + note.getSystemUser().getNameForDisplay() + " - " + note.getText();
							                                            if ( !StringUtil.isNullorNill(text)) {
	   	                                                                   out.println("<br/>");
	   	                                                                }
	                                                                   //wrap after 50 characters at first space found
	   	                                                               List list = StringUtil.createChunksOfText(text, 50, true);
	   	                                                               for (int textIndx = 0; textIndx < list.size(); textIndx++) {
	   	                                                                 String str = (String)list.get(textIndx);
	   	                                                                 if (textIndx > 0) {
	   	                                                                   out.println("<br/>");
	   	                                                                 }
	                                                                     out.println(str);
	  	                                                                 if (str.length() > tempWidth) {
	   	                                                                   tempWidth = str.length();
	   	                                                                 }
	   	                                                               }
	   	                                                             }
	   	                                                           }
	   	                                                        %>
	   	                                                        <script>
	   	                                                        //set textWidth in custom tooltip 
	   	                                                        var tt = document.getElementById('<%= "notepad_tooltip" + sta_ctr + "" + taCtr %>');
	   	                                                        tt.textWidth = <%= tempWidth%>;
	   	                                                        </script>
	                                                          </span>
	                                                        </div>
	                                                        <%--end bugzilla 2244 notepad tooltip--%>
															</td>
														</tr>
              										 </logic:iterate>
												</logic:notEmpty>
											</table>
										</logic:notEmpty>
									</td>
								</tr>
							</logic:iterate>
							</tbody>
						</table>
  					  </div>
				</table>
</logic:notEmpty>