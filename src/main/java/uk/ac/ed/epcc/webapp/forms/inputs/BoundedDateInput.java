//| Copyright - The University of Edinburgh 2018                            |
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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.preferences.Preference;

/** Interface for Date inputs with bounds
 * @author spb
 *
 */
public interface BoundedDateInput extends ParseInput<Date>, BoundedInput<Date> {
	public static final Preference USE_DATE_INPUT = new Preference("forms.use_date_input", true, "Use html date/time inputs in preference to multi-input");
	
	
	public static BoundedDateInput getInstance(AppContext conn,long resolution,int finest_field) {
		if( USE_DATE_INPUT.isEnabled(conn) && finest_field == Calendar.DAY_OF_MONTH) {
			return new RelativeDateInput();
		}
		return new TimeStampMultiInput(resolution, finest_field);
	}
	
	public static BoundedDateInput getInstance(AppContext conn, int finest_field) {
		return getInstance(conn,1000L, finest_field);
	}
}
