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

import org.junit.Before;
import org.junit.Test;

import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;


public class AccessionNumberSiteYearValidatorTest {

	private String siteCode = "51100";
	private IAccessionNumberValidator siteYearValidator;
	
	@Before
	public void setUp() throws Exception {
		siteYearValidator = new SiteYearAccessionValidator();
		ConfigurationProperties.getInstance().setPropertyValue(Property.ACCESSION_NUMBER_PREFIX, siteCode);
	}
	
	@Test
	public void siteYearAccessionValid(){
		String validNumber = siteCode + getTwoDigitCurrentYear() + "000001";
		
		assertTrue(IAccessionNumberValidator.ValidationResults.SUCCESS == siteYearValidator.validFormat(validNumber, true));
	}
	
	@Test
	public void siteYearAccessionSiteNotValid(){
		String validNumber = "41100" + getTwoDigitCurrentYear() + "000001";
		
		assertTrue(IAccessionNumberValidator.ValidationResults.SITE_FAIL == siteYearValidator.validFormat(validNumber, true));
	}
	
	@Test
	public void siteYearAccessionYearNotValid(){
		String validNumber = siteCode + "99" + "000001";
		
		assertTrue(IAccessionNumberValidator.ValidationResults.YEAR_FAIL == siteYearValidator.validFormat(validNumber, true));
	}
	
	@Test
	public void siteYearAccessionYearNotValidNotChecked(){
		String validNumber = siteCode + "99" + "000001";
		
		assertTrue(IAccessionNumberValidator.ValidationResults.SUCCESS == siteYearValidator.validFormat(validNumber, false));
	}
	
	@Test
	public void siteYearAccessionLengthNotValid(){
		String validNumber = siteCode + getTwoDigitCurrentYear() + "0001";
		
		assertTrue(IAccessionNumberValidator.ValidationResults.LENGTH_FAIL == siteYearValidator.validFormat(validNumber, true));
	}
	
	@Test
	public void siteYearAccessionFormatNotValid(){
		String validNumber = siteCode + getTwoDigitCurrentYear() + "0ab001";
		
		assertTrue(IAccessionNumberValidator.ValidationResults.FORMAT_FAIL == siteYearValidator.validFormat(validNumber, true));
	}
	
	@Test
	public void firstAccessionNumber(){
		String expectedNumber = siteCode +  getTwoDigitCurrentYear() + "000001";
		assertEquals( expectedNumber, siteYearValidator.createFirstAccessionNumber(null));
	}
	
	@Test
	public void incrementAccessionNumber(){
		String orgNumber = siteCode +  getTwoDigitCurrentYear() + "011111";
		String nextNumber = siteCode +  getTwoDigitCurrentYear() + "011112";
		
		assertEquals(nextNumber, siteYearValidator.incrementAccessionNumber(orgNumber));
	}
	
	@Test
	public void incrementAccessionNumberRollOverYear(){
		String orgNumber = siteCode + "08011111";
		String nextNumber = siteCode +  getTwoDigitCurrentYear() + "000001";
		
		assertEquals(nextNumber, siteYearValidator.incrementAccessionNumber(orgNumber));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void incrementAccessionNumberNoMoreNumbers(){
		String orgNumber = "31100" +  getTwoDigitCurrentYear() + "999999";
		
		siteYearValidator.incrementAccessionNumber(orgNumber);
	}
	
	
	private String getTwoDigitCurrentYear(){
		return DateUtil.getTwoDigitYear();
	}
	
	@Test
	public void needProgram() {
		boolean FALSE = false;
		assertEquals(FALSE, siteYearValidator.needProgramCode());
		
	}
}
