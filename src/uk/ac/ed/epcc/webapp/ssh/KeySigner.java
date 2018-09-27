//| Copyright - The University of Edinburgh 2018                            |
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
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.tomcat.util.codec.binary.Base64;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.session.RandomService;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.SSH2ByteBuffer;

/**
 * @author Stephen Booth
 *
 */
public class KeySigner extends AbstractContexed {

	private static final String[] defs = {
			"permit-X11-forwarding", 
			"permit-agent-forwarding",
			"permit-port-forwarding",
			"permit-pty",
			"permit-user-rc",
	};
	/**
	 * @param conn
	 */
	public KeySigner(AppContext conn) {
		super(conn);
		for( String d : defs) {
			addExtension(d, null);
		}
	}
	private TreeMap<String,SSH2ByteBuffer> critical=new TreeMap<>();
	private TreeMap<String,SSH2ByteBuffer> extensions=new TreeMap<>();
	private TreeSet<String> principals = new TreeSet<>();
	private Date valid_after=null;
	private Date valid_before=null;
	private void writeMap(SSH2ByteBuffer output,TreeMap<String,SSH2ByteBuffer> data) throws IOException {
		SSH2ByteBuffer map = new SSH2ByteBuffer();
		if( data != null) {
			for(Entry<String, SSH2ByteBuffer> e : data.entrySet()) {
				map.writeString(e.getKey());
				map.writeBuffer(e.getValue());
			}
		}
		output.writeBuffer(map);
	}
	private void writeSet(SSH2ByteBuffer output,Set<String> data) throws IOException {
		SSH2ByteBuffer set = new SSH2ByteBuffer();
		if( data != null) {
			for(String s: data) {
				set.writeString(s);
			}
		}
		output.writeBuffer(set);
	}
	
	private void writeNonce(SSH2ByteBuffer output) throws IOException {
		RandomService rnd = getContext().getService(RandomService.class);
		output.writeByteArray(rnd.randomBytes(32));
	}
	
	public String signKey(long serial, String key_id,KeyPair ca, PublicKey key) throws Exception{
		String type;
		
		SSH2ByteBuffer data = new SSH2ByteBuffer();
		String key_alg = key.getAlgorithm();
		if( key_alg.equals("RSA")) {
			type="ssh-rsa-cert-v01@openssh.com";
			data.writeString(type);
			writeNonce(data);
			RSAPublicKey rsa = (RSAPublicKey) key;
			data.writeMPint(rsa.getPublicExponent());
			data.writeMPint(rsa.getModulus());
		}else {
			throw new InvalidAlgorithmParameterException("Unsupported algorith "+key_alg);
		}
		data.writeUint64(serial);
		data.writeUint32(1); // user key
		data.writeString(key_id);
		writeSet(data, principals);
		if( valid_after == null) {
			data.writeUint64(0L);
		}else {
			data.writeUint64(valid_after.getTime()/1000L);
		}
		if( valid_before == null) {
			data.writeUint64(-1L);
		}else {
			data.writeUint64(valid_before.getTime()/1000L);
		}
		writeMap(data, critical);
		writeMap(data, extensions);
		data.writeByteArray(new byte[0]); // reserved
		PublicKey ca_public = ca.getPublic();
		String ca_alg = ca_public.getAlgorithm();
		if( ca_alg.equals("RSA")) {
			RSAPublicKey ca_pub_rsa=(RSAPublicKey) ca_public;
			SSH2ByteBuffer ca_buff = new SSH2ByteBuffer();
			ca_buff.writeString("ssh-rsa");
			ca_buff.writeMPint(ca_pub_rsa.getPublicExponent());
			ca_buff.writeMPint(ca_pub_rsa.getModulus());
			data.writeBuffer(ca_buff);
			
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initSign(ca.getPrivate());
			sig.update(data.toByteArray());
			SSH2ByteBuffer sig_part = new SSH2ByteBuffer();
			sig_part.writeString("ssh-rsa");
			sig_part.writeByteArray(sig.sign());
			data.writeBuffer(sig_part);
		}else {
			throw new InvalidAlgorithmParameterException("Unsupported algorith "+ca_alg);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(" ");
		sb.append(Base64.encodeBase64String(data.toByteArray()));
		return sb.toString();
	}
	public Date getValid_after() {
		return valid_after;
	}
	public void setValid_after(Date valid_after) {
		this.valid_after = valid_after;
	}
	public Date getValid_before() {
		return valid_before;
	}
	public void setValid_before(Date valid_before) {
		this.valid_before = valid_before;
	}
	public void addPrincipal(String name) {
		principals.add(name);
	}
	public void removePrincipal(String name) {
		principals.remove(name);
	}
	public void addExtension(String name, SSH2ByteBuffer value) {
		if( value == null) {
			extensions.put(name, new SSH2ByteBuffer());
		}else {
			extensions.put(name, value);
		}
	}
	public SSH2ByteBuffer getExtension(String name) {
		return extensions.get(name);
	}
	public void removeExtension(String name) {
		extensions.remove(name);
	}
	public void addCritical(String name, SSH2ByteBuffer value) {
		if( value == null) {
			critical.put(name, new SSH2ByteBuffer());
		}else {
			critical.put(name, value);
		}
	}
	public SSH2ByteBuffer getCritical(String name) {
		return critical.get(name);
	}
	public void removeCritical(String name) {
		critical.remove(name);
	}
}
