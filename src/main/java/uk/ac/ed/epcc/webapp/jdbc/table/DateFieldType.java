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

import java.util.Date;


/** Default field type for storing java {@link Date} objects.
 * 
 * @author spb
 *
 */
public class DateFieldType extends FieldType<Date> {

	private final boolean truncate;
	
	/** Timestamp
	 * 
	 * @param can_null
	 * @param default_val
	 */
	public DateFieldType( boolean can_null,
			Date default_val) {
		this(can_null,default_val,false);
	}
	/** Generic Date or timestamp
	 * 
	 * @param can_null    can field be null
	 * @param default_val default field value
	 * @param truncate  truncate to date not timestamp
	 */
	public DateFieldType( boolean can_null,
				Date default_val,boolean truncate) {
		super(Date.class, can_null, default_val);
		this.truncate=truncate;
	}

	@Override
	public void accept(FieldTypeVisitor vis) {
		vis.visitDateFieldType(this);
	}
	public boolean isTruncate() {
		return truncate;
	}

}