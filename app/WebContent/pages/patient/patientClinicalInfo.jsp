<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
                 us.mn.state.health.lims.common.util.SystemConfiguration,
                 us.mn.state.health.lims.common.formfields.FormFields,
                 us.mn.state.health.lims.common.formfields.FormFields.Field,
                 us.mn.state.health.lims.common.util.StringUtil,
                 us.mn.state.health.lims.common.util.Versioning,
                 us.mn.state.health.lims.patient.action.bean.PatientManagmentInfo" %>


<%@ taglib uri="/tags/struts-bean"		prefix="bean" %>
<%@ taglib uri="/tags/struts-html"		prefix="html" %>
<%@ taglib uri="/tags/struts-logic"		prefix="logic" %>
<%@ taglib uri="/tags/struts-tiles"		prefix="tiles" %>
<%@ taglib uri="/tags/struts-nested"	prefix="nested" %>
<%@ taglib uri="/tags/labdev-view"		prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>



<bean:define id="formName"		value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="patientProperties" name='<%=formName%>' property='patientProperties' type="PatientManagmentInfo" />


<%!
	String basePath = "";

 %>
<%
	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":"
			+ request.getServerPort() + path + "/";

%>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>

<script type="text/javascript" >


</script>
<nested:hidden name='<%=formName%>' property="patientProperties.currentDate" styleId="currentDate"/>

<div id="PatientClinicalPage" style="display:inline"  >
	<% if( FormFields.getInstance().useField(Field.SampleEntryPatientClinical)){ %>
	<h1><bean:message key="patient.clinical.head" /></h1><h2><bean:message key="patient.clinical.treatmentStatus"/></h2><br>
	<h3><bean:message key="patient.clinical.history"/></h3>
	<table style="width:80%">
		<tr>
			<td width="37%">(1)<bean:message key="patient.clinical.history.tb"/></td>
			<td width="10%">Oui/Non/Unk</td>
			<td width="6%">&nbsp;</td>
			<td width="37%">(2)<bean:message key="patient.clinical.history.std"/></td>
			<td width="10%">Oui/Non/Unk</td>
		</tr>
		<tr>
			<td align="right"><bean:message key="patient.clinical.tb.extraPulmonary" /></td>
			<td><input type="radio" name="patientClinicalProperties.tbExtraPulmanary" value="yes" >
			    <input type="radio" name="patientClinicalProperties.tbExtraPulmanary" value="no" >
			    <input type="radio" name="patientClinicalProperties.tbExtraPulmanary" value="unknown" checked="checked">
			</td>
			<td>&nbsp;</td>
			<td align="right"><bean:message key="patient.clinical.std.colon" /></td>
			<td><input type="radio" name="patientClinicalProperties.stdColonCancer" value="yes" >
			    <input type="radio" name="patientClinicalProperties.stdColonCancer" value="no" >
			    <input type="radio" name="patientClinicalProperties.stdColonCancer" value="unknown" checked="checked">
			</td>    
		</tr>
		<tr>
			<td align="right"><bean:message key="patient.clinical.tb.cerebral" /></td>
			<td><input type="radio" name="patientClinicalProperties.tbCerebral" value="yes" >
			    <input type="radio" name="patientClinicalProperties.tbCerebral" value="no" >
			    <input type="radio" name="patientClinicalProperties.tbCerebral" value="unknown" checked="checked">
			</td>
			<td>&nbsp;</td>
			<td align="right"><bean:message key="patient.clinical.std.candidose" /></td>
			<td><input type="radio" name="patientClinicalProperties.stdCandidiasis" value="yes" >
			    <input type="radio" name="patientClinicalProperties.stdCandidiasis" value="no" >
			    <input type="radio" name="patientClinicalProperties.stdCandidiasis" value="unknown" checked="checked">
			</td>    
		</tr>
		<tr>
			<td align="right"><bean:message key="patient.clinical.tb.meningitis" /></td>
			<td><input type="radio" name="patientClinicalProperties.tbMenigitis" value="yes" >
			    <input type="radio" name="patientClinicalProperties.tbMenigitis" value="no" >
			    <input type="radio" name="patientClinicalProperties.tbMenigitis" value="unknown" checked="checked">
			</td>
			<td>&nbsp;</td>
			<td align="right"><bean:message key="patient.clinical.std.kaposi" /></td>
			<td><input type="radio" name="patientClinicalProperties.stdKaposi" value="yes" >
			    <input type="radio" name="patientClinicalProperties.stdKaposi" value="no" >
			    <input type="radio" name="patientClinicalProperties.stdKaposi" value="unknown" checked="checked">
			</td>    
		</tr>
		<tr>
			<td align="right"><bean:message key="patient.clinical.tb.prurigo" /></td>
			<td><input type="radio" name="patientClinicalProperties.tbPrurigol" value=>
			    <input type="radio" name="patientClinicalProperties.tbPrurigol" value="no" >
			    <input type="radio" name="patientClinicalProperties.tbPrurigol" value="unknown" checked="checked">
			</td>
			<td>&nbsp;</td>
			<td align="right"><bean:message key="patient.clinical.std.zona" /></td>
			<td><input type="radio" name="patientClinicalProperties.stdZona" value="yes" >
			    <input type="radio" name="patientClinicalProperties.stdZona" value="no" >
			    <input type="radio" name="patientClinicalProperties.stdZona" value="unknown" checked="checked">
			</td>    
		</tr>
		<tr>
			<td align="right"><bean:message key="patient.clinical.tb.diarrhae" /></td>
			<td><input type="radio" name="patientClinicalProperties.tbDiarrhae" value="yes" >
			    <input type="radio" name="patientClinicalProperties.tbDiarrhae" value="no" >
			    <input type="radio" name="patientClinicalProperties.tbDiarrhae" value="unknown" checked="checked">
			</td>
			<td>&nbsp;</td>
			<td align="right" colspan="2"><bean:message key="other" />
				<html:text name='<%= formName %>' property="patientClinicalProperties.stdOther" />
			</td>
		</tr>
		<tr><td colspan="5" >&nbsp;</td></tr>
		<tr>
			<td colspan="4" align="right"><bean:message key="patient.clinical.prophylaxis.arv"/></td>
			<td><input type="radio" name="patientClinicalProperties.arvProphyaxixReceiving" value="yes" >
			    <input type="radio" name="patientClinicalProperties.arvProphyaxixReceiving" value="no" >
			    <input type="radio" name="patientClinicalProperties.arvProphyaxixReceiving" value="unknown" checked="checked">
			</td>
		</tr>
		<tr>
			<td colspan="4" align="right"><bean:message key="patient.clinical.prophylaxis.arv.type"/></td>
			<td>
				<html:select name='<%= formName %>' property="patientClinicalProperties.arvProphyaxixType">
					<html:optionsCollection name='<%= formName %>' property="patientClinicalProperties.prophyaxixTypes" label="value" value="value"/>
				</html:select>
			</td>
		</tr>
		<tr>
			<td colspan="4" align="right"><bean:message key="patient.clinical.treatment.arv"/></td>
			<td><input type="radio" name="patientClinicalProperties.arvTreatmentReceiving" value="yes" >
			    <input type="radio" name="patientClinicalProperties.arvTreatmentReceiving" value="no" >
			    <input type="radio" name="patientClinicalProperties.arvTreatmentReceiving" value="unknown" checked="checked">
			</td>
		</tr>
		<tr>
			<td colspan="4" align="right"><bean:message key="patient.clinical.treatment.arv.recall"/></td>
			<td><input type="radio" name="patientClinicalProperties.arvTreatmentRemembered" value="yes" >
			    <input type="radio" name="patientClinicalProperties.arvTreatmentRemembered" value="no" >
			    <input type="radio" name="patientClinicalProperties.arvTreatmentRemembered" value="unknown" checked="checked">
			</td>
		</tr>
		<tr>
			<td colspan="4" align="right"><bean:message key="patient.clinical.treatment.arv.type1"/></td>
			<td><html:text name='<%= formName %>' property="patientClinicalProperties.arvTreatment1" />
			</td>
		</tr>
		<tr>
			<td colspan="4" align="right"><bean:message key="patient.clinical.treatment.arv.type2"/></td>
			<td><html:text name='<%= formName %>' property="patientClinicalProperties.arvTreatment2" />
			</td>
		</tr>
		<tr>
			<td colspan="4" align="right">&nbsp;</td>
			<td><html:text name='<%= formName %>' property="patientClinicalProperties.arvTreatment3" />
			</td>
		</tr>
		<tr>
			<td colspan="4" align="right">&nbsp;</td>
			<td><html:text name='<%= formName %>' property="patientClinicalProperties.arvTreatment4" />
			</td>
		</tr>
		<tr><td colspan="5" >&nbsp;</td></tr>
		<tr>
			<td colspan="4" align="right"><bean:message key="patient.clinical.prophylaxis.cotrimoxazole"/></td>
			<td><input type="radio" name="patientClinicalProperties.cotrimoxazoleReceiving" value="yes" >
			    <input type="radio" name="patientClinicalProperties.cotrimoxazoleReceiving" value="no" >
			    <input type="radio" name="patientClinicalProperties.cotrimoxazoleReceiving" value="unknown" checked="checked">
			</td>
		</tr>
		<tr>
			<td colspan="4" align="right"><bean:message key="patient.clinical.cotrimoxazole.stage"/></td>
			<td>
				<html:select name='<%= formName %>' property="patientClinicalProperties.cotrimoxazoleType">
					<html:optionsCollection name='<%= formName %>' property="patientClinicalProperties.arvStages" label="value" value="value"/>
				</html:select>
			</td>
		</tr>
		<tr>
			<td colspan="4"><bean:message key="patient.clinical.infection"/></td>
		</tr>
		<tr>
			<td colspan="3">(1)<bean:message key="patient.clinical.history.tb"/></td>
			<td colspan="2">(2)<bean:message key="patient.clinical.history.std"/></td>
		</tr>	
		<tr>
			<td align="right"><bean:message key="patient.clinical.tb.extraPulmonary" /></td>
			<td><input type="radio" name="patientClinicalProperties.infectionExtraPulmanary" value="yes" >
			    <input type="radio" name="patientClinicalProperties.infectionExtraPulmanary" value="no" >
			    <input type="radio" name="patientClinicalProperties.infectionExtraPulmanary" value="unknown" checked="checked">
			</td>
			<td>&nbsp;</td>
			<td align="right"><bean:message key="patient.clinical.std.colon" /></td>
			<td><input type="radio" name="patientClinicalProperties.stdInfectionColon" value="yes" >
			    <input type="radio" name="patientClinicalProperties.stdInfectionColon" value="no" >
			    <input type="radio" name="patientClinicalProperties.stdInfectionColon" value="unknown" checked="checked">
			</td>    
		</tr>
		<tr>
			<td align="right"><bean:message key="patient.clinical.tb.cerebral" /></td>
			<td><input type="radio" name="patientClinicalProperties.infectionCerebral" value="yes" >
			    <input type="radio" name="patientClinicalProperties.infectionCerebral" value="no" >
			    <input type="radio" name="patientClinicalProperties.infectionCerebral" value="unknown" checked="checked">
			</td>
			<td>&nbsp;</td>
			<td align="right"><bean:message key="patient.clinical.std.candidose" /></td>
			<td><input type="radio" name="patientClinicalProperties.stdInfectionCandidiasis" value="yes" >
			    <input type="radio" name="patientClinicalProperties.stdInfectionCandidiasis" value="no" >
			    <input type="radio" name="patientClinicalProperties.stdInfectionCandidiasis" value="unknown" checked="checked">
			</td>    
		</tr>
		<tr>
			<td align="right"><bean:message key="patient.clinical.tb.meningitis" /></td>
			<td><input type="radio" name="patientClinicalProperties.infectionMeningitis" value="yes" >
			    <input type="radio" name="patientClinicalProperties.infectionMeningitis" value="no" >
			    <input type="radio" name="patientClinicalProperties.infectionMeningitis" value="unknown" checked="checked">
			</td>
			<td>&nbsp;</td>
			<td align="right"><bean:message key="patient.clinical.std.kaposi" /></td>
			<td><input type="radio" name="patientClinicalProperties.stdInfectionKaposi" value="yes" >
			    <input type="radio" name="patientClinicalProperties.stdInfectionKaposi" value="no" >
			    <input type="radio" name="patientClinicalProperties.stdInfectionKaposi" value="unknown" checked="checked">
			</td>    
		</tr>
		<tr>
			<td align="right"><bean:message key="patient.clinical.tb.prurigo" /></td>
			<td><input type="radio" name="patientClinicalProperties.infectionPrurigol" value="yes" >
			    <input type="radio" name="patientClinicalProperties.infectionPrurigol" value="no" >
			    <input type="radio" name="patientClinicalProperties.infectionPrurigol" value="unknown" checked="checked">
			</td>
			<td>&nbsp;</td>
			<td align="right"><bean:message key="patient.clinical.std.zona" /></td>
			<td><input type="radio" name="patientClinicalProperties.stdInfectionZona" value="yes" >
			    <input type="radio" name="patientClinicalProperties.stdInfectionZona" value="no" >
			    <input type="radio" name="patientClinicalProperties.stdInfectionZona" value="unknown" checked="checked">
			</td>    
		</tr>
		<tr>
			<td align="right" colspan="2"><bean:message key="other" />
				<html:text name='<%= formName %>' property="patientClinicalProperties.infectionOther" />
			</td>
			<td colspan="4">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="4" align="right"><bean:message key="patient.clinical.under.treatment"/></td>
			<td><input type="radio" name="patientClinicalProperties.infectionUnderTreatment" value="yes" >
			    <input type="radio" name="patientClinicalProperties.infectionUnderTreatment" value="no" >
			    <input type="radio" name="patientClinicalProperties.infectionUnderTreatment" value="unknown" checked="checked">
			</td>
		</tr>
		<tr>
			<td colspan="4" align="right"><bean:message key="patient.clinical.weight"/></td>
			<td><html:text name='<%= formName %>' property="patientClinicalProperties.weight" />
			</td>
		</tr>
			<tr>
			<td colspan="4" align="right"><bean:message key="patient.clinical.karnofsky"/></td>
			<td><html:text name='<%= formName %>' property="patientClinicalProperties.karnofskyScore" />
			</td>
		</tr>
		<tr>
			<td colspan="4" align="right"><bean:message key="patient.clinical.karnofsky.children"/></td>
			<td>&nbsp;</td>
		</tr>
	</table>
	
	<% } %>
</div>
 
<script type="text/javascript" language="JavaScript1.2">

</script>
