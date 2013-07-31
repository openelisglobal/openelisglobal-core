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
package us.mn.state.health.lims.reports.valueholder.audittrail;

import java.util.Comparator;

//bugzilla 2569
public class HistoryComparator implements Comparable {
	   String name;
	   String date;

	   
	   // You can put the default sorting capability here
	   public int compareTo(Object obj) {
	      HistoryXmlHelper h = (HistoryXmlHelper)obj;
	      String name = h.getUserName();
	      return this.name.compareTo(name);
	   }
	   
	 

	 
	   public static final Comparator NAME_COMPARATOR =
	     new Comparator() {
	      public int compare(Object a, Object b) {
	    	  HistoryXmlHelper h_a = (HistoryXmlHelper)a;
	    	  HistoryXmlHelper h_b = (HistoryXmlHelper)b;
	    	  
	          return ((h_a.getUserName().toLowerCase()).compareTo((h_b.getUserName().toLowerCase())));
	 
	      }
	   };
	   
	   
	   public static final Comparator DATE_COMPARATOR =
		     new Comparator() {
		      public int compare(Object a, Object b) {
		    	  HistoryXmlHelper h_a = (HistoryXmlHelper)a;
		    	  HistoryXmlHelper h_b = (HistoryXmlHelper)b;
		    	  
		          return ((h_b.getDate()).compareTo((h_a.getDate())));
		 
		      }
		   };
	   
}
