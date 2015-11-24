// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;

/** A transition for selecting a target using methods from a {@link FormUpdate}.
 * 
 * @author spb
 *
 * @param <K>
 * @param <X>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: AbstractFormUpdateTransition.java,v 1.1 2015/04/11 14:56:59 spb Exp $")
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
				return new MessageResult("no_select_target");
			}
			return new ChainedTransitionResult<X, K>(tp, target, next);
		}
	}

	public void buildForm(Form f, AppContext c) throws TransitionException {
		FormUpdate<X> update = getUpdate(c);
		update.buildSelectForm(f, label, null);
		f.addAction(" Select ", new FormUpdateAction(update));

	}
}