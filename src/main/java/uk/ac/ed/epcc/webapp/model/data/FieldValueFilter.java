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

import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** A filter that matches (exactly) a value using a {@link FieldValue}
 * Normally {@link FieldValue}s are either {@link FieldSQLExpression}s or {@link FilterProvider}s
 * but this will work in the remaining cases e.g. {@link TypeProducerFieldValue}
 * 
 * @author Stephen Booth
 * @param <V> type of value
 * @param <T> type of filter
 * @see FieldValue
 * @see TypeProducerFieldValue
 */
public class FieldValueFilter<V,T> implements PatternFilter<T>, SQLFilter<T> {
	/**
	 * @param target 
	 * @param field
	 * @param value
	 */
	public FieldValueFilter(Class<T> target,FieldValue<V, T> field, V value) {
		super();
		this.target=target;
		this.field = field;
		this.value = value;
	}
	private final Class<T> target;
	private final FieldValue<V, T> field;
	private final V value;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<T> getTarget() {
		return target;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter#getParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list.add(new FieldValuePatternArgument<>(field, value));
		return list;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter#addPattern(java.util.Set, java.lang.StringBuilder, boolean)
	 */
	@Override
	public StringBuilder addPattern(Set<Repository> tables, StringBuilder sb, boolean qualify) {
		field.addField(sb, qualify);
		sb.append("=?");
		return sb;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		FieldValueFilter other = (FieldValueFilter) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
