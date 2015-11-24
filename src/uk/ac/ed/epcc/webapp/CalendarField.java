// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp;

import java.util.Calendar;
/** Enum representing Calendar fields in order of significance.
 * 
 * @author spb
 *
 */
public enum CalendarField {
   Millisecond(Calendar.MILLISECOND),
   Second(Calendar.SECOND),
   Minute(Calendar.MINUTE),
   Hour(Calendar.HOUR),
   Day(Calendar.DAY_OF_MONTH),
   Month(Calendar.MONTH),
   Year(Calendar.YEAR);
   
   private final int field;
   CalendarField(int field){ this.field=field; }
   public int getField(){ return field; }
}