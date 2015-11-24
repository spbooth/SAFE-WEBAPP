// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.convert;

import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** TypeProducer with methods to create Filters.
 * 
 * Methods are available to explicitly produce SQL and Accept 
 * filters as well as a default Filter.
 * 
 * @author spb
 *
 * @param <T> Type of object produced
 * @param <D> Type of Object stored in DB field.
 */
public interface TypeFilterProducer<T,D> extends TypeProducer<T,D>  {
	public <I extends DataObject> AcceptFilter<I> getAcceptFilter(DataObjectFactory<I> fac,T val);
	public <I extends DataObject> SQLFilter<I> getSQLFilter(DataObjectFactory<I> fac, T val);
	public <I extends DataObject> BaseFilter<I> getFilter(DataObjectFactory<I> fac, T val);
	public <I extends DataObject> AcceptFilter<I> getAcceptFilter(DataObjectFactory<I> fac, Set<T> val);
	public <I extends DataObject> SQLFilter<I> getSQLFilter(DataObjectFactory<I> fac, Set<T> val);
	public <I extends DataObject> BaseFilter<I> getFilter(DataObjectFactory<I> fac,Set<T> val);
	public <I extends DataObject> AcceptFilter<I> getAcceptExcludeFilter(DataObjectFactory<I> fac, Set<T> val);
	public <I extends DataObject> SQLFilter<I> getSQLExcludeFilter(DataObjectFactory<I> fac, Set<T> val);
	public <I extends DataObject> BaseFilter<I> getExcludeFilter(DataObjectFactory<I> fac,Set<T> val);
}