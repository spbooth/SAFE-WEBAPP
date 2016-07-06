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
package uk.ac.ed.epcc.webapp.model.data.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** General purpose key class used to key transitions.
 * Transitions can be keyed by any class and enums are often used for this.
 * This class is useful where the set of transitions may come from multiple sources
 * e.g. when sub-classes define additional transitions.
 * TransitionKey objects are parameterised by a target type. 
 * Though this is usually the target type of the Transitions it can be any type that uniquely identifies
 * the set of transitions.
 * @author spb
 *
 * @param <T> parameter type
 */


public class TransitionKey<T> {
	private final String name;
	private final String help;
	private final Class<? super T> target;
	public TransitionKey(Class<? super T> t,String name,String help){
		this.name=name;
		this.help=help;
		this.target=t;
	}
	public TransitionKey(Class<? super T> t,String name){
		this(t,name,null);
	}
	public String getName(){
		return name;
	}
    public String getHelp(){
    	return help;
    }
    public Class<? super T> getTarget(){
    	return target;
    }
    @Override
	public String toString(){
    	return getName();
    }
    /* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof TransitionKey ){
		   TransitionKey key = (TransitionKey) obj;
		   return (key == this) || 
		   ( key.getClass() == getClass() && key.getName().equals(getName()) && key.getTarget()==getTarget() );
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	protected final Logger getLogger(AppContext conn){
		return conn.getService(LoggerService.class).getLogger(getClass());
	}
}