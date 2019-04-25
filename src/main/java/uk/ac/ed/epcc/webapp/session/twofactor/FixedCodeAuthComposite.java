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

import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;

/** A debugging/testing {@link FormAuthComposite} that uses a single fixed code
 * @author Stephen Booth
 *
 */
public class FixedCodeAuthComposite<A extends AppUser> extends CodeAuthComposite<A,String> {

	/**
	 * @param fac
	 */
	public FixedCodeAuthComposite(AppUserFactory<A> fac) {
		super(fac);
	}

	private String getAuthCode() {
		return getContext().getInitParameter(getConfigPrefix()+".code");
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.twofactor.CodeAuthComposite#enabled(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	protected boolean enabled(A user) {
		return getAuthCode() != null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.twofactor.CodeAuthComposite#getInput()
	 */
	@Override
	public Input<String> getInput() {
		TextInput input = new TextInput();
		input.setSingle(true);
		return input;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.twofactor.CodeAuthComposite#verify(java.lang.Object)
	 */
	@Override
	public boolean verify(A target,String value) {
		if( value == null) {
			return false;
		}
		return getAuthCode().equals(value);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.twofactor.FormAuthComposite#getConfigPrefix()
	 */
	@Override
	protected String getConfigPrefix() {
		// TODO Auto-generated method stub
		return "fixed_auth_code";
	}

	
}
