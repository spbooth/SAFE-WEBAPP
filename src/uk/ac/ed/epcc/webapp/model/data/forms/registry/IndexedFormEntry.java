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
package uk.ac.ed.epcc.webapp.model.data.forms.registry;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.forms.registry.FormEntry;
import uk.ac.ed.epcc.webapp.forms.registry.FormPolicy;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.Owner;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;

public abstract  class IndexedFormEntry<F extends IndexedProducer<T>&Contexed,T extends Indexed> extends FormEntry<F, T> {

	public IndexedFormEntry(String name, Class<? extends F> c,
			String config_tag, FormPolicy policy) {
		super(name, c, config_tag, policy);
	}



	protected IndexedFormEntry(String name, Class<? extends F> c,
			FormPolicy policy) {
		super(name, c, policy);
	}

	

	public final String getID(T target) {
		return Integer.toString(target.getID());
	}

	public final T getTarget(AppContext c,String id) {
		try {
			F factory = getFactory(c);
			try{
				return factory.find(Integer.parseInt(id));
			}catch(NumberFormatException nfe){
				if( factory instanceof ParseFactory){
					return ((ParseFactory<T>)factory).findFromString(id);
				}
				return null;
			}
		} catch (Exception e) {
		    return null;
		}
	}
	public boolean targets(AppContext c, Object target) {
		try{
			F fac = getFactory(c);
			if( fac instanceof Owner){
				return ((Owner) fac).isMine(target);
			}
		}catch(Exception e){
			c.error(e,"Error getting owner");
		}
		// benefit of the doubt
		return true;
	}
}