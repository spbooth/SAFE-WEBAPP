// Copyright - The University of Edinburgh 2011
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
   public FilterFinder(AppContext c,Class<? super T> target,boolean allow_null){
	   super(c,target);
	   this.allow_null = allow_null;
   }
   public FilterFinder(AppContext c,Class<? super T> target){
	   super(c,target);
   }
   
   public O find(SQLFilter<T> f) throws  DataException{
	   return find(f,allow_null);
   }
   public O find(SQLFilter<T> f, boolean allow) throws  DataException{
	   setFilter((BaseFilter<T>)f);
	   O res = make();
	   if( res == null && ! allow){
		  throw new DataNotFoundException("No result from FilterFinder");
	   }
	   return res;
   }
}