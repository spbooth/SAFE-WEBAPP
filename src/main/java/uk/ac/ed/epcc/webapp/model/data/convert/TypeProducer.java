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
package uk.ac.ed.epcc.webapp.model.data.convert;

import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldType;
import uk.ac.ed.epcc.webapp.model.data.BasicType;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;



/** Implemented by classes that produce classes based on the value of a database field.
 * <p>
 * 
 * @see Repository
 * @see BasicType
 * @author spb
 *
 * @param <T> Type of object produced.
 * @param <D> Type of Object stored in DB field.
 */
public interface TypeProducer<T,D> extends TypeConverter<T,D>, Selector<Input<? extends D>>{
	/** Name of the Database field we index.
	 * 
	 * @return Field name
	 */
  public String getField();
  /** Create a {@link  FieldType} to create an appropriate field.
   * 
   * The type of the field can be more tightly specified than the types that
   * can be converted.
   * 
   * @param def
   * @return FieldType
   */
  public FieldType<? extends D> getFieldType(T def);
 
}