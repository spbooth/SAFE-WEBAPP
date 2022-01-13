//| Copyright - The University of Edinburgh 2014                            |
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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.PublicKeyParseException;

/**
 * @author spb
 *
 */

public class PublicKeyReaderTest {


	
@Test
public void testRoundTrip() throws PublicKeyParseException, IOException{
	
	Set<String> good = new LinkedHashSet<>();
	// with and without newline breaks
	good.add("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQBRQkTnsRzUM9mLrgEMFk78CLdOxtepxPp1JQSfRc3/A1cy"+
"D8NV/gxINRNhMIVkIofUexxtLfAfmNRf666SSei/w2kPX9ndOJ32y2OUUKkijJvEdeMEuFido9Kifc79"+
"p0q1KcOhAdRNmmE+LriqsbhJJVQz0OeOKw7wPN9KNYfTevZleQAJBRKr99rBgyRrtrXBhnjYu3yb8E/l"+
"f4g8MiBuLGcezzi310RwKMFnamr6MTbA3KBvgvFrPmsjVyedn1IyMdgQ0x8OZMQbr6hesvnR8HuKYfFt"+
"m4Vjx7bS+Dyqn+PlPrWH/fjs1957fe57gtZ9eM2S0lsv5cagcWghPAZP rsa-key-20110308");
good.add("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQBRQkTnsRzUM9mLrgEMFk78CLdOxtepxPp1JQSfRc3/A1cy\n"+
"D8NV/gxINRNhMIVkIofUexxtLfAfmNRf666SSei/w2kPX9ndOJ32y2OUUKkijJvEdeMEuFido9Kifc79\n"+
"p0q1KcOhAdRNmmE+LriqsbhJJVQz0OeOKw7wPN9KNYfTevZleQAJBRKr99rBgyRrtrXBhnjYu3yb8E/l\n"+
"f4g8MiBuLGcezzi310RwKMFnamr6MTbA3KBvgvFrPmsjVyedn1IyMdgQ0x8OZMQbr6hesvnR8HuKYfFt\n"+
"m4Vjx7bS+Dyqn+PlPrWH/fjs1957fe57gtZ9eM2S0lsv5cagcWghPAZP rsa-key-20110308");
	good.add("ssh-dss AAAAB3NzaC1kc3MAAACBAMot/qdk4G5WgLUTodzXa8YdvzmvsQsEHRMhgfRtq2DGDXn6QYuO"+
"I4ORiXHXLhyaY4Igtc3bj3QFPNNy/tDzFt+ejhknPlgGZtn1W9ye4/b//EBiyUFT/y7ETYP2tvfAhVJn"+
"BkmDEXxoMgnTyZLGwjzHkhpsN1yeZkS+cISZVXP/AAAAFQCrvEDO7oarp4uNPfsiX7fb390fawAAAIB2"+
"RnGx/WHGPjYolKNKC8oTmzO7CRLs6SqMjdCcqNCDYRkt7DnS4VKO1KCjO03mI1kTjNXgjX7zlMFkmF4o"+
"pWQmDgqB7HPBb0/dgdyCXOIOErzMCjqu70anMaFz3m5buH7hq2lcuxrLlAX5tNmHU2WL/nEgSb5CnKXS"+
"NA0h0pbnyAAAAIBLFrX7ZWEuZnzFThiRVrkHJY5GcEVt8PM16MvhVdbYbxJhWeVfQHwCX4j8ngVxywBw"+
"dEuAWm066xAfWLU1cf/PM/CbKwaMMHA/i2VMlydDhfMHeiV3ydhDFke7h9geKAxM26/Cewd6U4OOJyrr"+
"1cYMVm+GXeHOML6nGR41aK48eQ== spb@t3400spb.epcc.ed.ac.uk");
	
	// linebreak and extra space
	good.add("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXA9/mz912d721UhE0bP5othL0sqWrG6BppZNlOcfN\n"+
	"iNmzBQEkM7I0Y+O+8dpeRUK9BOzqW5Snb/r0/WMQs/6OJZ11J9SJiAfgO80LLyu2WPkUHv/xy3i57Vi8\n"+
	" VRCzt+rkiYF42je/xX6E+t4gaVP8dgO2cMJ46b8+2O6jAl0bmAc4V6joBHtcLhqu2zF9jh4U59fDrYM/\n"+
	" 5NwU1nApiGe/yRWr9qSGrQEJCUlGBX0BPwpyv4unwzU6KVWlhL5IvgQU6BocQfCw+xX74k1MlWDMTO8t\n"+
	" nwnUbI9GgLJ74uNaT12mrlOptO+36yy4tvNLTiJn2KfzI1D+0FadNboYzI3H mcdn20@chpc-iztac");
	
	// cr not nl
	good.add("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXA9/mz912d721UhE0bP5othL0sqWrG6BppZNlOcfN\r"+
			"iNmzBQEkM7I0Y+O+8dpeRUK9BOzqW5Snb/r0/WMQs/6OJZ11J9SJiAfgO80LLyu2WPkUHv/xy3i57Vi8\r"+
			" VRCzt+rkiYF42je/xX6E+t4gaVP8dgO2cMJ46b8+2O6jAl0bmAc4V6joBHtcLhqu2zF9jh4U59fDrYM/\r"+
			" 5NwU1nApiGe/yRWr9qSGrQEJCUlGBX0BPwpyv4unwzU6KVWlhL5IvgQU6BocQfCw+xX74k1MlWDMTO8t\r"+
			" nwnUbI9GgLJ74uNaT12mrlOptO+36yy4tvNLTiJn2KfzI1D+0FadNboYzI3H mcdn20@chpc-iztac");
	
	// one line
	good.add("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXA9/mz912d721UhE0bP5othL0sqWrG6BppZNlOcfN"+
			"iNmzBQEkM7I0Y+O+8dpeRUK9BOzqW5Snb/r0/WMQs/6OJZ11J9SJiAfgO80LLyu2WPkUHv/xy3i57Vi8"+
			"VRCzt+rkiYF42je/xX6E+t4gaVP8dgO2cMJ46b8+2O6jAl0bmAc4V6joBHtcLhqu2zF9jh4U59fDrYM/"+
			"5NwU1nApiGe/yRWr9qSGrQEJCUlGBX0BPwpyv4unwzU6KVWlhL5IvgQU6BocQfCw+xX74k1MlWDMTO8t"+
			"nwnUbI9GgLJ74uNaT12mrlOptO+36yy4tvNLTiJn2KfzI1D+0FadNboYzI3H mcdn20@chpc-iztac");

	// just spaces
	good.add("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXA9/mz912d721UhE0bP5othL0sqWrG6BppZNlOcfN"+
			" iNmzBQEkM7I0Y+O+8dpeRUK9BOzqW5Snb/r0/WMQs/6OJZ11J9SJiAfgO80LLyu2WPkUHv/xy3i57Vi8"+
			" VRCzt+rkiYF42je/xX6E+t4gaVP8dgO2cMJ46b8+2O6jAl0bmAc4V6joBHtcLhqu2zF9jh4U59fDrYM/"+
			" 5NwU1nApiGe/yRWr9qSGrQEJCUlGBX0BPwpyv4unwzU6KVWlhL5IvgQU6BocQfCw+xX74k1MlWDMTO8t"+
			" nwnUbI9GgLJ74uNaT12mrlOptO+36yy4tvNLTiJn2KfzI1D+0FadNboYzI3H mcdn20@chpc-iztac");
	
	good.add("---- BEGIN SSH2 PUBLIC KEY ----\n"+
			"Comment: \"comment goes here\"\n"+
			"AAAAB3NzaC1yc2EAAAABJQAAAQEAlLhFLr/4LGC3cM1xgRZVxfQ7JgoSvnVXly0K\n"+
			"7MNufZbUSUkKtVnBXAOIjtOYe7EPndyT/SAq1s9RGZ63qsaVc/05diLrgL0E0gW+\n"+
			"9VptTmiUh7OSsXkoKQn1RiACfH7sbKi6H373bmB5/TyXNZ5C5KVmdXxO+laT8IdW\n"+
			"7JdD/gwrBra9M9vAMfcxNYVCBcPQRhJ7vOeDZ+e30qapH4R/mfEyKorYxrvQerJW\n"+
			"OeLKjOH4rSnAAOLcEqPmJhkLL8k6nQAAK3P/E1PeOaB2xD7NNPqfIsjhAJLZ+2wV\n"+
			"3eUZATx9vnmVF0YafOjvzcoK2GqUrhNAvi7k0f+ihh8twkfthj==\n"+
			"---- END SSH2 PUBLIC KEY ----");
	good.add("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAlLhFLr/4LGC3cM1xgRZVxfQ7JgoSvnVXly0K7MNufZbUSUkKtVnBXAOIjtOYe7EPndyT/SAq1s9RGZ63qsaVc/05diLrgL0E0gW+9VptTmiUh7OSsXkoKQn1RiACfH7sbKi6H373bmB5/TyXNZ5C5KVmdXxO+laT8IdW7JdD/gwrBra9M9vAMfcxNYVCBcPQRhJ7vOeDZ+e30qapH4R/mfEyKorYxrvQerJWOeLKjOH4rSnAAOLcEqPmJhkLL8k6nQAAK3P/E1PeOaB2xD7NNPqfIsjhAJLZ+2wV3eUZATx9vnmVF0YafOjvzcoK2GqUrhNAvi7k0f+ihh8twkfthg==");
	good.add("ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAlLhFLr/4LGC3cM1xgRZVxfQ7JgoSvnVXly0K7MNufZbUSUkKtVnBXAOIjtOYe7EPndyT/SAq1s9RGZ63qsaVc/05diLrgL0E0gW+9VptTmiUh7OSsXkoKQn1RiACfH7sbKi6H373bmB5/TyXNZ5C5KVmdXxO+laT8IdW7JdD/gwrBra9M9vAMfcxNYVCBcPQRhJ7vOeDZ+e30qapH4R/mfEyKorYxrvQerJWOeLKjOH4rSnAAOLcEqPmJhkLL8k6nQAAK3P/E1PeOaB2xD7NNPqfIsjhAJLZ+2wV3eUZATx9vnmVF0YafOjvzcoK2GqUrhNAvi7k0f+ihh8twkfthj==");

	
	good.add("ssh-rsa AAAAB3NzaC1kc3MAAACBAL7MOkyYBePWSt/HcDIi0BKf8zXVRliTlnrVywO+8/64TN+Rh8LKBGe5CMiXlAKSRDuTuTG/AGL1qLBCAE2NXQOPrQz9M3gpDwE8VWA0aTyIE5BfU1dXKi/Eliz91zRybQ2NBeo1qOgmI+4h2aTCyCvTM6awyNBSToh7V2FWVA8/AAAAFQDaQ/hwNSbWhNHUTO28P/XgHBAabwAAAIBomZrXiODzcSg0cZ70hjEOspiYZUYeK2Q/Q72A7j9e2hwovEUOYO6CJ3cG6QBcb9TA/GQvw4w5OmfCiUbdm+R+QolZ3tNptY342hgQbCu2fp4l7FF13+u2O7ZF8qHKUD7uRC/hJSGGJCgFLm4mgZjdAO5qoSrWokDZvoXJKZ5YrgAAAIBJ1JsUny3haeJZohb9uS+q8j8Y+VpqUAYZSaNO3Iza9Cbx7IKlX5gJ6u9dAVbIKRgOUY8qTdAht2wLg45+RgLhHdtRNTpUhh9Pw6vHJoEKs/1Kw2M7I6oRLPKpfMxUPYepaRZuennh/zHV4QcndU4wszBXZABpIFY9xfX8IKEVCQ==");
	//good.add("ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAIEAyt7lXWuriIr92Na7gR+plEPK0zK5fpKGEz1mNKCdR7VE13NlG27e/4C9wtEjvp5Sv16yi+Ub5w1QsM9EO8N1pwez3iAgJf/OuyEoZt4AdJqtt+/vpzKwSdo38hkisppsA+4AqyA4kxQMGek+WdKVCOQ5oOjKPItW5uZma8du");
	good.add("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDijTyh36xJxRRsY0CvMILp/a3RcYj2z//oQr1zXtmX/+XIfWUaMXi1Pvlf57q3UHzz7PddzBGGKPqwbJ59Bu0AmuDIZDHy6j6Fhz0cKlvKpc+bx4RmEEI8XCmVD3CM9tA1bWgRsd5AjU5VhKx9tLtgUt+pzwgHBUESLakEBU0C/GCDi0Wz6/8wTjlDtvbCKJfgbiolb1Vc97yJq35oF5ci7pHxQ7SRs95Bk+O//Mhs2anYelv+rGPTsLEkbuqyOVFbo/k0ympAgTFO1FdqRntJD09Cd+65vtq++R8Rb4fxFDbIWqfdZNsQIVwg836zEJ5JmXBQVg0ElIgHXDmxz6Uzssg=");
	//good.add("ssh-rsa AAAAB4NzaC1yc2EAAAADAQABAAABAQDcfrV/MWDKMvTmMbYHhU1n/EaiW1LXCO2cZ6nSDHn06TxRcTWD0CP08m8y5Mq0aQjp6xwBDxhQJ4gs9PFfM9rTE17ZFNN2+a79Xv8MxuKSURwm3k7q+S/0yfKO+AKCP9dA079QPNDzgV9zH0b3qDgonnR7AWSV95j3TMYQHsm0dMWlst4l+Sd9b5GRJ4If/xDpKtMcaGnJlfbrxjQnZ11g1GM0LwjU6/ksiERXruu8KU/Mxfy+3g2wGtoY3MSIcG0Br5NcezgAYmWLr7AyN00IMK7L288knTR3JriG0DAiJajciFshebl3XtKio6XFHZTil0sV3z+mPH3BGw8bHI0P");

	
	for( String key_str : good){
	

	PublicKey key= PublicKeyReaderUtil.load(key_str);
    String normalised = PublicKeyReaderUtil.format(key);
    PublicKey key2 = PublicKeyReaderUtil.load(normalised);
    System.out.println(key_str+"\n|\n|\nV\n"+normalised);
    assertTrue(key.equals(key2));
    assertEquals(key,key2);
    if( key instanceof RSAPublicKey) {
    	RSAPublicKey rsa_key = (RSAPublicKey) key;
    	RSAPublicKey rsa_key2 = (RSAPublicKey) key2;
    	assertEquals(rsa_key.getModulus(), rsa_key2.getModulus());
    	assertEquals(rsa_key.getPublicExponent(),rsa_key2.getPublicExponent());
    	System.out.println(rsa_key.getModulus());
    	System.out.println(rsa_key.getPublicExponent());
    }
}
	
	
}
}