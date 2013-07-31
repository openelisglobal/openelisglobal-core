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
package us.mn.state.health.lims.county.valueholder;

import java.util.Comparator;


public class CountyComparator implements Comparable {
   String name;

   
   // You can put the default sorting capability here
   public int compareTo(Object obj) {
	   County c = (County)obj;
      return this.name.compareTo(c.getCounty());
   }
   
 

 
   public static final Comparator NAME_COMPARATOR =
     new Comparator() {
      public int compare(Object a, Object b) {
    	  County c_a = (County)a;
    	  County c_b = (County)b;
 
         return ((c_a.getCounty().toLowerCase()).compareTo((c_b.getCounty().toLowerCase())));

      }
   };
   

}
