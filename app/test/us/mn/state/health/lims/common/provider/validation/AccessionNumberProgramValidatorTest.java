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
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*
* Contributor(s): CIRG, University of Washington, Seattle WA.
*/
package us.mn.state.health.lims.common.provider.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import us.mn.state.health.lims.project.dao.ProjectDAO;
import us.mn.state.health.lims.project.valueholder.Project;

public class AccessionNumberProgramValidatorTest {

	
	private String programCode = "ASDF";
	private String wrongCode = "AAAA";
	private ProgramAccessionValidator programValidator;
	ProjectDAO mockProjectDAO;
	private static List<Project> projectList;
	
	@BeforeClass 
	public static void beforeClass() {
		projectList = new ArrayList<Project>();
		projectList.add(createProject("ASDF"));
		projectList.add(createProject("FFFF"));
		
	}
	
	private static Project createProject(String code) {
		Project project = new Project();
		project.setProgramCode(code);
		return project;
	}
	
	@Before
	public void setUp() throws Exception {
		programValidator = new ProgramAccessionValidator();

		mockProjectDAO = mock( ProjectDAO.class);
		ProgramAccessionValidator.setProjectDAO(mockProjectDAO);
	}
	
	@Test
	public void programAccessionValid(){
		String validNumber = programCode + "00001";
		
		when(mockProjectDAO.getAllProjects()).thenReturn( projectList);
		assertTrue(IAccessionNumberValidator.ValidationResults.SUCCESS == programValidator.validFormat(validNumber, false));
	}
	

	@Test
	public void programAccessionProgramNotValid(){
		String validNumber = wrongCode + "00001";
		when(mockProjectDAO.getAllProjects()).thenReturn( projectList);
		assertTrue(IAccessionNumberValidator.ValidationResults.PROGRAM_FAIL == programValidator.validFormat(validNumber, false));
	}
	
	@Test
	public void programAccessionLengthNotValid(){
		String validNumber = programCode + "000001";
		
		assertTrue(IAccessionNumberValidator.ValidationResults.LENGTH_FAIL == programValidator.validFormat(validNumber, false));
	}
	
	@Test
	public void programAccessionFormatNotValid(){
		String validNumber = programCode + "ab011";
		when(mockProjectDAO.getAllProjects()).thenReturn( projectList);
		assertTrue(IAccessionNumberValidator.ValidationResults.FORMAT_FAIL == programValidator.validFormat(validNumber, false));
	}
	
	@Test
	public void firstAccessionNumber(){
		String expectedNumber = programCode + "00001";
		assertTrue(expectedNumber.equals(programValidator.createFirstAccessionNumber(programCode)));
		
	}
	
	@Test
	public void incrementAccessionNumber(){
		String orgNumber = programCode + "00011";
		String nextNumber = programCode + "00012";
		
		assertEquals(nextNumber, programValidator.incrementAccessionNumber(orgNumber));
	}
	
	
	@Test (expected=IllegalArgumentException.class)
	public void incrementAccessionNumberNoMoreNumbers(){
		String orgNumber = programCode + "99999";
		
		programValidator.incrementAccessionNumber(orgNumber);
	}
	
	@Test
	public void needProgram() {
		boolean TRUE = true;
		assertEquals(TRUE, programValidator.needProgramCode());
		
	}
}
