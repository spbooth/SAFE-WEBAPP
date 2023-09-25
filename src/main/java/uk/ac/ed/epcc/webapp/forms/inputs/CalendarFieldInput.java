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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;



public class CalendarFieldInput extends IntegerSetInput implements MinMaxInput<Integer>{
  private static final Integer[] field_list=new Integer[]{Calendar.SECOND,Calendar.MINUTE,Calendar.HOUR,Calendar.DAY_OF_MONTH,Calendar.WEEK_OF_YEAR,Calendar.MONTH,Calendar.YEAR};
  public CalendarFieldInput(){
	  super(field_list);
  }
  /** Constructor limiting the finest resolution field presented.
   * 
   * @param finest_field
   */
  public CalendarFieldInput(int finest_field){
	  super(getSet(finest_field));
  }
@Override
public String getText(Integer item) {
	if( item == null ){
		return "None";
	}
	switch(item.intValue()){
	case Calendar.SECOND: return "Seconds";
	case Calendar.MINUTE: return "Minutes";
	case Calendar.HOUR: return "Hours";
	case Calendar.DAY_OF_MONTH: return "Days";
	case Calendar.WEEK_OF_YEAR: return "Weeks";
	case Calendar.MONTH: return "Months";
	case Calendar.YEAR: return "Years";
	default: return "Calendar field "+item.intValue();
	}
}


private static Set<Integer> getSet(int max){
	LinkedHashSet<Integer> result = new LinkedHashSet<>(field_list.length);
	for(Integer i : field_list){
		if( i <= max ){
			result.add(i);
		}
	}
	return result;
}
}