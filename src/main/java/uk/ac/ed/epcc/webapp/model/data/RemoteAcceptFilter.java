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

import uk.ac.ed.epcc.webapp.jdbc.filter.AbstractAcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.Joiner;

/** An {@link AcceptFilter} version of a {@link Joiner}
 * 
 * If you need a remote filter which is not known to be a 
 * {@link AcceptFilter} use 
 * {@link DataObjectFactory#getRemoteFilter(DataObjectFactory, String, BaseFilter)}
 * as this uses a visitor to produce an optimised implementation strategy 
 * 
 * 
 * @author spb
 * @param <T> type of filter
 * @param <R> remote type
 *
 */
public class RemoteAcceptFilter<T extends DataObject, R extends DataObject> extends AbstractAcceptFilter<T>{

	/**
	 * @param target  type of filter 
	 * @param remote factory for remote type
	 * @param field field to join on
	 * @param fil {@link AcceptFilter} to apply
	 */
	public RemoteAcceptFilter(String target,DataObjectFactory<R> remote,String field, BaseFilter<R> fil) {
		super(target);
		this.remote=remote;
		this.field=field;
		this.fil=fil;
	}
	private final DataObjectFactory<R> remote;
	private final String field;
	private final BaseFilter<R> fil;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean test(T o) {
	    R remote_target = remote.find(o.record.getNumberProperty(field));
	    if( remote_target == null){
	    	return false;
	    }
	    return remote.matches(fil, remote_target);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((fil == null) ? 0 : fil.hashCode());
		result = prime * result + ((remote == null) ? 0 : remote.hashCode());
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
		RemoteAcceptFilter other = (RemoteAcceptFilter) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (fil == null) {
			if (other.fil != null)
				return false;
		} else if (!fil.equals(other.fil))
			return false;
		if (remote == null) {
			if (other.remote != null)
				return false;
		} else if (!remote.equals(other.remote))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "RemoteAcceptFilter(remote=" + remote.getTag() + ", field=" + field + ", fil=" + fil + ")";
	}

}
