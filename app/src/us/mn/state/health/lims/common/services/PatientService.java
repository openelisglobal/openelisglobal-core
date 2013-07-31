/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/ 
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
*
*/
package us.mn.state.health.lims.common.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.address.daoimpl.AddressPartDAOImpl;
import us.mn.state.health.lims.address.valueholder.AddressPart;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.util.PatientUtil;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.patientidentity.dao.PatientIdentityDAO;
import us.mn.state.health.lims.patientidentity.daoimpl.PatientIdentityDAOImpl;
import us.mn.state.health.lims.patientidentity.valueholder.PatientIdentity;
import us.mn.state.health.lims.patientidentitytype.daoimpl.PatientIdentityTypeDAOImpl;
import us.mn.state.health.lims.patientidentitytype.valueholder.PatientIdentityType;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;

public class PatientService implements IPatientService {
	
	private static String PATIENT_GUID_IDENTITY;
	private static String PATIENT_NATIONAL_IDENTITY;
	private static String PATIENT_ST_IDENTITY;
	private static String PATIENT_SUBJECT_IDENTITY;
	private static Map<String, String> addressPartIdToNameMap = new HashMap<String, String>();
	private static final PatientIdentityDAO patientIdentityDAO = new PatientIdentityDAOImpl();
	private static final SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
	private static final PatientDAO patientDAO = new PatientDAOImpl();
	
	private Patient patient;
	private PersonService personService;
	
	static{
		PatientIdentityType patientType = new PatientIdentityTypeDAOImpl().getNamedIdentityType("GUID");
		if( patientType != null){
			PATIENT_GUID_IDENTITY = patientType.getId();
		}
		
		patientType = new PatientIdentityTypeDAOImpl().getNamedIdentityType("NATIONAL");
		if( patientType != null){
			PATIENT_NATIONAL_IDENTITY = patientType.getId();
		}
		
		patientType = new PatientIdentityTypeDAOImpl().getNamedIdentityType("ST");
		if( patientType != null){
			PATIENT_ST_IDENTITY = patientType.getId();
		}
				
		patientType = new PatientIdentityTypeDAOImpl().getNamedIdentityType("SUBJECT");
		if( patientType != null){
			PATIENT_SUBJECT_IDENTITY = patientType.getId();
		}

		List<AddressPart> parts = new AddressPartDAOImpl().getAll();
		
		for( AddressPart part : parts){
			addressPartIdToNameMap.put(part.getId(), part.getPartName());
		}
	}
	
	public PatientService(Patient patient){
		this.patient = patient;
		
		if( patient == null){
			personService = new PersonService( null );
			return;
		} 
			
		if( patient.getPerson() == null){
			new PatientDAOImpl().getData(this.patient);
		}
		personService = new PersonService(patient.getPerson());

	}
	
	/**
	 * Gets the patient for the sample and then calls the constructor with patient argument
	 * @param sample
	 */
	public PatientService(Sample sample){
		this(sampleHumanDAO.getPatientForSample(sample));
	}
	
	/**
	 * Gets the patient with this guid
	 * @param guid
	 */
	public PatientService(String guid){
		this(getPatientForGuid( guid));
	}

	private static Patient getPatientForGuid(String guid){
		List<PatientIdentity> identites = patientIdentityDAO.getPatientIdentitiesByValueAndType(guid, PATIENT_GUID_IDENTITY);
		if( identites.isEmpty()){
			return null;
		}
		
		return patientDAO.getData( identites.get(0).getPatientId() );
	}

	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getGUID()
	 */
	@Override
	public String getGUID(){
		return getIdentityInfo(PATIENT_GUID_IDENTITY);
	}
	
	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getNationalId()
	 */
	@Override
	public String getNationalId(){
		if( patient == null){
			return "";
		}
		
		if( !GenericValidator.isBlankOrNull(patient.getNationalId())){
			return patient.getNationalId();
		}else{
			return getIdentityInfo(PATIENT_NATIONAL_IDENTITY);
		}
	}
	
	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getSTNumber()
	 */
	@Override
	public String getSTNumber(){
		return getIdentityInfo(PATIENT_ST_IDENTITY);
	}

	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getSubjectNumber()
	 */
	@Override
	public String getSubjectNumber(){
		return getIdentityInfo(PATIENT_SUBJECT_IDENTITY);
	}
	
	private String getIdentityInfo(String identityId) {
		if( patient == null){
			return "";
		}
		
		PatientIdentity identity = patientIdentityDAO.getPatitentIdentityForPatientAndType(patient.getId(), identityId);
		
		if( identity != null){
			return identity.getIdentityData();
		}else{
			return "";
		}
	}
	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getFirstName()
	 */
	@Override
	public String getFirstName(){
		return personService.getFirstName();
	}
	
	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getLastName()
	 */
	@Override
	public String getLastName(){
		return personService.getLastName();
	}
	
	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getLastFirstName()
	 */
	@Override
	public String getLastFirstName(){
		return personService.getLastFirstName();
	}
	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getGender()
	 */
	@Override
	public String getGender(){
		return patient != null ? patient.getGender() : "";
	}
	
	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getAddressComponents()
	 */
	@Override
	public Map<String, String> getAddressComponents(){
		return personService.getAddressComponents();
	}
	
	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getDOB()
	 */
	@Override
	public String getDOB(){
			return patient.getBirthDateForDisplay();
	}
	
	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getPhone()
	 */
	@Override
	public String getPhone(){
		return personService.getPhone();
	}
	
	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getPerson()
	 */
	@Override
	public Person getPerson(){
		return personService.getPerson();
	}

	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getPatientId()
	 */
	@Override
	public String getPatientId(){
		return patient != null ? patient.getId() : null;
	}
	
	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getBirthdayForDisplay()
	 */
	@Override
	public String getBirthdayForDisplay(){
		return patient != null ? DateUtil.convertTimestampToStringDate(patient.getBirthDate()) : "";
	}

	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getIdentityList()
	 */
	@Override
	public List<PatientIdentity> getIdentityList(){
		return patient != null ? PatientUtil.getIdentityListForPatient(patient) : new ArrayList<PatientIdentity>();
	}

	/* (non-Javadoc)
	 * @see us.mn.state.health.lims.common.services.IPatientService#getPatient()
	 */
	@Override
	public Patient getPatient(){
		return patient;
	}
}
