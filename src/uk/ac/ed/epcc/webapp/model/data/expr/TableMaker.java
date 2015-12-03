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