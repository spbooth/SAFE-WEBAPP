package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraContent;
import uk.ac.ed.epcc.webapp.forms.transition.FormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Retirable;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** A generic retire transition.
 * 
 * This is intended to re-locate the {@link RetireAction} to a transition rather
 * than an action on update.
 * 
 * @author Stephen Booth
 *
 * @param <R>
 */
public class RetireTransition<K,R extends DataObject & Retirable> implements FormTransition<R>, ExtraContent<R> {

	
	private final String type_name;
	private final TransitionFactory<K, R>  provider;
	
	public class CancelAction extends FormAction{

		private final R target;
		public CancelAction(R target) {
			this.target=target;
			setMustValidate(false);
		}
		@Override
		public FormResult action(Form f) throws ActionException {
			if( provider instanceof ViewTransitionFactory) {
				return new ViewTransitionResult<R,K>((ViewTransitionFactory<K, R>)provider,target);
			}
			return new MessageResult("aborted");
		}
		
	}
	public RetireTransition(String type_name,TransitionFactory<K, R>  provider) {
		this.type_name=type_name;
		this.provider=provider;
	}

	@Override
	public void buildForm(Form f, R target, AppContext conn) throws TransitionException {
		f.addAction("Retire", new RetireAction<R>(type_name, target));
		f.addAction("Cancel", new CancelAction(target));
	}

	@Override
	public <X extends ContentBuilder> X getExtraHtml(X cb, SessionService<?> op, R target) {
		
		Object warning = target.getRetireWarning();
		if( warning != null) {
			cb.addObject(warning);
		}
		return cb;
	}

}
