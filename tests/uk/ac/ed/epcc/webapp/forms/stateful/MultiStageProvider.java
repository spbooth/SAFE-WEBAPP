//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.forms.stateful;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AnonymousTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.DefaultingTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactory;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;

/**
 * @author Stephen Booth
 *
 */
public class MultiStageProvider extends AbstractTransitionProvider<Number,TransitionKey<Number>> implements AnonymousTransitionFactory<TransitionKey<Number>, Number>, IndexTransitionFactory<TransitionKey<Number>, Number>{

	
	/**
	 * 
	 */
	public static final TransitionKey<Number> CREATE_KEY = new TransitionKey<Number>(Number.class, "Create");
	/**
	 * 
	 */
	public static final String UNITS = "Units";
	/**
	 * 
	 */
	public static final String TENS = "Tens";
	/**
	 * 
	 */
	public static final String HUNDREDS = "Hundreds";
	public class MultiStageCreate extends AbstractTargetLessTransition<Number>{

	

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, AppContext c) throws TransitionException {
			FormResult self = new ChainedTransitionResult<>(MultiStageProvider.this, null, CREATE_KEY);
			IntegerInput hundreds = new IntegerInput();
			hundreds.setMin(0);
			hundreds.setMax(1000);
			hundreds.setStep(100);
			f.addInput(HUNDREDS, HUNDREDS, hundreds);
			if( f.poll(self)) {
				Integer h = (Integer) f.get(HUNDREDS);
				IntegerInput tens = new IntegerInput();
				tens.setMin(h.intValue());
				tens.setMax(h.intValue()+90);
				tens.setStep(10);
				f.addInput(TENS, TENS, tens);
				if( f.poll(self)) {
					Integer t = (Integer) f.get(TENS);
					IntegerInput units = new IntegerInput();
					units.setMin(t.intValue());
					units.setMax(t.intValue()+9);
					units.setStep(1);
					f.addInput(UNITS, UNITS, units);
					f.addAction("Create", new FormAction() {
						
						@Override
						public FormResult action(Form f) throws ActionException {
							Integer unit = (Integer) f.get(UNITS);
							return new MessageResult("object_created", "Number", unit);
						}
					});
				}
				
			}
			
		}
		
	}

	/**
	 * @param c
	 * @param fac
	 * @param target_name
	 */
	public MultiStageProvider(AppContext c) {
		super(c);
		addTransition(CREATE_KEY, new MultiStageCreate());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getTarget(java.lang.String)
	 */
	@Override
	public Number getTarget(String id) {
		if( id == null) {
			return null;
		}
		return Integer.parseInt(id);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getID(java.lang.Object)
	 */
	@Override
	public String getID(Number target) {
		return Integer.toString(target.intValue());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getTargetName()
	 */
	@Override
	public String getTargetName() {
		return "MultiStage";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean allowTransition(AppContext c, Number target, TransitionKey<Number> key) {
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb, Number target) {
		return cb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactory#getIndexTransition()
	 */
	@Override
	public TransitionKey<Number> getIndexTransition() {
		return CREATE_KEY;
	}

	

	
}
