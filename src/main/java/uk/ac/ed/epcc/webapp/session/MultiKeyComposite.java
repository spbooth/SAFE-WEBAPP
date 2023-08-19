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

import java.io.IOException;
import java.security.PublicKey;

import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.PublicKeyParseException;
import uk.ac.ed.epcc.webapp.ssh.RsaPublicKeyArrayInput;
import uk.ac.ed.epcc.webapp.ssh.SshPublicKeyArrayInput;

/**
 * @author spb
 *
 */
public class MultiKeyComposite extends PublicKeyComposite<PublicKey[]> {

	/**
	 * @param fac
	 */
	public MultiKeyComposite(AppUserFactory fac,String tag) {
		super(fac,tag);
		
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PublicKeyComposite#getInput()
	 */
	protected ParseAbstractInput<String> getInput(){
		SshPublicKeyArrayInput input = new SshPublicKeyArrayInput();
		if( SSH_REQUIRE_RSA_FEATURE.isEnabled(getContext())){
			input = new RsaPublicKeyArrayInput();	
		}
		input.setMinBits(getContext().getIntegerParameter("ssh.min.bits", 2048));
		return input;
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PublicKeyComposite#load(java.lang.String)
	 */
	@Override
	protected PublicKey[] load(String value) throws PublicKeyParseException {
		return SshPublicKeyArrayInput.load(value);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PublicKeyComposite#format(java.lang.Object)
	 */
	@Override
	protected String format(PublicKey[] keys) throws PublicKeyParseException, IOException {
		return SshPublicKeyArrayInput.format(keys);
	}
	@Override
	public String[] getPublicKeys(AppUser person) {
		String key = getPublicKey(person);
		if( key == null || key.isEmpty()) {
			return new String[0];
		}
		try {
			PublicKey keys[] = load(key);
			String result[] = new String[keys.length];
			for(int i =0; i< keys.length ; i++) {
				result[i]=PublicKeyReaderUtil.format(keys[i]);
			}
			return result;
		}catch(Exception e) {
			getLogger().error("Error generating key list",e);
			return new String[0];
		}
	}


}
