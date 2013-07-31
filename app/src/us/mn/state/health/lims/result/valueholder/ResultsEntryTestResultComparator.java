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
package us.mn.state.health.lims.result.valueholder;

import java.util.Comparator;

import us.mn.state.health.lims.testresult.valueholder.TestResult;

public class ResultsEntryTestResultComparator implements Comparable {
   String name;

   
   // You can put the default sorting capability here
   public int compareTo(Object obj) {
	   if (obj instanceof TestResult_AddedReflexTests) {
	     TestResult_AddedReflexTests tr_art = (TestResult_AddedReflexTests)obj;
         return this.name.compareTo(tr_art.getSortTestResultValue());
	   } else {
		 TestResult tr = (TestResult)obj;
	     return this.name.compareTo(tr.getValue());
	   }
   }
   
 

 
   public static final Comparator VALUE_COMPARATOR =
     new Comparator() {
      public int compare(Object a, Object b) {
    	 String aValue = "";
    	 String bValue = "";
   	   if (a instanceof TestResult_AddedReflexTests && b instanceof TestResult_AddedReflexTests) {
    	  TestResult_AddedReflexTests tr_art_a = (TestResult_AddedReflexTests)a;
    	  TestResult_AddedReflexTests tr_art_b = (TestResult_AddedReflexTests)b;

          //bugzilla 2184: handle null sort value
     	  if (tr_art_a != null && tr_art_a.getSortTestResultValue() != null) {
    		  aValue = tr_art_a.getSortTestResultValue();
    	  } 
    	  
     	  if (tr_art_b != null && tr_art_b.getSortTestResultValue() != null) {
    		  bValue = tr_art_b.getSortTestResultValue();
    	  } 
   	   } else {
   	   	  TestResult tr_a = (TestResult)a;
    	  TestResult tr_b = (TestResult)b;

 
     	  if (tr_a != null && tr_a.getValue() != null) {
    		  aValue = tr_a.getValue();
    	  } 
    	  
     	  if (tr_b != null && tr_b.getValue() != null) {
    		  bValue = tr_b.getValue();
    	  }   
   	   }
          return (aValue.toLowerCase().compareTo(bValue.toLowerCase()));

      }
   };
   
   //bugzilla 1845
   public static final Comparator SORTORDER_VALUE_COMPARATOR =
	   new Comparator() {
	   public int compare(Object a, Object b) {
		   String a1Value = "";
		   String b1Value = "";
		   String a2Value = "";
		   String b2Value = "";
		   if (a instanceof TestResult_AddedReflexTests && b instanceof TestResult_AddedReflexTests) {
			   TestResult_AddedReflexTests tr_art_a = (TestResult_AddedReflexTests)a;
			   TestResult_AddedReflexTests tr_art_b = (TestResult_AddedReflexTests)b;

			   if (tr_art_a.getTestResult() != null && tr_art_a.getTestResult().getSortOrder() != null) {
				   a1Value = getLongVersionOfNumber(tr_art_a.getTestResult().getSortOrder());
			   }

			   if (tr_art_b.getTestResult() != null && tr_art_b.getTestResult().getSortOrder() != null) {
				   b1Value = getLongVersionOfNumber(tr_art_b.getTestResult().getSortOrder());
			   }

			   if (tr_art_a != null && tr_art_a.getSortTestResultValue() != null) {
				   a2Value = tr_art_a.getSortTestResultValue();
			   } 

			   if (tr_art_b != null && tr_art_b.getSortTestResultValue() != null) {
				   b2Value = tr_art_b.getSortTestResultValue();
			   } 
		   } else {
			   TestResult tr_a = (TestResult)a;
			   TestResult tr_b = (TestResult)b;

			   if (tr_a.getSortOrder() != null) {
				   a1Value = getLongVersionOfNumber(tr_a.getSortOrder());
			   }

			   if (tr_b.getSortOrder() != null) {
				   b1Value = getLongVersionOfNumber(tr_b.getSortOrder());
			   }


			   if (tr_a != null && tr_a.getValue() != null) {
				   a2Value = tr_a.getValue();
			   } 

			   if (tr_b != null && tr_b.getValue() != null) {
				   b2Value = tr_b.getValue();
			   } 
		   }

		   String aValue = a1Value.concat(a2Value);
		   String bValue = b1Value.concat(b2Value);
		   return (aValue.toLowerCase().compareTo(bValue.toLowerCase()));

	   }
   };
   
        //bugzila 1845
		private static String getLongVersionOfNumber(String number) {
			String longVersion = "";
			if (number.length() < 22) {
				int zeroPaddingLength = 22 - number.length();
				StringBuffer zeros = new StringBuffer();
				for (int i = 0; i < zeroPaddingLength; i++) {
					zeros.append("0");
				}
				longVersion = zeros + number;
			}
			if (number.length() == 22) {
				longVersion = number;
			}

			return longVersion;
		}   
}
