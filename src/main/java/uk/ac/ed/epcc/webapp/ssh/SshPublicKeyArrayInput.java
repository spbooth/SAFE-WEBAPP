//| Copyright - The University of Edinburgh 2015                            |
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

import java.io.IOException;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.PublicKeyParseException;


/** Input for array of SSH public keys.
 * 
 * @author spb
 *
 */
public class SshPublicKeyArrayInput extends ParseAbstractInput<String> implements ItemInput<String,PublicKey[]> {

	

	public SshPublicKeyArrayInput(){
		super();
		setBoxWidth(48);
		setSingle(true);
		setMaxResultLength(4096);
		addValidator(new FieldValidator<String>() {
			
			@Override
			public void validate(String value) throws FieldException {
				PublicKey[] keys;
				if( value != null && value.trim().length() > 0){
					try{
						keys = load(value);
					}catch(Exception e){
						throw new ValidateException("Bad PublicKey:"+e.getMessage(), e);
					}
					validateKeys(keys);
				}
				
			}
		});
	}
	int minBits=2048;
	public void setMinBits(int bits){
		minBits=bits;
	}

	public PublicKey[] getItembyValue(String value) {
		try {
			return load(value.trim());
		} catch (PublicKeyParseException e) {
			return null;
		}
	}

	public void setItem(PublicKey item[]) {
		try {
			setValue(format(item));
		} catch (Exception e) {
			throw new ConsistencyError("setItem not supported",e);
		}
		
		
	}

	
	protected void validateKeys(PublicKey keys[])throws ValidateException{
		for( PublicKey key : keys) {
			validateKey(key);
		}
	}
	protected void validateKey(PublicKey key)throws ValidateException{
		if( key.getAlgorithm().equalsIgnoreCase("RSA")){

			int bits = ((RSAKey)key).getModulus().bitLength()+1;
			if( bits < minBits){
				throw new ValidateException("Key bit-length too short minimum="+minBits+" This key="+bits);
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseInput#parse(java.lang.String)
	 */
	public String parseValue(String v) throws ParseException {
		try{
			PublicKey[] keys = load(v);
			return format(keys);
		}catch(Exception e){
			throw new ParseException("Invalid public key", e);
		}
		
	}
	@Override
	public boolean isEmpty() {
		String v = getValue();
		return v == null || v.trim().isEmpty();
	}
	public static PublicKey[] load(String value) throws PublicKeyParseException {
		if( value == null || value.isEmpty()) {
			return null;
		}
		Set<PublicKey> set = new LinkedHashSet<>();
		for(String v : value.split("\\s*,\\s*")) {
			set.add(PublicKeyReaderUtil.load(v.trim()));
		}
		return (PublicKey[]) set.toArray(new PublicKey[set.size()]);
	}
	
	public static String format(PublicKey keys[]) throws PublicKeyParseException, IOException {
		if( keys == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean seen=false;
		for(PublicKey key : keys) {
			if( seen) {
				sb.append(",\n");
			}
			sb.append(PublicKeyReaderUtil.format(key));
			seen=true;
		}
		return sb.toString();
	}
}