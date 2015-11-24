// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.SessionService;

public abstract class AbstractTableRegistry implements CompositeTableTransitionRegistry {

	/**
	 * 
	 */
	public static final String CHANGE_TABLE_STRUCTURE_ROLE = "ChangeTableStructure";
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.transition.TableTransitionTarget#allowTableTransition(uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey, uk.ac.ed.epcc.webapp.model.AppUser)
	 */
	public boolean allowTableTransition(TransitionKey name, SessionService operator) {
		return operator.hasRole(CHANGE_TABLE_STRUCTURE_ROLE);
	}
	
	private Map<TransitionKey,Transition> table_transitions = new LinkedHashMap<TransitionKey,Transition>();
	/** Method to allow sub-classes to add table transitions
	 * 
	 * @param key
	 * @param t
	 */
	protected <X extends TableTransitionTarget> void addTableTransition(TransitionKey<X> key, Transition<X> t){
    	table_transitions.put(key,t);
    }
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.CompositeTableTransitionRegistry#addTransitionSource(uk.ac.ed.epcc.webapp.jdbc.table.TransitionSource)
	 */
	public <X extends TableTransitionTarget> void  addTransitionSource(TransitionSource<X> source){
		if( source == null ){
			return;
		}
		Map<TransitionKey<X>,Transition<X>> map = source.getTransitions();
		for(TransitionKey<X> key : map.keySet()){
			addTableTransition(key, map.get(key));
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.transition.TableTransitionTarget#getTableTransition(uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey)
	 */
	@SuppressWarnings("unchecked")
	public Transition<TableTransitionTarget> getTableTransition(
			TransitionKey name) {
		return table_transitions.get(name);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.transition.TableTransitionTarget#getTableTransitionKeys()
	 */
	public Set<TransitionKey> getTableTransitionKeys() {
		return table_transitions.keySet();
	}
	

}