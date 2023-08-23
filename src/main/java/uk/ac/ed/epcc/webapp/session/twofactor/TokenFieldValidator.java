//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.session.twofactor;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;

/** A {@link FieldValidator} for MFA tokens
 * @author Stephen Booth
 *
 */
public class TokenFieldValidator<A extends AppUser, T> implements FieldValidator<T> {

	/**
	 * @param comp
	 */
	public TokenFieldValidator(CodeAuthComposite<A, T> comp,A user) {
		super();
		this.comp = comp;
		this.user=user;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.FieldValidator#validate(java.lang.Object)
	 */
	@Override
	public void validate(T data) throws FieldException {
		// Try to prevent brute-forcing by
		// simultaneously posting multiple codes to the form.
		synchronized (getClass()) {

			boolean ok = comp.verify(user, data);
			if( ! ok ) {
				try {
					long delay = comp.getContext().getLongParameter("twofactor.auth-delay", 500);
					if( delay > 0L ) {
						Thread.sleep(delay);
					}
				} catch (InterruptedException e) {

				}
				throw new ValidateException("Incorrect");
			}
			comp.authenticated();
		}
	}
	private final CodeAuthComposite<A, T> comp;
	private final A user;
}
