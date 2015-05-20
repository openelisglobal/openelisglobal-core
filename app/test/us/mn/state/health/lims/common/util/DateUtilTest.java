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
package us.mn.state.health.lims.common.util;

import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.DECEMBER;
import static java.util.Calendar.FEBRUARY;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.JANUARY;
import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

/**
 * @author pahill (pahill@uw.edu)
 * @since May 17, 2011
 */
public class DateUtilTest {
    
    private Calendar end = new GregorianCalendar();
    private Calendar start = new GregorianCalendar();

    @Before
    public void setup() {
        end.set(HOUR_OF_DAY, 0);
        start.set(HOUR_OF_DAY, 0);        
    }

    @Test
    public void testAgeInWeeksBasic() throws Exception {
        start.set(2011, JANUARY, 1);
        end.set(2011, JANUARY, 1);
        assertEquals(0, DateUtil.getAgeInWeeks(start.getTime(), end.getTime()));
        
        start.set(2011, JANUARY, 1);
        end.set(2011, JANUARY, 7);
        assertEquals(0, DateUtil.getAgeInWeeks(start.getTime(), end.getTime()));
        
        start.set(2011, JANUARY, 1);
        end.set(2011, JANUARY, 8);
        assertEquals(1, DateUtil.getAgeInWeeks(start.getTime(), end.getTime()));
        
        start.set(2011, JANUARY, 1);
        end.add(DAY_OF_YEAR, 5);
        assertEquals(1, DateUtil.getAgeInWeeks(start.getTime(), end.getTime()));
        
        start.set(2011, JANUARY, 1);
        end.add(DAY_OF_YEAR, 3);
        assertEquals(2, DateUtil.getAgeInWeeks(start.getTime(), end.getTime()));
        assertEquals(-2, DateUtil.getAgeInWeeks(end.getTime(), start.getTime()));
    }
    
    @Test
    public void testAgeInWeeksDifferentYears() {
        start.set(  2010, JANUARY, 1);
        end.set(    2011, JANUARY, 1);
        assertEquals(52, DateUtil.getAgeInWeeks(start.getTime(), end.getTime()));        

        start.set(  2010, DECEMBER, 26);
        end.set(    2011, JANUARY, 1);
        assertEquals(0, DateUtil.getAgeInWeeks(start.getTime(), end.getTime()));        

        start.set(  2010, DECEMBER, 25);
        end.set(    2011, JANUARY, 1);
        assertEquals(1, DateUtil.getAgeInWeeks(start.getTime(), end.getTime()));
        
        start.set(  2010, DECEMBER, 24);
        end.set(    2011, JANUARY, 1);
        assertEquals(1, DateUtil.getAgeInWeeks(start.getTime(), end.getTime()));        

        start.set(  2010, DECEMBER, 17);
        end.set(    2011, JANUARY, 1);
        assertEquals(2, DateUtil.getAgeInWeeks(start.getTime(), end.getTime()));        

        end.set(    2010, DECEMBER, 17);
        start.set(  2011, JANUARY, 1);
        assertEquals(-2, DateUtil.getAgeInWeeks(start.getTime(), end.getTime()));
        
        start.set(  2009, DECEMBER, 17);
        end.set(    2011, JANUARY, 1);
        assertEquals(54, DateUtil.getAgeInWeeks(start.getTime(), end.getTime()));        
    }
    
    @Test
    public void testAgeInMonths() {
        start.set(  2010, DECEMBER, 17);
        end.set(    2011, JANUARY, 1);
        assertEquals(0, DateUtil.getAgeInMonths(start.getTime(), end.getTime()));                

        start.set(  2010, DECEMBER, 17);
        end.set(    2011, JANUARY, 17);
        assertEquals(1, DateUtil.getAgeInMonths(start.getTime(), end.getTime()));                    

        start.set(  2010, DECEMBER, 17);
        end.set(    2011, JANUARY, 27);
        assertEquals(1, DateUtil.getAgeInMonths(start.getTime(), end.getTime()));
        
        start.set(  2010, DECEMBER, 17);
        end.set(    2011, FEBRUARY, 30);
        assertEquals(2, DateUtil.getAgeInMonths(start.getTime(), end.getTime()));        

        start.set(  2010, DECEMBER, 17);
        end.set(    2011, DECEMBER, 30);
        assertEquals(12, DateUtil.getAgeInMonths(start.getTime(), end.getTime()));        
    }
    
    @Test
    public void testAgeInYears() {
        start.set(  2011, JANUARY, 1);
        end.set(    2011, JANUARY, 17);
        assertEquals(0, DateUtil.getAgeInYears(start.getTime(), end.getTime()));                
        
        start.set(  2011, JANUARY, 17);
        end.set(    2011, DECEMBER, 1);
        assertEquals(0, DateUtil.getAgeInYears(start.getTime(), end.getTime()));                

        start.set(  2011, JANUARY, 17);
        end.set(    2012, JANUARY, 16);
        assertEquals(0, DateUtil.getAgeInYears(start.getTime(), end.getTime()));
        
        start.set(  2011, JANUARY, 17);
        end.set(    2012, JANUARY, 17);
        assertEquals(1, DateUtil.getAgeInYears(start.getTime(), end.getTime()));
        
        start.set(  2011, JANUARY, 17);
        end.set(    2012, JANUARY, 18);
        assertEquals(1, DateUtil.getAgeInYears(start.getTime(), end.getTime()));                

        start.set(  2011, JANUARY, 17);
        end.set(    2021, JANUARY, 18);
        assertEquals(10, DateUtil.getAgeInYears(start.getTime(), end.getTime()));                
    }

    @Test
    public void stringSubstitution(){
        assertEquals("/bc", StringUtil.replaceCharAtIndex("abc",'/', 0));
        assertEquals("a/c", StringUtil.replaceCharAtIndex("abc",'/', 1));
        assertEquals("ab/", StringUtil.replaceCharAtIndex("abc",'/', 2));
        assertEquals("abc", StringUtil.replaceCharAtIndex("abc",'/', 3));
        assertEquals("abc", StringUtil.replaceCharAtIndex("abc",'/', -1));
        assertEquals("", StringUtil.replaceCharAtIndex("",'/', 3));
        assertEquals(null, StringUtil.replaceCharAtIndex(null,'/', 3));
    }

    @Test
    public void timeStampConversion(){
        assertEquals("01/01/2010", DateUtil.adjustAmbiguousDate("xx/xx/2010"));
        assertEquals("01/20/2010", DateUtil.adjustAmbiguousDate("xx/20/2010"));
        assertEquals("01/01/2010", DateUtil.adjustAmbiguousDate("xX/xx/2010"));
        assertEquals("01/01/2010", DateUtil.adjustAmbiguousDate("xX/xxX2010"));
    }
}
