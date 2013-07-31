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
package us.mn.state.health.lims.typeofsample.valueholder;

import java.util.Comparator;

import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;


/**
 * @author AIS
 * bug 1719
 *
 */
public class TypeOfSampleComparator implements Comparable {
   String name;

   
   // You can put the default sorting capability here
   public int compareTo(Object obj) {
	   TypeOfSample l = (TypeOfSample)obj;
      return this.name.compareTo(l.getDescription());
   }
   
 

 
   public static final Comparator NAME_COMPARATOR =
     new Comparator() {
      public int compare(Object a, Object b) {
    	  TypeOfSample c_a = (TypeOfSample)a;
    	  TypeOfSample c_b = (TypeOfSample)b;
 
         return ((c_a.getDescription().toLowerCase()).compareTo((c_b.getDescription().toLowerCase())));

      }
   };
   

}
