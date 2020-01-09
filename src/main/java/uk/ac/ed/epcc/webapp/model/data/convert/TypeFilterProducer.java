//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.convert;

import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrFilter;
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
	public <I extends DataObject> SQLFilter<I> getFilter(DataObjectFactory<I> fac, T  val );
	
	public <I extends DataObject> SQLFilter<I> getExcludeFilter(DataObjectFactory<I> fac,T val);
	
	
	
	default public <I extends DataObject> SQLFilter<I> getFilter(DataObjectFactory<I> fac,Set<T> val) {
		SQLOrFilter<I> or = new SQLOrFilter<I>(fac.getTarget());
		for( T t : val) {
			or.addFilter(getFilter(fac, t));
		}
		return or;
	}
	
	default public <I extends DataObject> SQLFilter<I> getFilter(DataObjectFactory<I> fac,T ... val) {
		SQLOrFilter<I> or = new SQLOrFilter<I>(fac.getTarget());
		for( T t : val) {
			or.addFilter(getFilter(fac, t));
		}
		return or;
	}
	
	default public <I extends DataObject> SQLFilter<I> getExcludeFilter(DataObjectFactory<I> fac,Set<T> val) {
		SQLAndFilter<I> and = new SQLAndFilter<I>(fac.getTarget());
		for( T t : val) {
			and.addFilter(getExcludeFilter(fac, t));
		}
		return and;
	}
	
	default public <I extends DataObject> SQLFilter<I> getExcludeFilter(DataObjectFactory<I> fac,T ... val) {
		SQLAndFilter<I> and = new SQLAndFilter<I>(fac.getTarget());
		for( T t : val) {
			and.addFilter(getExcludeFilter(fac, t));
		}
		return and;
	}
}