// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author spb
 * @param <D> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: AbstractFilterResult.java,v 1.3 2015/02/03 09:36:28 spb Exp $")
public abstract class AbstractFilterResult<D extends DataObject> implements FilterResult<D> {
	
	  /* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.model.data.FilterResult#toCollection()
			 */
	        public final List<D> toCollection(){
	        	// default ot LinekdList to preserve ordering
	        	return toCollection(new LinkedList<D>());
	        }
	        /* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.model.data.FilterResult#toCollection(X)
			 */
	        public final <X extends Collection<D>> X toCollection(X res){
	        	
	        	Iterator<D> iterator = iterator();
	        	if( iterator != null){
	        		for(Iterator<D> it = iterator; it.hasNext();){
	        			res.add(it.next());
	        		}
	        	}
	        	return res;
	        }
}
