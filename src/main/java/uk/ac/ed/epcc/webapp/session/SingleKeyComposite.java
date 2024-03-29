//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.ssh.AuthorizedKeyValidator;
import uk.ac.ed.epcc.webapp.ssh.BadKeyFactory;
import uk.ac.ed.epcc.webapp.ssh.SimpleKeyInput;

/**
 * @author spb
 *
 */
public class SingleKeyComposite extends PublicKeyComposite<String> {
	public static final Feature CHECK_BAD_KEYS = new Feature("ssh.public_keys.bad_key_list",false,"Check a table of known bad keys");
	/**
	 * @param fac
	 */
	public SingleKeyComposite(AppUserFactory fac) {
		super(fac);
	}
	
	@Override
	protected ParseAbstractInput<String> getInput(){
		
		SimpleKeyInput input = new SimpleKeyInput(getContext());
		if( CHECK_BAD_KEYS.isEnabled(getContext())) {
			input.addValidator(new BadKeyFactory(getContext()));
		}
		return input;
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PublicKeyComposite#load(java.lang.String)
	 */
	@Override
	protected String load(String value) throws ParseException {
		AuthorizedKeyValidator val = new AuthorizedKeyValidator(getContext());
		return val.normalise(value);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PublicKeyComposite#format(java.lang.Object)
	 */
	@Override
	protected String format(String key) {
		return key;
	}
	
	
}
