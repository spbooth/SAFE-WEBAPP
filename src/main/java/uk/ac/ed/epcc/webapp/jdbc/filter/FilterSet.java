//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;

/** Abstract class for a set of Filters 
 * 
 * This class performs type checking but the implementation assumes that 
 * filters for super-types are also valid.
 * @author spb
 *
 * @param <T> type of filter
 */
public abstract class FilterSet<T> {


	protected String target_tag;

	/**
	 * @param target type of filter
	 * 
	 */
	public FilterSet(String tag) {
		super();
		this.target_tag=tag;
	}

	/** get a visitor to add the visitors target to the set.
	 * 
	 * @return
	 */
	protected abstract FilterVisitor getAddVisitor();
	protected final FilterSet<T> add(BaseFilter<? super T> fil, boolean check_types) {
		
		if( fil == null || fil==this){
			return this;
		}
		if( check_types){
			if( target_tag == null ){
				target_tag = fil.getTag();
			}else{
				// Its OK to add a super-type filter to a more specific filter but
				// not the other way round.
				String tag2 = fil.getTag();
				if( target_tag != null && tag2 != null && ! target_tag.equals(tag2)){
				
						throw new ConsistencyError("Incompatible filter types "+target_tag+","+tag2);
				}
			}
		}
		try {
			fil.acceptVisitor(getAddVisitor());
		} catch (Exception e) {
			throw new ConsistencyError("Fatal filter combine error",e);
		}
		return this;
	}
	
	public final String getTag() {
		return target_tag;
	}

	
	/** is the set of selection filters empty
	 * 
	 * @return
	 */
	public abstract boolean isEmpty();
	
	/** return the number of filters that will be returned by the {@link #getSet()} method
	 * 
	 * @return
	 */
	public abstract int size();

	/** get a {@link Set} of selection filters represented by this {@link FilterSet}
	 * order only filters may not be represented
	 * @return
	 */
	public abstract Set<BaseFilter> getSet();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((target_tag == null) ? 0 : target_tag.hashCode());
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
		FilterSet other = (FilterSet) obj;
		if (target_tag == null) {
			if (other.target_tag != null)
				return false;
		} else if (!target_tag.equals(other.target_tag))
			return false;
		return true;
	}
}