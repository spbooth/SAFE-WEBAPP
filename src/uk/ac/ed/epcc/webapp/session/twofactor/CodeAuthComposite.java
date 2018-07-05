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

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;

/** A {@link FormAuthComposite} with a single field.
 * @author Stephen Booth
 *
 */
public abstract class CodeAuthComposite<AU extends AppUser,T> extends FormAuthComposite<AU> {

	/**
	 * 
	 */
	public static final String CODE = "Code";
	

	/**
	 * @param fac
	 */
	public CodeAuthComposite(AppUserFactory<AU> fac) {
		super(fac);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.twofactor.FormAuthComposite#modifyForm(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public void modifyForm(AU user,Form f) {
		f.addInput(CODE, "2fa Verification code", getInput());
		f.getField(CODE).addValidator(new TokenFieldValidator(this, user));
	}
	
	public abstract Input<T> getInput();

	public abstract boolean verify(AU target, T value);
}
