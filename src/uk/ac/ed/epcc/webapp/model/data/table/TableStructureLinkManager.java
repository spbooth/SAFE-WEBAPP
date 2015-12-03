//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.model.data.table;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.CompositeTableTransitionRegistry;
import uk.ac.ed.epcc.webapp.jdbc.table.DefaultTableTransitionRegistry;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.jdbc.table.TableStructureTransitionTarget;
import uk.ac.ed.epcc.webapp.jdbc.table.TableTransitionRegistry;
import uk.ac.ed.epcc.webapp.jdbc.table.TransitionSource;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.LinkManager;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.TableStructureContributer;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** A {@link LinkManager} that implements {@link TableStructureTransitionTarget}.
 * @author spb
 * @param <T> 
 * @param <L> 
 * @param <R> 
 *
 */

public abstract class TableStructureLinkManager<T extends LinkManager.Link<L,R>,L extends DataObject,R extends DataObject> extends LinkManager<T, L, R> implements TableStructureTransitionTarget{
	private TableTransitionRegistry reg=null;
	
	public final TableTransitionRegistry getTableTransitionRegistry() {
		if( reg == null ){
			reg = makeTableRegistry();
			if( reg instanceof CompositeTableTransitionRegistry){
				CompositeTableTransitionRegistry comp = (CompositeTableTransitionRegistry)reg;
				for(TableStructureContributer c : getTableStructureContributers()){
					if( c instanceof TransitionSource){
						comp.addTransitionSource((TransitionSource)c);
					}
				}
			}
		}
		return reg;
	}
	
	protected TableTransitionRegistry makeTableRegistry() {
		TableSpecification spec = getDefaultTableSpecification(getContext(), getTag(),getLeftFactory(),getLeftField(),getRightFactory(),getRightField());
		return new DefaultTableTransitionRegistry<TableStructureLinkManager<Link<L,R>, L, R>>(res, spec);
	}
	protected void resetTransitionRegistry(){
		reg=null;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.transition.TableTransitionTarget#getTableTransitionID()
	 */
	public final String getTableTransitionID() {
		return getTag();
	}
	

	/**
	 * @param c
	 * @param table
	 * @param left_fac
	 * @param left_field
	 * @param right_fac
	 * @param right_field
	 */
	public TableStructureLinkManager(AppContext c, String table,
			DataObjectFactory<L> left_fac, String left_field,
			DataObjectFactory<R> right_fac, String right_field) {
		super(c, table, left_fac, left_field, right_fac, right_field);
	}

	public void resetStructure() {
		reg=null;
	}


}