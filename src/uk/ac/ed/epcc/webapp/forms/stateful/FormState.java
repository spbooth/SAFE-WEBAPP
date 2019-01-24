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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Form state held in the session.
 * 
 * This is to implement multi-stage forms built using a single build method.
 * The normal idiom is that the multi-stage form is a single form where sets of
 * inputs are added in stages (being validated and locked on each stage transition)
 * allowing the values chosen in early stages to be used to customise the form contents in later stages.
 * 
 * @see RedisplayResult
 * @author Stephen Booth
 *
 */
public class FormState extends AbstractContexed {
	/**
	 * @author Stephen Booth
	 *
	 */
	public final class ResetAction extends FormAction {
		@Override
		public String getHelp() {
			return "Delete saved state and return the form to its initial stage";
		}

		@Override
		public boolean getMustValidate() {
			return false;
		}

		@Override
		public FormResult action(Form f) throws ActionException {
			clear();
			return self;
		}
	}
	/**
	 * @author Stephen Booth
	 *
	 */
	public final class NextAction extends FormAction {
		@Override
		public FormResult action(Form f) throws ActionException {
			Map data = makeCached();
			for(Iterator<String> it=f.getFieldIterator();it.hasNext();) {
				String field = it.next();
				data.put(field, f.get(field));
			}
			return self;
		}
	}
	/**
	 * 
	 */
	public static final String NEXT_ACTION = "Next";
	/**
	 * 
	 */
	public static final String RESET_ACTION = "Reset";
	private final String name;
	private final SessionService sess;
	private final FormResult self;
	/**
	 * @param conn
	 */
	public FormState(AppContext conn, String name,FormResult self) {
		super(conn);
		this.name=name;
		this.self=self;
		sess=conn.getService(SessionService.class);
	}

	/** poll the cached state for the contents of a partially build form.
	 * If all the form inputs have values cached in the form state then the
	 * form contents will be updated to match.
	 * <p>
	 * If the form validates in the new state:
	 * <ul>
	 * <li> The existing inputs are all locked</li>
	 * <li> A reset action is added to clear the cached state</li>
	 * <li> The method returns true.</li>
	 * </ul>
	 * If the form fails to validate an exception is thrown. It should validate if the form is built consistently as the 
	 * cached values will have come from a previous iteration.
	 * <p>
	 * If the cached values are not found:
	 * <ul>
	 * <li>A <b>next</b> action is added that will update the cached state. Note that
	 * the reset action may also be present if this is not the first stage.</li>
	 * <li> The method returns false to indicate to the form building method that form building should stop at this point.</li>
	 * </ul>
	 * 
	 * 
	 * @param f  {@link Form} being built
	 * @return  true/false if form building should continue.
	 * @throws TransitionException
	 */
	public boolean poll(Form f) throws TransitionException {
		Map data = getCached();
		if( data != null) {

			boolean complete=true;
			for(Iterator<String> it=f.getFieldIterator();it.hasNext();) {
				String field = it.next();
				if( ! data.containsKey(field)) {
					complete=false;
					break;
				}
			}
			if( complete) {
				try {
					for(Iterator<String> it=f.getFieldIterator();it.hasNext();) {
						String field = it.next();
						f.put(field, data.get(field));
						Field ff = f.getField(field);
						ff.validate();
						ff.lock();
					}
					for(FormValidator v : f.getValidators()) {
						v.validate(f);
					}
				}catch(FieldException e) {
					getLogger().error("Re-validate error in multi-stage form", e);
					clear();
					throw new TransitionException("Internal error please re-try");
				}
				f.addAction(RESET_ACTION, new ResetAction());
				return true;
			}
		}
		f.addAction(NEXT_ACTION, new NextAction());
		if( data != null ) {
			// this will have been added by last poll but
			// we want it the last action
			f.removeAction(RESET_ACTION);
			f.addAction(RESET_ACTION, new ResetAction());
		}
		return false;
	}
	
	/** Delete all cached state under this name.
	 * 
	 * Normally this should be called as part of the final form action
	 * to reset the multi-stage form. Otherwise it will return to the last
	 * stage if re-visited.
	 * 
	 */
	public void clear() {
		sess.removeAttribute(keyName());
	}
	
	private Map getCached() {
		return (Map) sess.getAttribute(keyName());
	}

	/**
	 * @return
	 */
	private String keyName() {
		return "FormState"+name;
	}
	private Map makeCached() {
		Map result = getCached();
		if( result == null) {
			result = new LinkedHashMap();
			sess.setAttribute(keyName(), result);
		}
		return result;
	}
}
