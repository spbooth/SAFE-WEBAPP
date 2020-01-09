//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
/** Basic class for building email messages
 * 
 * 
 * @author spb
 *
 */


public class TextMailBuilder {
	private Set<String> refs = new LinkedHashSet<String>();
	protected StringBuilder text;  // current text part
	protected String separator=null;
	
	private String prefix="";
	private boolean at_start=true;
	
	public TextMailBuilder(){
		text=new StringBuilder();
	}
	/** add text to the message applying any quoting and seperators.
	 * 
	 * @param s
	 * @return number of parts added
	 */
	public int addText(String s){
		if(separator != null){
			text.append(separator);
			separator=null;
			at_start=true;
		}
		if( prefix.length()==0){
			text.append(s);
		}else{
			StringTokenizer st = new StringTokenizer(s.replaceAll("\\R", "\n"),"\n",true);
			while(st.hasMoreElements()){
				if( at_start ){
					doPrefix();
					at_start=false;
				}
				String l = st.nextToken();
				if( l.equals("\n")){
					text.append("\n");
					at_start=true;
				}else{
				    text.append(l);
				}
			}
		}
		return 0;
	}
	/** Set a prefix string to be applied at the beginning of every line added with addText
	 * 
	 * @param p
	 */
	public void setPrefix(String p){
		prefix=p;
	}
	/** add a separator to denote a new part of the message. This text is only added if additional text is appended
	 * afterwards.
	 * 
	 * @param text 
	 * @return number of parts added
	 * @throws MessagingException
	 */
	public int separate(String text) throws MessagingException{
		separator=text;
		return 0;
	}
	/** change the text of a pending separator (if there is any).
	 * 
	 * @param new_sep
	 */
	public void changeSeparator(String new_sep){
		if( separator != null ){
			separator = new_sep;
		}
	}
	@Override
	public String toString(){
		return text.toString();
	}
	
	private void doPrefix() {
		text.append(prefix);
	}

	/** A a message-id referenced by this email
	 * 
	 * @param msg_id
	 */
	public void addReference(String msg_id) {
		if( msg_id == null || msg_id.isEmpty()) {
			return;
		}
		for(String s : msg_id.split("\\s+")) {
			refs.add(s);
		}
	}

	public Set<String> getReferences(){
		return refs;
	}
}