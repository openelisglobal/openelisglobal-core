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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import us.mn.state.health.lims.common.provider.validation.HaitiPasswordValidation;
import us.mn.state.health.lims.common.provider.validation.MinnPasswordValidation;

public class PasswordValidationTest {

	private HaitiPasswordValidation haitiValidator = new HaitiPasswordValidation();
	private MinnPasswordValidation minnValidator = new MinnPasswordValidation();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/* Current Haiti requirements
	 * 	# Password must:be at least 7 characters long
	 *	# contain only upper or lower case characters
	 *	# numbers
	 *	# must have at least one of the following characters (*,$,#,!)
	 *
	 */
	
	@Test
	public void testHaitiPasswordValid() {
		assertTrue(haitiValidator.passwordValid("ad14AC*"));
	}

	@Test
	public void testHaitiPasswordNull() {
		assertFalse(haitiValidator.passwordValid(null));
	}

	@Test
	public void testHaitiPasswordShort() {
		assertFalse(haitiValidator.passwordValid("12bCD#"));
	}

	@Test
	public void testHaitiPasswordBadChar() {
		assertFalse(haitiValidator.passwordValid("12abcCD#("));
	}

	@Test
	public void testHaitiPasswordValidWithNoAlphaNumeric() {
		assertTrue(haitiValidator.passwordValid("###*$!#####"));
	}

	@Test
	public void testHaitiPasswordMissingSpecial() {
		assertFalse(haitiValidator.passwordValid("12bCDDDDD9999"));
	}

	/* Current requirements -- validation.xml only checks that there are one or more characters
	 * that are alpha-numeric 
	 * 
	 * 	 Remember passwords must meet the following complexity requirementsThe password is at least eight characters long.
		 The password does not contain three or more characters from the user name.
		 The password contains characters from at least three of the following four categories:

		    * English uppercase characters (A - Z)
		    * English lowercase characters (a - z)
		    * Base 10 digits (0 - 9)
		    * Non-alphanumeric (For example: !, $, #, or %)
	*/	

	
	@Test
	public void testMinnPasswordValid() {
		assertTrue(minnValidator.passwordValid("12bCDDDDD9999#"));
	}

	@Test
	public void testMinnPasswordNull() {
		assertFalse(minnValidator.passwordValid(null));
	}

	@Test
	public void testMinnPasswordShort() {
		assertFalse(minnValidator.passwordValid("1bCD99#"));
	}

	@Test
	public void testMinnPasswordBadChar() {
		assertFalse(minnValidator.passwordValid("12bCDDDDD9999#{"));
	}

	@Test
	public void testMinnPasswordNotComplexEnough() {
		assertFalse("Missing digits and special char", minnValidator.passwordValid("dferbCDD"));
		assertFalse("Missing digits and uppercase",minnValidator.passwordValid("bsdakkjsda#"));
		assertFalse("Missing digits and lowercase",minnValidator.passwordValid("CDDDDD#####"));
		assertFalse("Missing lowercase and special char",minnValidator.passwordValid("12134CDDDDD"));
		assertFalse("Missing lowercase and uppercase",minnValidator.passwordValid("120873!$%#"));
		assertFalse("Missing uppercase and special char",minnValidator.passwordValid("12314bfdgfsdg"));
	}
}
