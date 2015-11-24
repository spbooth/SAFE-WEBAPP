// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.ssh;

import java.security.PublicKey;
import java.security.interfaces.RSAKey;

import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: RsaPublicKeyInput.java,v 1.6 2014/09/15 14:30:36 spb Exp $")
public class RsaPublicKeyInput extends SshPublicKeyInput {

	
	/**
	 * 
	 */
	public RsaPublicKeyInput() {
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
			return PublicKeyReaderUtil.format(PublicKeyReaderUtil.load(val));
		}catch(Exception e){
			return val;
		}
	}

}
