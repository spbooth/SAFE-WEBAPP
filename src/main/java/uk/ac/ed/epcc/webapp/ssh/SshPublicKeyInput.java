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

import java.security.PublicKey;
import java.security.interfaces.RSAKey;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.PublicKeyParseException;
import uk.ac.ed.epcc.webapp.validation.SingleLineFieldValidator;
import uk.ac.ed.epcc.webapp.validation.MaxLengthValidator;


/** Input for SSH public keys.
 * 
 * @author spb
 *
 */
public class SshPublicKeyInput extends ParseAbstractInput<String> implements ItemInput<String,PublicKey> {

	public SshPublicKeyInput(){
		super();
		setBoxWidth(48);
		setSingle(true);
		addValidator(new MaxLengthValidator(4096));
		addValidator(new SingleLineFieldValidator() {
			
			@Override
			public void validate(String value) throws FieldException {
				PublicKey key;
				if( value != null && value.trim().length() > 0){
					try{
						key = PublicKeyReaderUtil.load(value);
					}catch(Exception e){
						throw new ValidateException("Bad PublicKey:"+e.getMessage(), e);
					}
					validateKey(key);
				}
				
			}
		});
	}
	int minBits=2048;
	public void setMinBits(int bits){
		minBits=bits;
	}

	public PublicKey getItembyValue(String value) {
		try {
			return PublicKeyReaderUtil.load(value.trim());
		} catch (PublicKeyParseException e) {
			return null;
		}
	}

	public void setItem(PublicKey item) {
		try {
			setValue(PublicKeyReaderUtil.format(item));
		} catch (Exception e) {
			throw new ConsistencyError("setItem not supported",e);
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
			if( v == null || v.isEmpty()) {
				return null;
			}
			PublicKey key = PublicKeyReaderUtil.load(v);
			return PublicKeyReaderUtil.format(key);
		}catch(Exception e){
			throw new ParseException("Invalid public key", e);
		}
		
	}

	@Override
	public String getString(String val) {
		if( val == null){
			return null;
		}
		try{
			// Try to normalise form
			return PublicKeyReaderUtil.format(PublicKeyReaderUtil.load(val));
		}catch(Exception e){
			return val;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.AbstractInput#convert(java.lang.Object)
	 */
	@Override
	public String convert(Object v) throws TypeException {
		if( v == null ) {
			return null;
		}
		try {
			// Try to force into canonical form
			if( v instanceof PublicKey) {
				return PublicKeyReaderUtil.format((PublicKey)v);
			}
//			if( v instanceof String) {
//				return getString((String)v);
//			}
		}catch(Exception e) {
			throw new TypeException("Error formatting PublicKey", e);
		}
		return super.convert(v);
	}

	@Override
	public boolean isEmpty() {
		String value = getValue();
		return value == null || value.trim().isEmpty();
	}
}