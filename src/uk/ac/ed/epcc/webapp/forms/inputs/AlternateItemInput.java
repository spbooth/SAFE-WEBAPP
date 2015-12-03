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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Iterator;



/**
 * 
 * @author spb
 *
 * @param <T> Input type
 * @param <I> Item type
 */
public class AlternateItemInput<T,I> extends AlternateInput<T> implements ItemInput<I> {

	public I getItem() {
		for(Iterator<Input<T>> it = getInputs();it.hasNext();){
			ItemInput<I> i =  (ItemInput<I>) it.next();
			I val = i.getItem();
			if( val != null ){
				return val;
			}
		}
		return null;
	}

	public void setItem(I item) {
		
			boolean set=false;
			for(Iterator<Input<T>> it = getInputs();it.hasNext();){
				ItemInput<I> i =  (ItemInput<I>) it.next();
				if( ! set ){
				   i.setItem(item);
				   set = true;
				}else{
					i.setItem(null);
				}
				
			}
			
		
	}


}