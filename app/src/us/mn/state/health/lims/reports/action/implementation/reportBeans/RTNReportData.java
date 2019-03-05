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
* Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
*
*/
package us.mn.state.health.lims.reports.action.implementation.reportBeans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.services.NoteService;
import us.mn.state.health.lims.common.services.QAService;
import us.mn.state.health.lims.common.services.QAService.QAObservationType;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.qaevent.valueholder.retroCI.QaEventItem;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.sampleqaevent.dao.SampleQaEventDAO;
import us.mn.state.health.lims.sampleqaevent.daoimpl.SampleQaEventDAOImpl;
import us.mn.state.health.lims.sampleqaevent.valueholder.SampleQaEvent;

public class RTNReportData {
	
	
	private String vih;
	private String ampli2;
	private String ampli2lo;
	private String subjectNumber;
	private String birth_date;
	private String age;
	private String gender;
	private String collectiondate;
	private String receptiondate;
	private String orgname;
	private String doctor;	
	private String compleationdate;
	private String labNo;
	private String pcr;
	private String status;
	private Boolean showSerologie = Boolean.FALSE;
	private Boolean duplicateReport = Boolean.FALSE;
	
	private List<SampleQaEvent> sampleQAEventList;
	private String allQaEvents=null;
	private String receptionQaEvent=null;
	private String serologyQaEvent=null;
	
	
	private Map<String, String> previousResultMap = new HashMap<String, String>();

	
	public String getVih() {
		return vih;
	}
	public void setVih(String vih) {
		this.vih = vih;
	}
	public String getAmpli2() {
		return ampli2;
	}
	public void setAmpli2(String ampli2) {
		this.ampli2 = ampli2;
	}
	public String getAmpli2lo() {
		return ampli2lo;
	}
	public void setAmpli2lo(String ampli2lo) {
		this.ampli2lo = ampli2lo;
	}

	public String getSubjectNumber() {
		return subjectNumber;
	}
	public void setSubjectNumber(String subjectNumber) {
		this.subjectNumber = subjectNumber;
	}
	public String getBirth_date() {
		return birth_date;
	}
	public void setBirth_date(String birthDate) {
		birth_date = birthDate;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getCollectiondate() {
		return collectiondate;
	}
	public void setCollectiondate(String collectiondate) {
		this.collectiondate = collectiondate;
	}
	public String getOrgname() {
		return orgname;
	}
	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}
	public String getDoctor() {
		return doctor;
	}
	public void setDoctor(String doctor) {
		this.doctor = doctor;
	}
	public String getCompleationdate() {
		return compleationdate;
	}
	public void setCompleationdate(String compleationdate) {
		this.compleationdate = compleationdate;
	}
	public String getLabNo() {
		return labNo;
	}
	public void setLabNo(String labNo) {
		this.labNo = labNo;
	}
	
	public String getPcr() {
		return pcr;
	}
	public void setPcr(String pcr) {
		this.pcr = pcr;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatus() {
		return status;
	}
	public String getReceptiondate() {
		return receptiondate;
	}
	public void setReceptiondate(String receptiondate) {
		this.receptiondate = receptiondate;
	}
	
	public Boolean getShowSerologie(){
		return showSerologie;
	}
	public void setShowSerologie(Boolean showSerologie){
		this.showSerologie = showSerologie;
	}
	
	public Boolean getDuplicateReport() {
		return duplicateReport;
	}
	public void setDuplicateReport(Boolean duplicateReport) {
		this.duplicateReport = duplicateReport;
	}
	public String getReceptionQaEvent() {
		return receptionQaEvent;
	}
	public void setReceptionQaEvent(String receptionQaEvent) {
		this.receptionQaEvent = receptionQaEvent;
	}
	
	public String getSerologyQaEvent() {
		return serologyQaEvent;
	}
	public void setSerologyQaEvent(String serologyQaEvent) {
		this.serologyQaEvent = serologyQaEvent;
	}
	
	/**
	 * @param sample
	 * @return
	 */
	public String getAllQaEvents(){
		return allQaEvents;
	}
	public void setAllQaEvents(String  allQaEvents){
		this.allQaEvents=allQaEvents;
	}
	public Map<String, String>  getPreviousResultMap(){
		return previousResultMap;
	}
	public void setPreviousResultMap(Map<String, String> previousResultMap){
		this.previousResultMap=previousResultMap;
	}
	public void getSampleQaEventItems(Sample sample){
		if(sample != null){
			getSampleQaEvents(sample);
			for(SampleQaEvent event : sampleQAEventList){
				QAService qa = new QAService(event);
				QaEventItem item = new QaEventItem();
				item.setId(qa.getEventId());
				item.setQaEvent(qa.getQAEvent().getId());
				SampleItem sampleItem = qa.getSampleItem();
                // -1 is the index for "all samples"
				String sampleType=(sampleItem == null) ? "-1" : sampleItem.getTypeOfSample().getNameKey();
				allQaEvents=allQaEvents==null?sampleType+":"+qa.getQAEvent().getNameKey():allQaEvents+";"+sampleType+":"+qa.getQAEvent().getNameKey();
				if(!GenericValidator.isBlankOrNull(qa.getObservationValue( QAObservationType.SECTION )) && qa.getObservationValue( QAObservationType.SECTION ).equals("testSection.Serology"))
					serologyQaEvent=serologyQaEvent==null ? qa.getQAEvent().getLocalizedName() : serologyQaEvent+" , "+qa.getQAEvent().getLocalizedName();
	
			}
		}

	}
	public void getSampleQaEvents(Sample sample){
		SampleQaEventDAO sampleQaEventDAO = new SampleQaEventDAOImpl();
		sampleQAEventList = sampleQaEventDAO.getSampleQaEventsBySample(sample);
	}
	public static String getNoteForSampleQaEvent(SampleQaEvent sampleQaEvent){
		if(sampleQaEvent == null || GenericValidator.isBlankOrNull(sampleQaEvent.getId())){
			return null;
		}else{
	        Note note = new NoteService( sampleQaEvent ).getMostRecentNoteFilteredBySubject( null );
			return note != null ? note.getText() : null;
		}
	}
}
