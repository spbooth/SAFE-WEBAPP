// Copyright - The University of Edinburgh 2011
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