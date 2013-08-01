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

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="idSeparator" value='<%= SystemConfiguration.getInstance().getDefaultIdSeparator() %>' />
<bean:define id="humanDomain" value='<%= SystemConfiguration.getInstance().getHumanDomain() %>' />

<%--bugzilla 1904 did some reformatting for longer label and extra row--%>
<table width="100%">
	<tr>
	<%--bugzilla 2438 adjust for increased length of project.name--%>
		<td colspan="2" valign="top">
			<table width="95%" height="40">
				<tr>
					<td width="163">
						<bean:message key="humansampleone.projectNumber" />
						:
					</td>
					<td colspan="3">
						<strong> <app:text name="<%=formName%>" property="projectIdAndName" allowEdits="false" /> </strong>
					</td>
				</tr>
				<tr>
					<td width="163">
						<bean:message key="humansampleone.project2Number" />
						:
					</td>
					<td colspan="3">
						<strong><app:text name="<%=formName%>" property="project2IdAndName" allowEdits="false" /></strong>
					</td>
				</tr>
			</table>
		</td>
	  </tr>
	  <tr>
    	  <td width="50%" valign="top">
			<table width="95%">
				<tr>
					<td colspan="4">
						<h2 align="left">
							<bean:message key="humansampleone.subtitle.requestor" />
						</h2>
					</td>
				</tr>
				<%--Submitter Number--%>
				<tr>
					<td width="163"><%--bugzilla 2069--%>
						<bean:message key="humansampleone.provider.organization.localAbbreviation" />
						:
					</td>
					<td colspan="3">
						<strong> <app:text name="<%=formName%>" property="organizationBoth" allowEdits="false" /> </strong>
					</td>
				</tr>
				<%--Provider Person information--%>
				<tr>
					<td width="163">
						<bean:message key="humansampleone.provider.lastName" />
						: <font size="1"><bean:message key="humansampleone.provider.addionalOrClinician" /></font>
					</td>
					<td colspan="3">
						<strong> <app:text name="<%=formName%>" property="providerLastName" allowEdits="false" /></strong>

					</td>
				</tr>
				<tr>
					<td width="163">
						<bean:message key="humansampleone.provider.firstName" />
						: <font size="1"><bean:message key="humansampleone.provider.addionalOrClinician" /></font>
					</td>
					<td colspan="3">
						<strong> <app:text name="<%=formName%>" property="providerFirstName" allowEdits="false" /></strong>

					</td>
				</tr>
				<tr>
					<td width="163">
						<bean:message key="humansampleone.provider.workPhone" />
						:
					</td>
					<td width="173">
						<strong> <app:text name="<%=formName%>" property="providerWorkPhone" onblur="myCheckPhone(this)" allowEdits="false" /></strong>

					</td>
					<td width="32">
						<bean:message key="humansampleone.provider.workPhone.extension" />:
					</td>
					<td width="129">
						<strong> <app:text name="<%=formName%>" property="providerWorkPhoneExtension" allowEdits="false" /></strong>

					</td>
				</tr>
				<tr>
					<td width="163">
						&nbsp;
					</td>
					<td width="173">
						&nbsp;
					</td>
					<td width="32">
						&nbsp;
					</td>
					<td width="129">
						&nbsp;
					</td>
				</tr>
			</table>
		</td>
		<td width="50%" valign="top">
			<table width="95%">
				<tr>
					<td colspan="5">
						<h2>
							<bean:message key="humansampleone.subtitle.patient" />
						</h2>
					</td>
				</tr>
				<%--bugzilla 1904 moved externalid to top--%>
				<tr>
					<td width="116">
						<bean:message key="patient.externalId" />
						:
					</td>
					<td colspan="4">
						<strong> <app:text name="<%=formName%>" property="externalId" allowEdits="false" /></strong>

					</td>
				</tr>
				<tr>
					<td width="116">
						<bean:message key="person.lastName" />
						:
					</td>
					<td colspan="4">
						<strong> <app:text name="<%=formName%>" property="lastName" allowEdits="false" /> </strong>

					</td>
				</tr>
				<tr>
					<td width="116">
						<bean:message key="person.firstName" />
						:
					</td>
					<td colspan="2">
						<strong> <app:text name="<%=formName%>" property="firstName" allowEdits="false" /></strong>

					</td>
					<td width="31">
						<bean:message key="person.middleName" />
						:
					</td>
					<td width="132">
						<strong> <app:text name="<%=formName%>" property="middleName" allowEdits="false" /></strong>

						</td>
				</tr>
				<tr>
					<td width="116">
						<bean:message key="person.streetAddress" />
						:
					</td>
					<td colspan="2">
						<strong> <app:text name="<%=formName%>" property="streetAddress" allowEdits="false" /></strong>

					</td>
					<td width="31">
						<bean:message key="person.multipleUnit" />
						:
					</td>
					<td width="132">
						<strong> <app:text name="<%=formName%>" property="multipleUnit" allowEdits="false" /></strong>

					</td>
				</tr>
				<tr>
					<td width="116">
						<bean:message key="person.city" />
						:
					</td>
					<td colspan="4">
						<strong> <app:text name="<%=formName%>" property="city" allowEdits="false" /></strong>
				
						</td>
						</tr>
						<tr>
					<td width="116">
						<bean:message key="person.state" />
						:
					</td>
					<td width="276">
						<strong> <app:text name="<%=formName%>" property="state" allowEdits="false" /></strong>

					</td>
					<td width="29">
						&nbsp;
					</td>
					<td width="31">
						<bean:message key="person.zipCode" />
						:
					</td>
					<td width="132">
						<strong> <app:text name="<%=formName%>" property="zipCode" allowEdits="false" /></strong>

					</td>

				</tr>
				<%--bugzilla 1904 added new field chart number--%>
				<tr>
					<td width="116">
						<bean:message key="patient.chartNumber" />
						:
					</td>
					<td colspan="2">
						<strong> <app:text name="<%=formName%>" property="chartNumber" allowEdits="false" /></strong>

					</td>
					<td width="31">
						<bean:message key="patient.gender" />
						:
					</td>
					<td width="132">
						<strong> <app:text name="<%=formName%>" property="gender" allowEdits="false" /></strong>

					</td>
				</tr>
				<tr>
					<td width="116">
						<bean:message key="patient.birthDate" />
						:

					</td>
					<td colspan="4">
						<strong> <app:text name="<%=formName%>" property="birthDateForDisplay" allowEdits="false" /></strong>

					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<%--bottom sample information--%>
<table width="100%">
	<tr>
		<td colspan="8">
			<h2>
				<bean:message key="humansampleone.subtitle.sample" />
			</h2>
		</td>
	</tr>
	<tr>
		<td width="121">
			<bean:message key="sample.collectionDate" />
			:

		</td>
		<td width="212">
			<strong> <app:text name="<%=formName%>" property="collectionDateForDisplay" allowEdits="false" /> </strong>

		</td>
		<td width="109">
			<bean:message key="sample.collectionTime" />:

		</td>
		<td width="215">
			<strong> <app:text name="<%=formName%>" property="collectionTimeForDisplay" allowEdits="false" /> </strong>
		</td>
		<td width="150">
			<bean:message key="sample.clientReference" />
			:
		</td>
		<td width="81" valign="top">
			<strong> <app:text name="<%=formName%>" property="clientReference" allowEdits="false" /> </strong>

		</td>
		<td width="100">
			<bean:message key="sample.referredCultureFlag" />
			:
		</td>
		<td width="76" valign="top">
			<strong> <app:text name="<%=formName%>" property="referredCultureFlag" allowEdits="false" /></strong>

		</td>
	</tr>
	<tr>
		<td width="121">
			<bean:message key="sampleitem.typeOfSample" />
			:
		</td>
		<td width="212">
			<strong> <app:text name="<%=formName%>" property="typeOfSample.description" allowEdits="false" /></strong>

		</td>
		<td width="109">
			<bean:message key="sampleitem.sourceOfSample" />
			:
		</td>
		<td width="215">
			<strong> <app:text name="<%=formName%>" property="sourceOfSample.description" allowEdits="false" /></strong>

		</td>
		<td width="52">
			<bean:message key="sampleitem.sourceOther" />
			:
		</td>
		<td colspan="3" valign="top">
			<strong> <app:text name="<%=formName%>" property="sourceOther" allowEdits="false" /> </strong>

		</td>
	</tr>
	<tr>
		<td width="121">
			<bean:message key="sample.receivedDate" />
			:

		</td>
		<td width="212">
			<%--received date will only display but cannot be updated on HSE II--%>
			<strong> <app:text name="<%=formName%>" property="receivedDateForDisplay" allowEdits="false" /></strong>

		</td>
		<td width="109">
			<bean:message key="sample.stickerReceivedFlag" />
			:
		</td>
		<td width="215" valign="top">
			<strong> <app:text name="<%=formName%>" property="stickerReceivedFlag" allowEdits="false" /> </strong>

		</td>
		<td width="52">
			&nbsp;
		</td>
		<td width="179">
			&nbsp;
		</td>
		<td width="100">
			&nbsp;
		</td>
		<td width="76">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td width="121">
			&nbsp;
		</td>
		<td width="212">
			&nbsp;
		</td>
		<td width="109">
			&nbsp;
		</td>
		<td width="215">
			&nbsp;
		</td>
		<td width="52">
			&nbsp;
		</td>
		<td width="179">
			&nbsp;
		</td>
		<td width="100">
			&nbsp;
		</td>
		<td width="76">
			&nbsp;
		</td>
	</tr>
</table>

