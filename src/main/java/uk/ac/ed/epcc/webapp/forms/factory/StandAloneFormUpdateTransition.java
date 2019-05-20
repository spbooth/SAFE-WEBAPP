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
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.data.forms.UpdateTransition;


/** A edit/update transition that wraps a {@link StandAloneFormUpdate}.
 * 
 * 
 * @author Stephen Booth
 * @see UpdateTransition
 * @see StandAloneFormUpdateProducerTransition
 * @param <T>
 */
public class StandAloneFormUpdateTransition<T> extends EditTransition<T> {
    private final StandAloneFormUpdate<T> update;
    public StandAloneFormUpdateTransition(String type_name,StandAloneFormUpdate<T> update){
    	super(type_name);
    	this.update=update;
    }
	@Override
	public StandAloneFormUpdate<T> getUpdate(AppContext c,T dat) {
		return update;
	}

}