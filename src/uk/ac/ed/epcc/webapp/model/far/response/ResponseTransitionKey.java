// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.response;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class ResponseTransitionKey<D extends DynamicForm, R extends Response<D>> extends TransitionKey<ResponseTarget<D,R>> {

	/**
	 * @param t
	 * @param name
	 * @param help
	 */
	public ResponseTransitionKey(String name, String help) {
		super(ResponseTarget.class, name, help);
	}

	/**
	 * @param t
	 * @param name
	 */
	public ResponseTransitionKey(String name) {
		super(ResponseTarget.class, name);
	}
	
	public boolean allow(ResponseTarget<D, R> target, SessionService<?> sess){
		return target.getResponse().canEdit(sess);
	}

}
