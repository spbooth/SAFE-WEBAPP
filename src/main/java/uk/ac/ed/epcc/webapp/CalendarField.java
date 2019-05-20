//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
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