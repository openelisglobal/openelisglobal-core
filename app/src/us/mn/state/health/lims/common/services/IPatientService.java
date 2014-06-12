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

import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.patientidentity.valueholder.PatientIdentity;
import us.mn.state.health.lims.person.valueholder.Person;

import java.util.List;
import java.util.Map;

public interface IPatientService{

	public abstract String getGUID();

	public abstract String getNationalId();

	public abstract String getSTNumber();

	public abstract String getSubjectNumber();

	public abstract String getFirstName();

	public abstract String getLastName();

	public abstract String getLastFirstName();

	public abstract String getGender();

    public abstract String getLocalizedGender();

	public abstract Map<String, String> getAddressComponents();

	public abstract String getDOB();

	public abstract String getPhone();

	public abstract Person getPerson();

	public abstract String getPatientId();

	public abstract String getBirthdayForDisplay();

	public abstract List<PatientIdentity> getIdentityList();

	public abstract Patient getPatient();

    public abstract String getAKA();

    public abstract String getMother();

    public abstract String getInsurance();

    public abstract String getOccupation();

    public abstract String getOrgSite();

    public abstract String getMothersInitial();

    public abstract String getEducation();

    public abstract String getMaritalStatus();

    public abstract String getHealthDistrict();

    public abstract String getHealthRegion();

    public abstract String getObNumber();

    public abstract String getPCNumber();
}