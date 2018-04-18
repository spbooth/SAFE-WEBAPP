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
package uk.ac.ed.epcc.webapp.jdbc.table;

import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.ConfirmTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ForwardTransition;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** TransitionSource for the basic table edit operations
 * 
 * @author spb
 *
 * @param <T>
 */


public class GeneralTransitionSource<T extends DataObjectFactory> implements TransitionSource<T> {
	
	/**
	 * 
	 */
	static final String ADD_FOREIGN_KEYS_KEY = "AddForeignKeys";
	/**
	 * 
	 */
	static final String DROP_TABLE_KEY = "DropTable";
	/**
	 * 
	 */
	static final String DROP_FIELD_KEY = "DropField";
	/**
	 * 
	 */
	static final String DROP_INDEX_KEY = "DropIndex";
	/**
	 * 
	 */
	static final String DROP_FOREIGN_KEY_KEY = "DropForeignKey";
	/**
	 * 
	 */
	static final String ADD_REFERENCE_FIELD_KEY = "AddReferenceField";
	
	/**
	 * 
	 */
	static final String ADD_DATE_FIELD_KEY = "AddDateField";
	/**
	 * 
	 */
	static final String ADD_TEXT_FIELD_KEY = "AddTextField";
	/**
	 * 
	 */
	static final String ADD_INTEGER_FIELD_KEY = "AddIntegerField";
	/**
	 * 
	 */
	static final String ADD_LONG_FIELD_KEY = "AddLongField";
	/**
	 * 
	 */
	static final String ADD_FLOAT_FIELD_KEY = "AddFloatField";
	/**
	 * 
	 */
	static final String ADD_DOUBLE_FIELD_KEY = "AddDoubleField";
	private Map<TableTransitionKey,Transition<T>> table_transitions = new LinkedHashMap<TableTransitionKey,Transition<T>>();
	
	public GeneralTransitionSource(Repository res){
		
		addTransition(DROP_TABLE_KEY,new ConfirmTransition<T>(
			     "Delete this table ? (all data will be lost)", 
			     new DropTableTransition<T>(res.getContext()), 
			     new ForwardTransition<T>(new MessageResult("aborted"))) );
		addTransition(ADD_FOREIGN_KEYS_KEY,new ConfirmTransition<T>(
			     "Add Foreign Key definitions?", 
			     new AddForeignKeyTransition<T>(), 
			     new ForwardTransition<T>(new MessageResult("aborted"))) );
	
		addTransition(DROP_FIELD_KEY, new DropFieldTransition<T>());
		addTransition(DROP_INDEX_KEY, new DropIndexTransition<T>());
		addTransition(DROP_FOREIGN_KEY_KEY, new DropForeignKeyTransition<T>());
		addTransition(ADD_REFERENCE_FIELD_KEY, new AddReferenceTransition<T>());
		addTransition(ADD_DATE_FIELD_KEY, new AddDateFieldTransition<T>());
		addTransition(ADD_TEXT_FIELD_KEY, new AddTextFieldTransition<T>());
		addTransition(ADD_INTEGER_FIELD_KEY, new AddIntegerFieldTransition<T>());
		addTransition(ADD_LONG_FIELD_KEY, new AddLongFieldTransition<T>());
		addTransition(ADD_FLOAT_FIELD_KEY, new AddFloatFieldTransition<T>());
		addTransition(ADD_DOUBLE_FIELD_KEY, new AddDoubleFieldTransition<T>());
	}
	
	private void addTransition(String name,Transition<T> t){
		table_transitions.put(new TableDeveloperKey(DataObjectFactory.class, name),t);
	}
	public Map<TableTransitionKey, Transition<T>> getTransitions() {
		return table_transitions;
	}

}