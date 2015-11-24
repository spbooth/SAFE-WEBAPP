// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;

import uk.ac.ed.epcc.webapp.Targetted;

/**
 * Base Interface for all Filter types.
 * 
 * There are multiple sub-interfaces that provide different functionality 
 * and but we also need to support composite filters that combine this functionality.
 * Code that uses filters and the composite types need to be updated if a new sub-interface is
 * introduced. We therefore require that filters either implement just one of the sub-types or
 * extend one of the canonical combining types. This is enforced using the 
 * visitor pattern.
 * <p>
 * All filters are parameterised by the type of object they select for and implements {@link Targetted} 
 * so that this can be checked at run-time.
 * 
 * @author spb
 * @param <T> target type.
 */
public interface BaseFilter<T> extends Targetted<T>{
	public <X> X acceptVisitor(FilterVisitor<X,? extends T> vis) throws Exception;

}