<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date, java.util.List, java.util.Locale,
	org.apache.struts.Globals,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.StringUtil,
	us.mn.state.health.lims.common.util.DateUtil,
	us.mn.state.health.lims.note.valueholder.Note,
    us.mn.state.health.lims.qaevent.valueholder.QaEvent,
	us.mn.state.health.lims.sampleqaevent.valueholder.SampleQaEvent,
	us.mn.state.health.lims.sampleqaeventaction.valueholder.SampleQaEventAction,
	us.mn.state.health.lims.common.util.SystemConfiguration" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<%--bugzilla 2053, 2501, 2504, 2502--%>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<bean:define id="textSeparator" value='<%= SystemConfiguration.getInstance().getDefaultTextSeparator() %>' />
<bean:define id="noteRefTableIdForSample" value='<%= SystemConfiguration.getInstance().getSampleQaEventActionReferenceTableId() %>' />
<bean:define id="qaEventIdParm" value='<$=IActionConstants.QA_EVENT_ID%>' />
<bean:define id="internalNote" value='<%= SystemConfiguration.getInstance().getNoteTypeInternal() %>' />
<bean:define id="externalNote" value='<%= SystemConfiguration.getInstance().getNoteTypeExternal() %>' />

<html:hidden property="addQaEventPopupSelectedQaEventIdsForSample" name="<%=formName%>" />
<html:hidden property="addActionPopupSelectedSampleQaEventIds" name="<%=formName%>" />
<html:hidden property="addActionPopupSelectedActionIdsForSample" name="<%=formName%>" />


<%!

String allowEdits = "true";

Locale locale = null;

String notes = "";

String path = "";
String basePath = "";
String internalNotesHeading = "";
String externalNotesHeading = "";
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
request.setAttribute(IActionConstants.ADD_DISABLED_ALL_SAMPLE_QAEVENTS_COMPLETED, "true");

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


function submitTheFormWithNotes(form, notesRefId, noteRefTableId) {
   document.forms[0].noteRefId.value = notesRefId; 
   document.forms[0].noteRefTableId.value = noteRefTableId;   
   //alert(" I am in submitTheFormWithNotes");
   setAction(form, 'NotesUpdate', 'yes', '');
}

function myPopupNotesForSample(form, actionId, externalNotesDisabled) {
     popupNotes(document.forms[0], '<%=noteRefTableIdForSample%>', actionId, externalNotesDisabled);
}

function popupAddEventsToSample(form) {
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
	var href = context+"/QaEventsEntryAddQaEventsToSamplePopup.do?accessionNumber="+myfield.value+"&"+sessionid;
	//alert("href "+ href);
	createPopup(href, 880, 500);
}

function popupAddActionsToSample(form) {
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
	var href = context+"/QaEventsEntryAddActionsToSampleQaEventsPopup.do?accessionNumber="+myfield.value+"&"+sessionid;
	//alert("href "+ href);
	createPopup(href, 880, 500);
}

function saveThis(form) {
   setAction(window.document.forms[0], 'Update', 'yes', '');
}

function initializeNotes() {
    //initialize
    document.forms[0].noteIds.value = '';
    document.forms[0].noteSubjects.value = '';
    document.forms[0].noteTexts.value = '';
    document.forms[0].noteTypes.value = '';
    document.forms[0].noteLastupdateds.value = '';
}

//This is for notes from notesPopup
function setANote(form, id, subject, text, type, lastupdated) {

    document.forms[0].noteIds.value += id + '<%=textSeparator%>';
    if (subject == null || subject == '') {
      document.forms[0].noteSubjects.value += ' ' + '<%=textSeparator%>';
    } else {
      document.forms[0].noteSubjects.value += subject + '<%=textSeparator%>';
    }
    if (text== null || text == '') {
      document.forms[0].noteTexts.value += ' ' + '<%=textSeparator%>';
    } else {
      document.forms[0].noteTexts.value += text + '<%=textSeparator%>';
    }
    if (type == null || type == '') {
      document.forms[0].noteTypes.value += ' ' + '<%=textSeparator%>';
    } else {
      document.forms[0].noteTypes.value += type + '<%=textSeparator%>';
    }
    document.forms[0].noteLastupdateds.value += lastupdated + '<%=textSeparator%>';
}


//This is for the ADD QA Event functionality
function setAddSampleQaEventResults(addQaEventForm)
{
	var popupSelectListOptions = addQaEventForm.SelectList.options
	var thisForm = document.forms[0];
    //initialize
    document.forms[0].addQaEventPopupSelectedQaEventIdsForSample.value = '';
    
    for (var i = 0; i < popupSelectListOptions.length; i++) 
    { 
     if (popupSelectListOptions[i].selected) {
         document.forms[0].addQaEventPopupSelectedQaEventIdsForSample.value += '<%=idSeparator%>';
         document.forms[0].addQaEventPopupSelectedQaEventIdsForSample.value += popupSelectListOptions[i].value;        
     }
        
    }

    //submit parent form
    setAction(document.forms[0], 'Update', 'yes', '?ID=');
}

//This is for the ADD QA Action functionality
function setAddSampleActionResults(addActionForm)
{
	var popupPickListOptions = addActionForm.PickList.options;
	var popupSelectListOptions = addActionForm.SelectList.options
	var thisForm = document.forms[0];
    //initialize
    document.forms[0].addActionPopupSelectedSampleQaEventIds.value = '';
    document.forms[0].addActionPopupSelectedActionIdsForSample.value = '';
    
    for (var i = 0; i < popupSelectListOptions.length; i++) 
    { 
     if (popupSelectListOptions[i].selected) {
         document.forms[0].addActionPopupSelectedActionIdsForSample.value += '<%=idSeparator%>';
         document.forms[0].addActionPopupSelectedActionIdsForSample.value += popupSelectListOptions[i].value;        
     }
        
    }
    for (var i = 0; i < popupPickListOptions.length; i++) 
    { 
     if (popupPickListOptions[i].selected) {
         document.forms[0].addActionPopupSelectedSampleQaEventIds.value += '<%=idSeparator%>';
         document.forms[0].addActionPopupSelectedSampleQaEventIds.value += popupPickListOptions[i].value;        
      }
        
    }
    //submit parent form
    setAction(document.forms[0], 'Update', 'yes', '?ID=');
}

//bugzilla 2471 (like 2244)- add prototype based Tooltip (for formatting and to fix timeout problem)
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
<%--bugzilla 2501--%>
<logic:notEmpty name="<%=formName%>" property="accessionNumber">
<table width="100%">
  <tr>
    <td width="100%" valign="top">
      <div id="sampleDiv" class="scrollvertical" style="height:115px;width:100%">
       <table width="100%">
         <logic:notEmpty name="<%=formName%>" property="sampleQaEvents">
          <%--external notes are disabled--%>
          <bean:define id="disabled" value="true"/>
          <logic:iterate id="sampleQaEvent" indexId="samp_qaEv_ctr" name="<%=formName%>" property="sampleQaEvents" type="us.mn.state.health.lims.qaevent.valueholder.Sample_QaEvent_Actions">
            <bean:define id="sampQaEvent" name="sampleQaEvent" property="qaEvent" type="us.mn.state.health.lims.sampleqaevent.valueholder.SampleQaEvent"/>
            <bean:define id="sampQaEventID" name="sampQaEvent" property="id"/>
            <bean:define id="qaEvent" name="sampQaEvent" property="qaEvent" type="us.mn.state.health.lims.qaevent.valueholder.QaEvent" />
               <tr width="100%">
                  <td scope="row">&nbsp;</td>
               </tr>
               <tr width="100%">
                 <td width="5%">&nbsp;</td>
  	             <td width="32%">
                  <strong><bean:message key="qaeventsentry.qaevent.title"/>:</strong>
                   &nbsp;
                  <bean:write name="qaEvent" property="qaEventDisplayValue"/>
                 </td>
 	             <td width="20%">
                  <strong><bean:message key="qaeventsentry.qaevent.type.title"/>:</strong>
                   &nbsp;
                  <%--bugzilla 2033--%>
                  <bean:write name="qaEvent" property="type.dictEntry"/>
                 </td>
 	             <td width="20%">
                  <strong><bean:message key="qaeventsentry.qaevent.category.title"/>:</strong>
                   &nbsp;
                   <logic:notEmpty name="qaEvent" property="category">
                   <bean:write name="qaEvent" property="category.dictEntry"/>
                   </logic:notEmpty>
                 </td>
                 <logic:empty name="sampQaEvent" property="completedDate">
                 <% 
                    //bugzilla 2033 disable add actions if events are completed
                    request.setAttribute(IActionConstants.ADD_DISABLED_ALL_SAMPLE_QAEVENTS_COMPLETED, "false"); 
                 %>
                 <td width="23%" valign="top" align="right">
                    <strong><bean:message key="qaeventsentry.qaevent.notcompleted.title"/></strong>
                     &nbsp;
           	         <html:multibox name="<%=formName%>" property="selectedSampleQaEventIdsForCompletion">
	                     <bean:write name="sampQaEventID" />
	                 </html:multibox> 
	              </td>
	              </logic:empty>
	              <logic:notEmpty name="sampQaEvent" property="completedDate">
	               <td width="30%" valign="top" align="right">
                    <strong><bean:message key="qaeventsentry.qaevent.completed.title"/></strong>
                     &nbsp;
                     <bean:write name="sampQaEvent" property="completedDateForDisplay" />
                   </td>
                    <html:hidden name="<%=formName%>" property="selectedSampleQaEventIdsForCompletion" value="<%=String.valueOf(sampQaEventID)%>" styleId="selectedSampleQaEventIdsForCompletion"/> 
	              </logic:notEmpty>
                </tr>
                <logic:notEmpty name="sampleQaEvent" property="actions">
                <tr>
                 <td colspan="1">&nbsp;</td>
                 <td colspan="4">
                 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                 <table width="100%" border="1">
                 <logic:iterate id="sampQaEventAction" indexId="samp_qaEvAct_ctr" name="sampleQaEvent" property="actions" type="us.mn.state.health.lims.sampleqaeventaction.valueholder.SampleQaEventAction">
                  <bean:define id="refIdForSample" name="sampQaEventAction" property="id"/>
                  <bean:define id="actionNotes" name="sampleQaEvent" property='<%= "notes[" + samp_qaEvAct_ctr + "]"%>' type="java.util.List"/>
                  <bean:define id="action" name="sampQaEventAction" property="action"/>
                   <logic:equal name="samp_qaEvAct_ctr" value="0">
                    <tr width="100%">
                      <td width="15%">
                        <strong><bean:message key="qaeventsentry.action.code.title"/></strong>
                     </td>
                     <td width="45%">
                        <strong><bean:message key="qaeventsentry.action.title"/></strong>
                     </td>
                     <%--bugzilla 2481--%>
                     <td width="10%">
                        <strong><bean:message key="qaeventsentry.action.owner"/></strong>
                     </td>
                     <td width="15%">
                        <strong><bean:message key="qaeventsentry.action.date.title"/></strong>
                     </td>
                     <td width="10%">
                        <strong><bean:message key="qaeventsentry.action.notes.title"/></strong>
                     </td>
                    </tr>
                   </logic:equal>
                   <tr width="100%">
                     <td width="15%">
                      <bean:write name="action" property="code"/>
                    </td>
                    <td width="45%">
                      <bean:write name="action" property="description"/>
                    </td>
                    <%--bugzilla 2481--%>
                    <td width="10%">
                      <bean:write name="sampQaEventAction" property="systemUser.nameForDisplay"/>
                    </td>
                    <td width="15%">
                      <bean:write name="sampQaEventAction" property="createdDateForDisplay"/>
                    </td>
                      <%
                        //bugzilla 2471 find out if we have notes or not
   	                    StringBuffer sbuf = new StringBuffer();
   	                    notes = "";
   	                    if (actionNotes != null) {
   	                      for (int i = 0; i < actionNotes.size(); i++) {
   	                        Note note = (Note)actionNotes.get(i);
 	                        String text = note.getText();
   	                        sbuf.append(text);
   	                      }
   	                      notes = sbuf.toString();
   	                    }
                      %> 
                  <td width="10%">
      				   <%--bugzilla 2471--%>
	                   <a href="" onclick="myPopupNotesForSample(document.forms[0], '<%=refIdForSample%>', '<%=disabled%>');return false;">
	                      <div id='<%= "notepad" + samp_qaEv_ctr + "" + samp_qaEvAct_ctr%>'
	   	                    <% if (StringUtil.isNullorNill(notes)){ %>
	   	                    	class="notepad" 
	   	                   <% }else{ %>
	   	                   		class="notepaddata"
	   	                   <% } %>
	   	                    style="background-color: #f7f7e7;">&nbsp;
	   	                 </div>  
	                   </a>
	                   <%--bugzilla 2471 notepad tooltip--%>
                       <div id='<%= "notepad_tooltip" + samp_qaEv_ctr + "" + samp_qaEvAct_ctr %>' class='<%= "tooltip for_notepad" + samp_qaEv_ctr + "" + samp_qaEvAct_ctr %>'>
	                     <span class="tooltip">
	                       <%  
	                        int tempWidth = 0;
	                        //bugzilla 2446 sort notes, add heading, date and time
       	                    String noteType = "";
	                        
  	                        for (int i = 0; i < actionNotes.size(); i++) {
   	                          Note note = (Note)actionNotes.get(i);
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
	   	                     var tt = document.getElementById('<%= "notepad_tooltip" + samp_qaEv_ctr + "" + samp_qaEvAct_ctr %>');
	   	                     tt.textWidth = <%= tempWidth%>;
	   	                   </script>
	                     </span>
	                  </div>
	                  <%--end bugzilla 2471 notepad tooltip--%>
                 </td>
                </tr>
                </logic:iterate>
                </table>
                </td>
               </tr>
              </logic:notEmpty>
              <logic:empty name="sampleQaEvent" property="actions">
               <tr width="100%">
                <td width="5%">&nbsp;</td>
                <td colspan="3"><strong><bean:message key="qaeventsentry.qaevent.error.sample.action.notAssigned" /></strong></td>
              </tr>
              </logic:empty>
          </logic:iterate>
          </logic:notEmpty> 
          <logic:empty name="<%=formName%>" property="sampleQaEvents">
              <tr width="100%">
                <td width="5%">&nbsp;</td>
                <td colspan="3"><strong><bean:message key="qaeventsentry.qaevent.error.sample.qaevent.notAssigned" /></strong></td>
              </tr>
           </logic:empty>
         </table>
        <%--bugzilla 2446 need additional line at the bottom so that tooltips don't go offscreen--%>
        <br/>
      </div>
    </td>
  </tr>  
</table>
</logic:notEmpty>
