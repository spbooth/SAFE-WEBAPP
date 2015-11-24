// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import uk.ac.ed.epcc.webapp.AppContext;

@uk.ac.ed.epcc.webapp.Version("$Id: Hash.java,v 1.6 2014/09/15 14:30:36 spb Exp $")
/** Enum of possible password hash algorithms 
 * 
 * When stored in the database we can use numeric codes to avoid making the algorithm used too explicit if data is exposed.
 * 
 * Note the {@link #toString()} method should return the standard name used to retrieve the
 * 
*/
public enum Hash{
	
	MD5{

		@Override
		public boolean enableByDefault() {
			return true;
		}
	
	},
	SHA1{
		
		public String toString(){
			return "SHA-1";
		}
	},
	SHA256{
		public String toString(){
			return "SHA-256";
		}
		public int size(){
			return 256;
		}
	},
	SHA384{
		public String toString(){
			return "SHA-384";
		}
		public int size(){
			return 384;
		}
	},
	SHA512{
		public String toString(){
			return "SHA-512";
		}
		public int size(){
			return 512;
		}
	};
	public MessageDigest getDigest() throws NoSuchAlgorithmException{
		return MessageDigest.getInstance(toString());
	}
	/** get a hash value encoded as a hexadecimal string (for compatibility with
	 * mysql built in hash functions).
	 * 
	 * @param value
	 * @return hex coded hash string
	 * @throws NoSuchAlgorithmException 
	 */
	public String getHash(String value) throws NoSuchAlgorithmException{
		MessageDigest d = getDigest();
		d.update(value.getBytes());
		return getHex(d.digest());
	}
	public static String getHex(byte bytes[]){
		StringBuilder sb = new StringBuilder();
		for( byte b : bytes){
			int i = b;
			if( i < 0){
				i += 256;
			}
			sb.append(String.format("%02x", i));
		}
		return sb.toString();
	}
	public int size(){
		return 0;
	}
	public boolean enableByDefault(){
		return false;
	}
	public static Hash getDefault(AppContext c){
		return c.getEnumParameter(Hash.class, "password.hash",MD5);
	}
}