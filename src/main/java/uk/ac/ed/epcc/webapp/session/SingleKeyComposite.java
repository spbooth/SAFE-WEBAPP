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

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.ssh.AuthorizedKeyValidator;
import uk.ac.ed.epcc.webapp.ssh.RsaKeyValidator;

/**
 * @author spb
 *
 */
public class SingleKeyComposite extends PublicKeyComposite<String> {

	/**
	 * @param fac
	 */
	public SingleKeyComposite(AppUserFactory fac) {
		super(fac);
		// TODO Auto-generated constructor stub
	}
	
	public class SimpleKeyInput extends TextInput{
		private final AuthorizedKeyValidator val;
		
		public SimpleKeyInput() {
			setBoxWidth(48);
			setMaxResultLength(4096);
			setSingle(true);
			
			if( SSH_REQUIRE_RSA_FEATURE.isEnabled(getContext())){
				RsaKeyValidator val = new RsaKeyValidator();
				val.setMinBits(getContext().getIntegerParameter("ssh.min.bits", 2048));
				this.val=val;
			}else {
				this.val = new AuthorizedKeyValidator();
			}
			addValidator(val);
		}

		@Override
		public String parseValue(String v) throws ParseException {
			return val.normalise(super.parseValue(v));
		}
	}
	
	@Override
	protected ParseAbstractInput<String> getInput(){
		
		return new SimpleKeyInput();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PublicKeyComposite#load(java.lang.String)
	 */
	@Override
	protected String load(String value) throws ParseException {
		AuthorizedKeyValidator val = new AuthorizedKeyValidator();
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
