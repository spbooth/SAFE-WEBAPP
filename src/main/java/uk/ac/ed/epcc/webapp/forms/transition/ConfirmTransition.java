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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.ChainAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** FormTransition that selects a choice of two DirectTransitions as
 * a result of a yes/no question
 * @see ForwardTransition
 * @author spb
 *
 * @param <T>
 */


public class ConfirmTransition<T> implements ExtraFormTransition<T>{
	/** attribute that that can be added to an AppContext to convert ConfirmTransition
	 * into a single action form (ie with a default action) for api use
	 * 
	 */
	public static final String FORCE_CONFIRM_ATTR="TransitionForceConfirm";
	/**
	 * 
	 */
	public static final String NO = "No";
	/**
	 * 
	 */
	public static final String YES = "Yes";
	private final DirectTransition<T> yes_transition;
	private final DirectTransition<T> no_transition;
	private final String text;
	public ConfirmTransition(String text, DirectTransition<T> yes, DirectTransition<T> no){
		this.text=text;
		this.yes_transition=yes;
		this.no_transition=no;
	}
	@Override
	public void buildForm(Form f, T target, AppContext c)
			throws TransitionException {
		Object force = c.getAttribute(FORCE_CONFIRM_ATTR);
		boolean show_no=true;
		if( force != null && force instanceof Boolean && ((Boolean)force).booleanValue()) {
			show_no=false;
		}
		
		f.addAction(YES, new ChainAction<>(target,c,yes_transition));
		if( show_no) {
			f.addAction(NO, new ChainAction<>(target,c,no_transition));
		}
		
	}
	@Override
	public <X extends ContentBuilder> X getExtraHtml(X cb,SessionService<?> op, T target) {
		cb.addHeading(2, text);
		return cb;
	}
	@Override
	public FormResult getResult(TransitionVisitor<T> vis) throws TransitionException {
		return vis.doFormTransition(this);
	}
}