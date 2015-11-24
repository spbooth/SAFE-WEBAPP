// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.response.personal;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionProvider;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.SimpleTransitionProvider;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseTarget;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseTransitionProvider;
import uk.ac.ed.epcc.webapp.model.far.response.personal.PersonalResponseManager.PersonalResponse;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 * @param <T> 
 * @param <D> 
 * @param <K> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.5 $")
public class PersonalResponseTransitionProvider<T extends PersonalResponse<D>,D extends DynamicForm, K extends PersonalResponseKey<T>> extends
		SimpleTransitionProvider<T,K> implements IndexTransitionProvider<K, T>{


	private final PersonalResponseManager<T, D> getManager(){
		return (PersonalResponseManager<T, D>) getProducer();
	}
	
	
	public static final PersonalResponseKey CREATE = new PersonalResponseKey("Create", "Create a new reponse");
	/**
	 * @param c
	 */
	public PersonalResponseTransitionProvider(String tag,PersonalResponseManager<T, D> manager) {
		super(manager.getContext(),manager,tag);
		addTransition((K) CREATE, new CreateTransition());
	}
	
	public class CreateTransition extends AbstractTargetLessTransition<T>{

		/**
		 * 
		 */
		public static final String FORM_INPUT_FIELD = "Form";
		public class ViewAction extends FormAction{

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				D form = (D) f.getItem(FORM_INPUT_FIELD);
				try {
					T response =getManager().getResponse(form);
					Part p = form.getManager().getChildManager().getFirst(form);
					ResponseTransitionProvider<D, Response<D>> provider = getManager().getPathResponseProvider(); 
					if( p == null ){
						getLogger().error("null first child in form");
						return new MessageResult("internal_error");
					}
					return provider.new ViewResult(new ResponseTarget(response, p));
				} catch (DataException e) {
					throw new ActionException("Problem making response", e);
				}
			}
			
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, AppContext c) throws TransitionException {
			//TODO should probably restrict to frozen forms
			f.addInput(FORM_INPUT_FIELD, "form", getManager().getManager().getInput());
			f.addAction("Response", new ViewAction());
		}
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean allowTransition(AppContext c, T target, K key) {
		if( target == null && key.equals(CREATE)){
			return true;
		}
		try {
			return target.getPerson().equals(c.getService(SessionService.class).getCurrentPerson());
		} catch (DataException e) {
			getLogger().error("Problem checking access",e);
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb,
			T target) {
		return cb;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactory#getIndexTransition()
	 */
	@Override
	public K getIndexTransition() {
		return (K) CREATE;
	}

	
}
