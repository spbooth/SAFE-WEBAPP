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
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;

/** A marker for a stand alone interface that can be
 * implemented directly or via a composite.
 * The {@link AppContext#makeObject(Class, String)} method will
 * return either the parent object of the composite in this case.
 * @author Stephen Booth
 *
 */
public interface Composable {

	/** Convert an object to a {@link Composable}.
	 * The object either has to implement the interface or be a {@link DataObjectFactory} in which case
	 * the key is used to look up a {@link Composite}
	 * 
	 * @param target
	 * @param o
	 * @return
	 * @throws InvalidArgument
	 */
	public static <X extends Composable> X getComposable(Class<X> target, Class<? extends X> key, Object o) throws InvalidArgument {
		if( o == null) {
			return null;
		}
		if( target.isAssignableFrom(o.getClass())) {
			return (X) o;
		}else if( o instanceof DataObjectFactory) {
			return (X) ((DataObjectFactory)o).getComposite(key);
		}
		throw new InvalidArgument(o.getClass().getCanonicalName()+" cannot generate a "+target.getCanonicalName());
	}
}
