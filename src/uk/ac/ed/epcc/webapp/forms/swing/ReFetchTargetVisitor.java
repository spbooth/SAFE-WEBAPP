// Copyright - The University of Edinburgh 2016
package uk.ac.ed.epcc.webapp.forms.swing;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/** A {@link TransitionFactoryVisitor} that performs a clean re-fetch of the 
 * target.  This is to allow the target to be re-fetched within a database transaction.
 * 
 * Note that this can (and usually does) destroy the original copy of the target. 
 * @author spb
 *
 */
public class ReFetchTargetVisitor<T,K> implements TransitionFactoryVisitor<T, T, K>{
	private T target;
	
	/**
	 * 
	 */
	public ReFetchTargetVisitor(T target) {
		this.target=target;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor#visitTransitionProvider(uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider)
	 */
	@Override
	public T visitTransitionProvider(TransitionProvider<K, T> prov) {
		String id = prov.getID(target);
		clearTarget();
		return prov.getTarget(id);
	}
	public void clearTarget() {
		if( target instanceof DataObject){
			((DataObject)target).release(); // remove from cache
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor#visitPathTransitionProvider(uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider)
	 */
	@Override
	public T visitPathTransitionProvider(PathTransitionProvider<K, T> prov) {
		LinkedList<String> id = prov.getID(target);
		clearTarget();
		return prov.getTarget(id);
	}

	

}
