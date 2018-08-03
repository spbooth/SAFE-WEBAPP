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
package uk.ac.ed.epcc.webapp.servlet;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ConfirmTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link TransitionProvider} for testing
 * 
 * this just operates on integers
 * 
 * @author spb
 *
 */

public class TestTransitionProvider extends AbstractTransitionProvider<Number, TransitionKey<Number>> implements ViewTransitionProvider<TransitionKey<Number>, Number>{

	/**
	 * 
	 */
	public static final String VALUE = "Value";
	/**
	 * 
	 */
	public static final TransitionKey<Number> FORM_ADD_KEY = new TransitionKey<Number>(Number.class, "FormAdd");
	/**
	 * 
	 */
	public static final TransitionKey<Number> ADD_KEY = new TransitionKey<Number>(Number.class, "Add");
	
	public static final TransitionKey<Number> CONFIRM_ADD_KEY = new TransitionKey<Number>(Number.class, "ConfirmAdd");
	/**
	 * 
	 */
	private static final int MAX_ALLOWED = 10;
    private class AddAction extends FormAction{

    	private Number target;
    	public AddAction(Number target){
    		this.target=target;
    	}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public FormResult action(Form f) throws ActionException{
			return new ChainedTransitionResult(TestTransitionProvider.this, target.intValue()+(Integer)f.get(VALUE), null);
		}
    	
    }
	/**
	 * @param c
	 */
	public TestTransitionProvider(AppContext c) {
		super(c);
		addTransition(ADD_KEY, new AbstractDirectTransition<Number>() {
			public FormResult doTransition(Number target, AppContext c)
					throws TransitionException {
				return new ChainedTransitionResult<Number, TransitionKey<Number>>(TestTransitionProvider.this, target.intValue()+1, null);
			}
		});
		addTransition(FORM_ADD_KEY, new AbstractFormTransition<Number>() {
			public void buildForm(Form f, Number target, AppContext conn)
					throws TransitionException {
				f.addInput(VALUE, VALUE, new IntegerInput());
				f.addAction("Add", new AddAction(target));
			}
			
		});
		addTransition(CONFIRM_ADD_KEY, new ConfirmTransition<>("Are you sure?",
				new AbstractDirectTransition<Number>() {
					public FormResult doTransition(Number target, AppContext c)
					throws TransitionException {
							return new ChainedTransitionResult<Number, TransitionKey<Number>>(TestTransitionProvider.this, target.intValue()+1, null);
					}
				}, 
				new AbstractDirectTransition<Number>() {
					public FormResult doTransition(Number target, AppContext c)
					throws TransitionException {
						return new ViewTransitionResult<Number, TransitionKey<Number>>(TestTransitionProvider.this, target.intValue());
					}
				}	
			));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getTarget(java.lang.String)
	 */
	public Number getTarget(String id) {
		return Integer.parseInt(id);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getID(java.lang.Object)
	 */
	public String getID(Number target) {
		return target.toString();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getTargetName()
	 */
	public String getTargetName() {
		return "Test";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	public boolean allowTransition(AppContext c, Number target,
			TransitionKey<Number> key) {
		return target.intValue() < MAX_ALLOWED ;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
	 */
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb,
			Number target) {
		return cb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#canView(java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	public boolean canView(Number target, SessionService<?> sess) {
		return target.intValue() < MAX_ALLOWED;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getTopContent(uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	public <X extends ContentBuilder> X getTopContent(X cb, Number target,
			SessionService<?> sess) {
		return cb;
	}
	public <X extends ContentBuilder> X getBottomContent(X cb, Number target,
			SessionService<?> sess) {
		return cb;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getLogContent(uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	public <X extends ContentBuilder> X getLogContent(X cb, Number target,
			SessionService<?> sess) {
		return cb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getHelp(java.lang.Object)
	 */
	public String getHelp(TransitionKey<Number> key) {
		return null;
	}
	public String getText(TransitionKey<Number> key){
		return key.toString();
	}
	
}