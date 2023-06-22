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
/** Class representing a database field specification
 * This is intended to capture constraints on behaviour rather than fully specify
 * implementation.
 * <b>
 * We use a visitor pattern to implement operations such as generating SQL fragments. This ensures that any changes to the {@link FieldType}
 * type hierarchy.
 * will have to be reflected in database specific implementation code.
 * @author spb
 *
 * @param <T> type of field
 */
public  abstract class FieldType<T> {
   public abstract void accept(FieldTypeVisitor vis);
   private final Class<? super T> target;
   private final boolean can_be_null;
   private final T default_value;
   public FieldType(Class<? super T> clazz, boolean can_null, T default_val){
	   target=clazz;
	   can_be_null=can_null;
	   default_value=default_val;
   }
   public Class<? super T> geTarget(){
	   return target;
   }
   public boolean canBeNull(){
	   return can_be_null;
   }
   public T getDefault(){
	   return default_value;
   }
}