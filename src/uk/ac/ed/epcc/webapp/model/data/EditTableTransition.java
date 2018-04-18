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

import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.jdbc.table.TableStructureListener;

/**
 * @author spb
 *
 */
public abstract class EditTableTransition<T extends DataObjectFactory<?>> implements Transition<T> {
    
	protected final Repository getRepository(T fac) {
		return fac.res;
	}
	protected final void resetStructure(T fac) {
		if( fac instanceof TableStructureListener) {
			((TableStructureListener)fac).resetStructure();
		}
		for(TableStructureListener l : fac.getComposites(TableStructureListener.class)) {
			l.resetStructure();
		}
		Repository.reset(fac.getContext(), fac.getTag());
	}
}
