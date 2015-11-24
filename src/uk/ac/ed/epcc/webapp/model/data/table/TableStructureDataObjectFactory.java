// Copyright - The University of Edinburgh 2014
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
@uk.ac.ed.epcc.webapp.Version("$Id: TableStructureDataObjectFactory.java,v 1.4 2015/08/18 08:57:12 spb Exp $")
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
