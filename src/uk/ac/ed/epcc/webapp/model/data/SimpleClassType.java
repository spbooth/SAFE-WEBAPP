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
package uk.ac.ed.epcc.webapp.model.data;


/** Varient of BasicType where the field is used to specify 
 * the class of a java object included in the 
 * DataObject by composition.
 * Be default we assume a no-argument constructor but 
 * sub-classes can add methods for additional constructors
 * 
 * @author spb
 *
 * @param <T> Type of Value object
 * @param <O> base type of generated classes
 */
public abstract class SimpleClassType<T extends SimpleClassType.ClassValue, O> extends BasicType<T> {


	protected SimpleClassType( String field){
		super(field);

	}
	public abstract

 static class ClassValue<S> extends BasicType.Value{
		private Class<? extends S> c;
		protected ClassValue(SimpleClassType  parent, Class<? extends S> c,String tag, String name) {
			super(parent,tag, name);
			this.c=c;
		}	

		public S getInstance() throws InstantiationException, IllegalAccessException{
			return  c.newInstance();
		}
	}
}