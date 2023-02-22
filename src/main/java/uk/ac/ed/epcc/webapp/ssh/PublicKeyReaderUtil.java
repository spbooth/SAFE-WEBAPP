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

/*
 * Copyright 2009 The jSVNServe Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Id:        $Rev$
 * Last Changed:    $Date: 2016/01/26 18:57:04 $
 * Last Changed By: $Author: spb $
 * 
 * Imported into WEBAPP code base. Fixed for dss keys.
 * 
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.PublicKeyParseException;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.PublicKeyParseException.ErrorCode;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.SSH2DataBuffer;

/**
 * The class is a utility class to read OpenSSH or SECSH encoded public key
 * texts.
 *
 * @author jSVNServe Team
 * @version $Id: PublicKeyReaderUtil.java,v 1.10 2016/01/26 18:57:04 spb Exp $
 */
public final class PublicKeyReaderUtil
{
    /**
     * Begin marker for the SECSH public key file format.
     *
     * @see #extractSecSHBase64(String)
     */
    private static final String BEGIN_PUB_KEY = "---- BEGIN SSH2 PUBLIC KEY ----";

    /**
     * End marker for the SECSH public key file format.
     *
     * @see #extractSecSHBase64(String)
     */
    private static final String END_PUB_KEY   = "---- END SSH2 PUBLIC KEY ----";

    /**
     * Key name of the type of public key for DSA algorithm.
     *
     * @see #load(String)
     */
    public static final String SSH2_DSA_KEY = "ssh-dss";

    /**
     * Key name of the type of public key for RSA algorithm.
     *
     * @see #load(String)
     */
    public static final String SSH2_RSA_KEY = "ssh-rsa";

    /**
     * Default constructor is private so that the public key read utility class
     * could not be instantiated.
     */
    private PublicKeyReaderUtil()
    {
    }

    /**
     * Decodes given public <code>_key</code> text and returns the related
     * public key instance.
     *
     * @param _key      text key of the encoded public key
     * @return decoded public key
     * @throws PublicKeyParseException if the public key could not be parsed
     *                                 from <code>_key</code>
     * @see PublicKeyParseException.ErrorCode#UNKNOWN_PUBLIC_KEY_FILE_FORMAT
     * @see PublicKeyParseException.ErrorCode#UNKNOWN_PUBLIC_KEY_CERTIFICATE_FORMAT
     */
    public static PublicKey load(String _key)
        throws PublicKeyParseException
    {
    	final int c = _key.charAt(0);

        final String base64;

        if (c == 's')  {
        	//_key = _key.replaceAll("\\s*[\n\r]\\s*", ""); // remove spurious newline/carridge-return and associates space from cut-paste        
            base64 = PublicKeyReaderUtil.extractOpenSSHBase64(_key);
        } else if (c == '-')  {
        	if( _key.contains("PRIVATE KEY")) {
        		throw new PublicKeyParseException(PublicKeyParseException.ErrorCode.UNEXPECTED_PRIVATE_KEY); 
        	}
            base64 = PublicKeyReaderUtil.extractSecSHBase64(_key);
        } else {
            throw new PublicKeyParseException(
                    PublicKeyParseException.ErrorCode.UNKNOWN_PUBLIC_KEY_FILE_FORMAT);
        }

        final SSH2DataBuffer buf = new SSH2DataBuffer(Base64.getDecoder().decode(base64));
        final String type = buf.readString();
        final PublicKey ret;
        if (PublicKeyReaderUtil.SSH2_DSA_KEY.equals(type))  {
            ret = decodeDSAPublicKey(buf);
        } else if (PublicKeyReaderUtil.SSH2_RSA_KEY.equals(type))  {
            ret = decodePublicKey(buf);
        } else  {
            throw new PublicKeyParseException(
                    PublicKeyParseException.ErrorCode.UNKNOWN_PUBLIC_KEY_CERTIFICATE_FORMAT);
        }

        return ret;
    }
    /** format a key (of the types supported by the {@link #load(String)} method. 
     * 
     * @param key
     * @return public key string
     * @throws PublicKeyParseException 
     * @throws IOException 
     */
    public static String format(PublicKey key) throws PublicKeyParseException, IOException{
    	StringBuilder sb = new StringBuilder();
    	String alg = key.getAlgorithm();
    	if( alg.equalsIgnoreCase("RSA")){
    		RSAPublicKey pub = (RSAPublicKey) key;
    		sb.append(SSH2_RSA_KEY);
    		sb.append(" ");
    		SSH2ByteBuffer buf = new SSH2ByteBuffer();
    		packRSAkey(pub, buf);
    		sb.append(Base64.getEncoder().encodeToString(buf.toByteArray()));
    	}else if( alg.equalsIgnoreCase("DSA")){
    		DSAPublicKey pub = (DSAPublicKey) key;
    		sb.append(SSH2_DSA_KEY);
    		sb.append(" ");
    		SSH2ByteBuffer buf = new SSH2ByteBuffer();
    		
    		packDSAkey(pub, buf);
    		sb.append(Base64.getEncoder().encodeToString(buf.toByteArray()));
    	}else{
    		throw new PublicKeyParseException(ErrorCode.UNKNOWN_PUBLIC_KEY_FILE_FORMAT);
    	}
    	return sb.toString();
    }

	/**
	 * @param pub
	 * @param buf
	 * @throws IOException
	 */
	public static void packDSAkey(DSAPublicKey pub, SSH2ByteBuffer buf) throws IOException {
		buf.writeString(SSH2_DSA_KEY);
		packDSAKeyParams(pub, buf);
	}

	/**
	 * @param pub
	 * @param buf
	 * @throws IOException
	 */
	public static void packDSAKeyParams(DSAPublicKey pub, SSH2ByteBuffer buf) throws IOException {
		DSAParams params = pub.getParams();
		buf.writeMPint(params.getP());
		buf.writeMPint(params.getQ());
		buf.writeMPint(params.getG());
		buf.writeMPint(pub.getY());
	}

	/**
	 * @param pub
	 * @param buf
	 * @throws IOException
	 */
	public static void packRSAkey(RSAPublicKey pub, SSH2ByteBuffer buf) throws IOException {
		buf.writeString(SSH2_RSA_KEY);
		packRSAKeyParams(pub, buf);
	}

	/**
	 * @param pub
	 * @param buf
	 * @throws IOException
	 */
	public static void packRSAKeyParams(RSAPublicKey pub, SSH2ByteBuffer buf) throws IOException {
		buf.writeMPint(pub.getPublicExponent());
		buf.writeMPint(pub.getModulus());
	}

    public static String normalise(String key) throws PublicKeyParseException, IOException{
    	if( key == null || key.trim().length() == 0){
    		return key;
    	}
    	return format(load(key));
    }
   
    
    /**
     * <p>Extracts from the OpenSSH public key format the base64 encoded SSH
     * public key.</p>
     * <p>An example of such a definition is:<br/>
     * <code>ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAIEA1on8gxCGJJWSRT4uOrR130....</code>
     * </p>
     *
     * @param _key      text of the public key defined in the OpenSSH format
     * @return base64 encoded public-key data
     * @throws PublicKeyParseException if the OpenSSH public key string is
     *                                 corrupt
     * @see PublicKeyParseException.ErrorCode#CORRUPT_OPENSSH_PUBLIC_KEY_STRING
     * @see <a href="http://www.openssh.org">OpenSSH</a>
     */
    public static String extractOpenSSHBase64(final String _key)
        throws PublicKeyParseException
    {
        final String base64;
        try {
            final StringTokenizer st = new StringTokenizer(_key);
            st.nextToken();
            StringBuilder sb =new StringBuilder();
            while( st.hasMoreTokens()) {
            	String tok = st.nextToken();
            	if( tok.matches("[A-Za-z0-9+/=]+")) {
            		sb.append(tok);
            	}else {
            		break;
            	}
            }
            base64 = sb.toString();
            if( base64.isEmpty()) {
            	throw new PublicKeyParseException(
                        PublicKeyParseException.ErrorCode.CORRUPT_OPENSSH_PUBLIC_KEY_STRING);
            }
        } catch (final NoSuchElementException e) {
            throw new PublicKeyParseException(
                    PublicKeyParseException.ErrorCode.CORRUPT_OPENSSH_PUBLIC_KEY_STRING);
        }

        return base64;
    }

    /**
     * <p>Extracts from the SECSSH public key format the base64 encoded SSH
     * public key.</p>
     * <p>An example of such a definition is:
     * <pre>
     *  ---- BEGIN SSH2 PUBLIC KEY ----
     * Comment: This is my public key for use on \
     * servers which I don't like.
     * AAAAB3NzaC1kc3MAAACBAPY8ZOHY2yFSJA6XYC9HRwNHxaehvx5wOJ0rzZdzoSOXxbET
     * W6ToHv8D1UJ/z+zHo9Fiko5XybZnDIaBDHtblQ+Yp7StxyltHnXF1YLfKD1G4T6JYrdH
     * YI14Om1eg9e4NnCRleaqoZPF3UGfZia6bXrGTQf3gJq2e7Yisk/gF+1VAAAAFQDb8D5c
     * vwHWTZDPfX0D2s9Rd7NBvQAAAIEAlN92+Bb7D4KLYk3IwRbXblwXdkPggA4pfdtW9vGf
     * J0/RHd+NjB4eo1D+0dix6tXwYGN7PKS5R/FXPNwxHPapcj9uL1Jn2AWQ2dsknf+i/FAA
     * vioUPkmdMc0zuWoSOEsSNhVDtX3WdvVcGcBq9cetzrtOKWOocJmJ80qadxTRHtUAAACB
     * AN7CY+KKv1gHpRzFwdQm7HK9bb1LAo2KwaoXnadFgeptNBQeSXG1vO+JsvphVMBJc9HS
     * n24VYtYtsMu74qXviYjziVucWKjjKEb11juqnF0GDlB3VVmxHLmxnAz643WK42Z7dLM5
     * sY29ouezv4Xz2PuMch5VGPP+CDqzCM4loWgV
     * ---- END SSH2 PUBLIC KEY ----
     * </pre></p>
     *
     * @param _key      text of the public key defined in the SECSH format
     * @return base64 encoded public-key data
     * @throws PublicKeyParseException if the SECSSH key text file is corrupt
     * @see PublicKeyParseException.ErrorCode#CORRUPT_SECSSH_PUBLIC_KEY_STRING
     * @see <a href="http://tools.ietf.org/html/draft-ietf-secsh-publickeyfile">IETF Draft for the SECSH format</a>
     */
    private static String extractSecSHBase64(final String _key)
        throws PublicKeyParseException
    {
        final StringBuilder base64Data = new StringBuilder();

        boolean startKey = false;
        boolean startKeyBody = false;
        boolean endKey = false;
        boolean nextLineIsHeader = false;
        for (final String line : _key.split("\n"))  {
            final String trimLine = line.trim();
            if (!startKey && trimLine.equals(PublicKeyReaderUtil.BEGIN_PUB_KEY))  {
                startKey = true;
            } else if (startKey)  {
                if (trimLine.equals(PublicKeyReaderUtil.END_PUB_KEY))  {
                    endKey = true;
                    break;
                } else if (nextLineIsHeader)  {
                    if (!trimLine.endsWith("\\"))  {
                        nextLineIsHeader = false;
                    }
                } else if (trimLine.indexOf(':') > 0)  {
                    if (startKeyBody)  {
                        throw new PublicKeyParseException(
                                PublicKeyParseException.ErrorCode.CORRUPT_SECSSH_PUBLIC_KEY_STRING);
                    } else if (trimLine.endsWith("\\"))  {
                        nextLineIsHeader = true;
                    }
                } else  {
                    startKeyBody = true;
                    base64Data.append(trimLine);
                }
            }
        }

        if (!endKey)  {
            throw new PublicKeyParseException(
                    PublicKeyParseException.ErrorCode.CORRUPT_SECSSH_PUBLIC_KEY_STRING);
        }

        return base64Data.toString();
    }

    /**
     * <p>Decodes a DSA public key according to the SSH standard from the
     * data <code>_buffer</code> based on <b>NIST's FIPS-186</b>. The values of
     * the DSA public key specification are read in the order
     * <ul>
     * <li>prime p</li>
     * <li>sub-prime q</li>
     * <li>base g</li>
     * <li>public key y</li>
     * </ul>
     * With the specification the related DSA public key is generated.</p>
     *
     * @param _buffer   SSH2 data buffer where the type of the key is already
     *                  read
     * @return DSA public key instance
     * @throws PublicKeyParseException if the SSH2 public key blob could not be
     *                                 decoded
     * @see DSAPublicKeySpec
     * @see <a href="http://en.wikipedia.org/wiki/Digital_Signature_Algorithm">Digital Signature Algorithm  on Wikipedia</a>
     * @see <a href="http://tools.ietf.org/html/rfc4253#section-6.6">RFC 4253 Section 6.6</a>
     */
    private static PublicKey decodeDSAPublicKey(final SSH2DataBuffer _buffer)
        throws PublicKeyParseException
    {
        final BigInteger p = _buffer.readMPint();
        final BigInteger q = _buffer.readMPint();
        final BigInteger g = _buffer.readMPint();
        final BigInteger y = _buffer.readMPint();

        try {
            final KeyFactory dsaKeyFact = KeyFactory.getInstance("DSA");
            final DSAPublicKeySpec dsaPubSpec = new DSAPublicKeySpec(y, p, q, g);

            return dsaKeyFact.generatePublic(dsaPubSpec);

        } catch (final Exception e) {
            throw new PublicKeyParseException(
                    PublicKeyParseException.ErrorCode.SSH2DSA_ERROR_DECODING_PUBLIC_KEY_BLOB, e);
        }
    }

    /**
     * <p>Decode a RSA public key encoded according to the SSH standard from
     * the data <code>_buffer</code>. The values of the RSA public key
     * specification are read in the order
     * <ul>
     * <li>public exponent</li>
     * <li>modulus</li>
     * </ul>
     * With the specification the related RSA public key is generated.</p>
     *
     * @param _buffer   key / certificate data (certificate or public key
     *                  format identifier is already read)
     * @return RSA public key instance
     * @throws PublicKeyParseException if the SSH2 public key blob could not be
     *                                 decoded
     * @see RSAPublicKeySpec
     * @see <a href="http://en.wikipedia.org/wiki/RSA">RSA on Wikipedia</a>
     * @see <a href="http://tools.ietf.org/html/rfc4253#section-6.6">RFC 4253 Section 6.6</a>
     */
    static PublicKey decodePublicKey(final SSH2DataBuffer _buffer)
        throws PublicKeyParseException
    {
        final BigInteger e = _buffer.readMPint();
        final BigInteger n = _buffer.readMPint();
        //assert(_buffer.remaining()==0);
        try {
            final KeyFactory rsaKeyFact = KeyFactory.getInstance("RSA");
            final RSAPublicKeySpec rsaPubSpec = new RSAPublicKeySpec(n, e);

            return rsaKeyFact.generatePublic(rsaPubSpec);

        } catch (final Exception ex) {
            throw new PublicKeyParseException(
                    PublicKeyParseException.ErrorCode.SSH2RSA_ERROR_DECODING_PUBLIC_KEY_BLOB, ex);
        }
    }

    /**
     * The class is used to read from a SSH data buffer the protocol specific
     * formatting defined in
     * <a href="http://tools.ietf.org/html/rfc4253#section-6.6">RFC 4253
     * Section 6.6</a>.
     *
     * @see <a href="http://tools.ietf.org/html/rfc4253#section-6.6">RFC 4253 Section 6.6</a>
     */
    static class SSH2DataBuffer
    {
        /**
         * SSH2 data.
         */
        private final byte[] data;

        /**
         * Current position in {@link #data}.
         */
        private int pos;

        /**
         * Initialize the SSH2 data buffer.
         *
         * @param _data binaray data blob
         * @see #data
         */
        public SSH2DataBuffer(final byte[] _data)
        {
            this.data = _data;
        }

        /**
         * Reads a big integer from {@link #data} starting with {@link #pos}. A
         * big integer is stored as byte array (see {@link #readByteArray()}).
         *
         * @return read big integer
         * @throws PublicKeyParseException if the byte array holds not enough
         *                                 bytes
         * @see #readByteArray()
         */
        public BigInteger readMPint()
            throws PublicKeyParseException
        {
            final byte[] raw = this.readByteArray();
            return (raw.length > 0) ? new BigInteger(raw) : BigInteger.valueOf(0);
        }

        /**
         * Reads a string from {@link #data} starting with {@link #pos}. A
         * string is stored as byte array (see {@link #readByteArray()}) in
         * UTF8 format.
         *
         * @return read string
         * @throws PublicKeyParseException if the byte array holds not enough
         *                                 bytes
         * @see #readByteArray()
         */
        public String readString()
            throws PublicKeyParseException
        {
            return new String(this.readByteArray());
        }

        /**
         * Reads from the {@link #data} starting with {@link #pos} the next
         * four bytes and prepares an integer.
         *
         * @return 32 bit integer value
         */
        public int readUInt32()
        {
            final int byte1 = 0xff & this.data[this.pos++];
            final int byte2 = 0xff & this.data[this.pos++];
            final int byte3 = 0xff & this.data[this.pos++];
            final int byte4 = 0xff & this.data[this.pos++];
            return ((byte1 << 24) + (byte2 << 16) + (byte3 << 8) + (byte4 << 0));
        }
        /**
         * Reads from the {@link #data} starting with {@link #pos} the next
         * eight bytes and prepares a long.
         *
         * @return 64 bit long value
         */
        public long readUInt64()
        {
            final long byte1 = 0xff & this.data[this.pos++];
            final long byte2 = 0xff & this.data[this.pos++];
            final long byte3 = 0xff & this.data[this.pos++];
            final long byte4 = 0xff & this.data[this.pos++];
            final long byte5 = 0xff & this.data[this.pos++];
            final long byte6 = 0xff & this.data[this.pos++];
            final long byte7 = 0xff & this.data[this.pos++];
            final long byte8 = 0xff & this.data[this.pos++];
            return ((byte1 << 56) + (byte2 << 48) + (byte3 << 40) + (byte5 << 32) +
            		(byte5 << 24) + (byte6 << 16) + (byte7 << 8 ) + (byte8 << 0 ));
        }
        /**
         * Reads from the {@link #data} starting with {@link #pos} a byte
         * array. The byte array is defined as:
         * <ul>
         * <li>first the length of the byte array is defined as integer
         *     (see {@link #readUInt32()})</li>
         * <li>then the byte array itself is defined</li>
         * </ul>
         *
         * @return read byte array from {@link #data}
         * @throws PublicKeyParseException if the byte array holds not enough
         *                                 bytes
         * @see #readUInt32()
         * @see PublicKeyParseException.ErrorCode#CORRUPT_BYTE_ARRAY_ON_READ
         */
        public byte[] readByteArray()
            throws PublicKeyParseException
        {
        	try {
        		final int len = this.readUInt32();
        		if ((len < 0) || (len > (this.data.length - this.pos)))  {
        			throw new PublicKeyParseException(
        					PublicKeyParseException.ErrorCode.CORRUPT_BYTE_ARRAY_ON_READ);
        		}
        		final byte[] str = new byte[len];
        		System.arraycopy(this.data, this.pos, str, 0, len);
        		this.pos += len;
        		return str;
        	}catch(ArrayIndexOutOfBoundsException e) {
        		// from the readUINT32
        		throw new PublicKeyParseException(
        				PublicKeyParseException.ErrorCode.CORRUPT_BYTE_ARRAY_ON_READ);
        	}
        }
        /** reads a nested buffer.
         * 
         * @return
         * @throws PublicKeyParseException
         */
        public SSH2DataBuffer readBuffer() throws PublicKeyParseException {
        	return new SSH2DataBuffer(readByteArray());
        }
        
        public int remaining() {
        	return data.length - pos;
        }
        
        public int pos() {
        	return pos;
        }
        
        public void reset() {
        	pos=0;
        }
    }

    static class SSH2ByteBuffer extends ByteArrayOutputStream{
    	public void writeMPint(BigInteger i) throws IOException{
    		writeByteArray(i.toByteArray());
    	}
    	public void writeString(String s) throws IOException{
    		writeByteArray(s.getBytes());
    	}
    	public void writeUint32(int i){
    		write( ( i >> 24 ) & 0xff );
    		write( ( i >> 16 ) & 0xff );
    		write( ( i >> 8 ) & 0xff );
    		write(  i  & 0xff );
    	}
    	public void writeUint64(long i){
    		write( (int) (( i >> 56 ) & 0xff ));
    		write( (int) (( i >> 48 ) & 0xff ));
    		write( (int) (( i >> 40 ) & 0xff ));
    		write( (int) (( i >> 32 ) & 0xff ));
    		write( (int) (( i >> 24 ) & 0xff ));
    		write( (int) (( i >> 16 ) & 0xff ));
    		write( (int) (( i >> 8 ) & 0xff ));
    		write( (int) (i  & 0xff) );
    	}
    	public void writeBuffer(SSH2ByteBuffer buffer) throws IOException {
    		if( buffer ==null) {
    			writeByteArray(new byte[0]);
    		}else {
    			writeByteArray(buffer.toByteArray());
    		}
    	}
    	public void writeByteArray(byte data[]) throws IOException{
    		writeUint32(data.length);
    		write(data);
    	}
    }
    /**
     * The Exception is throws if the public key encoded text could not be
     * parsed. For the related {@link PublicKeyParseException#errorCode} see
     * enumeration {@link PublicKeyParseException.ErrorCode}.
     */
    public static final class PublicKeyParseException
            extends Exception
    {
        /**
         * Defines the serialize version unique identifier.
         */
        private static final long serialVersionUID = 1446034172449421912L;

        /**
         * Error code of the public key parse exception.
         */
        private final ErrorCode errorCode;

        /**
         * Creates a new exception for defined <code>_errorCode</code>.
         *
         * @param _errorCode    error code
         */
        private PublicKeyParseException(final ErrorCode _errorCode)
        {
            super(_errorCode.message);
            this.errorCode = _errorCode;
        }

        /**
         * Creates a new exception for defined <code>_errorCode</code> and
         * <code>_cause</code>.
         *
         * @param _errorCode    error code
         * @param _cause        throwable clause
         */
        private PublicKeyParseException(final ErrorCode _errorCode,
                                        final Throwable _cause)
        {
            super(_errorCode.message, _cause);
            this.errorCode = _errorCode;
        }

        /**
         * Returns the error code enumeration of this public key parse
         * exception instance.
         *
         * @return error code of the public key parse exception instance
         * @see #errorCode
         */
        public ErrorCode getErrorCode()
        {
            return this.errorCode;
        }

        /**
         * Enumeration of the error codes if the public key could not parsed.
         */
        public enum ErrorCode
        {
            /**
             * The format of the given ASCII key is not known and could not be
             * parsed. Only OpenSSH (starts with 's') and SECSH (starts with
             * '-') are currently supported.
             *
             * @see PublicKeyReaderUtil#load(String)
             */
            UNKNOWN_PUBLIC_KEY_FILE_FORMAT("Corrupt or unknown public key file format"),

            /**
             * The binary blob of the key definition used a not supported
             * public key certificate format. Only DSA and RSA are currently
             * supported.
             *
             * @see PublicKeyReaderUtil#SSH2_DSA_KEY
             * @see PublicKeyReaderUtil#SSH2_RSA_KEY
             * @see PublicKeyReaderUtil#load(String)
             */
            UNKNOWN_PUBLIC_KEY_CERTIFICATE_FORMAT("Corrupt or unknown public key certificate format"),

            /**
             * The public key string is not defined correctly in OpenSSH
             * format.
             *
             * @see PublicKeyReaderUtil#extractOpenSSHBase64(String)
             */
            CORRUPT_OPENSSH_PUBLIC_KEY_STRING("Corrupt OpenSSH public key string"),

            /**
             * The public key string is not defined correctly in SECSSH
             * format.
             *
             * @see PublicKeyReaderUtil#extractSecSHBase64(String)
             */
            CORRUPT_SECSSH_PUBLIC_KEY_STRING("Corrupt SECSSH public key string"),

            /**
             * The DSA public key blob could not decoded.
             *
             * @see PublicKeyReaderUtil#decodeDSAPublicKey(SSH2DataBuffer)
             */
            SSH2DSA_ERROR_DECODING_PUBLIC_KEY_BLOB("SSH2DSA: error decoding public key blob"),

            /**
             * The RSA public key blob could not decoded.
             *
             * @see PublicKeyReaderUtil#decodePublicKey(SSH2DataBuffer)
             */
            SSH2RSA_ERROR_DECODING_PUBLIC_KEY_BLOB("SSH2RSA: error decoding public key blob"),

            /**
             * @see PublicKeyReaderUtil.SSH2DataBuffer#readByteArray()
             */
            CORRUPT_BYTE_ARRAY_ON_READ("Corrupt byte array on read"),

        	
        	UNEXPECTED_PRIVATE_KEY("Private key where public key expected");
            /**
             * English message of the error code.
             */
            private final String message;

            /**
             * Constructor used to initialize the error codes with an error
             * message.
             *
             * @param _message  message text of the error code
             */
            ErrorCode(final String _message)
            {
                this.message = _message;
            }
        }
    }
}