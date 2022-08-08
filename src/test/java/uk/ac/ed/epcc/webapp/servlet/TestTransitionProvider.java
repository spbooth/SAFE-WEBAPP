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
import uk.ac.ed.epcc.webapp.forms.inputs.FileUploadDecorator;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTargetlessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ConfirmTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link TransitionProvider} for testing
 * 
 * this just operates on integers
 * 
 * @author spb
 *
 */

public class TestTransitionProvider extends AbstractViewTransitionProvider<Number, TransitionKey<Number>> implements ViewTransitionProvider<TransitionKey<Number>, Number>{

	/**
	 * 
	 */
	public static final String VALUE = "Value";
	/**
	 * 
	 */
	public static final TransitionKey<Number> FORM_ADD_KEY = new TransitionKey<>(Number.class, "FormAdd");
	/**
	 * 
	 */
	public static final TransitionKey<Number> ADD_KEY = new TransitionKey<>(Number.class, "Add");
	
	public static final TransitionKey<Number> DUAL_ADD_KEY = new TransitionKey<>(Number.class, "DualAdd");

	public static final TransitionKey<Number> CONFIRM_ADD_KEY = new TransitionKey<>(Number.class, "ConfirmAdd");
	
	public static final TransitionKey<Number> THREE_KEY = new TransitionKey<>(Number.class,"Three");
	
	public static final TransitionKey<Number> SET_KEY = new TransitionKey<>(Number.class,"Set");
	
	public static final TransitionKey<Number> UPLOAD_KEY = new TransitionKey<>(Number.class,"Upload");
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
    private class SetAction extends FormAction{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public FormResult action(Form f) throws ActionException {
			
			return new ViewResult((Number)f.get(VALUE));
		}
    	
    }
    
    private class UploadAction extends FormAction{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public FormResult action(Form f) throws ActionException {
			String text = (String) f.get(VALUE);
			int result = 0;
			if( text != null && ! text.isEmpty()) {
				result = Integer.parseInt(text);
			}
			return new ViewResult(result);
		}
    	
    }
	/**
	 * @param c
	 */
	public TestTransitionProvider(AppContext c) {
		super(c);
	}
	@Override
	protected void setupTransitions() {
		
		addTransition(THREE_KEY,new AbstractDirectTargetlessTransition<Number>() {

			@Override
			public FormResult doTransition(AppContext c) throws TransitionException {
				
				return new ViewResult(Integer.valueOf(3));
			}
		});
		addTransition(ADD_KEY, new AbstractDirectTransition<Number>() {
			@Override
			public FormResult doTransition(Number target, AppContext c)
					throws TransitionException {
				return new ChainedTransitionResult<>(TestTransitionProvider.this, target.intValue()+1, null);
			}
		});
		addTransition(DUAL_ADD_KEY, new AbstractDirectTransition<Number>() {
			@Override
			public FormResult doTransition(Number target, AppContext c)
					throws TransitionException {
				return new ChainedTransitionResult<>(TestTransitionProvider.this, target.intValue()+1, ADD_KEY);
			}
		});
		addTransition(FORM_ADD_KEY, new AbstractFormTransition<Number>() {
			@Override
			public void buildForm(Form f, Number target, AppContext conn)
					throws TransitionException {
				f.addInput(VALUE, VALUE, new IntegerInput());
				f.addAction("Add", new AddAction(target));
			}
			
		});
		addTransition(CONFIRM_ADD_KEY, new ConfirmTransition<>("Are you sure?",
				new AbstractDirectTransition<Number>() {
					@Override
					public FormResult doTransition(Number target, AppContext c)
					throws TransitionException {
							return new ChainedTransitionResult<>(TestTransitionProvider.this, target.intValue()+1, null);
					}
				}, 
				new AbstractDirectTransition<Number>() {
					@Override
					public FormResult doTransition(Number target, AppContext c)
					throws TransitionException {
						return new ViewTransitionResult<>(TestTransitionProvider.this, target.intValue());
					}
				}	
			));
		addTransition(SET_KEY, new AbstractTargetLessTransition<Number>() {

			@Override
			public void buildForm(Form f, AppContext c) throws TransitionException {
				f.addInput(VALUE, VALUE, new IntegerInput());
				f.addAction("Set", new SetAction());
			}
		});
		// Test the file-upload decorator (this is to expose a test to RestServlet)
		addTransition(UPLOAD_KEY,new AbstractFormTransition<Number>() {

			@Override
			public void buildForm(Form f, Number target, AppContext conn) throws TransitionException {
				f.addInput(VALUE,VALUE, new FileUploadDecorator(new TextInput())).setOptional(true);
				f.put(VALUE, target.toString());
				f.addAction("Set", new UploadAction());
				
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getTarget(java.lang.String)
	 */
	@Override
	public Number getTarget(String id) {
		return Integer.parseInt(id);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getID(java.lang.Object)
	 */
	@Override
	public String getID(Number target) {
		return target.toString();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getTargetName()
	 */
	@Override
	public String getTargetName() {
		return "Test";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean allowTransition(AppContext c, Number target,
			TransitionKey<Number> key) {
		if(target == null) {
			return key == THREE_KEY || key == SET_KEY || key == UPLOAD_KEY;
		}
		return target.intValue() < MAX_ALLOWED ;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb,
			Number target) {
		return cb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#canView(java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean canView(Number target, SessionService<?> sess) {
		return target.intValue() < MAX_ALLOWED;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getTopContent(uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public <X extends ContentBuilder> X getTopContent(X cb, Number target,
			SessionService<?> sess) {
		return cb;
	}
	@Override
	public <X extends ContentBuilder> X getBottomContent(X cb, Number target,
			SessionService<?> sess) {
		return cb;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getLogContent(uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public <X extends ContentBuilder> X getLogContent(X cb, Number target,
			SessionService<?> sess) {
		return cb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getHelp(java.lang.Object)
	 */
	@Override
	public String getHelp(TransitionKey<Number> key) {
		return null;
	}
	
	
}