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
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.*;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;

/** A transition for selecting a target using methods from a {@link FormUpdate}.
 * 
 * @author spb
 *
 * @param <K>
 * @param <X>
 */

public abstract class AbstractFormUpdateTransition<K, X> extends AbstractTargetLessTransition<X> {
	private final TransitionProvider<K, X> tp;
	private final K next;
	private final String label;

	/**
	 * 
	 * @param label
	 *            label to use for selector
	 * @param tp
	 *            TransitionProvider
	 * @param next
	 *            transition to recurse to
	 */
	public AbstractFormUpdateTransition(String label, TransitionProvider<K, X> tp, K next) {
		this.label = label;
		this.tp = tp;
		this.next = next;
	}

	public abstract FormUpdate<X> getUpdate(AppContext c);


	private final class FormUpdateAction extends FormAction {
		private final FormUpdate<X> update;

		public FormUpdateAction(FormUpdate<X> update) {
			this.update = update;
		}

		@Override
		public FormResult action(Form f)
				throws uk.ac.ed.epcc.webapp.forms.exceptions.ActionException {
			X target = update.getSelected(f);
			if (target == null) {
				// TransitionException reported to user
				return new WarningMessageResult("no_select_target");
			}
			return new ChainedTransitionResult<>(tp, target, next);
		}
	}

	public void buildForm(Form f, AppContext c) throws TransitionException {
		FormUpdate<X> update = getUpdate(c);
		update.buildSelectForm(f, label, null);
		f.addAction(" Select ", new FormUpdateAction(update));

	}
}