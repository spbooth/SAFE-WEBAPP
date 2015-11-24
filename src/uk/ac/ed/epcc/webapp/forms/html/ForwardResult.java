// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.html;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor;
/** FormResult to indicate forwarding to a page from within a servlet.
 * From a transition the current target/provider will be passed by attribute by default.
 * Optionally additional attributes can be supplied by this class.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ForwardResult.java,v 1.3 2014/09/15 14:30:18 spb Exp $")
public class ForwardResult implements FormResult {
	private final String url;
	private final HashMap<String,Object> attr;
	/** Constructor
	 * 
	 * @param url Target url relative to application root
	 */
	public ForwardResult(String url){
		this.url=url;
		this.attr=null;
	}
	/** Constructor
	 * 
	 * @param url Target url relative to application root
	 * @param attr 
	 */
	public ForwardResult(String url,Map<String,Object> attr){
		this.url=url;
		this.attr=new HashMap<String, Object>();
		this.attr.putAll(attr);
	}
	public String getURL(){
		return url;
	}
	public Map<String,Object> getAttr(){
		if( attr == null ){
			return null;
		}
		return (Map<String, Object>) attr.clone();
	}
	public void accept(FormResultVisitor vis) throws Exception {
		if( vis instanceof WebFormResultVisitor){
			((WebFormResultVisitor)vis).visitForwardResult(this);
			return;
		}
		throw new UnsupportedResultException();
	}
}