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



import uk.ac.ed.epcc.webapp.jdbc.table.CompositeTableTransitionRegistry;
import uk.ac.ed.epcc.webapp.jdbc.table.DefaultTableTransitionRegistry;
import uk.ac.ed.epcc.webapp.jdbc.table.TableStructureTransitionTarget;
import uk.ac.ed.epcc.webapp.jdbc.table.TableTransitionRegistry;
import uk.ac.ed.epcc.webapp.jdbc.table.TransitionSource;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.TableStructureContributer;

/** A  {@link DataObjectFactory} with a default implementation of {@link TableStructureTransitionTarget}.
 * 
 * Though this behaviour is mostly added by composition it is still useful to have default superclass that can be extended to to futher reduce code duplication. 
 * @author spb
 * @param <BDO> 
 *
 */

public abstract class TableStructureDataObjectFactory<BDO extends DataObject> extends DataObjectFactory<BDO> implements TableStructureTransitionTarget {

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
	
	protected TableTransitionRegistry makeTableRegistry(){
		return new DefaultTableTransitionRegistry<TableStructureDataObjectFactory>(res, getFinalTableSpecification(getContext(), getTag()));
	}


	public final String getTableTransitionID() {
		return getTag();
	}

	public void resetStructure() {
		reg=null;
	}


	

	
}