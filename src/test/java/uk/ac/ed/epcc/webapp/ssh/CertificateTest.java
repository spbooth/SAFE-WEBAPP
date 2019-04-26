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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import javax.crypto.Cipher;


import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayStreamData;
import uk.ac.ed.epcc.webapp.servlet.MockRandomService;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.PublicKeyParseException;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.SSH2ByteBuffer;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.SSH2DataBuffer;

/**
 * @author Stephen Booth
 *
 */
public class CertificateTest extends WebappTestBase {
	public static final String cert = "ssh-rsa-cert-v01@openssh.com AAAAHHNzaC1yc2EtY2VydC12MDFAb3BlbnNzaC5jb20AAAAgk2YibTp5CaoCezyp1NaJsAciDDcHs6Ka/ThXHutJI7YAAAADAQABAAABAQDCzQ2GE1xzVDHOgSzeRxM8cUX50CZTTiMqOyiUo5jlFJq1Ijt9jujS3oVsavPtVXGw/PV8+DF+J5OC6OpxHN0O1dOyO6JueC2LhEceiyZ6iajDGn+NzlkJZ+R6+s4E13B8oDBnmRtu9t33ncmTGH2RVlN4JyI12ycbsPCQcjd1x2+koROVnbpn91aYaEmZwdPaGGkptKQkRjyQ5TdACMLOAobldM6vtUl0/LP6pguetZSmNH36kgRU3yUKjEPh8Gbst5d8jv/FaZb7g5QZwXjkmdj52JG2icuV0Jot9+qBQb6iX96gnUrHue4pwjAAmy0zzNYWAT+qBfrnxeZ017wpAAAAAAAAAAAAAAABAAAABHRlc3QAAAAKAAAABnNib290aAAAAAAAAAAA//////////8AAAAiAAAADWZvcmNlLWNvbW1hbmQAAAANAAAACS9iaW4vZGF0ZQAAAIIAAAAVcGVybWl0LVgxMS1mb3J3YXJkaW5nAAAAAAAAABdwZXJtaXQtYWdlbnQtZm9yd2FyZGluZwAAAAAAAAAWcGVybWl0LXBvcnQtZm9yd2FyZGluZwAAAAAAAAAKcGVybWl0LXB0eQAAAAAAAAAOcGVybWl0LXVzZXItcmMAAAAAAAAAAAAAARcAAAAHc3NoLXJzYQAAAAMBAAEAAAEBAPbNgjBJFEqj7ADq+/39vQGK4XB91jYiws42KkKb7L4pGQKHNKn+rcmRTsVg0FtDyIDvoyAQotAcsS/lJCZf61JuQFQZzYcmu8mcXqtNpGeDvTr+kQ3M6Gawfv9MlOBe5VmhEozu2IrvuVPegB3AyMAGy+h+Ap8Uezd0BWUuRG+hgWwqEoyGf3zPz8o3b3jNoWlyP3rg7DJaP1uqK8tVksbDN8QYfckNqGGNudAF8+utm2JYji4nBQBUVh5GB6gFiDOXKa8fhnMH6VPSJlLu9APk2zuN+KU7o+7k0Bwh9snW+ZxcsjbOEdZED+itRBcJCa9FV1i5m4vA+BFtYlmemkMAAAEPAAAAB3NzaC1yc2EAAAEA47q9W7frQfyBWNoeFfQlMWuU8JD+rZMjXTUqbaEQeXO7VZjGJxAq48I64JNkFO7klCzqnr1dp1ozfp6cu+XRMGpj2w4VBU9yAGUXFpmB6KKesBxouD76jzzwzOMFAkElo2c0OhCQFIMwWMiiaNZLIznFqoqk1/rJTsbDV/CbZCRkxWBzqbfrY6GoJDAx3MK+0bWOWWfMvsTFP2Z5WkyNRXv4ZwQN2qc+2CP+CtxrQg++WB70kCgMg5O86jCeVRPeFh2z2ZNkC5oCRIHGMVUcywaHQZrN3iMyz1UV41ofiKAPSO2vKoaV9qzZqRLcypuI5+feq/5M3/rb44njceA5UQ== sbooth@safe-dev.epcc.ed.ac.uk";

	public static final String ca_pub = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQD2zYIwSRRKo+wA6vv9/b0BiuFwfdY2IsLONipCm+y+KRkChzSp/q3JkU7FYNBbQ8iA76MgEKLQHLEv5SQmX+tSbkBUGc2HJrvJnF6rTaRng706/pENzOhmsH7/TJTgXuVZoRKM7tiK77lT3oAdwMjABsvofgKfFHs3dAVlLkRvoYFsKhKMhn98z8/KN294zaFpcj964OwyWj9bqivLVZLGwzfEGH3JDahhjbnQBfPrrZtiWI4uJwUAVFYeRgeoBYgzlymvH4ZzB+lT0iZS7vQD5Ns7jfilO6Pu5NAcIfbJ1vmcXLI2zhHWRA/orUQXCQmvRVdYuZuLwPgRbWJZnppD sbooth@safe-dev.epcc.ed.ac.uk";
	
	public static final String orig_pub = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDCzQ2GE1xzVDHOgSzeRxM8cUX50CZTTiMqOyiUo5jlFJq1Ijt9jujS3oVsavPtVXGw/PV8+DF+J5OC6OpxHN0O1dOyO6JueC2LhEceiyZ6iajDGn+NzlkJZ+R6+s4E13B8oDBnmRtu9t33ncmTGH2RVlN4JyI12ycbsPCQcjd1x2+koROVnbpn91aYaEmZwdPaGGkptKQkRjyQ5TdACMLOAobldM6vtUl0/LP6pguetZSmNH36kgRU3yUKjEPh8Gbst5d8jv/FaZb7g5QZwXjkmdj52JG2icuV0Jot9+qBQb6iX96gnUrHue4pwjAAmy0zzNYWAT+qBfrnxeZ017wp sbooth@safe-dev.epcc.ed.ac.uk"; 
	public static final byte expected_nonce[] = {-109, 102, 34, 109, 58, 121, 9, -86, 2, 123, 60, -87, -44, -42, -119, -80, 7, 34, 12, 55, 7, -77, -94, -102, -3, 56, 87, 30, -21, 73, 35, -74};
	//	[sbooth@safe-dev play]$ ssh-keygen -L -f looky-cert.pub
//	looky-cert.pub:
//	        Type: ssh-rsa-cert-v01@openssh.com user certificate
//	        Public key: RSA-CERT SHA256:j5Q5FqMD7u0w1MBOibYpQS4N1mdlM3333LmmzL7q2gw
//	        Signing CA: RSA SHA256:pQnkBhfOneE6/jfexglVDmcUwZiAzrDOXKSqmCu/t6Y
//	        Key ID: "test"
//	        Serial: 0
//	        Valid: forever
//	        Principals:
//	                sbooth
//	        Critical Options:
//	                force-command /bin/date
//	        Extensions:
//	                permit-X11-forwarding
//	                permit-agent-forwarding
//	                permit-port-forwarding
//	                permit-pty
//	                permit-user-rc

	
	@Test
	public void testDecode() throws PublicKeyParseException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
		decodeCert(cert);
	}


	/** decode the expected certificate
	 * @throws PublicKeyParseException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public void decodeCert(String input) throws PublicKeyParseException, NoSuchAlgorithmException, InvalidKeySpecException,
			InvalidKeyException, SignatureException {
		String parts[] = input.split("\\s+");
		
		String base64 = parts[1];
		byte[] data = Base64.decodeBase64(base64.getBytes());
		SSH2DataBuffer buf = new SSH2DataBuffer(data);
		
		String type=buf.readString();
		assertEquals("ssh-rsa-cert-v01@openssh.com", type);
		byte nonce[] = buf.readByteArray();
		System.out.println(nonce.length);
		printBytes(nonce);
	
		assertTrue(Arrays.equals(expected_nonce,nonce));
		BigInteger e = buf.readMPint();
		System.out.println("e="+e);
		BigInteger n = buf.readMPint();
		System.out.println("n="+n);
		long serial = buf.readUInt64();
		assertEquals(0L, serial);
		int itype = buf.readUInt32();
		assertEquals(1,itype);
		String id = buf.readString();
		assertEquals("test",id);
		printPackedList("Principal",buf);
		//assertEquals("sbooth",principals);
		long after = buf.readUInt64();
		assertEquals(0L, after);
		Date start=new Date(after*1000L);
		System.out.println(start);
		long before = buf.readUInt64();
		assertEquals(-1L,before);
		Date end = new Date(before*1000L);
		System.out.println(end);
		printPackedTuples("Critical",buf);
		printPackedTuples("Extension",buf);
		printPackedTuples("Reserved",buf);
		byte sig_key[] = buf.readByteArray();
		int pos = buf.pos();
		System.out.println("pos="+pos);
		byte signature[] = buf.readByteArray();
		assertEquals(0, buf.remaining());
		String ca_base64 = Base64.encodeBase64String(sig_key);
		SSH2DataBuffer key_buf = new SSH2DataBuffer(sig_key);
		String ca_key_type= key_buf.readString();
		assertEquals("ssh-rsa",ca_key_type);
		final BigInteger ca_e = key_buf.readMPint();
		final BigInteger ca_n = key_buf.readMPint();
       
        
        System.out.println("ca_e="+ca_e.toString());
        System.out.println("ca_n="+ca_n.toString());
        assertEquals(0, key_buf.remaining());
        final KeyFactory rsaKeyFact = KeyFactory.getInstance("RSA");
        final RSAPublicKeySpec rsaPubSpec = new RSAPublicKeySpec(ca_n, ca_e);

        PublicKey ca_p_k =  rsaKeyFact.generatePublic(rsaPubSpec);
		
		System.out.println(ca_base64);
		String ca_pub_parts[] = ca_pub.split("\\s+");
		assertEquals(ca_pub_parts[1], ca_base64);
		
		System.out.println("Signature="+Base64.encodeBase64String(signature));
		SSH2DataBuffer sig_buf = new SSH2DataBuffer(signature);
		String sig_type = sig_buf.readString();
		System.out.println(sig_type);
		assertEquals("ssh-rsa", sig_type);
		byte sig_blob[] = sig_buf.readByteArray();
		assertEquals(0,sig_buf.remaining());
		int bits = ((RSAKey)ca_p_k).getModulus().bitLength();
		System.out.println("bits="+bits);
//		if( bits > sig_blob.length) {
//			byte tmp[] = sig_blob;
//			sig_blob = new byte[bits];
//			System.arraycopy(tmp, 0, sig_blob, bits-sig_blob.length, tmp.length);
//		}
		
		
		Signature s = Signature.getInstance("SHA1withRSA");
		s.initVerify(ca_p_k);
		s.update(data, 0, pos);
		//s.update(nonce.getBytes());
		
		
		boolean verify = s.verify(sig_blob);
		System.out.println(s.toString());
		assertTrue("verify",verify);
	}


	/**
	 * @param buf
	 * @throws PublicKeyParseException
	 */
	public void printPackedList(String type,SSH2DataBuffer buf) throws PublicKeyParseException {
		SSH2DataBuffer principals= new SSH2DataBuffer(buf.readByteArray());
		while(principals.remaining() > 0) {
			String name = principals.readString();
			System.out.println(type+": ["+name+"]");
		}
	}
	
	public void printBytes(byte data[]) {
		System.out.print("{");
		for(int i=0; i< data.length; i++) {
			System.out.print(Integer.toString((int)data[i]));
			if( i < data.length-1) {
				System.out.print(", ");
			}
		}
		System.out.println("}");
	}
	public void printPackedTuples(String type,SSH2DataBuffer buf) throws PublicKeyParseException {
		byte[] array = buf.readByteArray();
		SSH2DataBuffer tuples= new SSH2DataBuffer(array);
		while(tuples.remaining() > 0) {
			String name = tuples.readString();
			byte val[] =tuples.readByteArray();
			String value = "";
			if( val.length >0) {
				SSH2DataBuffer vb=new SSH2DataBuffer(val);
				value=vb.readString();
				assertEquals(0,vb.remaining());
			}
			System.out.println(type+": ["+name+"]=["+value+"]");
		}
	}
	
	
	
	
	@Test
	public void testSignVerify() throws DataFault, IOException, NoSuchAlgorithmException, InvalidKeySpecException, PublicKeyParseException, SignatureException, InvalidKeyException {
		KeyPairGenerator kpgen = KeyPairGenerator.getInstance("RSA");
		kpgen.initialize(2048);
		KeyPair pair = kpgen.genKeyPair();
		PublicKey pub = pair.getPublic();
		PrivateKey priv = pair.getPrivate();
		
		byte data[] = "Now is the winter of our discontent".getBytes();
		
		Signature sig = Signature.getInstance("SHA1withRSA");
		sig.initSign(priv);
		sig.update(data);
		byte blob[] = sig.sign();
		
		sig.initVerify(pub);
		sig.update(data);
		boolean verif = sig.verify(blob);
		
		
		assertTrue(verif);
		String ca_parts[] = ca_pub.split("\\s+");
		
		String base64String = ca_parts[1];
		System.out.println(base64String);
		byte[] decode_data = Base64.decodeBase64(base64String);
		SSH2DataBuffer key_buf = new SSH2DataBuffer(decode_data);
		String type = key_buf.readString();
		System.out.println(type);
		final BigInteger ca_e = key_buf.readMPint();
		System.out.println("ca_e="+ca_e);
		
        final BigInteger ca_n = key_buf.readMPint();
        System.out.println("ca_n="+ca_n);
        System.out.println(key_buf.remaining());
        
        final KeyFactory rsaKeyFact = KeyFactory.getInstance("RSA");
        final RSAPublicKeySpec rsaPubSpec = new RSAPublicKeySpec(ca_n, ca_e);

        PublicKey ca_p_k =  rsaKeyFact.generatePublic(rsaPubSpec);
        sig.initVerify(ca_p_k);
        sig.update(data);
        verif = sig.verify(blob);
		
		
		assertFalse(verif);
		
	}
	
	@Test
	public void testKeySigner() throws Exception {
		// Duplicating the openssh made key in java
		// need to load correct ca key and finagle the nonce.
		 KeyFactory rsaKeyFact = KeyFactory.getInstance("RSA");
		 PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(getResourceAsBytes("ca.pk8"));
		 PrivateKey ca_private = rsaKeyFact.generatePrivate(spec);
		 PublicKey ca_public = PublicKeyReaderUtil.load(ca_pub);
		 KeyPair pair = new KeyPair(ca_public, ca_private);
		
		MockRandomService mock_random = new MockRandomService();
		mock_random.setByteData(expected_nonce);
		ctx.setService(mock_random);
		
		
		
		KeySigner signer = new KeySigner(getContext());
		signer.addPrincipal("sbooth");
		SSH2ByteBuffer val=new SSH2ByteBuffer();
		val.writeString("/bin/date");
		signer.addCritical("force-command", val);
		PublicKey orig = PublicKeyReaderUtil.load(orig_pub);
		String result = signer.signKey(0L, "test", pair, orig);
		result = result+" sbooth@safe-dev.epcc.ed.ac.uk";
		decodeCert(result);
		assertEquals(cert,result);
	}
	
	
}
