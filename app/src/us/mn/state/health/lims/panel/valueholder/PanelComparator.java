//AIS Created for bugzilla 1776
package us.mn.state.health.lims.panel.valueholder;

import java.util.Comparator;

public class PanelComparator implements Comparable {
   String name;

   
   // You can put the default sorting capability here
   public int compareTo(Object obj) {
      Panel t = (Panel)obj;
      return this.name.compareTo(t.getDescription());
   }
   
 

 
   public static final Comparator NAME_COMPARATOR =
     new Comparator() {
      public int compare(Object a, Object b) {
    	  Panel t_a = (Panel)a;
    	  Panel t_b = (Panel)b;
 
         return ((t_a.getDescription().toLowerCase()).compareTo(t_b.getDescription().toLowerCase()));

      }
   };
   

}
