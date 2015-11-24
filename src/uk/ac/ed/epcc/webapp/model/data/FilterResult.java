// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;

import java.util.Collection;
import java.util.List;


/** Interface for containers that hold Filter results
 * Normally a FilterSet produced by the factory. 
 * Having a separate interface makes the source code much clearer in particular when
 * referencing results returned by other factories.
 * 
 *  This should be the default type returned by methods that return multiple results as
 *  it can be used directly in loop and is easily added/converted to a {@link Collection}
 *  and hence made into an array. 
 * 
 * @author spb
 *
 * @param <D> Type of object produced
 */
public interface FilterResult<D extends DataObject>  extends Iterable<D>{

	/** Generate a {@link List} representing the contents of the {@link FilterResult}
	 * 
	 * @return {@link List}
	 */
	public abstract List<D> toCollection();

	/** Add the contents of the {@link FilterResult} to a collection
	 * and return the modified collection.
	 * 
	 * @param res {@link Collection}
	 * @return modified {@link Collection}
	 */
	public abstract <X extends Collection<D>> X toCollection(X res);

}