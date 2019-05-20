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

import uk.ac.ed.epcc.webapp.AppContext;

/** A {@link Transform} that wraps a {@link Labeller}
 * 
 * Because a {@link FormatProvider} can return a null {@link Labeller}
 * this class handles null labellers as well to reduce the need to check the return values.
 * @author spb
 *
 */

public class LabellerTransform implements Transform{

	public LabellerTransform(AppContext conn, Labeller labeller) {
		super();
		this.conn = conn;
		this.labeller = labeller;
	}

	private final AppContext conn;
	private final Labeller labeller;

	
	@SuppressWarnings("unchecked")
	public Object convert(Object old) {
		if( labeller != null && labeller.accepts(old)){
			// Transforms are type tollerant so
			// check labeller can handle type.
			return labeller.getLabel(conn, old);
		}
		return old;
	}

}