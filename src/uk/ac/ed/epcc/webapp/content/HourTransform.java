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
         long value = d.longValue();
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