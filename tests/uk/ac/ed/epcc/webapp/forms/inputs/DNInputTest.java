// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.AbstractInputTestCase;
import uk.ac.ed.epcc.webapp.forms.ParseAbstractInputTestCase;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

import static org.junit.Assert.*;
/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.5 $")
public class DNInputTest extends ParseAbstractInputTestCase<String,DNInput> {
	
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getGoodParseData()
	 */
	public Set<String> getGoodParseData() {
		Set<String> gp = new HashSet<String>();
		for(String s : getGoodData()){
			gp.add(s);
			gp.add("\""+s+"\""); // allow quoted input
		}
		// allow ldap form input
		gp.add("CN=Engbert Heupers, O=sara, O=users, O=dutchgrid");
		return gp;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getBadParseData()
	 */
	public Set<String> getBadParseData() {
		return getBadData();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	public Set<String> getGoodData()  {
		Set<String> good = new HashSet<String>();
		good.add("/c=UK/o=eScience/ou=Edinburgh/l=NeSC/cn=stephen booth");
		good.add("/C=UK/O=eScience/OU=Edinburgh/L=NeSC/CN=stephen \\,booth"); // escaped commas
		good.add("/DC=uk/DC=ac/DC=ceda/O=STFC RAL/CN=https://ceda.ac.uk/openid/Stephen.Pascoe");
		return good;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getBadData()
	 */
	public Set<String> getBadData() {
		Set<String> bad = new HashSet<String>();
		bad.add("  ");
		bad.add("wombles");
		bad.add("/C=UK/O=eScience/OU=Edinburgh/L=NeSC/CN=stephen ,booth"); // un-escaped commas)
		return bad;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getInput()
	 */
	public DNInput getInput() throws Exception {
		
		return new DNInput();
	}

	@Test
	public void testGlobusName() throws InvalidNameException{
			String name = "/C=UK/O=eScience/OU=Edinburgh/L=NeSC/CN=stephen booth";
			List<Rdn> list = new LinkedList<Rdn>();
			for(String s : name.substring(1).split("/")){
				list.add(new Rdn(s));
			}
			LdapName ldn = new LdapName(list);
			System.out.println(ldn.toString());
			assertTrue(ldn.equals(new LdapName("CN=stephen booth,L=NeSC,OU=Edinburgh,O=eScience,C=UK")));
			 
		 }
	
	@Test
		 public void testLdapName() throws InvalidNameException{
			 new LdapName("CN=stephen booth,L=NeSC,OU=Edinburgh,O=eScience,C=UK");
			 
			 
		 }
		 
	@Test
	public void testStatics() throws ParseException{
		assertTrue(DNInput.parseGlobusName("/C=UK/O=eScience/OU=Edinburgh/L=NeSC/CN=stephen booth").equals(DNInput.parseLDAPName("CN=stephen booth,L=NeSC,OU=Edinburgh,O=eScience,C=UK")));
		assertTrue("/C=UK/O=eScience/OU=Edinburgh/L=NeSC/CN=stephen booth".equals(DNInput.makeGlobusName(DNInput.parseLDAPName("CN=stephen booth,L=NeSC,OU=Edinburgh,O=eScience,C=UK"))));
		
		
	}
	
	@Test
	public void testReverse() throws InvalidNameException{
		LdapName orig =  new LdapName("CN=stephen booth,L=NeSC,OU=Edinburgh,O=eScience,C=UK");
		LdapName reverse = new LdapName("C=UK,O=eScience,OU=Edinburgh,L=NeSC,CN=stephen booth");
		
		assertTrue(orig.equals(DNInput.reverse(reverse)));
	}
	@Test
		 public void testGlobusCertificateVerity(){
			 assertTrue(DNInput.validateGlobusDN("/C=UK/O=eScience/OU=Edinburgh/L=NeSC/CN=stephen booth"));
			  assertFalse(DNInput.validateGlobusDN("/C=UK/O=eScience/OU=Edinburgh/L=NeSC/CN=stephen ,booth"));
			  assertTrue(DNInput.validateGlobusDN("/C=UK/O=eScience/OU=Edinburgh/L=NeSC/CN=stephen \\,booth"));
			  assertTrue(DNInput.validateGlobusDN("/DC=uk/DC=ac/DC=ceda/O=STFC RAL/CN=https://ceda.ac.uk/openid/Stephen.Pascoe"));
			  assertFalse(DNInput.validateGlobusDN("fred"));
		 }
	

}
