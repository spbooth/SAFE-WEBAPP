package uk.ac.ed.epcc.webapp.ssh;

import java.io.IOException;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.PublicKeyParseException;


/** Input for SSH public keys.
 * 
 * @author spb
 *
 */
public class SshPublicKeyInput extends ParseAbstractInput<String> implements ItemInput<PublicKey> {

	public SshPublicKeyInput(){
		super();
		setBoxWidth(48);
		setSingle(true);
		setMaxResultLength(4096);
	}
	int minBits=2048;
	public void setMinBits(int bits){
		minBits=bits;
	}

	public PublicKey getItem() {
		try {
			return PublicKeyReaderUtil.load(getValue().trim());
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

	@Override
	public void validate() throws FieldException {
		super.validate();
		String value = getValue();
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
	public void parse(String v) throws ParseException {
		if( isOptional() && (v == null || v.trim().length() == 0) ){
			setValue(null);
			return;
		}
		try{
			PublicKey key = PublicKeyReaderUtil.load(v);
			setValue(PublicKeyReaderUtil.format(key));
		}catch(Exception e){
			throw new ParseException("Invalid public key", e);
		}
		
	}
}
