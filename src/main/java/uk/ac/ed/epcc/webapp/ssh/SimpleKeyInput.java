//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.ssh;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.session.SingleKeyComposite;

public class SimpleKeyInput extends TextInput{
	
	private final AuthorizedKeyValidator val;
	
	public SimpleKeyInput(AppContext conn) {
		setBoxWidth(48);
		setMaxResultLength(4096);
		setSingle(true);
		
		if( SingleKeyComposite.SSH_REQUIRE_RSA_FEATURE.isEnabled(conn)){
			RsaKeyValidator val = new RsaKeyValidator();
			val.setMinBits(conn.getIntegerParameter("ssh.min.bits", 2048));
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