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

import org.junit.Test;

import us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator;
import us.mn.state.health.lims.common.provider.validation.YearNumAccessionValidator;
import us.mn.state.health.lims.common.util.DateUtil;


public class AccessionNumberYearValidatorTest {

	private IAccessionNumberValidator validator = new YearNumAccessionValidator(6, null);
	
	
	@Test
	public void siteYearAccessionValid(){
		String validNumber = getTwoDigitCurrentYear() + "000001";
		
		assertTrue(IAccessionNumberValidator.ValidationResults.SUCCESS == validator.validFormat(validNumber, true));
	}
	
	
	@Test
	public void siteYearAccessionYearNotValid(){
		String validNumber = "99" + "000001";
		
		assertTrue(IAccessionNumberValidator.ValidationResults.YEAR_FAIL == validator.validFormat(validNumber, true));
	}
	
	@Test
	public void siteYearAccessionYearNotValidNotChecked(){
		String validNumber = "99" + "000001";
		
		assertTrue(IAccessionNumberValidator.ValidationResults.SUCCESS == validator.validFormat(validNumber, false));
	}
	
	@Test
	public void siteYearAccessionLengthNotValid(){
		String validNumber = getTwoDigitCurrentYear() + "0001";
		
		assertTrue(IAccessionNumberValidator.ValidationResults.LENGTH_FAIL == validator.validFormat(validNumber, true));
	}
	
	@Test
	public void siteYearAccessionFormatNotValid(){
		String validNumber = getTwoDigitCurrentYear() + "0ab001";
		
		assertTrue(IAccessionNumberValidator.ValidationResults.FORMAT_FAIL == validator.validFormat(validNumber, true));
	}
	
	@Test
	public void firstAccessionNumber(){
		String expectedNumber = getTwoDigitCurrentYear() + "000001";
		assertEquals( expectedNumber, validator.createFirstAccessionNumber(null));
	}
	
	@Test
	public void incrementAccessionNumber(){
		String orgNumber = getTwoDigitCurrentYear() + "011111";
		String nextNumber = getTwoDigitCurrentYear() + "011112";
		
		assertEquals(nextNumber, validator.incrementAccessionNumber(orgNumber));
	}
	
	@Test
	public void incrementAccessionNumberRollOverYear(){
		String orgNumber = "08011111";
		String nextNumber = getTwoDigitCurrentYear() + "000001";
		
		assertEquals(nextNumber, validator.incrementAccessionNumber(orgNumber));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void incrementAccessionNumberNoMoreNumbers(){
		String orgNumber = getTwoDigitCurrentYear() + "999999";
		
		validator.incrementAccessionNumber(orgNumber);
	}
	
	
	private String getTwoDigitCurrentYear(){
		return DateUtil.getTwoDigitYear();
	}
	
	@Test
	public void needProgram() {
		boolean FALSE = false;
		assertEquals(FALSE, validator.needProgramCode());
		
	}
}
