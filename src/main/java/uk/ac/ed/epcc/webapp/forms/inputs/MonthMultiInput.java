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
import java.util.Date;

/** An input that selects a day using pull down menus.
 * The class also implements ParseInput to support setting 
 * default values.
 * 
 * @author spb
 *
 */


public class MonthMultiInput extends TimeStampMultiInput {
    
	public MonthMultiInput(Date now){
		super(now,1000L,Calendar.MONTH);
	}
	public MonthMultiInput(Date now,long resolution){
		super(now,resolution,Calendar.MONTH);
	}
}