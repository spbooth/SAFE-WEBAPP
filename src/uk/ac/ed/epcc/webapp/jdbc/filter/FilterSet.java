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

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseCombineFilter.AddFilterVisitor;

/**
 * @author spb
 *
 * @param <T>
 */
public abstract class FilterSet<T> {

	protected Class<? super T> target;

	/**
	 * 
	 */
	public FilterSet(Class<? super T> target) {
		super();
		this.target=target;
	}

	protected abstract FilterVisitor getAddVisitor();
	protected final FilterSet<T> add(BaseFilter<? super T> fil, boolean check_types) {
		
		if( fil == null || fil==this){
			return this;
		}
		if( check_types){
			if( target == null ){
				target=fil.getTarget();
			}else{
				// Its OK to add a super-type filter to a more specific filter but
				// not the other way round.
				Class target2 = fil.getTarget();
				if( target2 != null && target != null && ! target2.isAssignableFrom(target)){
					if( target.isAssignableFrom(target2)){
						// adding more restricive target
						target=target2;
					}else{
						//TODO check this always but run as assertion for a bit.
						assert(false);
						//throw new ConsistencyError("Incompatible filter types "+target2.getCanonicalName()+","+target.getCanonicalName());
					}
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
	
	public final Class<? super T> getTarget(){
		return target;
	}

}