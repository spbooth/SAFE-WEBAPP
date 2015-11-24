// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.ssh;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.PublicKeyParseException;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class PublicKeyReaderTest {


	
@Test
public void testRoundTrip() throws PublicKeyParseException, IOException{
	
	Set<String> good = new HashSet<String>();
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
for( String key_str : good){
	

	PublicKey key= PublicKeyReaderUtil.load(key_str);
    String normalised = PublicKeyReaderUtil.format(key);
    PublicKey key2 = PublicKeyReaderUtil.load(normalised);
    assertEquals(key,key2);
}
	
	
}
}
