// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.html;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor;
/** A FormResult indicating a navigation to a new page within the current application
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: RedirectResult.java,v 1.3 2014/09/15 14:30:18 spb Exp $")

public class RedirectResult implements FormResult {
	private final String url;
	/** Constructor
	 * 
	 * @param url Target url relative to application root
	 */
	public RedirectResult(String url){
		this.url=url;
	}
   public String getURL(){
	   return url;
   }
public void accept(FormResultVisitor vis) throws Exception {
	if( vis instanceof WebFormResultVisitor){
		((WebFormResultVisitor)vis).visitRedirectResult(this);
		return;
	}
	throw new UnsupportedResultException();
}
}