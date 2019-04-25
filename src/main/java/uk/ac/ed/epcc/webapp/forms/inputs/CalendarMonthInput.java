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




public class CalendarMonthInput extends IntegerSetInput {
	static final String names[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	
	private final Calendar c;
  public CalendarMonthInput(){
	  super(new Integer[]{Calendar.JANUARY,Calendar.FEBRUARY,Calendar.MARCH,Calendar.APRIL,Calendar.MAY,Calendar.JUNE,Calendar.JULY,Calendar.AUGUST,Calendar.SEPTEMBER,Calendar.OCTOBER,Calendar.NOVEMBER,Calendar.DECEMBER});
	  c = Calendar.getInstance();
	  c.setTimeInMillis(0L);
  }

@Override
public String getText(Integer item) {
	c.set(Calendar.MONTH, item.intValue());
	int month = c.get(Calendar.MONTH);
	return names[month];
}
}