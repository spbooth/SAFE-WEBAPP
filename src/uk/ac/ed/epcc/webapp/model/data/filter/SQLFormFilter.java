// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.filter;

import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.forms.SQLMatcher;
/** Form based SQL only filter
 * 
 * @author spb
 * @param <T> type of value being matched
 */
@uk.ac.ed.epcc.webapp.Version("$Id: SQLFormFilter.java,v 1.4 2014/09/15 14:30:31 spb Exp $")

public class SQLFormFilter<T extends DataObject> extends SQLAndFilter<T>{

	public SQLFormFilter(Form f) {
		this(DataObject.class,null,f,null);

	}
    public SQLFormFilter(Form f, Map<String,SQLMatcher<T>> m){
    	this(DataObject.class,null,f,m);
    }
    @SuppressWarnings("unchecked")
	public SQLFormFilter(Class<? super T> target,Repository res,Form f, Map<String,SQLMatcher<T>> matchers){ 
    	super(target);
    	for(Iterator<String> it=f.getFieldIterator(); it.hasNext();){
			String field = it.next();
			SQLMatcher<T> m=null;
			if(matchers != null){
				m = matchers.get(field);
			}
			// if the input implements SQLMatcher then use that this is used by NullListInput 
			// to match against null values
			Input i = f.getInput(field);
			if( i instanceof SQLMatcher ){
				m = (SQLMatcher<T>) i;
			}
			
			Object o =  f.get(field);
			// At the moment we treat null as a wildcard so can't select for null
			if( o != null ){
				if( m == null ){
					if( res.hasField(field)){
						addFilter(new SQLValueFilter<T>(target,res,field,o));
					}else{
						if( res.isUniqueIdName(field)){
							addFilter(new SQLIdFilter<T>(target,res, (Integer)o));
						}else{
							throw new ConsistencyError("Unrecognised field "+field);
						}
					}
				}else{
					addFilter(m.getSQLFilter(target,res,field, o));
				}
			}
		}
    }
}