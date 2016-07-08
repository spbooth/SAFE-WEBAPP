package uk.ac.ed.epcc.webapp.model.far.response;

import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.session.SessionService;

public interface ShowButton<D extends DynamicForm,R extends ResponseManager.Response<D>> {
	public boolean showButton(ResponseTarget<D,R> target, SessionService<?> sess);
}
