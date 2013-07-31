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
package us.mn.state.health.lims.sampleqaeventaction.valueholder;

import java.util.Comparator;

import us.mn.state.health.lims.sampleqaevent.valueholder.SampleQaEvent;

/**
 * @author benzd1
 * bugzilla 2501
 */
public class SampleQaEventComparator implements Comparable {
   String name;

   
   // You can put the default sorting capability here
   public int compareTo(Object obj) {
      SampleQaEvent sq = (SampleQaEvent)obj;
      String concatenatedName = (sq.getSample()).getAccessionNumber() + sq.getQaEvent().getQaEventDisplayValue();
      return this.name.compareTo(concatenatedName);
   }
   
 

 
   public static final Comparator NAME_COMPARATOR =
     new Comparator() {
      public int compare(Object a, Object b) {
    	  SampleQaEvent sq_a = (SampleQaEvent)a;
    	  SampleQaEvent sq_b = (SampleQaEvent)b;
    	  
    	  String sq_aConcatenatedName = (sq_a.getSample().getAccessionNumber() + sq_a.getQaEvent().getQaEventDisplayValue());
       	  String sq_bConcatenatedName = (sq_b.getSample().getAccessionNumber() + sq_b.getQaEvent().getQaEventDisplayValue());
       	 
          return ((sq_aConcatenatedName.toLowerCase()).compareTo((sq_bConcatenatedName.toLowerCase())));
 
      }
   };
   

}
