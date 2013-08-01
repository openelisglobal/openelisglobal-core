<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants, 
	us.mn.state.health.lims.common.util.SystemConfiguration" %>  

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>'/>
<bean:define id="expectedStatus" value='<%= SystemConfiguration.getInstance().getSampleStatusEntry2Complete() %>' />
<bean:define id="expectedDomain" value='<%= SystemConfiguration.getInstance().getNewbornDomain() %>' />

<table border="0" cellpadding="1" cellspacing="0">
    <tr>
        <td>
            <bean:message key="newborn.sample.full.barcode"/>:
        </td>
        <td colspan="5">
            <strong><bean:write name="<%=formName%>" property="barcode"/></strong>
        </td>
    </tr>
    <tr>    
        <td>
            <bean:message key="newborn.sample.full.medical.record"/>:     
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="medicalRecordNumber"/></strong>
        </td>
        <td>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <bean:message key="newborn.sample.full.y.number"/>:     
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="ynumber"/></strong>
        </td>
        <td>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <bean:message key="newborn.sample.full.yellow.card"/>:
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="yellowCard"/></strong>
        </td>                 
    </tr>
    <tr><td colspan="6">&nbsp;</td></tr>
</table>

<table border="0" width="100%" cellpadding="1" cellspacing="0">    
    <tr>
        <td colspan="8"> 
            <h2 align="left"><bean:message key="newborn.sample.full.infant.info"/></h2>
        </td>    
    </tr>
    <tr>
        <td>
            <bean:message key="newborn.sample.full.last.name"/>: 
        </td>
        <td width="14%">
            <strong><bean:write name="<%=formName%>" property="lastName"/></strong>
        </td>
        <td>
            <bean:message key="newborn.sample.full.first.name"/>:
        </td>
        <td width="14%">
            <strong><bean:write name="<%=formName%>" property="firstName"/></strong>
        </td>
        <td>
            <bean:message key="newborn.sample.full.birth.date"/>:<br>
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="birthDateForDisplay"/></strong>
        </td>
        <td>
            <bean:message key="newborn.time.of.birth"/>:<br>
        </td>		
        <td>
            <strong><bean:write name="<%=formName%>" property="birthTimeForDisplay"/></strong>
        </td>	            
    </tr>
	<tr>
        <td>
            <bean:message key="newborn.sample.full.weight"/>:
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="birthWeight"/></strong>
        </td>
        <td>
            <bean:message key="newborn.sample.full.multiple.birth"/>:     
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="multipleBirth"/></strong>
            <strong><bean:write name="<%=formName%>" property="birthOrder"/></strong>
        </td>
        <td width="10%" noWrap>
            <bean:message key="newborn.sample.full.gestational.week"/>:     
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="gestationalWeek"/></strong>
        </td>
        <td>
            <bean:message key="newborn.sample.full.gender"/>:
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="gender"/></strong>
        </td>		
    </tr>
    <tr>
        <td width="10%" noWrap>
            <bean:message key="newborn.sample.full.date.of.first.feeding"/>:<br>
        </td>		
        <td valign="top">
            <strong><bean:write name="<%=formName%>" property="dateFirstFeedingForDisplay"/></strong>
        </td>
        <td width="10%" noWrap>
            <bean:message key="newborn.sample.full.time.of.first.feeding"/>:<br>
        </td>		
        <td colspan="5">
            <strong><bean:write name="<%=formName%>" property="timeFirstFeedingForDisplay"/></strong>
        </td>
    </tr>
    <tr>        
        <td>
            <bean:message key="newborn.sample.full.type.of.feeding"/>:     
        </td>
        <td colspan="7">
            <table border="0">
                <tr>
                    <td>
                        <bean:message key="newborn.sample.full.breast"/>:
                    </td>    
                    <td>
                        <strong><bean:write name="<%=formName%>" property="breast"/></strong>
                    </td>
                    <td>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <bean:message key="newborn.sample.full.tpn"/>:
                    </td>
                    <td>
                        <strong><bean:write name="<%=formName%>" property="tpn"/></strong>
                    </td>
                    <td>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <bean:message key="newborn.sample.full.formula"/>:
                    </td>
                    <td>
                        <strong><bean:write name="<%=formName%>" property="formula"/></strong>
                    </td>
                    <td>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <bean:message key="newborn.sample.full.milk"/>:
                    </td>
                    <td>
                        <strong><bean:write name="<%=formName%>" property="milk"/></strong>
                    </td>
                    <td>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <bean:message key="newborn.sample.full.soy"/>:
                    </td>
                    <td>
                        <strong><bean:write name="<%=formName%>" property="soy"/></strong>
                    </td>
                </tr>                        
            </table>    
        </td>    
    </tr>
    <tr>	            
        <td>
		    <bean:message key="newborn.sample.full.date.of.collection"/>:<br>
	    </td>    
        <td> 
            <strong><bean:write name="<%=formName%>" property="collectionDateForDisplay"/></strong>
        </td>
        <td>
            <bean:message key="newborn.sample.full.time.of.collection"/>:<br>
        </td>		
        <td colspan="5">
            <strong><bean:write name="<%=formName%>" property="collectionTimeForDisplay"/></strong>
        </td>
    </tr>        
    <tr>    
        <td>
            <bean:message key="newborn.sample.full.clinical.info"/>:     
        </td>
        <td colspan="5">
            <table border="0">
                <tr>
                    <td>
                        <bean:message key="newborn.sample.full.jaundice"/>: 
                    </td>
                    <td>
                        <strong><bean:write name="<%=formName%>" property="jaundice"/></strong>
                    </td>
                    <td>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <bean:message key="newborn.sample.full.antibiotic"/>: 
                    </td>
                    <td>
                        <strong><bean:write name="<%=formName%>" property="antibiotic"/></strong>                        
                    </td>
                    <td>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <bean:message key="newborn.sample.full.transfused"/>: 
                    </td>
                    <td>
                        <strong><bean:write name="<%=formName%>" property="transfused"/></strong>
                    </td>                     
                </tr>
            </table>
        </td>
        <td width="10%" noWrap>
		    <bean:message key="newborn.sample.full.date.of.transfusion"/>:<br>
	    </td>    
        <td> 
            <strong><bean:write name="<%=formName%>" property="dateTransfutionForDisplay"/></strong>
        </td>	
    </tr>
    <tr><td colspan="8">&nbsp;</td></tr>
</table>
  
<table border="0" width="100%" cellpadding="1" cellspacing="0">  
    <tr>
        <td colspan="8"> 
            <h2 align="left"><bean:message key="newborn.sample.full.risk.factor"/></h2>
        </td>    
    </tr>
    <tr>
        <td>
            <bean:message key="newborn.sample.full.nicu.patient"/>:     
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="nicuPatient"/></strong>
        </td>
        <td>
            <bean:message key="newborn.sample.full.birth.defect"/>:     
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="birthDefect"/></strong>
        </td>
        <td width="20%" noWrap>
            <bean:message key="newborn.sample.full.maternal.pregnancy.compensation"/>:
        </td>
        <td colspan="3">
            <strong><bean:write name="<%=formName%>" property="pregnancyComplication"/></strong>
        </td>
    </tr>
    <tr>
        <td width="10%" noWrap>
            <bean:message key="newborn.sample.full.deceased.sibling"/>:     
        </td>
        <td width="10%">
            <strong><bean:write name="<%=formName%>" property="deceasedSibling"/></strong>
        </td>
        <td width="10%" noWrap>
            <bean:message key="newborn.sample.full.cause.of.death"/>:     
        </td>
        <td colspan="5">
            <strong><bean:write name="<%=formName%>" property="causeOfDeath"/></strong>
        </td>
    </tr>  
    <tr>
        <td colspan="3" noWrap>
            <bean:message key="newborn.sample.full.family.history"/>:     
        </td>
        <td width="6%">
            <strong><bean:write name="<%=formName%>" property="familyHistory"/></strong>
        </td>
        <td align="right">
            <bean:message key="newborn.sample.full.other"/>:     
        </td>
        <td colspan="3">
            <strong><bean:write name="<%=formName%>" property="other"/></strong>
        </td>     
    </tr>                   				
    <tr><td colspan="8">&nbsp;</td></tr>
</table>

<table border="0" width="100%" cellpadding="1" cellspacing="0">  
    <tr>
        <td colspan="8"> 
            <h2 align="left"><bean:message key="newborn.sample.full.mother.info"/></h2>
        </td>
    </tr>
    <tr>        
        <td>
            <bean:message key="newborn.sample.full.last.name"/>:
        </td>
        <td width="14%">
            <strong><bean:write name="<%=formName%>" property="motherLastName"/></strong>
        </td>
        <td width="5%" noWrap>
            <bean:message key="newborn.sample.full.first.name"/>:
        </td>
        <td width="14%">
            <strong><bean:write name="<%=formName%>" property="motherFirstName"/></strong>
        </td>
        <td width="8%" noWrap>
            <bean:message key="newborn.sample.full.birth.date"/>:<br>
        </td>
        <td>           
            <strong><bean:write name="<%=formName%>" property="motherBirthDateForDisplay"/></strong>            
        </td>
        <td width="8%" noWrap>
             <bean:message key="newborn.sample.full.phone"/>:<br>
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="motherPhoneNumber"/></strong>                    
        </td>                          
    </tr>	
    <tr>
        <td width="10%" noWrap>
            <bean:message key="newborn.sample.full.street.address"/>:
        </td>
        <td colspan="7">
            <strong><bean:write name="<%=formName%>" property="motherStreetAddress"/></strong>                            
        </td>
    </tr>
    <tr>     
        <td>
           <bean:message key="newborn.sample.full.city"/>:
        </td>
        <td colspan="3">
            <strong><bean:write name="<%=formName%>" property="city"/></strong>                                        
        </td>
        <td>
            <bean:message key="newborn.sample.full.state"/>:
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="state"/></strong>                                        
        </td>
        <td>
            <bean:message key="newborn.sample.full.zip.code"/>:
        </td>
        <td>
            <strong><bean:write name="<%=formName%>" property="zipCode"/></strong>                                        
        </td>
    </tr>    
    <tr><td colspan="8">&nbsp;</td></tr>
</table>

<table border="0" width="100%" cellpadding="1" cellspacing="0">
    <tr>
        <td colspan="8"> 
            <h2 align="left"><bean:message key="newborn.sample.full.provider.info"/></h2>
        </td>        
    </tr>
    <tr>
        <td>
            <bean:message key="newborn.sample.full.submitter.number"/>
        </td>
        <td colspan="7">
            <strong><bean:write name="<%=formName%>" property="submitterNumber"/></strong>                                        
        </td>
    </tr>
    <tr>
       <td width="10%" noWrap>
            <bean:message key="newborn.sample.full.physician.last.name"/>:
        </td>
        <td width="20%">
            <strong><bean:write name="<%=formName%>" property="physicianLastName"/></strong>
        </td>
        <td width="10%" noWrap>
            <bean:message key="newborn.sample.full.physician.first.name"/>:
        </td>
        <td width="20%">
            <strong><bean:write name="<%=formName%>" property="physicianFirstName"/></strong>
        </td>
        <td width="10%" noWrap>
            <bean:message key="newborn.sample.full.physician.phone"/>:
        </td>
        <td colspan="3">
            <strong><bean:write name="<%=formName%>" property="physicianPhoneNumber"/></strong>
        </td>          
    </tr> 
    <tr>
        <td colspan="8">&nbsp;</td>
    </tr>        
</table>