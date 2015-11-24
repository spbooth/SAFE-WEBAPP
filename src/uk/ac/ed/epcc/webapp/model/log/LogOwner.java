// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.log;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;

public  interface LogOwner<O extends Indexed> extends Contexed, IndexedProducer<O> {
	/** Get the LogFactory 
	 * 
	 * @return LogFactory
	 */
	public LogFactory getLogFactory();
	/** get the tag used to create this factory via the AppContext
	 * 
	 * @return tag
	 */
	public String getTag();
}