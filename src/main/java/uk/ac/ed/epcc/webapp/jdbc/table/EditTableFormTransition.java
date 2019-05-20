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
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.FormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.EditTableTransition;

/**
 * @author spb
 *
 */
public abstract class EditTableFormTransition<T extends DataObjectFactory> extends EditTableTransition<T> implements FormTransition<T>{

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.Transition#getResult(uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor)
	 */
	@Override
	public FormResult getResult(TransitionVisitor<T> vis) throws TransitionException {
		return vis.doFormTransition(this);
	}
    
	
}
