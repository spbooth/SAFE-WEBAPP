// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model.lifecycle;

/** A default (does nothing) {@link LifeCycleListener}.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: AbstractListener.java,v 1.3 2014/09/15 14:30:33 spb Exp $")
public abstract class AbstractListener<R> extends AbstractAction<R> implements LifeCycleListener<R> {

	/**
	 * 
	 */
	public AbstractListener() {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.lifecycle.LifeCycleListener#prepare(java.lang.Object)
	 */
	public void prepare(R target) throws Exception {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.lifecycle.LifeCycleListener#abort(java.lang.Object)
	 */
	public void abort(R target) {
		
	}

}
