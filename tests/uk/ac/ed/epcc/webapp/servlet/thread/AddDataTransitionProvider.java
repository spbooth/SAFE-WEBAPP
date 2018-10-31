// Copyright - The University of Edinburgh 2016
package uk.ac.ed.epcc.webapp.servlet.thread;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.SimpleViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.servlet.thread.TestFactory.TestData;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
public class AddDataTransitionProvider extends SimpleViewTransitionProvider<TestFactory.TestData, TransitionKey<TestFactory.TestData>> implements ViewTransitionProvider<TransitionKey<TestFactory.TestData>, TestData> {

	public static final TransitionKey<TestData> ADD = new TransitionKey<>(TestData.class, "ADD");

	
	public class AddTransition extends AbstractFormTransition<TestData>{
		/**
		 * 
		 */
		private static final String COMMENT_INPUT = "Comment";
		/**
		 * 
		 */
		private static final String WAIT_INPUT = "Wait";
		/**
		 * 
		 */
		private static final String DATA_INPUT = "Data";
		private static final String DATA2_INPUT = "Data2";
		public class AddAction extends FormAction{
			private final TestData target;
			public AddAction(TestData target){
				this.target=target;
			}
			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				int old = target.getData();
				int add = (Integer) f.get(DATA_INPUT);
				int add2 = (Integer) f.get(DATA2_INPUT);
				Integer wait = (Integer) f.get(WAIT_INPUT);
				String text = (String) f.get(COMMENT_INPUT);
				int wait_time=0;
				if( wait != null ){
					wait_time=wait;
				}
				target.setData(old + add);
				
				try {
					target.commit();
					if( wait_time > 0 ){
						Thread.sleep(wait_time*1000);
					}
					target.setData(target.getData()+add2);
					target.commit();
					if( text != null && text.length() > 0){
						DataPersonManager man = new DataPersonManager(getContext());
						man.addComment(target, text+"\n");
					}
				} catch (Exception e) {
					throw new ActionException("Wait interrupted", e);
				}
				return new ViewTransitionResult<>(AddDataTransitionProvider.this, target);
			}
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, TestData target, AppContext conn) throws TransitionException {
			IntegerInput data_input = new IntegerInput();
			IntegerInput data2_input = new IntegerInput();
			f.addInput(DATA_INPUT, "Number to add", data_input);
			data2_input.setValue(0);
			f.addInput(DATA2_INPUT, "Number to add after wait", data2_input);
			IntegerInput wait = new IntegerInput();
			wait.setValue(0);
			f.addInput(WAIT_INPUT, "time to wait",wait);
			TextInput text = new TextInput();
			text.setOptional(true);
			f.addInput(COMMENT_INPUT, "Comment to add in link", text);
			f.addAction("Add", new AddAction(target));
			
		}
		
	}
	
	/**
	 * @param c
	 * @param fac
	 * @param target_name
	 */
	public AddDataTransitionProvider(AppContext c,  String target_name) {
		super(c, new TestFactory(c), target_name);
		addTransition(ADD, new AddTransition());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean allowTransition(AppContext c, TestData target, TransitionKey<TestData> key) {
		return true;
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#canView(java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean canView(TestData target, SessionService<?> sess) {
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getTopContent(uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public <X extends ContentBuilder> X getTopContent(X cb, TestData target, SessionService<?> sess) {
		cb.addHeading(3, "Value");
		cb.addText(Integer.toString(target.getData()));
		return cb;
	}

	
	


	public TestFactory getFactory(){
		return (TestFactory) getProducer();
	}

}
