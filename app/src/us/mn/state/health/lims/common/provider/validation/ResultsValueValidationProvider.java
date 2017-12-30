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
*/
package us.mn.state.health.lims.common.provider.validation;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

//bugzilla 2347/2361 fixing message handling: this validator handles
//both validation coming from javascript client and coming from server-side validation clients
public class ResultsValueValidationProvider extends BaseValidationProvider {

	Locale locale;
	public ResultsValueValidationProvider() {
		super();
	}

	public ResultsValueValidationProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}

	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// get Values from request
		String resultValue = (String) request.getParameter("val");
		String testResultId = (String) request.getParameter("trId");
		String formField = (String) request.getParameter("field");
		String clientType = null;
		if (request.getParameter("clientType") != null) {
			clientType = (String) request.getParameter("clientType");
		}

		locale = (java.util.Locale) request.getSession()
		.getAttribute("org.apache.struts.action.LOCALE");
		
		String result = validate(resultValue, testResultId, clientType);
		ajaxServlet.sendData(formField, result, request, response);
	}

	public String validate(String resultValue, String testResultId, String clientType)

	throws LIMSRuntimeException {

		TestResult testResult = new TestResult();
		TestResultDAO testResultDAO = new TestResultDAOImpl();

		testResult.setId(testResultId);
		testResult = testResultDAO.getTestResultById(testResult);

		String value = testResult.getValue();
		String sigDigit = testResult.getSignificantDigits();
		if (testResult.getTestResultType().equalsIgnoreCase(SystemConfiguration.getInstance().getNumericType())) {
			return validateAsPerTypeN(value, sigDigit, resultValue, clientType);
		} else { // T
			return validateAsPerTypeT(value, resultValue, clientType);
		}

	}

	public String validateAsPerTypeT(String value, String resultValue, String clientType) {
		StringBuffer s = new StringBuffer();
		String titerMin = new String();
		String titerMax = new String();
		try {

			String[] minMax = value.split(",");
			String min = minMax[0];
			String max = minMax[1];
			titerMin = "1:" + min;
			titerMax = "1:" + max;

			int minval = Integer.parseInt(min);
			int maxval = Integer.parseInt(max);

			double d = Double.valueOf(resultValue.trim()).doubleValue();
			double intpart = Math.floor(d);
			double decipart = d - intpart;

			int mainInt = (int) intpart;

			if (decipart == 0) {
				if (mainInt >= minval && mainInt <= maxval) {

					if ((minval % 10) == 0) {
						if ((mainInt % 10) == 0) {
							mainInt = mainInt / 10;
						}
					}
					// x is a power of two ==> (x > 0) and ((x & (x ? 1)) == 0)
					if (!((mainInt & (mainInt - 1)) == 0)) {
						s.append(INVALID);
					} else {
						//bugzilla 2017: this condition was missing
						s.append(VALID);
					}

				} else {
					s.append(INVALID);
				}
			} else {
				s.append(INVALID);
			}
		} catch (NumberFormatException nFE) {
            //bugzilla 2154
			LogEvent.logError("ResultsValueValidationProvider","validateAsPerTypeT()",nFE.toString());
			s.append(INVALID);
		}

		String msg = ResourceLocator.getInstance()
		.getMessageResources().getMessage(locale,
				"resultsentry.invalidresultvalue.titer.message", titerMin, titerMax);

		String str = s.toString();
		//bugzilla 2360 if clientType is js (=javascript) then return complete message
		//else return message key and replacement variables to construct an ActionError
		if (clientType != null && clientType.equals("js")) {
			str = str + SystemConfiguration.getInstance().getDefaultIdSeparator() + msg;
		} else {
			str = str + SystemConfiguration.getInstance().getDefaultIdSeparator()
			+ SystemConfiguration.getInstance().getTiterType() + SystemConfiguration.getInstance().getDefaultIdSeparator()
			+ titerMin + SystemConfiguration.getInstance().getDefaultIdSeparator() + titerMax;
		}           
		return str;
	}

	public String validateAsPerTypeN(String value, String sigDigit,
			String resultValue, String clientType) {

		StringBuffer s = new StringBuffer();
		try {

			String[] minMax = value.split(",");
			String min = minMax[0];
			String max = minMax[1];

			int minval = Integer.parseInt(min);
			int maxval = Integer.parseInt(max);

			double d = Double.valueOf(resultValue.trim()).doubleValue();
			double intpart = Math.floor(d);
			double decipart = d - intpart;

			int mainInt = (int) intpart;

			char[] sigdigiarr = resultValue.toCharArray();
			StringBuffer sigdigiOnly = new StringBuffer();
			boolean flag = false;

			for (int i = 0; i < sigdigiarr.length; i++) {

				if (flag == true) {
					sigdigiOnly.append(sigdigiarr[i]);
				} else {
					if (sigdigiarr[i] == '.') {
						flag = true;
					}
				}
			}

			String afterReverse = sigdigiOnly.reverse().toString();

			char[] sigDigitChanged = afterReverse.toCharArray();
			StringBuffer sigDigitChangedOnly = new StringBuffer();
			boolean flagnow = false;

			for (int i = 0; i < sigDigitChanged.length; i++) {

				if (flagnow == true) {
					sigDigitChangedOnly.append(sigDigitChanged[i]);
				} else {
					if (sigDigitChanged[i] != '0') {
						sigDigitChangedOnly.append(sigDigitChanged[i]);
						flagnow = true;
					}
				}
			}

			// ex: [0,100]
			if (mainInt >= minval && mainInt <= maxval) {
				if (mainInt == maxval) {
					if (decipart == 0) {
						// ex: 100.00
						s.append(VALID);
					} else {
						// ex: 100.98
						s.append(INVALID);
					}
				} else {
					// check if the number of digits equals to sig digits in
					// table
					// ex: 78.99
					//bugzilla 2347 (null causing NumberFormatException)
					if (sigDigit == null) {
						sigDigit = "0";
					}
					if (sigDigitChangedOnly.length() <= Integer
							.parseInt(sigDigit)) {
						s.append(VALID);
					} else {
						s.append(INVALID);
					}
				}
			} else {
				s.append(INVALID);
			}
		} catch (NumberFormatException nFE) {
            //bugzilla 2154
			LogEvent.logError("ResultsValueValidationProvider","validateAsPerTypeN()",nFE.toString());
			s.append(INVALID);
		}

		String msg = ResourceLocator.getInstance()
		.getMessageResources().getMessage(locale,
				"resultsentry.invalidresultvalue.numeric.message", value, sigDigit);

		String str = s.toString();
		//bugzilla 2360 if clientType is js (=javascript) then return complete message
		//else return message key and replacement variables to construct an ActionError
		if (clientType != null && clientType.equals("js")) {
			str = str + SystemConfiguration.getInstance().getDefaultIdSeparator() + msg;
		} else {
			str = str + SystemConfiguration.getInstance().getDefaultIdSeparator()
			+ SystemConfiguration.getInstance().getNumericType() + SystemConfiguration.getInstance().getDefaultIdSeparator()
			+ value + SystemConfiguration.getInstance().getDefaultIdSeparator() + sigDigit;
		}

		return str;

	}

}