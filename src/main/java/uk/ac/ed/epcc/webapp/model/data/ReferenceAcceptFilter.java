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
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;

/** An AcceptFilter version of {@link ReferenceFilter}
 * 
 * @author spb
 *@see ReferenceFilter
 * @param <R>
 */
public final class ReferenceAcceptFilter<R extends Indexed,T extends DataObject> implements AcceptFilter<T>{
	  
	  private final R peer;
	  private final String field;
        /** Make the filter
         * 
         * @param field field referencing the peer
         * @param peer DataObject null for all records
         */
        public ReferenceAcceptFilter(String field, R peer){
        	this.field =field;
        	this.peer=peer;
        }
		
		
		public final boolean test(T d) {
			return d.record.getIntProperty(field, 0) == peer.getID();
		}	
}