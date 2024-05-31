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
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.forms.Creator;
/** A create transition that uses a {@link Creator} object
 * 
 * @author spb
 *
 * @param <X> type of object created
 */
public abstract class CreatorTransition<X> extends AbstractTargetLessTransition<X> {
	
	public CreatorTransition(){
	}
	public void buildForm(Form f, AppContext ctx) throws TransitionException {
		FormCreator c = getCreator(ctx);
		try {
			c.buildCreationForm(f);
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Error building Creation form",e);
			throw new TransitionException("Internal error");
		}
	}

	public abstract FormCreator getCreator(AppContext c);
}