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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.model.data.Duration;
/** A {@link NumberTransform} that format a second-count into a
 * H:MM:SS format.
 * 
 * If the input is a {@link Duration} then the second value will be taken
 * (even though the default numerical value of a {@link Duration} is millisecond. However as it is easy to
 * lose the duration nature of a value (for example by taking averages) it is better to use a {@link MillisToHourTransform}
 * for {@link Duration} values.
 * 
 * 
 * @author Stephen Booth
 * @See {@link MillisToHourTransform}
 *
 */
public class HourTransform implements NumberTransform{
	private String default_value="0:00:00";
	
	
	public HourTransform(){
	}
	
	public String getDefaultValue() {
		return default_value;
	}
	public void setDefaultValue(String default_value) {
		this.default_value = default_value;
	}
	/** Convert a {@link Number} of seconds into a HH:MM:SS string
	 * 
	 * @param d
	 * @return
	 */
	 public static String toHrsMinSec(Number d) {
         String result = "";
         long value;
         if( d instanceof Duration) {
        	 value=((Duration)d).getSeconds();
         }else {
        	 value= d.longValue();
         }
         if( value < 0L){
        	 result = result+"-";
        	 value = -value;
         }
         long hours = (long) (value / 3600L);
         result = result + hours + ":";
         long minutes = (long) (value % 3600L) / 60L;
         if (minutes < 10L)
             result = result + "0";
         result = result + minutes + ":";
         long seconds = (long) (value % (60L));
         if (seconds < 10L)
             result = result + "0";
         result = result + seconds;
         return result;
     }
	  public Object convert(Object old) {
          if (old != null) {

              if (old instanceof Number) {
                  return toHrsMinSec((Number) old);
              } else {
                  return old;
              }
          } else {
              return default_value;
          }
	  }
}