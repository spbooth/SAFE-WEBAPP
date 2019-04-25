//| Copyright - The University of Edinburgh 2013                            |
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

import java.security.PublicKey;
import java.security.interfaces.RSAKey;

import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/**
 * @author spb
 *
 */

public class RsaPublicKeyArrayInput extends SshPublicKeyArrayInput {

	
	/**
	 * 
	 */
	public RsaPublicKeyArrayInput() {
		super();
	}
	

	@Override
	protected void validateKey(PublicKey key) throws ValidateException {
		if( ! key.getAlgorithm().equalsIgnoreCase("RSA")){
			throw new ValidateException("Not an RSA key");
		}
		int bits = ((RSAKey)key).getModulus().bitLength()+1;
		if( bits < minBits){
			throw new ValidateException("Key bit-length too short minimum="+minBits+" This key="+bits);
		}
	}


	@Override
	public String getString(String val) {
		if( val == null){
			return null;
		}
		try{
			// Try to normalise form
			return format(load(val));
		}catch(Exception e){
			return val;
		}
	}

}