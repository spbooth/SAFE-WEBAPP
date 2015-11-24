// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.reference;

import uk.ac.ed.epcc.webapp.Indexed;

public interface ReferenceProvider extends Indexed{
	/** 
	 * 
	 * @return an IndexedReference to self.
	 */
  public IndexedReference getReference();
}