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

/** Object creation transition.
 * 
 * @author spb
 *
 * @param <X> type of object created
 */


public class FormCreatorProducerTransition<X> extends CreatorTransition<X> {
    
    private final FormCreatorProducer producer;
	
   
    public FormCreatorProducerTransition(String type_name,FormCreatorProducer producer){
    	super(type_name);
    	this.producer=producer;
    }
	
	
	@Override
	public FormCreator getCreator(AppContext c) {
		return producer.getFormCreator(c);
	}

}