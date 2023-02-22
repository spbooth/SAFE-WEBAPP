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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.PublicKeyParseException;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.SSH2DataBuffer;

/** class to verify the options string in a openssh AuthorizedKeys file
 * @author Stephen Booth
 *
 */
public class AuthorizedKeyValidator extends AbstractContexed implements FieldValidator<String>{
   public AuthorizedKeyValidator(AppContext conn) {
		super(conn);
	}

/** option keywords without arguments
    * 
    */
   public static final String options[] = {
		   "agent-forwarding", 
		   "cert-authority",
		   "no-agent-forwarding",
		   "no-port-forwarding",
		   "no-pty",
		   "no-user-rc",
		   "no-X11-forwarding",
		   "port-forwarding",
		   "pty",
		   "no-touch-required",
		   "restrict",
		   "user-rc",
		   "X11-forwarding"
   };
   /** options keywords that take arguments
    * 
    */
   public static final String arg_options[] = {

		   "command",	
		   "environment",
		   "expiry-time",
		   "from",
		   "permit-listen",
		   "permit-open",
		   "principals",
		   "tunnel"
   };	
   
   public static final String algs[] = {

		    "sk-ecdsa-sha2-nistp256@openssh.com",
		    "ecdsa-sha2-nistp256",
		    "ecdsa-sha2-nistp384",
		    "ecdsa-sha2-nistp521",
		    "sk-ssh-ed25519@openssh.com",
		    "ssh-ed25519",
		    "ssh-dss",
		    "ssh-rsa",
   };
   
   private static String group(String inner) {
	   return "(?:"+inner+")";
   }
   public static String getOptionPattern() {
	   StringBuilder sb = new StringBuilder();
	   for(String c : arg_options) {
		   sb.append(c);
		   sb.append("=\"(?:[^\"\\\\]|(:?\\\\\"))+\"");
		   sb.append("|");
	   }
	   sb.append(String.join("|", options));
	   
	   String single = group(sb.toString());
	   
	   String pattern = group(single+",")+"*"+single;
	  
	   return "(?i:"+pattern+")";
	   
   }
   
   private static final Pattern PREFIX_PATTERN=Pattern.compile(getOptionPattern()+"\\s+");
   private static final Pattern ALG_PATTERN=Pattern.compile("("+String.join("|",algs)+")\\s+");
   private static final Pattern BASE64_PATTERN=Pattern.compile("([A-Za-z0-9/\\+]+={0,3})");
   
   private static final Pattern KEY_PATTERN=Pattern.compile("(\\S+)\\s+(\\S+)(.*)");
   
   private boolean allow_options=true;
   @Override
   public void validate(String key) throws ValidateException{
	   
	   if( key == null || key.trim().isEmpty()) {
		   throw new ValidateException("No public key found");
	   }
	   key=key.trim();
	   if( key.contains("PRIVATE KEY")) {
		   throw new ValidateException("This is a PRIVATE key, a PUBLIC key is required");
	   }
	   if( key.contains("BEGIN")) {
		   throw new ValidateException("Please use the openssh public key format");
	   }
	   Matcher prefix_m = PREFIX_PATTERN.matcher(key);
	   if( prefix_m.lookingAt() ) {
		   key = key.substring(prefix_m.end());
		   if( ! allow_options) {
			   throw new ValidateException("Options not permitted");
		   }
	   }
	   Matcher alg_m= ALG_PATTERN.matcher(key);
	   if( ! alg_m.lookingAt()) {
		   // unrecognised algorithm
		   throw new ValidateException("Unrecognised key algorithm "+key);
	   }
	   String algorithm = alg_m.group(1);
	   if( ! validateAlgorith(algorithm)) {
		   throw new ValidateException("Invalid/Forbidden key algorithn "+algorithm);
	   }
	   
	   key = key.substring(alg_m.end());
	   
	   Matcher base64_m = BASE64_PATTERN.matcher(key);
	   if( ! base64_m.lookingAt()) {
		   throw new ValidateException("Missing Base64 encoded key");
	   }
	   String base64 = base64_m.group(1);
	   final SSH2DataBuffer buf = new SSH2DataBuffer(Base64.getDecoder().decode(base64.getBytes()));
	   try {
		   // The certificate should start with the correct algorithm.
		   // don't validate beyond this
		   String alg2 = buf.readString();
		   if(! algorithm.equals(alg2) ) {
			   throw new ValidateException("Key algorithm does not match "+algorithm+"!="+alg2);
		   }
		   // expect a series of data blocks
		   while( buf.remaining() > 0 ) {
			   buf.readByteArray();
		   }
	   } catch (PublicKeyParseException e) {
		   throw new ValidateException("Corrupt key");
	   }
	   buf.reset();
	   validateBlock(buf);
   }
   /** convert a key to normalised form 
    * (without options or comment)
    * If the key fails to parse null is returned.
    * 
    * 
    * @param key
    * @return normalised key or null
    */
   public String normalise(String key) throws ParseException{
	   if( key == null || key.trim().isEmpty()) {
		   return null;
	   }
	   key=key.trim();
	   Matcher prefix_m = PREFIX_PATTERN.matcher(key);
	   if( prefix_m.lookingAt() ) {
		   key = key.substring(prefix_m.end());
	   }
	   Matcher alg_m= ALG_PATTERN.matcher(key);
	   if( ! alg_m.lookingAt()) {
		   // unrecognised algorithm
		   throw new ParseException("Unrecognised key algorithm");
	   }
	   String algorithm = alg_m.group(1);
	   key = key.substring(alg_m.end());
	   
	   Matcher base64_m = BASE64_PATTERN.matcher(key);
	   if( ! base64_m.lookingAt()) {
		   throw new ParseException("Missing Base64 encoded key");
	   }
	   String base64 = base64_m.group(1);
	   base64 = Base64.getEncoder().encodeToString(Base64.getDecoder().decode(base64));
	   return algorithm+" "+base64;
   }
   /** Get just the Base64 encoded key-block without options or algorithm.
    * 
    * @param key
    * @return
    * @throws ParseException
    */
   public String getKeyBlock(String key) throws ParseException{
	   if( key == null || key.trim().isEmpty()) {
		   return null;
	   }
	   key=key.trim();
	   Matcher prefix_m = PREFIX_PATTERN.matcher(key);
	   if( prefix_m.lookingAt() ) {
		   key = key.substring(prefix_m.end());
	   }
	   Matcher alg_m= ALG_PATTERN.matcher(key);
	   if( ! alg_m.lookingAt()) {
		   // unrecognised algorithm
		   throw new ParseException("Unrecognised key algorithm");
	   }
	   String algorithm = alg_m.group(1);
	   key = key.substring(alg_m.end());
	   
	   Matcher base64_m = BASE64_PATTERN.matcher(key);
	   if( ! base64_m.lookingAt()) {
		   throw new ParseException("Missing Base64 encoded key");
	   }
	   String base64 = base64_m.group(1);
	   base64 = Base64.getEncoder().encodeToString(Base64.getDecoder().decode(base64));
	   return base64;
   }
   public String fingerprint(String key) throws ParseException, NoSuchAlgorithmException {
	   if( key == null || key.trim().isEmpty()) {
		   return null;
	   }
	   key=key.trim();
	   Matcher prefix_m = PREFIX_PATTERN.matcher(key);
	   if( prefix_m.lookingAt() ) {
		   key = key.substring(prefix_m.end());
	   }
	   Matcher alg_m= ALG_PATTERN.matcher(key);
	   if( ! alg_m.lookingAt()) {
		   // unrecognised algorithm
		   throw new ParseException("Unrecognised key algorithm");
	   }
	   String algorithm = alg_m.group(1);
	   key = key.substring(alg_m.end());
	   
	   Matcher base64_m = BASE64_PATTERN.matcher(key);
	   if( ! base64_m.lookingAt()) {
		   throw new ParseException("Missing Base64 encoded key");
	   }
	   String base64 = base64_m.group(1);
	   
	   MessageDigest messageDigest = MessageDigest.getInstance("MD5");
       
       byte[] digest = messageDigest.digest(Base64.getDecoder().decode(base64.getBytes()));
       final StringBuilder toRet = new StringBuilder();
       toRet.append("MD5");
       for (int i = 0; i < digest.length; i++) {
           toRet.append(":");
           int b = digest[i] & 0xff;
           String hex = Integer.toHexString(b);
           if (hex.length() == 1) toRet.append("0");
           toRet.append(hex);
       }
       return toRet.toString();
	   
   }
   public String fingerprint2(String key) throws ParseException, NoSuchAlgorithmException {
	   if( key == null || key.trim().isEmpty()) {
		   return null;
	   }
	   key=key.trim();
	   Matcher prefix_m = PREFIX_PATTERN.matcher(key);
	   if( prefix_m.lookingAt() ) {
		   key = key.substring(prefix_m.end());
	   }
	   Matcher alg_m= ALG_PATTERN.matcher(key);
	   if( ! alg_m.lookingAt()) {
		   // unrecognised algorithm
		   throw new ParseException("Unrecognised key algorithm");
	   }
	   String algorithm = alg_m.group(1);
	   key = key.substring(alg_m.end());
	   
	   Matcher base64_m = BASE64_PATTERN.matcher(key);
	   if( ! base64_m.lookingAt()) {
		   throw new ParseException("Missing Base64 encoded key");
	   }
	   String base64 = base64_m.group(1);
	   
	   MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
       
       byte[] digest = messageDigest.digest(Base64.getDecoder().decode(base64));
       final StringBuilder toRet = new StringBuilder();
       toRet.append("SHA256:");
       String b = Base64.getEncoder().encodeToString(digest);
       while( b.endsWith("=")) {
    	   b = b.substring(0, b.length()-1);
       }
       toRet.append(b);
       return toRet.toString();
	   
   }
   /** return the trailing comment from a key
    * If the key fails to parse null is returned.
    * @param key
    * @return comment or null;
    */
   public String getComment(String key) {
	   if( key == null || key.isEmpty()) {
		   return null;
	   }
	   key=key.trim();
	   Matcher prefix_m = PREFIX_PATTERN.matcher(key);
	   if( prefix_m.lookingAt() ) {
		   key = key.substring(prefix_m.end());
	   }
	   Matcher alg_m= ALG_PATTERN.matcher(key);
	   if( ! alg_m.lookingAt()) {
		   // unrecognised algorithm
		   return null;
	   }
	   String algorithm = alg_m.group(1);
	   key = key.substring(alg_m.end());
	   
	   Matcher base64_m = BASE64_PATTERN.matcher(key);
	   if( ! base64_m.lookingAt()) {
		   return null;
	   }
	   return key.substring(base64_m.end());
   }
   
   /** Extension point validator.
    * This can add additional restrictions on the algorithm or the key itself
    * 
    */
   protected void validateBlock(SSH2DataBuffer buf ) throws ValidateException{
	   try {
		   String alg = buf.readString();
		   if( alg.equals("ssh-rsa")) {
			   // RSA key specific validation
			   PublicKey key= PublicKeyReaderUtil.decodePublicKey(buf);

			   if( ! key.getAlgorithm().equalsIgnoreCase("RSA")){
				   throw new ValidateException("Not an RSA key");
			   }
			   int minBits = getContext().getIntegerParameter("ssh.min.bits", 2048);
			   int bits = ((RSAKey)key).getModulus().bitLength()+1;
			   if( bits < minBits){
				   throw new ValidateException("Key bit-length too short minimum="+minBits+" This key="+bits);
			   }
		   }
		} catch (PublicKeyParseException e) {
			throw new ValidateException("Corrupt public key");
		}
   }
   protected boolean validateAlgorith(String alg) {
	   return getContext().getBooleanParameter("allow_ssh_key."+alg, true);
   }
/**
 * @return the allow_options
 */
private boolean getAllowOptions() {
	return allow_options;
}
/**
 * @param allow_options the allow_options to set
 */
private void setAllowOptions(boolean allow_options) {
	this.allow_options = allow_options;
}
}
