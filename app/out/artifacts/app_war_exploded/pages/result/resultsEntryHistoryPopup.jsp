<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date, java.util.List, java.util.Locale,
	org.apache.struts.Globals,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.util.StringUtil,
	us.mn.state.health.lims.note.valueholder.Note,
	us.mn.state.health.lims.testresult.valueholder.TestResult,
	us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults,
	us.mn.state.health.lims.common.util.SystemConfiguration" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<bean:define id="textSeparator" value='<%= SystemConfiguration.getInstance().getDefaultTextSeparator() %>' />
<bean:define id="selectedTestIdParm" value='<%= IActionConstants.SELECTED_TEST_ID%>' />
<bean:define id="analysisIdParm" value='<%= IActionConstants.ANALYSIS_ID%>' />
<bean:define id="analyteIdParm" value='<%= IActionConstants.ANALYTE_ID%>' />
<bean:define id="dictType" value='<%= SystemConfiguration.getInstance().getDictionaryType() %>' />
<bean:define id="titerType" value='<%= SystemConfiguration.getInstance().getTiterType() %>' />
<bean:define id="numericType" value='<%= SystemConfiguration.getInstance().getNumericType() %>' />

<%!

String allowEdits = "true";
String analysisId = "";

String path = "";
String basePath = "";

%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}

//this is the test selected for editing
analysisId = (String)request.getAttribute(IActionConstants.ANALYSIS_ID);
%>

<table width="100%" border="1">
      <tr>
          <td colspan="1" scope="col" width="5%" bgcolor="#CCCC99"><strong><bean:message key="resultsentry.history.revision.title"/></strong></td>
          <td colspan="1" scope="col" width="10%" bgcolor="#CCCC99"><strong><bean:message key="resultsentry.history.printed.date.title"/></strong></td>
          <td colspan="1" scope="col" width="20%" bgcolor="#CCCC99"><strong><bean:message key="resultsentry.history.component.title"/></strong></td>
          <td colspan="1" scope="col" width="20%" bgcolor="#CCCC99"><strong><bean:message key="resultsentry.history.result.title"/></strong></td>
      </tr>
        <logic:notEmpty name="<%=formName%>" property="historyTestTestAnalytes">
         <logic:iterate id="hist_tst_ta" indexId="ctr" name="<%=formName%>" property="historyTestTestAnalytes" type="us.mn.state.health.lims.result.valueholder.Test_TestAnalyte">
          <bean:define id="hist_tas" name="hist_tst_ta" property="testAnalytes" />
          <bean:define id="hist_results" name="hist_tst_ta" property="results" type="us.mn.state.health.lims.result.valueholder.Result[]"/>
          <bean:define id="hist_test" name="hist_tst_ta" property="test" />
          <bean:define id="hist_testId" name="hist_test" property="id" />
          <bean:define id="hist_analysis" name="hist_tst_ta" property="analysis" type="us.mn.state.health.lims.analysis.valueholder.Analysis" />
          <bean:size id="hist_ct" name="hist_tas" />

          <logic:notEmpty name="hist_tas">
           <logic:iterate id="hist_ta" indexId="hist_taCtr" name="hist_tst_ta" property="testAnalyteTestResults" type="us.mn.state.health.lims.result.valueholder.TestAnalyte_TestResults">
           <bean:define id="hist_result" name="hist_tst_ta" property='<%= "results[" + hist_taCtr + "]"%>'/>
            <tr height="100%">
        	 <logic:equal name="hist_taCtr" value="0">
              <td rowspan="<%=hist_ct%>" scope="row" valign="top">
                 <logic:notEmpty name="hist_analysis" property="revision">
	                 <logic:notEqual name="hist_analysis" property="revision" value="0">
	                        <bean:write name="hist_analysis" property="revision"/>
                     </logic:notEqual>
                     <logic:equal name="hist_analysis" property="revision" value="0">
	                        &nbsp;
                     </logic:equal>
	               </logic:notEmpty>
	               <logic:empty name="hist_analysis" property="revision">
	                     &nbsp;
	               </logic:empty>
	           </td>
              <td rowspan="<%=hist_ct%>" scope="row" valign="top">
                 <logic:notEmpty name="hist_analysis" property="printedDate">
	                     <bean:write name="hist_analysis" property="printedDateForDisplay"/>
  		  	     </logic:notEmpty>
  		  	     <logic:empty name="hist_analysis" property="printedDate">
	                       &nbsp;
  		  	     </logic:empty>
               </td>
	          </logic:equal>
              <td>
                  <bean:write name="hist_ta" property="testAnalyte.analyte.analyteName"/>:
              </td>
              <td>
                  <logic:notEmpty name="hist_result" property="value">
                   <logic:notEqual name="hist_result" property="resultType" value="<%=titerType%>">
                    <bean:write name="hist_result" property="value" /> 	     		  
  	               </logic:notEqual>
  	               <logic:equal name="hist_result" property="resultType" value="<%=titerType%>">
   	                <table width="50">
	                     <tr>	                     
		                     <td width="10">1:</td>
		                     <td width="40">	
		                     	<bean:write name="hist_result" property="value"/> 	     		  
		                 	 </td>
	                     </tr>
                     </table>
  	               </logic:equal>
  	              </logic:notEmpty>
               </td>
              </tr>
            </logic:iterate>
          </logic:notEmpty>
       </logic:iterate>
    </logic:notEmpty> 
 </table>
 

