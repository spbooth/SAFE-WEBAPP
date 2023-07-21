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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;
/** A {@link TextInput} to input Globus/LDAP DN names.
 * 
 * for historical reasons the canonical {@link String} representation is the
 * Globus one though both forms should be accepted as input and the class provides methods to convert between the
 * two forms.  
 * 
 */
public class DNInput extends ParseAbstractInput<String> implements ItemInput<String,LdapName>{



	private static final Pattern strip_pattern= Pattern.compile("\"(.*)\"");
	//private static final Pattern valid_pattern = Pattern.compile("(/\\w+=[^/=]+)+");
	public DNInput() {
		super();
		setBoxWidth(64);
		setSingle(true);
		addValidator(new FieldValidator<String>() {
			
			@Override
			public void validate(String val) throws FieldException {
				if( val == null || val.trim().length()==0){
					// have to check optional again as superclass accepts spaces
					return;
				}
				if( DNInput.validateGlobusDN(val)){
					return;
				}

				throw new ValidateException("Invalid format  DN");

			}
		});
	}

	
	
	public static LdapName parseGlobusName(String name) throws ParseException{
		if( name==null || !name.startsWith("/")){
			throw new ParseException("No leading /");
		}
		List<Rdn> list = new LinkedList<>();
		LinkedList<String> chunks= new LinkedList<>();
    	// Rdns may contain / but must contain an = 
		for(String s : name.substring(1).split("/")){
			if( s.contains("=") ){
    			chunks.add(s);
    		}else{
    			String new_chunk;
    			try{
    				new_chunk= chunks.removeLast()+"/"+s;
    			}catch(NoSuchElementException e){
    				new_chunk=s;
    			}
    			chunks.add(new_chunk);
    		}
		}
		for( String s : chunks){
			try {
				list.add(new Rdn(s));
			} catch (InvalidNameException e) {
				throw new ParseException("Bad DN format", e);
			}
		}
		return new LdapName(list);
	}
	public static LdapName parseLDAPName(String name) throws ParseException{
		try {
			return new LdapName(name);
		} catch (InvalidNameException e) {
			throw new ParseException("Invalid LDAP DN", e);
		}
	}
	public static LdapName reverse(LdapName name){
		List<Rdn> rdns = new LinkedList<>(name.getRdns());
		java.util.Collections.reverse(rdns);
		return new LdapName(rdns);
	}
	public static String makeGlobusName(LdapName name){
		StringBuilder sb = new StringBuilder();
		for(Enumeration<String> e = name.getAll(); e.hasMoreElements();){
			sb.append("/");
			sb.append(e.nextElement());
		}
		return sb.toString();
	}
	
	public static boolean validateLdapDN(String ldapDN){
		try {
			LdapName name = new LdapName(ldapDN);
			return true;
		} catch (InvalidNameException e) {
			return false;
		}
	}
	public static boolean validateGlobusDN(String globusDN){
		try {
			parseGlobusName(globusDN);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public String convert(Object v)  {
		String val = (String) v;
		if( val == null ){
			return null;
		}
		val=val.trim();
		// remove quotes
		Matcher m = strip_pattern.matcher(val);
		if( m.matches()){
			val=m.group(1);
		}
		
		return val;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseInput#parse(java.lang.String)
	 */
	@Override
	public String parseValue(String v) throws ParseException {
		if( v == null ){
			
			return null;
		}
		v=convert(v);
		if( v == null || v.trim().length() == 0){
			return null;
		}
		if( v.startsWith("/")){
			LdapName name= parseGlobusName(v);
			return makeGlobusName(name);
		}else{
			LdapName name = parseLDAPName(v);
			return makeGlobusName(name);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ItemInput#getItem()
	 */
	@Override
	public LdapName getItembyValue(String value) {
		try {
			return parseGlobusName(value);
		} catch (ParseException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ItemInput#setItem(java.lang.Object)
	 */
	@Override
	public void setItem(LdapName item) {
		try {
			setValue(makeGlobusName(item));
		} catch (TypeException e) {
			throw new TypeError(e);
		}
	}

	@Override
	public boolean isEmpty() {
		String value = getValue();
		return value == null || value.trim().isEmpty();
	}
   }