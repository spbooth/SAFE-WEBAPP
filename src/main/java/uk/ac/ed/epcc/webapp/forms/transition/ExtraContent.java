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
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Interface for {@link FormTransition} or {@link TargetLessTransition}s 
 * that provide additional content
 * together with the Form
 * 
 * @author spb
 * @param <O> target object of transition
 *
 */

public interface ExtraContent<O> {	 
	/** Add the extra content to be shown with the transition
	 * 
	 * @param cb
	 * @param op
	 * @param target
	 * @return
	 */
	public <X extends ContentBuilder> X getExtraHtml(X cb,SessionService<?> op, O target);
	
	/** A multi-stage form may need to customise content based on form state and can override this method.
	 *  It defaults to the same as {@link #getExtraHtml(ContentBuilder, SessionService, Object)}
	 * 
	 * 
	 * @param cb
	 * @param op
	 * @param target
	 * @param f
	 * @return
	 */
	default public <X extends ContentBuilder> X getExtraHtml(X cb,SessionService<?> op, O target, Form f) {
		return getExtraHtml(cb,op,target);
	}
}