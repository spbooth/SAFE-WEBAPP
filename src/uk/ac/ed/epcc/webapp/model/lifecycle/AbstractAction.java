// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model.lifecycle;

@uk.ac.ed.epcc.webapp.Version("$Id: AbstractAction.java,v 1.2 2014/09/15 14:30:33 spb Exp $")
/**
 * @author spb
 *
 * @param <R>
 */
public class AbstractAction<R> implements ActionListener<R>{

	/**
	 * 
	 */
	public AbstractAction() {
		super();
	}

	public boolean allow(R target, boolean throw_reason) throws LifeCycleException {
		return true;
	}

	public void action(R target) {
		
	}

}