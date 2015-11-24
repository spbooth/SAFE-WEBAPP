// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;

import java.util.StringTokenizer;

import javax.mail.MessagingException;
/** Basic class for building email messages
 * 
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: TextMailBuilder.java,v 1.2 2014/09/15 14:30:16 spb Exp $")

public class TextMailBuilder {
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
			StringTokenizer st = new StringTokenizer(s,"\n",true);
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

	

}