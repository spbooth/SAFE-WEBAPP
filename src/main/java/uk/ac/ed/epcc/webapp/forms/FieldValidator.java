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
package uk.ac.ed.epcc.webapp.forms;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;

/**
 * Interface that validates a value. These are most frequently used to validate
 * permitted values in UI forms. However they could also be used to validate constraints on the 
 * data model itself (these map to form field validation in edit/update forms but could also apply
 * be used for API validation.
 * 
 * 
 * As {@link FieldValidator}s are stored in Sets it is good practice to implement
 * <b>hashCode()</b> and <b>equals(Object)</b>.
 * 
 * 
 * Note that a {@link FieldValidator} could throw a {@link MissingFieldException} 
 * 
 * @author spb
 * @param <D> Type of input data
 */
public interface FieldValidator<D> {

	public  void validate(D data)
			throws FieldException;
}