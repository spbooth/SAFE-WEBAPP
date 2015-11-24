// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.reference;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.content.Transform;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
/** Table format form converting reference fields into 
 * object identifier strings.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: IndexedReferenceFormat.java,v 1.4 2014/09/15 14:30:32 spb Exp $")

public class IndexedReferenceFormat implements Transform{
    private AppContext c;
    public IndexedReferenceFormat(AppContext c){
    	this.c=c;
    }
    public Object convert(Object old) {
    	if( old == null ){
    		// could be a total row in a category sum
    		return null;
    	}
	    if( old instanceof IndexedReference ){
	    		IndexedReference ref = (IndexedReference) old;
	    		if( ref.getID() == 0 ){
	    			return "Unknown";
	    		}
	    		try {
					Indexed i = ref.getIndexed(c);
					if( i instanceof Identified){
						return ((Identified) i).getIdentifier();
					}
					return i.toString();
				} catch (Throwable e) {
					return "Unknown";
				}
	    }
    	return old;
    }
}