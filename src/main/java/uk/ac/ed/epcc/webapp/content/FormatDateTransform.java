//| Copyright - The University of Edinburgh 2013                            |
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
package uk.ac.ed.epcc.webapp.content;

import java.text.DateFormat;
import java.util.Date;


/**
 * Format Date cells of a Table using a DateFormat.
 * 
 * @author spb
 * 
 */
public class FormatDateTransform implements Transform {
	DateFormat nf;

	public FormatDateTransform(DateFormat f) {
		nf = f;
	}

	public Object convert(Object old) {
		if (old == null) {
			return null;
		}
		if (old instanceof Date) {
			return nf.format((Date) old);
		}
		return old;
	}

}