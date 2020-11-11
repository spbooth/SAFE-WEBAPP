//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;

/** A {@link PatternArg} corresponding to a constant value for a {@link FieldValue}
 * @author Stephen Booth
 *
 * @param <T> type of field data
 * @param <X> type of owning DataObject
 */
public class FieldValuePatternArgument<T,X> implements PatternArgument {

	/**
	 * @param field
	 * @param value
	 */
	public FieldValuePatternArgument(FieldValue<T, X> field, T value) {
		super();
		this.field = field;
		this.value = value;
	}

	private final FieldValue<T, X> field;
	private final T value;
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument#addArg(java.sql.PreparedStatement, int)
	 */
	@Override
	public void addArg(PreparedStatement stmt, int pos) throws SQLException {
		field.setObject(stmt, pos, value);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument#getField()
	 */
	@Override
	public String getField() {
		return field.getFieldName();
	}
	
	public FieldValue<T,X> getFieldValue(){
		return field;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument#getArg()
	 */
	@Override
	public Object getArg() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldValuePatternArgument other = (FieldValuePatternArgument) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}


}
