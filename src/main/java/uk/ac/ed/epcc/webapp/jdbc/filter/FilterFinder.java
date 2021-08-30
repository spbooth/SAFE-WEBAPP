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
package uk.ac.ed.epcc.webapp.jdbc.filter;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;

/** Class to create single items specified by a filter and {@link ResultMapper}.
 * 
 * @author spb
 * @param  <T> type of filter
 * @param <O> Type of object being produced
 *
 */
public abstract  class FilterFinder<T,O> extends FilterMaker<T,O> {
	boolean allow_null=false;
   public FilterFinder(AppContext c,Class<T> target,boolean allow_null){
	   super(c,target);
	   this.allow_null = allow_null;
   }
   public FilterFinder(AppContext c,Class<T> target){
	   super(c,target);
   }
   
   public O find(SQLFilter<T> f) throws  DataException{
	   return find(f,allow_null);
   }
   public O find(SQLFilter<T> f, boolean allow_null) throws  DataException{
	   setFilter((BaseFilter<T>)f);
	   O res = make();
	   if( res == null && ! allow_null){
		  throw new DataNotFoundException("No result from FilterFinder "+getLastQuery());
	   }
	   return res;
   }
}