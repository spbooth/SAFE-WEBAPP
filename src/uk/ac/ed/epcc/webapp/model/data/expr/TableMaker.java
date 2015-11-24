// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/** FilterMaker for creating tables.
 * 
 */
package uk.ac.ed.epcc.webapp.model.data.expr;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterMaker;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

public abstract class TableMaker<O> extends FilterMaker<O,Table>{
	public TableMaker(AppContext c,Class<? super O> target) {
		super(c,target);
	}
    protected void formatTable(Table t){
    	
    }
	public Table makeTable(SQLFilter<O> f) throws DataException{
		 setFilter(f);
		 Table t;
		
		t = make();
		
		 if( t == null ){
			 t = new Table();
		 }
		formatTable(t);
		return t;
	}
}