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


/**
 * @author paulsc
 *
 */
public interface IAccessionNumberValidator {

	public enum ValidationResults {
		SUCCESS, SITE_FAIL, YEAR_FAIL, USED_FAIL, IS_NOT_USED_FAIL, LENGTH_FAIL, FORMAT_FAIL, PROGRAM_FAIL, REQUIRED_FAIL,
		PATIENT_STATUS_FAIL, SAMPLE_STATUS_FAIL, SAMPLE_FOUND, SAMPLE_NOT_FOUND
	};


	/**
	 * @return does this accession number have a program code as part of it
	 */
	public boolean needProgramCode();

	/**
	 * @param accessionNumber -- The number to be checked
	 * @param checkDate -- true if there is a date element in the number which should be checked or false otherwise
	 *   If the number is one which has already been entered into the system then it would make sense to set this to false
	 *   since when the year turns over all of the sudden they will fail.
	 * @return One of the possible results for validation
	 * @throws IllegalArgumentException
	 */
	public ValidationResults validFormat(String accessionNumber, boolean checkDate) throws IllegalArgumentException;


	/**
	 * Helper method for getting an appropriate message for a validation result
	 *
	 * @param results -- the result for which the message is wanted
	 * @return -- the message
	 */
	public String getInvalidMessage(ValidationResults results);

    /**
     * Helper method for getting an appropriate message for a format validation result
     *
     * @param results -- the result for which the message is wanted
     * @return -- the message
     */
    public String getInvalidFormatMessage(ValidationResults results);

	/**
	 * @param programCode -- if used, may be null otherwise
	 * @return The first accession number if no others are have been generated
	 */
	public String createFirstAccessionNumber(String programCode);


	public String incrementAccessionNumber(String currentHighAccessionNumber);

	/**
	 * @param programCode -- code if needed, may be null
	 * @return The next available number, may be null if one can not be generated.
	 */
	public String getNextAvailableAccessionNumber(String programCode);

	public int getMaxAccessionLength();

	public boolean accessionNumberIsUsed(String accessionNumber, String recordType);

	public ValidationResults  checkAccessionNumberValidity(String accessionNumber, String recordType, String isRequired, String projectFormName);
	
	/**
	 * Get the part of the accession number which should not change.  ie. for Haiti it would be the site number, for Cote d'Ivoire it would
	 * be the Program prefix 
	 * @return
	 */
	public int getInvarientLength();
	
	/**
	 * The max length - the invarientLength
	 * @return
	 */
	public int getChangeableLength();

    public String getPrefix();
}
