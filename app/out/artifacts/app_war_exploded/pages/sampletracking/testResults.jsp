<%@ page language="java" contentType="text/html; charset=utf-8"
	import="java.util.Date,java.util.List,java.util.Locale,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.note.valueholder.Note,
	us.mn.state.health.lims.common.util.StringUtil,
	us.mn.state.health.lims.common.util.DateUtil,
	us.mn.state.health.lims.common.util.SystemConfiguration"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<bean:define id="analysisIdParm" value='<%= IActionConstants.ANALYSIS_ID%>' />
<%--bugzilla 2446--%>
<bean:define id="internalNote" value='<%= SystemConfiguration.getInstance().getNoteTypeInternal() %>' />
<bean:define id="externalNote" value='<%= SystemConfiguration.getInstance().getNoteTypeExternal() %>' />
<%--bugzilla 2300--%>
<bean:define id="canceledStatus" value='<%=SystemConfiguration.getInstance().getAnalysisStatusCanceled()%>' />

<%--AIS - bugzilla 1850 More changes--%>
<%--bugzilla 2028/2037--%>

<%
String Test = (String) request
					.getAttribute(IActionConstants.FORM_NAME);
			String idsep = SystemConfiguration.getInstance()
					.getDefaultIdSeparator();
%>

<%!
String allowEdits = "true";

String analysisId = "";
//bugzilla 2446
Locale locale = null;
String internalNotesHeading = "";
String externalNotesHeading = "";
%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
				allowEdits = (String) request
						.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
			}

			analysisId = (String) request.getAttribute("analysisId");
locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
//bugzilla 2446
internalNotesHeading = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "note.type.internal.heading");
externalNotesHeading = 
					us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(
					locale,
                    "note.type.external.heading");   
%>

<script language="JavaScript1.2">
//bugzilla 2227
function resultsEntryHistoryPopup (form, analysisId) {

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
 
    var param = '?' + '<%=analysisIdParm%>' + '=' + analysisId;
 	var href = context + "/ResultsEntryHistoryPopup.do" + param + sessionid;
    //alert("href "+ href);
	
	createPopup( href, null, null );
}
       
//bugzilla 2244- add prototype based Tooltip (for formatting and to fix timeout problem)
function pageOnLoad() {

var customTooltip = {

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
		   //200 is to adjust for difference between window width and scroll box width (trial and error)
           left = winWidth - 200 - (activator.Tooltip.textWidth * 7);
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
</script>
<table>

	<html:form action="/SampleTracking" method="post">
		<tr>
			<td>
				<strong><bean:message key="sampletracking.provider.samplestatus" />:</strong> &nbsp;
				<app:write name="<%=formName%>" property="status" />
			</td>

		</tr>
	</html:form>

	<tr>
		<td>
			&nbsp;
		</td>
	</tr>


	<tr>
		<td colspan="4">
			<h2 align="left">
				<bean:message key="sampletracking.subtitle.testresults" />
			</h2>
		</td>
	</tr>
	<tr>
		<td width="1140" height="300" valign="top">
			<div class="scrollhorizontalvertical" style="width:1140px;height:220px">
				<logic:iterate id="tst_ta" indexId="ctr" name="<%=formName%>" property="testTestAnalytes" type="us.mn.state.health.lims.result.valueholder.Test_TestAnalyte">
					<table width="830" style="border: 1px solid #000;">
						<bean:define id="tas" name="tst_ta" property="testAnalytes" />
						<bean:define id="test" name="tst_ta" property="test" />
						<bean:define id="testId" name="test" property="id" />
						<bean:define id="analysis" name="tst_ta" property="analysis" type="us.mn.state.health.lims.analysis.valueholder.Analysis"/>
						<bean:size id="ct" name="tas" />
						          
                        <%--bugzilla 2028 Qa Events determine value of hasPendingQaEvents--%>
                        <bean:define id="hasPendingQaEvents" value="false"/>
                        <logic:notEmpty name="tst_ta" property="analysisQaEvents">
                          <logic:iterate id="qaEvents" indexId="qaEvent_ctr" name="tst_ta" property="analysisQaEvents" type="us.mn.state.health.lims.analysisqaevent.valueholder.AnalysisQaEvent">
                            <logic:empty name="qaEvents" property="completedDate">
                               <% hasPendingQaEvents = "true"; %>
                            </logic:empty>
                         </logic:iterate>
                        </logic:notEmpty>
						<logic:iterate id="ta" indexId="taCtr" name="tas">
							<tr>
								<td colspan="4">
									<logic:equal name="taCtr" value="0">
										<table>
											<tr>
											    <td width="165">
  													<strong><bean:message key="sampletracking.provider.testname" />:</strong>&nbsp;
  												</td>
												<td width="442">
												    <%--bugzilla 1844, 2293--%>
 													<app:text name="test" property="testDisplayValue" styleId="testDisplayValue" styleClass="text" allowEdits="false" maxlength="30"/>
			                                        &nbsp;
													<%--bugzilla 2227 display revision numbers > 0--%>
	                                                <logic:notEmpty name="analysis" property="revision">
	                                                 <logic:notEqual name="analysis" property="revision" value="0">
	                                                   <font color="red"><bean:message key="resultsentry.label.revision.number"/>: <bean:write name="analysis" property="revision"/></font>
	                            	                 </logic:notEqual>
                                 	                </logic:notEmpty>
												</td>
												
												<td width="245">
													<strong><bean:message key="result.isReportable" />:</strong>&nbsp; <bean:write name="analysis" property="isReportable" />
												</td>
												<td width="5">

												</td>
												<td width="577">
													<strong><bean:message key="sampletracking.provider.analysisstatus" />:</strong>&nbsp;
													<app:write name="<%=formName%>" property='<%= "aStatus[" + ctr + "]" %>' />
												</td>
												
												<td width="400">
													<strong><bean:message key="sample.releasedDate" />:</strong>&nbsp; <bean:write name="analysis" property="releasedDateForDisplay" />
												</td>
											</tr>
									        <%--bugzilla 2037 change link color to blue and have a separate line for links, align with actual test name--%>
                                     		<tr>
        									    <td width="165">
  													&nbsp;
											    </td>
												<td width="442">
                                                   <%--bugzilla 2227 display revision numbers > 0--%>
                                                   <% if ((analysis.getRevision() != null && !analysis.getRevision().equals("0")) || (tst_ta.getAnalysisQaEvents() != null && ((List)tst_ta.getAnalysisQaEvents()).size() > 0)) { %>
                                                    <p style="BACKGROUND-COLOR: #f7f7e7">
                                                   <% } %>
	                                                <logic:notEmpty name="analysis" property="revision">
	                                                 <logic:notEqual name="analysis" property="revision" value="0">
	                                                  <a href="" onclick="resultsEntryHistoryPopup(document.forms[0], '<%=analysis.getId()%>');return false;" style="BACKGROUND-COLOR: #f7f7e7;color:blue">
                                                         <bean:message key="resultsentry.label.hyperlink.history" />
                                                      </a>	
                                   	                </logic:notEqual>
                                 	               </logic:notEmpty>
                                                   &nbsp;
                                                   <%--bugzilla 2227--%> 
													<%--bugzilla 2028, sub-bugzilla 2037--%>
                                                    <logic:notEmpty name="tst_ta" property="analysisQaEvents">
                                                       <%--bugzilla 2300--%>
                                                       <% if (!analysis.getStatus().equals(canceledStatus)) { %>
                                                        <html:link action="/ViewQaEventsEntryFromSampleTracking" paramName="<%=formName%>" paramId="accessionNumber" paramProperty="accessionNumber" style="BACKGROUND-COLOR: #f7f7e7;color:blue">
	                                                     <% if (hasPendingQaEvents.equals("true")) { %>
                                                             <bean:message key="resultsentry.label.hyperlink.pending.qaevents" />
                                                         <% } else { %>
                                                             <bean:message key="resultsentry.label.hyperlink.completed.qaevents" />
                                                         <% } %>
                                                        </html:link>
                                                       <% } else { %>
                                                         <% if (hasPendingQaEvents.equals("true")) { %>
                                                             <font color="blue"><bean:message key="resultsentry.label.hyperlink.pending.qaevents" /></font>
                                                         <% } else { %>
                                                             <font color="blue"><bean:message key="resultsentry.label.hyperlink.completed.qaevents" /></font>
                                                         <% } %>
                                                       <% } %>
                                                     </logic:notEmpty>
                                                   <% if ((analysis.getRevision() != null && !analysis.getRevision().equals("0")) || (tst_ta.getAnalysisQaEvents() != null && ((List)tst_ta.getAnalysisQaEvents()).size() > 0)) { %>
                                                   </p>
                                                   <% } %>
 												</td>
												
												<td width="245">
													&nbsp;
												</td>
												<td width="5">
                                                    &nbsp;
												</td>
												<td width="577">
												    &nbsp;
                     							</td>
												
												<td width="400">
												    &nbsp;
												</td>

											</tr>
										</table>
									</logic:equal>
								</td>
							</tr>
							
							<tr>
								<td width="5">
									<logic:equal name="taCtr" value="0">
        			 					<strong><bean:message key="sampletracking.provider.reportable" /></strong>
        			 				</logic:equal>
									
								</td>
								
								<td width="240">
									<logic:equal name="taCtr" value="0">
        			 					&nbsp; &nbsp; &nbsp; &nbsp;<strong><bean:message key="sampletracking.provider.components" /></strong>
									</logic:equal>
								</td>
								
								<td width="577">
									<logic:equal name="taCtr" value="0">
        			 					&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;
        			 					<strong><bean:message key="sampletracking.provider.result" /></strong>
									</logic:equal>
								</td>
								
								<td width="30">
								
								</td>
							</tr>

							<tr>
								<td width="5">
									<span> <bean:write name="tst_ta" property='<%= "results[" + taCtr + "].isReportable" %>' /> </span>
									<%--For design purpose, when blank--%>
									<logic:empty name="tst_ta" property='<%= "results[" + taCtr + "].value" %>'>
										<span style="color: #f7f7e7;">&nbsp;o</span>
									</logic:empty>
								</td>
								<td width="240">
									<app:text name="ta" property="analyte.analyteName" styleClass="text" disabled="true" size="50" />
								</td>
								<td width="577">
									<logic:notEmpty name="tst_ta" property='<%= "results[" + taCtr + "].value" %>'>
									    <%--bugzilla 2441--%>			
                                        <bean:define id="resultText" name="tst_ta" property='<%= "results[" + taCtr + "].value" %>' type="java.lang.String" />									    
                                        <%
                                            if ( resultText.length() > 100 ) { %>
                                                <app:textarea name="tst_ta" property='<%= "results[" + taCtr + "].value" %>' styleClass="text" readonly="true" cols="90" rows="3" />
                                        <%  } else { %>
										       <app:text name="tst_ta" property='<%= "results[" + taCtr + "].value" %>' styleClass="text" disabled="true" size="118" />
                                        <%  } %>                                           
									</logic:notEmpty>
									<logic:empty name="tst_ta" property='<%= "results[" + taCtr + "].value" %>'>
										<input type="text" name="empty" size="118" disabled="true" class="text">
									</logic:empty>
								</td>
								<td width="30">
								<bean:define id="notesList" name="tst_ta" property='<%= "notes[" + taCtr + "]" %>' type="java.util.ArrayList" />
									<%
										StringBuffer sb = new StringBuffer();
										String viewNotes = "";
										for (int i = 0; i < notesList.size(); i++) {
											if (i > 0) {
												sb.append("\n");
											}
											Note note = (Note) notesList.get(i);
											sb.append(note.getText());
										}
										viewNotes = sb.toString();

								%>
								   <%--bugzilla 2244--%>
				                   <a href="" onclick="return false;">
				                      <div id='<%= "notepad" + ctr + "" + taCtr%>'
				   	                    <% if (StringUtil.isNullorNill(viewNotes)){ %>
				   	                    	class="notepad" 
				   	                   <% }else{ %>
				   	                   		class="notepaddata"
				   	                   <% } %>
				   	                    style="background-color: #f7f7e7;">&nbsp;
				   	                 </div>  
				                   </a>
    			                   <%--bugzilla 2244 notepad tooltip--%>
			                       <div id='<%= "notepad_tooltip" + ctr + "" + taCtr %>' class='<%= "tooltip for_notepad" + ctr + "" + taCtr %>'>
				                     <span class="tooltip">
				                       <%  
				                        int tempWidth = 0;
				                        //bugzilla 2446 sort notes, add heading, date and time
                   	                    String noteType = "";
				                        
			  	                        for (int i = 0; i < notesList.size(); i++) {
			   	                        Note note = (Note)notesList.get(i);
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
			   	                        String dateAndTime = DateUtil.convertTimestampToStringDateAndTime(note.getLastupdated(), locale.toString());
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
				   	                   
				   	                   %>
				   	                   <script>
				   	                     //set textWidth in custom tooltip 
				   	                     var tt = document.getElementById('<%= "notepad_tooltip" + ctr + "" + taCtr %>');
				   	                     tt.textWidth = <%= tempWidth%>;
				   	                   </script>
				                     </span>
				                  </div>
				                  <%--end bugzilla 2244 notepad tooltip--%>
								</td>

                                <%--bugzilla 1908 removed currRecPos--%>
								<logic:equal name="taCtr" value='<%= (ct--).toString()%>'>
									<br>
								</logic:equal>
							</tr>
						</logic:iterate>
					</table>
				</logic:iterate>
			</div>
		</td>
	</tr>
</table>
