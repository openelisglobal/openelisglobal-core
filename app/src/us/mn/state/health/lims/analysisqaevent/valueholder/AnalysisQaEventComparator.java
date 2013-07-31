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
package us.mn.state.health.lims.analysisqaevent.valueholder;

import java.util.Comparator;

public class AnalysisQaEventComparator implements Comparable {
   String name;

   
   // You can put the default sorting capability here
   public int compareTo(Object obj) {
      AnalysisQaEvent aq = (AnalysisQaEvent)obj;
      String concatenatedName = (aq.getAnalysis().getTest().getTestDisplayValue() + aq.getQaEvent().getQaEventDisplayValue());
      return this.name.compareTo(concatenatedName);
   }
   
 

 
   public static final Comparator NAME_COMPARATOR =
     new Comparator() {
      public int compare(Object a, Object b) {
    	  AnalysisQaEvent aq_a = (AnalysisQaEvent)a;
    	  AnalysisQaEvent aq_b = (AnalysisQaEvent)b;
    	  
    	  String aq_aConcatenatedName = (aq_a.getAnalysis().getTest().getTestDisplayValue() + aq_a.getQaEvent().getQaEventDisplayValue());
       	  String aq_bConcatenatedName = (aq_b.getAnalysis().getTest().getTestDisplayValue() + aq_b.getQaEvent().getQaEventDisplayValue());
       	 
          return ((aq_aConcatenatedName.toLowerCase()).compareTo((aq_bConcatenatedName.toLowerCase())));
 
      }
   };
   

}
