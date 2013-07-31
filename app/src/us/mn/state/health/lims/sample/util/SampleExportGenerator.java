/*
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
package us.mn.state.health.lims.sample.util;

/**
 * This class has useful methods for generating parts needed for JasperReports and data export 
 * @author Paul A. Hill (pahill@uw.edu)
 * @since Jan 25, 2011
 */
public class SampleExportGenerator {
    
    /**
     * @author Paul A. Hill (pahill@uw.edu)
     * @since Jan 25, 2011
     */
    public static class ExportColumn {
        
        static int yOffset = 0;
        static int xOffset = 0;
        static int xSize   = 100;
        static int ySize   = 17;
        
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ExportColumn(String name) {
            this.name = name;
        }

        public String toFieldElement() {
            StringBuilder b = 
                new StringBuilder("<field name=\"")
                    .append(this.name)
                    .append("\" class=\"java.lang.String\"/>");
            return b.toString();
        }

        /**
         * @return
         */
        public String textField() {
            String text = 
                "\n\t\t\t<textField>" 
                    + "\n\t\t\t\t<reportElement positionType=\"Float\" "
                        + " x=\""    + xOffset + "\"" 
                        + " y=\""    + yOffset + "\""
                        + " width=\"" + xSize  + "\""
                        + " height=\"" + ySize  + "\"/>"
                    + "\n\t\t\t\t<textElement textAlignment=\"Center\" verticalAlignment=\"Middle\"/>"
                    + "\n\t\t\t\t<textFieldExpression class=\"java.lang.String\"><![CDATA[$F{" + name + "}]]></textFieldExpression>"
              + "\n\t\t\t</textField>";
            xOffset += xSize;
            return text;
        }
    }


    static ExportColumn[] columns = { 
        new ExportColumn("accession_number"                  ),
        new ExportColumn("national_id"                       ),
        new ExportColumn("samp_id"                           ),   
        new ExportColumn("aidsStage"                         ),  
        new ExportColumn("antiTbTreatment"                   ),  
        new ExportColumn("anyCurrentDiseases"                ),  
        new ExportColumn("anyPriorDiseases"                  ),  
        new ExportColumn("anySecondaryTreatment"             ),  
        new ExportColumn("arvProphylaxis"                    ),  
        new ExportColumn("arvProphylaxisBenefit"             ),  
        new ExportColumn("arvTreatmentAdvEffGrd"             ),  
        new ExportColumn("arvTreatmentAdvEffType"            ),  
        new ExportColumn("arvTreatmentAnyAdverseEffects"     ),  
        new ExportColumn("arvTreatmentChange"                ),  
        new ExportColumn("arvTreatmentNew"                   ),  
        new ExportColumn("arvTreatmentRegime"                ),  
        new ExportColumn("cd4Count"                          ),  
        new ExportColumn("cd4Percent"                        ),  
        new ExportColumn("clinicVisits"                      ),  
        new ExportColumn("cotrimoxazoleTreatAdvEffGrd"       ),  
        new ExportColumn("cotrimoxazoleTreatAdvEffType"      ),  
        new ExportColumn("cotrimoxazoleTreatAnyAdvEff"       ),  
        new ExportColumn("cotrimoxazoleTreatment"            ),  
        new ExportColumn("currentARVTreatment"               ),  
        new ExportColumn("currentDiseases"                   ),  
        new ExportColumn("currentOITreatment"                ),  
        new ExportColumn("educationLevel"                    ),  
        new ExportColumn("eidHowChildFed"                    ),  
        new ExportColumn("eidInfantCotrimoxazole"            ),  
        new ExportColumn("eidInfantPTME"                     ),  
        new ExportColumn("eidInfantsARV"                     ),  
        new ExportColumn("eidInfantSymptomatic"              ),  
        new ExportColumn("eidMothersARV"                     ),  
        new ExportColumn("eidMothersHIVStatus"               ),  
        new ExportColumn("eidStoppedBreastfeeding"           ),  
        new ExportColumn("eidTypeOfClinic"                   ),  
        new ExportColumn("eidTypeOfClinicOther"              ),  
        new ExportColumn("futureARVTreatmentINNs"            ),  
        new ExportColumn("hivStatus"                         ),  
        new ExportColumn("hospital"                          ),  
        new ExportColumn("hospitalPatient"                   ),  
        new ExportColumn("indFirstTestDate"                  ),  
        new ExportColumn("indFirstTestName"                  ),  
        new ExportColumn("indFirstTestResult"                ),  
        new ExportColumn("indSecondTestDate"                 ),  
        new ExportColumn("indSecondTestName"                 ),  
        new ExportColumn("indSecondTestResult"               ),  
        new ExportColumn("indSiteFinalResult"                ),  
        new ExportColumn("interruptedARVTreatment"           ),  
        new ExportColumn("karnofskyScore"                    ),  
        new ExportColumn("legalResidence"                    ),  
        new ExportColumn("maritalStatus"                     ),  
        new ExportColumn("nameOfDoctor"                      ),  
        new ExportColumn("nameOfRequestor"                   ),  
        new ExportColumn("nameOfSampler"                     ),  
        new ExportColumn("nationality"                       ),  
        new ExportColumn("PatientRecordStatus"               ),  
        new ExportColumn("patientWeight"                     ),  
        new ExportColumn("priorARVTreatment"                 ),  
        new ExportColumn("priorARVTreatmentINNs"             ),  
        new ExportColumn("priorCd4Date"                      ),  
        new ExportColumn("priorDiseases"                     ),  
        new ExportColumn("projectFormName"                   ),  
        new ExportColumn("reason"                            ),  
        new ExportColumn("reasonForSecondPCRTest"            ),  
        new ExportColumn("SampleRecordStatus"                ),  
        new ExportColumn("secondaryTreatment"                ),  
        new ExportColumn("service"                           ),  
        new ExportColumn("underInvestigation"                ),  
        new ExportColumn("underInvestigationComment"         ),  
        new ExportColumn("whichPCR"                          )
    };

    public static void main(String[] args) {
        for (ExportColumn column : columns) {
            System.out.println(column.toFieldElement());
        }
        
        for (ExportColumn column : columns) {
            System.out.println(column.textField());
        }
    }
}
